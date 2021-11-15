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

    private final int port = settings.getPort();

    private final UsersList usersList = new UsersList();

    private ExecutorService executorService;
    private Thread serverThread;

    public void startServer() {
        serverThread = Thread.currentThread();
        String curThread = serverThread.getName() + "-startServer()";

        executorService = Executors.newCachedThreadPool();
        logger.log(curThread, "ExecutorService create");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(2000);
            while (!serverThread.isInterrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    executorService.execute(() -> newUserThread(clientSocket));
                } catch (IOException e) {
                    if (!e.getMessage().equals(TIME_OUT.get())) {
                        logger.error(curThread, e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            logger.error(curThread, e.getMessage());
        }
    }

    private void newUserThread(Socket clientSocket) {
        String curThread = Thread.currentThread().getName() + "-newUserThread()";
        User user = null;

        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            logger.log(curThread, CONNECT.get() + clientSocket.getInetAddress());

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
            logger.error(curThread, e.getMessage());
        } finally {
            if (user != null) {
                userOffline(user);
            }
            logger.log(curThread,DISCONNECT.get() + clientSocket.getInetAddress());
        }
    }

    private void sendContactList(User user) {
        String curThread = Thread.currentThread().getName() + "-sendContactList()";
        int i = 0;
        for (User receiver : usersList.getUsers()) {
            user.sendMessage(i++ + ". " + receiver.getUserName());
        }
        logger.log(curThread, SEND_CONTACT_LIST.get() + user.getUserName());
    }

    public void stopServer() {
        String curThread = Thread.currentThread().getName() + "-stopServer()";
        sendToAllUsers(SERVER_SHUTDOWN.get());
        executorService.shutdown();
        serverThread.interrupt();
        logger.log(curThread, STOP_MSG.get());
    }

    private void userOffline(User user) {
        String curThread = Thread.currentThread().getName() + "-userOffline()";

        String msg = user.getUserName() + IS_OFF.get();
        usersList.removeUser(user);
        logger.log(curThread, msg);
        sendToAllUsers(msg);
    }

    private void sendToAllUsers(String message) {
        String curThread = Thread.currentThread().getName() + "-sendToAllUsers()";

        for (User receiver : usersList.getUsers()) {
            receiver.sendMessage(message);
            logger.log(curThread, SEND.get() + message);
        }
    }

    private void addUser(User user) {
        String curThread = Thread.currentThread().getName() + "-addUser()";

        if (usersList.addUser(user)) {
            String newUser = user.getUserName() + IS_ON.get();
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
        logger.log(curThread, CONFIRM_REG.get() + user.getUserName());
    }
}