package com.dev.touyou.ffmultiplier.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException

@Serializable
data class LocalScore(val score: Int, val epochMillis: Long)

private val Context.dataStore by preferencesDataStore(name = "ffmultiplier")

/** 端末内のスコア履歴と設定値。件数が小さいので履歴は JSON 1 本で持つ。 */
class ScoreStore(private val context: Context) {
    private val store get() = context.dataStore

    // ディスク読み込みの失敗で落ちないよう、読めなければ空として扱う
    private val data: Flow<Preferences> = store.data.catch { if (it is IOException) emit(emptyPreferences()) else throw it }

    val scores: Flow<List<LocalScore>> = data.map { decode(it[SCORES]) }
    val playerName: Flow<String?> = data.map { it[PLAYER_NAME]?.takeIf(String::isNotEmpty) }

    suspend fun highScore(): Int? = scores.first().maxOfOrNull { it.score }

    suspend fun add(score: Int, epochMillis: Long = System.currentTimeMillis()) {
        store.edit { prefs ->
            prefs[SCORES] = Json.encodeToString(decode(prefs[SCORES]) + LocalScore(score, epochMillis))
        }
    }

    suspend fun deleteAll() {
        store.edit { it.remove(SCORES) }
    }

    suspend fun playerName(): String? = playerName.first()

    suspend fun setPlayerName(name: String) {
        store.edit { it[PLAYER_NAME] = name }
    }

    /** 初回だけ true を返し、以後は false。 */
    suspend fun consumeFirstLaunch(): Boolean {
        var first = false
        store.edit {
            first = it[TUTORIAL_SHOWN] != true
            it[TUTORIAL_SHOWN] = true
        }
        return first
    }

    suspend fun flag(key: Preferences.Key<Boolean>): Boolean = data.first()[key] == true

    suspend fun setFlag(key: Preferences.Key<Boolean>) {
        store.edit { it[key] = true }
    }

    private fun decode(raw: String?): List<LocalScore> =
        raw?.let { runCatching { Json.decodeFromString<List<LocalScore>>(it) }.getOrNull() } ?: emptyList()

    companion object {
        private val SCORES = stringPreferencesKey("scores")
        private val PLAYER_NAME = stringPreferencesKey("playername")
        private val TUTORIAL_SHOWN = booleanPreferencesKey("tutorial")
        val RANKING_MIGRATED = booleanPreferencesKey("rankingMigratedToV2")
        val LEGACY_REMOVED = booleanPreferencesKey("rankingLegacyRemoved")
    }
}
