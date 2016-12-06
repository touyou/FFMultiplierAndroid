package com.dev.touyou.ffmultiplier.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.util.Date;

/**
 * Created by touyou on 2016/12/06.
 */
public class ScoreModel extends RealmObject {
    private int score;
    private Date date;

    public void setScore(int score) {
        this.score = score;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public int getScore() {
        return score;
    }
}
