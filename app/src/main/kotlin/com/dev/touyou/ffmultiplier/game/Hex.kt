package com.dev.touyou.ffmultiplier.game

import kotlin.random.Random

/** 16 進 1 桁(0〜F)の掛け算 1 問。 */
data class Problem(val left: Int, val right: Int) {
    val answer: String get() = (left * right).toString(16).uppercase()
}

fun hexDigit(value: Int): String = value.toString(16).uppercase()

/** 直前と同じ問題が続きにくいよう、重複を数回まで引き直して [count] 問作る。 */
fun makeProblems(count: Int, random: Random = Random.Default): List<Problem> {
    val problems = ArrayList<Problem>(count)
    repeat(count) {
        var problem = Problem(random.nextInt(16), random.nextInt(16))
        repeat(10) {
            if (problem !in problems) return@repeat
            problem = Problem(random.nextInt(16), random.nextInt(16))
        }
        problems += problem
    }
    return problems
}
