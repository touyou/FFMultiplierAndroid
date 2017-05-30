package com.dev.touyou.ffmultiplier.CustomClass

/**
 * Created by touyou on 2016/11/10.
 */
enum class FFNumber private constructor(val id: Int) {
    ZERO(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7),
    EIGHT(8), NINE(9), ANUM(10), BNUM(11), CNUM(12), DNUM(13), ENUM(14), FNUM(15);

    override fun toString(): String {
        if (id < 10) {
            return id.toString()
        } else {
            when (id) {
                10 -> return "A"
                11 -> return "B"
                12 -> return "C"
                13 -> return "D"
                14 -> return "E"
                15 -> return "F"
                else -> return "UNKNOWN"
            }
        }
    }

    companion object {

        fun valueOf(id: Int): FFNumber {
            for (fn in values()) {
                if (fn.id == id) {
                    return fn
                }
            }
            throw IllegalArgumentException("it's not hex number.")
        }
    }
}
