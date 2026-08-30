package org.dreambot.muling.server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(9696);
        server.start();
    }
}
