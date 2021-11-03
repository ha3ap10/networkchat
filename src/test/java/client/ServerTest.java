package client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import server.*;
import settings.Settings;

import java.io.*;
import java.net.Socket;

import static settings.Constants.CONFIRM;
import static settings.Constants.NEW_USER;

public class ServerTest {
    Server server;

    @Test
    public void testServer() {
        server = new Server();

        Thread serverThread = new Thread(() -> {
            server.startServer();
        });

        serverThread.start();

        Settings settings = Settings.getInstance();

        String host = settings.getHOST();
        String port = settings.getPORT();

        try (Socket socket = new Socket(host, Integer.parseInt(port));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String name = "NewName";
            String request = NEW_USER.get() + name;
            out.println(request);
            String answer = in.readLine();

            String expected = CONFIRM.get() + name;
            Assertions.assertEquals(expected, answer);

            server.stopServer();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
