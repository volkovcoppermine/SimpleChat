import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ChatServer {
    private static ChatServer server;
    private ArrayList<Client> clients;
    private ServerSocket serverSocket;

    public static ChatServer getServer() throws IOException {
        // Мне показалось, что есть смысл иметь только один экземпляр сервера.
        // Нужно ли это в данном упражнении - вопрос.
        if (server == null) server = new ChatServer();
        return server;
    }

    private ChatServer() throws IOException {
        clients = new ArrayList<>();
        serverSocket = new ServerSocket(1234);
    }

    private void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Клиент подключен: " + socket.getPort());
                clients.add(new Client(socket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendAll(String message) {
        for (Client c : clients) {
            c.receive(message);
        }
    }

    public static void main(String[] args) {
        System.out.println("Ожидание клиентов...");
        try {
            getServer().run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Client implements Runnable {
        Socket socket;
        int port;
        Scanner in;
        PrintStream out;

        public Client(Socket socket) {
            this.socket = socket;
            this.port = socket.getPort();
            new Thread(this).start();
        }

        private void receive(String message) {
            out.println(message);
        }


        @Override
        public void run() {
            try {
                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();

                in = new Scanner(is);
                out = new PrintStream(os);

                out.println("Приветствуем в чате!");
                String input = in.nextLine();

                while (!input.equals("exit")) {
                    // Это не полноценный ник, но позволит проще отличать клиентов друг от друга
                    getServer().sendAll(port + ": " + input);
                    input = in.nextLine();
                }

                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
