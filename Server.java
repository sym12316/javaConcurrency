import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static List<ClientHandler> clients = new ArrayList<>();
    private static ClientHandler sender;

    public static void main(String[] args) {
        final int PORT = 12345;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Начало работы сервера.");

            while (true) {
                // Ожидание подключения клиента
                Socket clientSocket = serverSocket.accept();
                System.out.println("Новое подключение");

                // Создание и запуск потока для обработки клиента
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                clientHandler.start();

                // Если отправитель уже определен и есть другие клиенты в режиме "send", отправляем сообщение отправителю
                if (sender != null && clients.size() > 1) {
                    sender.getWriter().println("Новый message клиент");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Класс для обработки клиентского подключения в отдельном потоке
    private static class ClientHandler extends Thread {
        private Socket clientSocket;
        private BufferedReader reader;
        private PrintWriter writer;
        private boolean isSender;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public PrintWriter getWriter() {
            return writer;
        }

        public boolean isSender() {
            return isSender;
        }

        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(clientSocket.getOutputStream(), true);

                while (true) {
                    String command = reader.readLine();

                    // Если клиент отправил команду "send", устанавливаем его в качестве отправителя
                    if (command.equals("send")) {
                        isSender = true;
                        sender = this;
                        writer.println("ты принимаешь");
                    } else {
                        if (!isSender) {
                            // Если клиент не является отправителем, отправляем его сообщение остальным клиентам в режиме "send"
                            for (ClientHandler client : clients) {
                                if (client.isSender()) {
                                    client.getWriter().println(command);
                                }
                            }
                            // Отправка сообщения об окончании сообщений

                            writer.println("-конец-");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                    writer.close();
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
