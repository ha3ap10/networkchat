package server;

import client.Client;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import settings.Settings;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;

public class ClientTest {
    Server server;
    Client client;

    @Test
    public void testClient() {
        server = new Server();
        client = new Client();

        Settings settings = Settings.getInstance();

        int port = settings.getPort();

        try {
            ServerSocket serverSocket = new ServerSocket(port);

            Thread clientThread = new Thread(() -> {
                try {
                    client.startClient();
                } catch (NoSuchElementException e) {
                    e.getMessage();
                }
            });
            clientThread.start();


            Socket clientSocket = serverSocket.accept();

            Assertions.assertTrue(clientSocket.isConnected());

        } catch (IOException e) {
            e.getMessage();
        }

    }
}
