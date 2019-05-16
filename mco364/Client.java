package com.yabbou;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

class Client extends Networking_Pong {
    private String chatServer;

    Client(String host) {
        setTitle("Client");
        chatServer = host;

        int FRAME_SPACING = 20;
        setLocation(Pong.getFRAME_WIDTH() + FRAME_SPACING, 0);
    }

    @Override
    void run() {
        try {
            connectToServer();
            super.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectToServer() throws IOException {
        connection = new Socket(InetAddress.getByName(chatServer), 12345);
    }
}