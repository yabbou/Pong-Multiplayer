package com.yabbou;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

class Pong extends JFrame {
    private static final int FRAME_HEIGHT = 550;
    private static final int FRAME_WIDTH = 700;

    private Board board = new Board(this);
    private JButton startButton;

    private int clientScore, serverScore;
    private JLabel clientScoreLabel = new JLabel("Client: " + clientScore);
    private JLabel serverScoreLabel = new JLabel("Server: " + serverScore);

    private Player newPlayer = new Player();
    private ArrayList<Player> topTenScores = new ArrayList<>(11);
    private JDialog newPlayerDialog;
    private DefaultTableModel scoreBoardModel = new DefaultTableModel();
    private JTable scoreBoard = new JTable(scoreBoardModel);
    private JPanel scorePanel = new JPanel();

    private String fileName = "pongHighScore.bin";
    private File file;

    Pong() {
        JPanel statusBar = new JPanel(new GridLayout(1, 3));
        add(statusBar, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent event) {
                file = new File(fileName);
                try {
                    if (file.createNewFile()) {
                        outputTopTenPlayers();
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                inputTopTenPlayers();
                displayTopTen();
            }

            @Override
            public void windowClosing(WindowEvent event) {
                outputTopTenPlayers();
            }
        });

        startButton = new JButton("Start");
        startButton.setFocusable(false);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                pressStartButton();
            }
        });

        statusBar.add(clientScoreLabel);
        statusBar.add(startButton);
        statusBar.add(serverScoreLabel);

        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setVisible(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    //getters

    Board getBoard() {
        return board;
    }

    static int getFRAME_WIDTH() {
        return FRAME_WIDTH;
    }

    JButton getStartButton() {
        return startButton;
    }

    //methods

    void pressStartButton() {
        if (startButton.getText().equals("Start")) {
            startButton.setText("Reset");

            remove(scorePanel);
            invalidate();

            add(board);
            revalidate();

            board.resetObjectLocations();
        } else {
            startButton.setText("Start");
            resetGame();
        }
    }

    void resetGame() {
        remove(board);
        invalidate();

        paint(getGraphics());

        startButton.setEnabled(false);
        getBoard().getTimer().stop();

        enterNewPlayerName();
    }

    private void inputTopTenPlayers() {
        String[] columns = {"Player", "Score"};
        scoreBoardModel.setColumnIdentifiers(columns);

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            topTenScores = (ArrayList<Player>) in.readObject();
            topTenScores.sort(Player.COMPARE_SCORES);

            scoreBoardModel.addRow(columns);
            for (Player p : topTenScores) { //yet accepts ones with >10!!
                scoreBoardModel.addRow(new String[]{p.getName(), String.valueOf(p.getScore())});
            }
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    private void outputTopTenPlayers() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(topTenScores);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enterNewPlayerName() {
        final int PLAYER_INITIALS = 3;

        JPanel newPlayerPanel = new JPanel();
        JTextField newPlayerName = new JTextField(PLAYER_INITIALS);
        JButton completedButton = new JButton("Done");

        newPlayerPanel.add(newPlayerName);
        newPlayerPanel.add(completedButton);

        if (serverScore >= clientScore) {
            if (getTitle().equals("Server")) {
                initNewPlayerDialog(newPlayerPanel, completedButton, newPlayerName);
            }
        } else if (getTitle().equals("Client")) {
            initNewPlayerDialog(newPlayerPanel, completedButton, newPlayerName);
        }
    }

    private void initNewPlayerDialog(JPanel newPlayerPanel, JButton completedButton, JTextField newPlayerName) {
        final int WIDTH = 300, HEIGHT = 100;
        newPlayerDialog = new JDialog(this, "New Player");

        newPlayerDialog.setLocation(this.getX() + (FRAME_WIDTH / 3), FRAME_HEIGHT / 3);
        newPlayerDialog.setSize(WIDTH, HEIGHT);
        newPlayerDialog.setResizable(false);

        newPlayerDialog.add(newPlayerPanel);
        newPlayerDialog.setVisible(true);

        completedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTopTenAndResetCounters(newPlayerName, newPlayerDialog);
            }
        });

        newPlayerDialog.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent arrowKey) {
                if (arrowKey.getKeyCode() == KeyEvent.VK_ENTER) {
                    updateTopTenAndResetCounters(newPlayerName, newPlayerDialog);
                }
            }
        });

        newPlayerDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                displayTopTen();
                resetCounters(newPlayerDialog);
            }
        });
    }

    private void updateTopTenAndResetCounters(JTextField newPlayerName, JDialog newPlayerDialog) {
        newPlayer = new Player(newPlayerName.getText().toUpperCase(), serverScore);
        updateTopTen();
        resetCounters(newPlayerDialog);
    }

    void resetCounters(JDialog newPlayerDialog) {
        updateCurrentScores("Reset");
        startButton.setEnabled(true);
        newPlayerDialog.dispose();
    }

    private void updateTopTen() {
        int MAX_SAVED_SCORES = 10, newPlayerIndex = 0, size = Math.max(0, topTenScores.size() - 1);

        if (!topTenScores.isEmpty() && serverScore > topTenScores.get(size).getScore()) {
            topTenScores.add(newPlayer);
            topTenScores.sort(Player.COMPARE_SCORES);

            if (topTenScores.size() > MAX_SAVED_SCORES) {
                topTenScores.remove(size);
            }

            for (Player p : topTenScores) {
                if (serverScore > p.getScore()) {
                    newPlayerIndex = topTenScores.indexOf(p);
                    break;
                }
            }
        }

        if (newPlayerIndex > size) {
            scoreBoardModel.moveRow(newPlayerIndex, size - 1, newPlayerIndex + 1);
        }
        scoreBoardModel.insertRow(
                newPlayerIndex, new String[]{newPlayer.getName(), String.valueOf(newPlayer.getScore())});
        newPlayer = new Player();

        displayTopTen();
    }

    private void displayTopTen() {
        int ROW_HEIGHT = 40;

        scoreBoard.setRowHeight(ROW_HEIGHT);
        scoreBoard.setEnabled(false);
        scorePanel.add(scoreBoard);
        add(scorePanel);
        validate();
    }

    void updateCurrentScores(String player) {
        if (player.equals("Server")) {
            serverScore++;
        } else if (player.equals("Client")) {
            clientScore++;
        } else {
            clientScore = 0;
            serverScore = 0;
        }
        clientScoreLabel.setText("Client: " + clientScore);
        serverScoreLabel.setText("Server: " + serverScore);
    }
}