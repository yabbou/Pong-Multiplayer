package com.yabbou;

public class ClientTest {
    public static void main(String[] args) {
        Client client;

        if (args.length == 0)
            client = new Client("127.0.0.1");
        else
            client = new Client(args[0]);

        client.run();
    }
}

