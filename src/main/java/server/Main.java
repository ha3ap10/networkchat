package server;

import java.util.Scanner;

import static settings.Constants.STOP_SERVER;

public class Main {

    public static Server server;
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("\"" + STOP_SERVER.get() + "\" - to stop server");
        server = new Server();

        Thread serverThread = new Thread(() -> {
            server.startServer();
        });

        serverThread.start();

        while (true) {

            String command = scanner.nextLine();

            if (STOP_SERVER.get().equals(command)) {
                server.stopServer();
                break;
            }
        }
    }
}
