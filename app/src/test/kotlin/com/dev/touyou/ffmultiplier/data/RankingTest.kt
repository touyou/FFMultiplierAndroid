package com.dev.touyou.ffmultiplier.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RankingTest {
    @Test
    fun tiesShareRankAndMyRowIsFoundByKey() {
        val rows = listOf(
            ScoreRow("a", 100, "A"),
            ScoreRow("me", 200, "Me"),
            ScoreRow("b", 200, "B"),
            ScoreRow("c", 50, "C"),
        )
        val result = rank(rows, "me")
        assertEquals(listOf(1, 1, 3, 4), result.entries.map { it.rank })
        assertEquals(1, result.myRank)
        assertEquals("Me", result.entries[result.myIndex!!].name)
    }

    @Test
    fun unknownKeyHasNoRank() {
        val result = rank(listOf(ScoreRow("a", 10, "A")), null)
        assertEquals(0, result.myRank)
        assertNull(result.myIndex)
    }

    @Test
    fun windowCentersOnMyIndex() {
        val items = (0 until 100).toList()
        assertEquals((0 until 50).toList(), windowAround(items, null))
        assertEquals((35 until 85).toList(), windowAround(items, 60))
        assertEquals((74 until 100).toList(), windowAround(items, 99))
        assertEquals((0 until 50).toList(), windowAround(items, 3))
    }

    @Test
    fun nameIsTrimmedAndCappedAtTwentyCodePoints() {
        assertEquals("touyou", normalizedName("  touyou "))
        assertEquals("a".repeat(20), normalizedName("a".repeat(30)))
        val emoji = "😀".repeat(25)
        assertEquals("😀".repeat(20), normalizedName(emoji))
    }
}
