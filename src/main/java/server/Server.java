package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
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
            System.err.println("Błąd serwera: " + e.getMessage());
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
        System.out.println("Usunięto klienta. Aktualna liczba połączonych: " + clients.size());
    }

    // Start serwera
    public static void main(String[] args) {
        Server server = new Server();
        server.listen();
    }
}
