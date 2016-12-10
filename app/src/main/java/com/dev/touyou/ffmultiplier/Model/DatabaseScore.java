package com.dev.touyou.ffmultiplier.Model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by touyou on 2016/12/10.
 */
@IgnoreExtraProperties
public class DatabaseScore {
    public String name;
    public int score;

    public DatabaseScore() {}

    public DatabaseScore(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public String getName() {
        return name;
    }
}
