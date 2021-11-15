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
    private final int port = settings.getPort();
    private final String host = settings.getHost();
    private Thread clientThread;

    private String userName;

    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Scanner scanner = new Scanner(System.in);

    public void startClient() {
        clientThread = Thread.currentThread();
        try (Socket clientSocket = new Socket(host, port);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            System.out.println(CLIENT_ENTER_NAME.get());
            String name = scanner.nextLine();
            if (name.equals(EXIT.get())) {
                stopClient();
                return;
            } else {
                clientNameSet(out, NEW_USER.get(), name);
            }

            String msg;

            executorService.execute(() -> receive(out, in));

            while (!clientThread.isInterrupted()) {
                msg = scanner.nextLine();

                sendToServer(out, msg);
                if (EXIT.get().equals(msg)) {
                    stopClient();
                    break;
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void receive(PrintWriter out, BufferedReader in) {
        String message;
        while ((message = readFromServer(in)) != null && !Thread.currentThread().isInterrupted()) {

            if (message.startsWith(FAIL_ADD_USER.get())) {
                System.out.println(CLIENT_FAIL_ADD_USER.get());
                logger.log(CLIENT_FAIL_ADD_USER.get());
                clientNameSet(out, NEW_USER.get(), scanner.nextLine());
            } else if (message.startsWith(CONFIRM.get())) {
                userName = message.replaceAll(CONFIRM.get(), "");
                System.out.println(CONFIRM_REG.get() + userName);
                System.out.println(CLIENT_COMMANDS);
                logger.log(CONFIRM_REG.get() + userName);
            } else if (message.startsWith(SERVER_SHUTDOWN.get())) {
                System.out.println(SERVER_SHUTDOWN_MSG.get());
                stopClient();
                sendToServer(out, EXIT.get());
                break;
            } else if(!message.startsWith(userName + ":")) {
                System.out.println(message);
            }
        }
    }

    public void stopClient() {
        executorService.shutdown();
        logger.log(STOP_MSG.get());
        clientThread.interrupt();
    }

    private void clientNameSet(PrintWriter out, String modifier, String name) {
        sendToServer(out, modifier + name);
    }

    private void sendToServer(PrintWriter out, String output) {
        if (!output.equals("")) {
            out.println(output);
            logger.log(SEND.get() + output);
        }
    }

    private String readFromServer(BufferedReader in) {
        String input = null;
        try {
            input = in.readLine();
        } catch (IOException e) {
            logger.error(e.getMessage());
            stopClient();
        }
        logger.log(READ.get() + input);
        return input;
    }
}
