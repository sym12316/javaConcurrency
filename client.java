import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class client {
    public static void main(String[] args) {
        final String SERVER_HOST = "localhost";
        final int SERVER_PORT = 12345;

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            String command;
            boolean isWaiting = false; // Флаг ожидания сообщений от других клиентов

            // Ожидание команд от пользователя
            while ((command = consoleReader.readLine()) != null) {
                if (command.equals("send")) {
                    writer.println(command); // Отправка команды "send" на сервер
                    String confirmation = serverReader.readLine(); // Получение подтверждения от сервера
                    System.out.println("status: " + confirmation);
                    isWaiting = true; // Включение режима ожидания сообщений от других клиентов

                    // Ожидание сообщений от сервера и вывод их на консоль клиента
                    while (isWaiting) {
                        String receivedMessage = serverReader.readLine();
                        System.out.println("Пересланое сообщение: " + receivedMessage);

                        // Если получено сообщение о завершении ожидания, выходим из цикла

                        if (receivedMessage.equals("-крнец-")) {
                            isWaiting = false;
                        }
                    }
                } else if (command.startsWith("message")) {
                    String[] splitCommand = command.split(" ", 2);
                    String message = splitCommand[1];
                    writer.println(message);
                    System.out.println("Сообщение отправлено");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
