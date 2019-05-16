package com.yabbou;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class Player implements Comparable<Player>, Serializable {

    private static final long serialVersionUID = 4801623306273802062L;

    private String name;
    private int score;

    static final Comparator<Player> COMPARE_SCORES = new Comparator<Player>() {
        @Override
        public int compare(Player player1, Player player2) {
            return player2.score - player1.score;
        }
    };

    Player() {
        this.name = "";
    }

    Player(String name, int score) {
        this.name = name;
        this.score = score;
    }

    String getName() {
        return name;
    }

    int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return name + " " + score;
    }

    @Override
    public int compareTo(Player player2) {
        return this.score - player2.score;
    }

    public boolean equals(Player player) {
        return score == player.score && name.equals(player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, score);
    }
}