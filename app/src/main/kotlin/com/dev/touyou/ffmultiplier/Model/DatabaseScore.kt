package com.dev.touyou.ffmultiplier.Model

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Created by touyou on 2016/12/10.
 */
@IgnoreExtraProperties
class DatabaseScore {
    var name: String
    var score: Int = 0

    constructor() {}

    constructor(name: String, score: Int) {
        this.name = name
        this.score = score
    }
}
