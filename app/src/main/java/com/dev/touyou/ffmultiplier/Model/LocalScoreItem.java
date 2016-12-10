package com.dev.touyou.ffmultiplier.Model;

import java.util.Date;

/**
 * Created by touyou on 2016/12/10.
 */
public class LocalScoreItem {
    long id;
    private int score;
    private int rank;
    private String name;
    private Date date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
