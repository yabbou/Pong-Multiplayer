package com.yabbou;

import java.io.IOException;
import java.net.ServerSocket;

class Server extends Networking_Pong {
    private ServerSocket server;

    Server() {
        setTitle("Server");
    }

    @Override
    void run() {
        try {
            server = new ServerSocket(12345, 1000);

            waitForConnection();
            super.run();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void waitForConnection() throws IOException {
        connection = server.accept();
    }
}