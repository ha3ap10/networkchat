package server;

import settings.Settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static settings.Constants.*;

public class Server {
    private final Logger logger = Logger.getInstance();
    private final Settings settings = Settings.getInstance();

    private final int port = Integer.parseInt(settings.getPORT());

    private final UsersList usersList = new UsersList();

    private ExecutorService executorService;
    private ServerSocket serverSocket;

    public void startServer() {
        String curThread = Thread.currentThread().getName() + "-startServer()";

        executorService = Executors.newCachedThreadPool();
        logger.log(curThread, "ExecutorService create");

        try {
            serverSocket = new ServerSocket(port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.execute(() -> newUserThread(clientSocket));
            }
        } catch (IOException e) {
            logger.log(curThread, e.getMessage());
        }
    }

    private void newUserThread(Socket clientSocket) {
        String curThread = Thread.currentThread().getName() + "-newUserThread()";
        User user = null;

        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            logger.log(curThread, "New connection accepted " + clientSocket.getInetAddress());

            String msg;
            while ((msg = in.readLine()) != null) {
                if (msg.startsWith(EXIT.get())) break;

                if (msg.startsWith(NEW_USER.get())) {
                    String userName = msg.replaceAll(NEW_USER.get(), "");
                    user = new User(userName, out);

                    checkUserName(user);
                    addUser(user);

                } else if (msg.startsWith(CONTACTS_LIST.get())) {
                    sendContactList(user);
                } else {
                    String text = null;
                    if (user != null) {
                        text = user.getUserName() + ": " + msg;
                    }
                    sendToAllUsers(text);
                    logger.log(curThread, text);
                }
            }
        } catch (IOException e) {
            logger.log(curThread, e.getMessage());
        } finally {
            if (user != null) {
                userOffline(user);
            }
            logger.log(curThread," Disconnected: " + clientSocket.getInetAddress());
        }
    }

    private void sendContactList(User user) {
        String curThread = Thread.currentThread().getName() + "-sendContactList()";
        int i = 0;
        for (User receiver : usersList.getUsers()) {
            user.sendMessage(i++ + ". " + receiver.getUserName());
        }
        logger.log(curThread, "send contact list to " + user.getUserName());
    }

    public void stopServer() {
        String curThread = Thread.currentThread().getName() + "-stopServer()";

        executorService.shutdown();
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.log(curThread, e.getMessage());
        }
    }

    private void userOffline(User user) {
        String curThread = Thread.currentThread().getName() + "-userOffline()";

        String msg = user.getUserName() + " is offline";
        usersList.removeUser(user);
        logger.log(curThread, msg);
        sendToAllUsers(msg);
    }

    private void sendToAllUsers(String message) {
        String curThread = Thread.currentThread().getName() + "-sendToAllUsers()";

        for (User receiver : usersList.getUsers()) {
            receiver.sendMessage(message);
            logger.log(curThread, message);
        }
    }

    private void addUser(User user) {
        String curThread = Thread.currentThread().getName() + "-addUser()";

        if (usersList.addUser(user)) {
            String newUser = user.getUserName() + " is online";
            logger.log(curThread, newUser);
            user.sendMessage(CONFIRM.get() + user.getUserName());
            sendToAllUsers(newUser);
        } else {
            String message = FAIL_ADD_USER.get() + user.getUserName();
            logger.log(curThread, message);
            user.sendMessage(message);
        }
    }

    private void checkUserName(User user) {
        String curThread = Thread.currentThread().getName() + "-checkUserName()";

        int i = 0;
        while (usersList.getUser(user)) {
            user.setUserName(user.getUserName() + i++);
        }
        logger.log(curThread, "User Name is " + user.getUserName());
    }
}