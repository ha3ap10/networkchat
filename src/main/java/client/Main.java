package client;

public class Main {
    public static void main(String[] args) {

        Client client = new Client();

        Thread clientThread = new Thread(() -> {
            client.startClient();
        });

        clientThread.start();
    }
}
