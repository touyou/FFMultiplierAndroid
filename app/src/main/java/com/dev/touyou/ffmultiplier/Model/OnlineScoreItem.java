package com.dev.touyou.ffmultiplier.Model;

/**
 * Created by touyou on 2016/12/10.
 */
public class OnlineScoreItem {
    long id;
    private int rank;
    private int score;
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
