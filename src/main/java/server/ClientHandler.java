package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Server server;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            out.println("Witaj na serwerze!");

            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println("Otrzymano od klienta: " + msg);
                server.broadcast("Klient [" + clientSocket.getInetAddress() + "]: " + msg);
            }

        } catch (IOException e) {
            System.err.println("Błąd klienta: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            server.removeClient(this);
            System.out.println("Klient rozłączony: " + clientSocket.getInetAddress());
        }
    }

    // Metoda do wysyłania wiadomości do tego klienta
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}
