package com.dev.touyou.ffmultiplier.appfunctions

import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.RequiresApi
import androidx.appfunctions.AppFunctionDeclaration
import androidx.appfunctions.AppFunctionSerializable
import androidx.appfunctions.AppFunctionService
import androidx.appfunctions.AppFunctionServiceEntryPoint
import com.dev.touyou.ffmultiplier.FFApplication
import com.dev.touyou.ffmultiplier.MainActivity
import com.dev.touyou.ffmultiplier.data.LocalScore
import kotlinx.coroutines.flow.first
import java.time.Instant

/**
 * A score from a time attack played on this device.
 *
 * @property score The points scored.
 * @property playedAt When the game ended.
 */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class FFScore(
    val score: Int,
    val playedAt: Instant,
)

/**
 * A screen of the FFMultiplier app that the user can open.
 *
 * @property description What the screen does.
 * @property intentToOpen Opens the screen.
 */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class FFScreen(
    val description: String,
    val intentToOpen: PendingIntent,
)

// iOS の App Intents(Start Time Attack / Get High Score / Show Scores)に揃える。ゲームを始めるものは
// アプリを直接前面に出さず、呼び出し側(エージェント)に開くための PendingIntent を返す。
// スコアの削除とランキングへの登録は確認や名前の入力が要るので出さない。
@RequiresApi(36)
@AppFunctionServiceEntryPoint(
    serviceName = "FFAppFunctionService",
    appFunctionXmlFileName = "ff_app_functions",
)
abstract class FFAppFunctions : AppFunctionService() {
    private val scoreStore get() = (application as FFApplication).scoreStore

    /**
     * Gets the best time attack score on this device.
     *
     * @return The high score, or null if no game has been played yet.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun getHighScore(): Int? = scoreStore.highScore()

    /**
     * Lists the best time attack scores played on this device.
     *
     * @param limit The maximum number of scores to return, from 1 to 50. Defaults to 10.
     * @return The scores, best first.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun getLocalScores(limit: Int? = null): List<FFScore> =
        scoreStore.scores.first()
            .sortedWith(compareByDescending<LocalScore> { it.score }.thenByDescending { it.epochMillis })
            .take((limit ?: 10).coerceIn(1, 50))
            .map { FFScore(it.score, Instant.ofEpochMilli(it.epochMillis)) }

    /**
     * Prepares a new time attack: answer as many hexadecimal multiplications as possible in 60 seconds.
     *
     * @return The game screen to open.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun startTimeAttack(): FFScreen {
        val intent = Intent(this, MainActivity::class.java)
            .setAction(MainActivity.ACTION_START_TIME_ATTACK)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        return FFScreen(
            description = "Start a time attack",
            intentToOpen = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT),
        )
    }
}
