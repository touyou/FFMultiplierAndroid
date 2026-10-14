package com.dev.touyou.ffmultiplier.data

/** ランキングの 1 行。 */
data class RankEntry(val rank: Int, val score: Int, val name: String)

data class RankedResult(val entries: List<RankEntry>, val myRank: Int, val myIndex: Int?)

/** 並べ替え前の 1 レコード。key はデータベース上の子キー(uid / 旧版の端末 ID)。 */
data class ScoreRow(val key: String, val score: Int, val name: String)

/** score 降順に並べ、同点は同順位にする。自分の行は同点者がいても key で一意に特定する。 */
fun rank(rows: List<ScoreRow>, myKey: String?): RankedResult {
    val entries = ArrayList<RankEntry>(rows.size)
    var myRank = 0
    var myIndex: Int? = null
    var currentRank = 0
    var previousScore: Int? = null
    rows.sortedByDescending { it.score }.forEachIndexed { index, row ->
        if (row.score != previousScore) {
            currentRank = index + 1
            previousScore = row.score
        }
        entries += RankEntry(currentRank, row.score, row.name)
        if (myKey != null && row.key == myKey) {
            myRank = currentRank
            myIndex = index
        }
    }
    return RankedResult(entries, myRank, myIndex)
}

/** Nearby 表示用。自分の行を中心に最大 [size] 件を切り出す(未登録なら上位)。 */
fun <T> windowAround(items: List<T>, center: Int?, size: Int = 50): List<T> {
    if (center == null) return items.take(size)
    val lower = maxOf(0, center - size / 2)
    val upper = minOf(items.size, lower + size)
    return items.subList(lower, upper)
}

const val MAX_NAME_LENGTH = 20

/**
 * ランキングに登録できる長さに名前を切り詰める。コードポイント単位で数えてサロゲートペアを割らない
 * (20 コードポイントは UTF-16 で最大 40 単位なので、ルールの上限 100 単位に収まる)。
 */
fun normalizedName(name: String): String {
    val trimmed = name.trim()
    val count = trimmed.codePointCount(0, trimmed.length)
    if (count <= MAX_NAME_LENGTH) return trimmed
    return trimmed.substring(0, trimmed.offsetByCodePoints(0, MAX_NAME_LENGTH))
}
