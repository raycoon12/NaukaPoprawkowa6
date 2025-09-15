package server;

import game.Duel;
import game.Gesture;
import server.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {

    private final Database database = new Database();
    private static final int PORT = 1234;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    // Metoda nasłuchująca klientów
    public void listen() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serwer uruchomiony na porcie " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nowy klient: " + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(clientSocket, this);
                clients.add(handler);
                new Thread(handler).start(); // każdy klient w osobnym wątku
            }

        } catch (IOException e) {
            System.err.println("Blad serwera: " + e.getMessage());
        }
    }

    // Wysyłanie wiadomości do wszystkich klientów
    public void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    // Usunięcie klienta po rozłączeniu
    public void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Usunieto klienta. Aktualna liczba polączonych: " + clients.size());
    }

    public void challengeToDuel(ClientHandler challenger, String challengeeLogin) {
        // Szukamy klienta o podanym loginie
        ClientHandler challengee = null;
        for (ClientHandler client : clients) {
            if (client.getLogin().equals(challengeeLogin)) {
                challengee = client;
                break;
            }
        }

        // Jeśli nie znaleziono gracza
        if (challengee == null) {
            challenger.sendMessage("Nie znaleziono gracza o loginie: " + challengeeLogin);
            return;
        }

        // Nie można wyzwać samego siebie
        if (challenger.equals(challengee)) {
            challenger.sendMessage("Nie mozesz wyzwac samego siebie do pojedynku!");
            return;
        }

        // Nie można wyzwać gracza, który jest już w pojedynku
        if (challenger.isDueling()) {
            challenger.sendMessage("Nie mozesz wyzwac, bo sam jestes juz w pojedynku!");
            return;
        }
        if (challengee.isDueling()) {
            challenger.sendMessage("Gracz " + challengeeLogin + " jest juz w pojedynku!");
            return;
        }

        // Wszystko OK – rozpoczynamy pojedynek
        startDuel(challenger, challengee);
    }

    private void startDuel(ClientHandler challenger, ClientHandler challengee) {
        // Tworzymy nowy pojedynek
        Duel duel = new Duel(challenger, challengee);

        // Ustawiamy funkcję, która wykona się po wybraniu gestów przez obu graczy
        duel.setOnEnd(() -> {
            Duel.Result result = duel.evaluate();

            if (result == null) {
                // remis
                ((ClientHandler) challenger).sendMessage("Pojedynek zakonczyl sie remisem!");
                ((ClientHandler) challengee).sendMessage("Pojedynek zakonczyl sie remisem!");
            } else {
                // wyłoniono zwycięzcę i przegranego
                ((ClientHandler) result.winner()).sendMessage(
                        "Wygrales pojedynek z graczem " + ((ClientHandler) result.loser()).getLogin() + "!");
                ((ClientHandler) result.loser()).sendMessage(
                        "Przegrales pojedynek z graczem " + ((ClientHandler) result.winner()).getLogin() + "!");

                database.updateLeaderboard(
                        ((ClientHandler) result.winner()).getLogin(),
                        ((ClientHandler) result.loser()).getLogin()
                );
            }

            // Po zakończeniu pojedynku gracze opuszczają pojedynek
            challenger.leaveDuel();
            challengee.leaveDuel();

            // wyświetlenie rankingu
            printLeaderboard();
        });

        // Informujemy graczy o rozpoczęciu pojedynku
        challenger.sendMessage("Pojedynek z graczem " + challengee.getLogin() + " rozpoczal sie.");
        challengee.sendMessage("Pojedynek z graczem " + challenger.getLogin() + " rozpoczal sie.");
    }


    // udostępniamy bazę danych handlerom
    public Database getDatabase() {
        return database;
    }

    public List<ClientHandler> getClients() {
        return clients;
    }

    public void printLeaderboard() {
        System.out.println("=== Ranking graczy ===");
        Map<String, Integer> leaderboard = database.getLeaderboard();

        int rank = 1;
        for (Map.Entry<String, Integer> entry : leaderboard.entrySet()) {
            System.out.println(rank + ". " + entry.getKey() + " - " + entry.getValue() + " pkt");
            rank++;
        }
        System.out.println("=====================");
    }



    // Start serwera
    public static void main(String[] args) {
        Server server = new Server();
        server.listen();
    }
}
