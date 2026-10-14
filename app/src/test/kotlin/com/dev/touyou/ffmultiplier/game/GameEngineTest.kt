package com.dev.touyou.ffmultiplier.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEngineTest {
    private fun engine(vararg problems: Problem) = GameEngine(ArrayDeque(problems.reversed()))

    @Test
    fun answerIsUppercaseHex() {
        assertEquals("E1", Problem(15, 15).answer)
        assertEquals("0", Problem(0, 9).answer)
        assertEquals("A", Problem(2, 5).answer)
    }

    @Test
    fun inputIsLimitedToTwoDigits() {
        val e = engine(Problem(1, 1))
        e.tapNumber(10)
        e.tapNumber(11)
        e.tapNumber(12)
        assertEquals("AB", e.input)
        e.delete()
        assertEquals("A", e.input)
    }

    @Test
    fun comboBonusGrowsEveryFiveAndIsCapped() {
        val e = engine(*Array(30) { Problem(1, 2) })
        val gains = (0 until 25).map {
            val before = e.score
            e.tapNumber(2)
            assertTrue(e.done())
            e.score - before
        }
        assertEquals(List(5) { 10 }, gains.subList(0, 5))
        assertEquals(List(5) { 15 }, gains.subList(5, 10))
        assertEquals(List(5) { 20 }, gains.subList(10, 15))
        assertEquals(List(10) { 25 }, gains.subList(15, 25))
    }

    @Test
    fun wrongAnswerLosesPointsAndResetsCombo() {
        val e = engine(Problem(3, 3), Problem(3, 3))
        e.tapNumber(9)
        e.done()
        e.tapNumber(1)
        assertFalse(e.done())
        assertEquals(5, e.score)
        assertEquals(0, e.combo)
    }

    @Test
    fun finishesAfterSixtyTicksAndIgnoresInput() {
        val e = engine(Problem(1, 1))
        repeat(GameEngine.LIMIT_SECONDS) { e.tick() }
        assertTrue(e.isFinished)
        e.tapNumber(1)
        assertEquals("", e.input)
        assertFalse(e.done())
    }

    @Test
    fun scoreIsAlwaysAMultipleOfFive() {
        // データベースのルールが score % 5 == 0 を要求する
        val e = GameEngine()
        repeat(200) { i ->
            e.current?.let { if (i % 3 == 0) e.tapNumber(0) else it.answer.forEach { c -> e.tapNumber(c.digitToInt(16)) } }
            e.done()
            assertEquals(0, e.score % 5)
        }
    }

    @Test
    fun makesRequestedNumberOfProblems() {
        val problems = makeProblems(1000)
        assertEquals(1000, problems.size)
        assertTrue(problems.all { it.left in 0..15 && it.right in 0..15 })
    }
}
