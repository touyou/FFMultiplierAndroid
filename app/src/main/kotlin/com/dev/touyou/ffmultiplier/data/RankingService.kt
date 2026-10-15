package com.dev.touyou.ffmultiplier.data

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Firebase Realtime Database のオンラインランキング(iOS 版と同じデータを共有する)。
 *
 * 自分のレコードは匿名認証の uid を key に `scores_v2/<uid>` へ置く。旧版は `scores/<広告 ID>` に
 * 認証なしで書いていたため、移行期間中は両方を読んで順位を出し、`scores_v2` に書けたら旧レコードを消す。
 */
class RankingService(private val context: Context, private val store: ScoreStore) {
    private val ref = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    /** 自分のスコアを登録する。書き込めたら true。 */
    suspend fun register(name: String, score: Int): Boolean {
        val normalized = normalizedName(name)
        if (normalized.isEmpty()) return false
        val uid = signedInUserId() ?: return false
        // priority = -score で降順に並ぶようにする(iOS 版と同じ)
        val value = mapOf("name" to normalized, "score" to score)
        return try {
            ref.child(PATH).child(uid).setValue(value, -score.toDouble()).await()
            removeLegacyRecordIfNeeded()
            true
        } catch (e: Exception) {
            Log.w(TAG, "register failed", e)
            false
        }
    }

    /** 自分のスコアを 0 にする(ローカル全削除時)。 */
    suspend fun reset(name: String) {
        register(name, 0)
    }

    /** 以前の版で登録していた端末のために、端末内の最高スコアを一度だけ `scores_v2` に登録し直す。 */
    suspend fun migrateIfNeeded() {
        if (store.flag(ScoreStore.RANKING_MIGRATED)) return
        val best = store.highScore() ?: return
        val name = store.playerName() ?: return
        if (register(name, best)) store.setFlag(ScoreStore.RANKING_MIGRATED)
    }

    suspend fun fetchRanking(): RankedResult = coroutineScope {
        val legacy = async { rows(LEGACY_PATH) }
        val legacyId = async { legacyDeviceId() }
        // scores_v2 の read は auth != null。初回はサインインを待たないと Permission denied で空になる
        val myUid = signedInUserId()
        val currentRows = rows(PATH)
        val hasCurrentRecord = myUid != null && currentRows.any { it.key == myUid }
        val legacyKey = legacyId.await()
        // 登録し直した端末の旧レコードがまだ残っていても二重に並べない
        val legacyRows = legacy.await().filterNot { hasCurrentRecord && it.key == legacyKey }
        rank(currentRows + legacyRows, if (hasCurrentRecord) myUid else legacyKey)
    }

    private suspend fun signedInUserId(): String? {
        auth.currentUser?.let { return it.uid }
        return try {
            auth.signInAnonymously().await().user?.uid
        } catch (e: Exception) {
            Log.w(TAG, "anonymous sign-in failed", e)
            null
        }
    }

    private suspend fun rows(path: String): List<ScoreRow> = try {
        ref.child(path).orderByPriority().get().await().toRows()
    } catch (e: Exception) {
        Log.w(TAG, "read $path failed", e)
        emptyList()
    }

    private fun DataSnapshot.toRows(): List<ScoreRow> = children.mapNotNull { child ->
        val key = child.key ?: return@mapNotNull null
        val score = (child.child("score").value as? Number)?.toInt() ?: return@mapNotNull null
        val name = (child.child("name").value as? String)?.takeIf(String::isNotEmpty) ?: "No Name"
        ScoreRow(key, score, name)
    }

    private suspend fun removeLegacyRecordIfNeeded() {
        if (store.flag(ScoreStore.LEGACY_REMOVED)) return
        val legacyKey = legacyDeviceId() ?: return
        try {
            ref.child(LEGACY_PATH).child(legacyKey).removeValue().await()
            store.setFlag(ScoreStore.LEGACY_REMOVED)
        } catch (e: Exception) {
            Log.w(TAG, "legacy cleanup failed", e)
        }
    }

    /** 旧版が key にしていた広告 ID。取れない(無効化・ゼロ埋め)なら null。 */
    private suspend fun legacyDeviceId(): String? = withContext(Dispatchers.IO) {
        runCatching { AdvertisingIdClient.getAdvertisingIdInfo(context) }
            .getOrNull()
            ?.takeUnless { it.isLimitAdTrackingEnabled }
            ?.id
            ?.takeUnless { it.all { c -> c == '0' || c == '-' } }
    }

    private companion object {
        const val TAG = "RankingService"
        const val PATH = "scores_v2"
        const val LEGACY_PATH = "scores"
    }
}
