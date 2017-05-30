package com.dev.touyou.ffmultiplier.Model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

import java.util.Date

/**
 * Created by touyou on 2016/12/06.
 */
open class ScoreModel : RealmObject() {
    var score: Int = 0
    var date: Date? = null
}
