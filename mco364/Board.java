package com.yabbou;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Board extends JPanel {
    private static final int BOARD_WIDTH = 600;
    private static final int BOARD_HEIGHT = 400;
    private static final int PADDLE_X_AXIS = 20;
    private static final int BOARD_ORIGIN = 50;
    private static final int CENTER_OF_BOARD = (BOARD_HEIGHT / 2) + BOARD_ORIGIN;

    private final int CLIENT_X_AXIS = BOARD_ORIGIN + PADDLE_X_AXIS;
    private final int SERVER_X_AXIS = BOARD_ORIGIN + BOARD_WIDTH - PADDLE_X_AXIS;

    private final Pong PONG;

    private Paddle clientPaddle = new Paddle(CLIENT_X_AXIS);
    private Paddle serverPaddle = new Paddle(SERVER_X_AXIS);
    private Ball ball = new Ball();

    private Timer timer;

    Board(Pong pong) {
        PONG = pong;

        int DELAY = 20;
        timer = new Timer(DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ball.updateBall();
                clientPaddle.setYAxis(clientPaddle.getYAxis() + deltaOfClientPaddle);
                serverPaddle.setYAxis(serverPaddle.getYAxis() + deltaOfServerPaddle);

                keepPaddleInsideTheBoard(serverPaddle);
                keepPaddleInsideTheBoard(clientPaddle);

                checkIfBallHitsThePaddle(serverPaddle, "Server");
                checkIfBallHitsThePaddle(clientPaddle, "Client");

                repaint();
            }
        });
    }

    //methods

    private void keepPaddleInsideTheBoard(Paddle paddle) {
        if (paddle.getYAxis() < BOARD_ORIGIN) {
            paddle.setYAxis(BOARD_ORIGIN);
        } else if (paddle.getYAxis() + Paddle.HEIGHT > BOARD_ORIGIN + BOARD_HEIGHT) {
            paddle.setYAxis(BOARD_ORIGIN + BOARD_HEIGHT - Paddle.HEIGHT);
        }
    }

    private void checkIfBallHitsThePaddle(Paddle paddle, String player) {
        if (ballHitsPaddleXAxis(paddle)) {
            if (ballHitsPaddleYAxis(paddle)) {
                PONG.updateCurrentScores(player);
            } else {
                PONG.resetGame();
            }
        }
    }

    private boolean ballHitsPaddleXAxis(Paddle paddle) {
        int BALL_PRECISE_WIDTH = 1;
        if (paddle == serverPaddle) {
            return ball.getXAxis() + ball.getDIAMETER() + BALL_PRECISE_WIDTH >= serverPaddle.getXAxis();
        } else {
            return ball.getXAxis() + BALL_PRECISE_WIDTH <= clientPaddle.getXAxis() + Paddle.WIDTH;
        }
    }

    private boolean ballHitsPaddleYAxis(Paddle paddle) {
        return ball.getYAxis() + ball.getDIAMETER() >= paddle.getYAxis() &&
                ball.getYAxis() <= paddle.getYAxis() + Paddle.HEIGHT;
    }

    void resetObjectLocations() {
        ball.recenter();
        ball.updateBall();
        clientPaddle.setYAxis(CENTER_OF_BOARD);
        serverPaddle.setYAxis(CENTER_OF_BOARD);

        timer.start();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.ORANGE);
        g.fillRect(BOARD_ORIGIN, BOARD_ORIGIN, BOARD_WIDTH, BOARD_HEIGHT);

        g.setColor(Color.BLUE);
        g.fillRect(SERVER_X_AXIS, serverPaddle.getYAxis(), Paddle.WIDTH, Paddle.HEIGHT);
        g.fillRect(CLIENT_X_AXIS, clientPaddle.getYAxis(), Paddle.WIDTH, Paddle.HEIGHT);
        g.fillOval(ball.getXAxis(), ball.getYAxis(), ball.getDIAMETER(), ball.getDIAMETER());
    }

    //getter

    static int getBoardOrigin() {
        return BOARD_ORIGIN;
    }

    static int getBoardHeight() {
        return BOARD_HEIGHT;
    }

    static int getBoardWidth() {
        return BOARD_WIDTH;
    }

    static int getPaddleXAxis() {
        return PADDLE_X_AXIS;
    }

    static int getCenterOfBoard() {
        return CENTER_OF_BOARD;
    }

    Timer getTimer() {
        return timer;
    }

    private int deltaOfServerPaddle = 2;
    private int deltaOfClientPaddle = 2;

    int getDeltaOfServerPaddle() {
        return deltaOfServerPaddle;
    }

    void setDeltaOfServerPaddle(int deltaOfServerPaddle) {
        this.deltaOfServerPaddle = deltaOfServerPaddle;
    }

    void setDeltaOfClientPaddle(int deltaOfClientPaddle) {
        this.deltaOfClientPaddle = deltaOfClientPaddle;
    }
}