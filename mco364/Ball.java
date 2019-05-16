package com.yabbou;

class Ball {

    private final int DIAMETER = 10;
    private int
            xAxis, 
            yAxis,
            DELTA_X = 2,
            DELTA_Y = 2;

    Ball() {
        recenter();
    }

    void recenter() {
        xAxis = (Board.getBoardOrigin() + Board.getBoardWidth()) / 2;
        yAxis = (Board.getBoardOrigin() + Board.getBoardHeight()) / 2;
    }

    int getXAxis() {
        return xAxis;
    }

    int getYAxis() {
        return yAxis;
    }

    int getDIAMETER() {
        return DIAMETER;
    }

    void updateBall() {
        if (xAxis <= Board.getBoardOrigin() + Board.getPaddleXAxis() + Paddle.WIDTH ||
                xAxis + DIAMETER >= Board.getBoardOrigin() + Board.getBoardWidth() - Board.getPaddleXAxis()) {
            DELTA_X = -DELTA_X;
        }
        if (yAxis <= Board.getBoardOrigin() ||
                yAxis >= Board.getBoardOrigin() + Board.getBoardHeight() - DIAMETER) {
            DELTA_Y = -DELTA_Y;
        }
        xAxis += DELTA_X;
        yAxis += DELTA_Y;
    }
}