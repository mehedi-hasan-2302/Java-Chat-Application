import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 8080;
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // Create a new thread for each client
                new Thread(new ClientHandler(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter writer;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                // Setup writer for the client
                writer = new PrintWriter(clientSocket.getOutputStream(), true);
                clientWriters.add(writer);


                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String message;
                while ((message = reader.readLine()) != null) {
                    broadcast(message);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Remove client writer on disconnect
                if (writer != null) {
                    clientWriters.remove(writer);
                }
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcast(String message) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }
}
