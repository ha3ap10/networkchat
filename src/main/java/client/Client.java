package client;

import settings.Settings;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static settings.Constants.*;

public class Client {

    private static final String CLIENT_COMMANDS = CONTACTS_LIST.get() + " - получить список пользователей\n" +
            EXIT.get() + " - покинуть чат\n";
    private final Logger logger = Logger.getInstance();
    private final Settings settings = Settings.getInstance();
    private final int port = Integer.parseInt(settings.getPORT());
    private final String host = settings.getHOST();

    private PrintWriter out;
    private BufferedReader in;
    private Socket clientSocket;

    private String userName;

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Scanner scanner = new Scanner(System.in);

    public void startClient() {
        try {
            clientSocket = new Socket(host, port);
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            System.out.println("\nВведите имя:");
            String name = scanner.nextLine();
            if (name.equals(EXIT.get())) {
                stopClient();
                return;
            } else {
                clientNameSet(NEW_USER.get(), name);
            }

            String msg;

            System.out.println(CLIENT_COMMANDS);
            executorService.execute(this::receive);

            while (true) {
                msg = scanner.nextLine();

                sendToServer(msg);
                if (EXIT.get().equals(msg)) {
                    stopClient();
                    break;
                }
            }
        } catch (IOException e) {
            logger.log(e.getMessage());
        }
    }

    private void receive() {
        String message;
        while ((message = readFromServer()) != null) {

            if (message.startsWith(FAIL_ADD_USER.get())) {
                System.out.println("Не удалось зарегистрировать пользователя, введите другое имя: ");
                logger.log("Не удалось зарегистрировать пользователя");
                clientNameSet(NEW_USER.get(), scanner.nextLine());
            } else if (message.startsWith(CONFIRM.get())) {
                userName = message.replaceAll(CONFIRM.get(), "");
                System.out.println("Вы зарегистрированы как " + userName);
                logger.log("Успешная регистрация: " + userName);
            } else if(!message.startsWith(userName + ":")) {
                System.out.println(message);
            }
        }
    }

    public void stopClient() {
        try {
            executorService.shutdown();
            out.close();
            in.close();
            clientSocket.close();
            logger.log("Stop client");
        } catch (IOException e) {
            logger.log(e.getMessage());
        }
    }

    private void clientNameSet(String modifier, String name) {
        sendToServer(modifier + name);
    }

    private void sendToServer(String output) {
        out.println(output);
        logger.log("Send to server: " + output);
    }

    private String readFromServer() {
        String input = null;
        try {
            input = in.readLine();
        } catch (IOException e) {
            logger.log(e.getMessage());
            stopClient();
        }
        logger.log("Read from server: " + input);
        return input;
    }
}
