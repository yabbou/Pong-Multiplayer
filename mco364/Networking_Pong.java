package com.yabbou;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

abstract class Networking_Pong extends Pong {
    Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;

    private int sign;

    Networking_Pong() {

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent wheel) {
                sign = (int) -Math.copySign(getBoard().getDeltaOfServerPaddle(),
                        wheel.getUnitsToScroll());

                if (wheel.getUnitsToScroll() != 0) {
                    setDeltaOfPaddleAndSendData();
                }
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent arrowKey) {
                if (arrowKey.getKeyCode() == KeyEvent.VK_UP) {
                    sign = (int) Math.copySign(getBoard().getDeltaOfServerPaddle(), -1f);
                    setDeltaOfPaddleAndSendData();

                } else if (arrowKey.getKeyCode() == KeyEvent.VK_DOWN) {
                    sign = (int) Math.copySign(getBoard().getDeltaOfServerPaddle(), 1f);
                    setDeltaOfPaddleAndSendData();
                }
            }
        });

        getStartButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                sendData(event);
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                super.windowClosing(event);
                sendData(event);
            }
        });
    }

    private void sendData(Object o) {
        try {
            output.writeObject(o);
            output.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    void run() {
        while (true) {
            try {
                getStreams();
                processConnection();
            } finally {
                closeConnection();
            }
        }
    }

    private void getStreams() {
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void processConnection() {
        do {
            try {
                Object in = input.readObject();

                if (in instanceof ActionEvent) {
                    pressStartButton();
                }

                if (in instanceof Integer) {
                    if (getTitle().equals("Server")) {
                        getBoard().setDeltaOfClientPaddle((int) in);
                    } else {
                        getBoard().setDeltaOfServerPaddle((int) in);
                    }
                }

                if (in instanceof String) {
                    super.updateCurrentScores((String) in);
                }

                if (in instanceof JDialog) {
                    getStartButton().setEnabled(true);
                }

                if (in instanceof WindowEvent) {
                    this.processWindowEvent((WindowEvent) in);
                }

            } catch (ClassNotFoundException | IOException exception) {
                exception.printStackTrace();
            }
        } while (true);
    }

    private void closeConnection() {
        try {
            output.close();
            input.close();
            connection.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void setDeltaOfPaddle() { 
        if (getTitle().equals("Server")) {
            getBoard().setDeltaOfServerPaddle(sign);
        } else {
            getBoard().setDeltaOfClientPaddle(sign);
        }
    }

    private void setDeltaOfPaddleAndSendData() {
        setDeltaOfPaddle();
        sendData(sign);
    }

    @Override
    void resetCounters(JDialog newPlayerDialog) {
        super.resetCounters(newPlayerDialog);
        sendData(newPlayerDialog);
    }

    @Override
    void updateCurrentScores(String player) {
        if (player.equals("Reset")) {
            super.updateCurrentScores(player);
        }
        sendData(player);
    }

}