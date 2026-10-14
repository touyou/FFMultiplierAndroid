package com.dev.touyou.ffmultiplier.game

import kotlin.math.min

/** 1 回のタイムアタックの出題・入力・採点・コンボ・残り時間。UI やタイマーには依存しない。 */
class GameEngine(private val problems: ArrayDeque<Problem> = ArrayDeque(makeProblems(PROBLEM_COUNT))) {
    var current: Problem? = problems.removeLastOrNull()
        private set
    var input: String = ""
        private set
    var score: Int = 0
        private set
    var combo: Int = 0
        private set
    var remaining: Int = LIMIT_SECONDS
        private set
    val isFinished: Boolean get() = remaining <= 0

    fun tapNumber(value: Int) {
        if (isFinished || input.length >= 2) return
        input += hexDigit(value)
    }

    fun delete() {
        input = input.dropLast(1)
    }

    /** 解答を確定する。正解なら true。 */
    fun done(): Boolean {
        val problem = current ?: return false
        if (isFinished) return false
        val accepted = problem.answer == input
        if (accepted) {
            score += POINTS_ACCEPTED + min(POINTS_COMBO * (combo / 5), MAX_COMBO_BONUS)
            combo += 1
        } else {
            score += POINTS_FAILED
            combo = 0
        }
        input = ""
        current = problems.removeLastOrNull()
        return accepted
    }

    fun tick() {
        if (remaining > 0) remaining -= 1
    }

    companion object {
        const val LIMIT_SECONDS = 60
        const val PROBLEM_COUNT = 1000
        const val POINTS_ACCEPTED = 10
        const val POINTS_FAILED = -5
        const val POINTS_COMBO = 5
        const val MAX_COMBO_BONUS = 15
    }
}
