import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class client2 {
    public static void main(String[] args) {
        final String SERVER_HOST = "localhost";
        final int SERVER_PORT = 12345;

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT)) {
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            String command;

            // Ожидание команд от пользователя
            while ((command = consoleReader.readLine()) != null) {
                if (command.equals("send")) {
                    writer.println(command); // Отправка команды "send" на сервер
                    String confirmation = serverReader.readLine(); // Получение подтверждения от сервера
                    System.out.println("Server confirmation: " + confirmation);
                    
                    // Ожидание сообщений от других клиентов и отправка их на сервер
                    String message;
                    while ((message = serverReader.readLine()) != null) {
                        System.out.println("Received message from another client: " + message);
                        writer.println(message);
                        System.out.println("Message sent to server");
                    }
                } else if (command.startsWith("message ")) {
                    String[] splitCommand = command.split(" ", 2);
                    String message = splitCommand[1];
                    writer.println(message);
                    System.out.println("Message sent to server");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
