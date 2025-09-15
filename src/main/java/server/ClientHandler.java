package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import game.Gesture;
import game.Player;
public class ClientHandler extends Player implements Runnable {
    private final Socket socket;
    private final Server server;
    private PrintWriter out;
    private BufferedReader in;
    private String login;  // login zalogowanego klienta


    public ClientHandler(Socket socket, Server server) {
        super();
        this.socket = socket;
        this.server = server;
    }
    public String getLogin() {
        return login;
    }


    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // krok 1: zapytanie o login
            out.println("Podaj login:");
            String providedLogin = in.readLine();

            // krok 2: zapytanie o hasło
            out.println("Podaj haslo:");
            String providedPassword = in.readLine();

            // krok 3: sprawdzenie w bazie
            if (!server.getDatabase().authenticate(providedLogin, providedPassword)) {
                out.println("Autoryzacja nieudana. Rozlaczam...");
                socket.close();              // zamykamy gniazdo
                server.removeClient(this);   // usuwamy klienta z listy w serwerze
                return;                      // kończymy działanie wątku
            }

            // jeżeli autoryzacja OK -> zapisz login w obiekcie klienta
            this.login = providedLogin;
            out.println("Witaj, " + login + "! Zostales zalogowany.");

            // obsługa komunikatów klienta
            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println("[" + login + "]: " + msg);

                if (isDueling()) { // jeśli gracz jest w pojedynku
                    Gesture gesture = Gesture.fromString(msg);
                    if (gesture != null) {
                        makeGesture(gesture); // wywołanie odziedziczonej metody Player
                    } else {
                        // wiadomość nie jest dopuszczalnym gestem, ignorujemy
                        sendMessage("Niepoprawny gest. Wpisz: r, p lub s");
                    }
                } else {
                    // gracz nie jest w pojedynku -> traktujemy wiadomość jako wyzwanie
                    server.challengeToDuel(this, msg);
                }
            }


        } catch (IOException e) {
            System.err.println("Klient rozlaczony: " + socket.getInetAddress());
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {}
            server.removeClient(this);
        }
    }

    public void setLogin(String login) {
        this.login = login;
    }

    // metoda wysyłania wiadomości do tego klienta
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

}
