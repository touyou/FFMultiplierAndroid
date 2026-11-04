package com.dev.touyou.ffmultiplier.data

import android.content.Context
import android.util.Log
import com.dev.touyou.ffmultiplier.ScreenshotFixture
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.security.MessageDigest

/**
 * Firebase Realtime Database のオンラインランキング(iOS 版と同じデータを共有する)。
 *
 * 自分のレコードは匿名認証の uid を key に `scores_v2/<uid>` へ置く。旧版は `scores/<広告 ID>` に書いていた。
 * 端末の ID をほかの利用者に配らないよう、旧レコードはサーバー側で key をハッシュにした `scores_legacy` へ
 * 写してあり、こちらだけを読む。`scores_v2` に書けたら、旧レコードを両方の場所から消す。
 */
class RankingService(private val context: Context, private val store: ScoreStore) {
    private val ref = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    /** 自分のスコアを登録する。書き込めたら true。 */
    suspend fun register(name: String, score: Int): Boolean {
        if (ScreenshotFixture.active) return false
        val normalized = normalizedName(name)
        if (normalized.isEmpty()) return false
        val uid = signedInUserId() ?: return false
        // priority = -score で降順に並ぶようにする(iOS 版と同じ)
        val value = mapOf("name" to normalized, "score" to score)
        return try {
            ref.child(PATH).child(uid).setValue(value, -score.toDouble()).await()
            if (!store.flag(ScoreStore.LEGACY_REMOVED)) removeLegacyRecords()
            true
        } catch (e: Exception) {
            Log.w(TAG, "register failed", e)
            false
        }
    }

    /** ランキングから自分の記録を消す(新旧すべての場所)。すべて消せたら true。 */
    suspend fun deleteMyRecords(): Boolean {
        if (ScreenshotFixture.active) return false
        val uid = signedInUserId() ?: return false
        return try {
            ref.child(PATH).child(uid).removeValue().await()
            removeLegacyRecords()
        } catch (e: Exception) {
            Log.w(TAG, "delete failed", e)
            false
        }
    }

    /** 以前の版で登録していた端末のために、端末内の最高スコアを一度だけ `scores_v2` に登録し直す。 */
    suspend fun migrateIfNeeded() {
        if (store.flag(ScoreStore.RANKING_MIGRATED)) return
        val best = store.highScore() ?: return
        val name = store.playerName() ?: return
        if (register(name, best)) store.setFlag(ScoreStore.RANKING_MIGRATED)
    }

    suspend fun fetchRanking(): RankedResult = coroutineScope {
        val legacy = async { rows(HASHED_LEGACY_PATH) }
        val legacyId = async { legacyDeviceId() }
        // scores_v2 の read は auth != null。初回はサインインを待たないと Permission denied で空になる
        val myUid = signedInUserId()
        val currentRows = rows(PATH)
        val hasCurrentRecord = myUid != null && currentRows.any { it.key == myUid }
        val legacyKey = legacyId.await()?.let(::hashedLegacyKey)
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

    /** 自分の旧レコードを `scores` と `scores_legacy` から消す。無いものを消しても成功になる。 */
    private suspend fun removeLegacyRecords(): Boolean {
        val legacyKey = legacyDeviceId() ?: return true
        return try {
            ref.child(LEGACY_PATH).child(legacyKey).removeValue().await()
            ref.child(HASHED_LEGACY_PATH).child(hashedLegacyKey(legacyKey)).removeValue().await()
            store.setFlag(ScoreStore.LEGACY_REMOVED)
            true
        } catch (e: Exception) {
            Log.w(TAG, "legacy cleanup failed", e)
            false
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

    companion object {
        private const val TAG = "RankingService"
        private const val PATH = "scores_v2"
        /** 旧版が書いていた場所。key が広告 ID なので読まない。自分の旧レコードを消すときだけ触る。 */
        private const val LEGACY_PATH = "scores"
        /** 旧レコードを key をハッシュにして写した場所(FFMultiply リポジトリの scripts/migrate-legacy-scores.mjs)。 */
        private const val HASHED_LEGACY_PATH = "scores_legacy"
        /** ハッシュの前に付ける文字列。移行スクリプトと iOS 版と同じ値にする。 */
        private const val LEGACY_KEY_SALT = "ffmultiplier-ranking-legacy:"

        /** `scores_legacy` での key。旧 key に salt を付けた SHA-256 の 16 進。 */
        fun hashedLegacyKey(deviceId: String): String =
            MessageDigest.getInstance("SHA-256")
                .digest((LEGACY_KEY_SALT + deviceId).toByteArray(Charsets.UTF_8))
                .joinToString("") { "%02x".format(it) }
    }
}
