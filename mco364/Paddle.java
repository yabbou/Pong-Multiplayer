package com.yabbou;

class Paddle {
    static final int HEIGHT = 60, WIDTH = 10;
    private int xAxis;
    private int yAxis = Board.getCenterOfBoard();

    Paddle(int xAxis) {
        this.xAxis = xAxis;
    }

    Paddle(int xAxis, int yAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    int getXAxis() {
        return xAxis;
    }

    int getYAxis() {
        return yAxis;
    }

    void setYAxis(int yAxis) {
        this.yAxis = yAxis;
    }

}
