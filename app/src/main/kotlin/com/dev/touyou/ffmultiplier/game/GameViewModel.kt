package com.dev.touyou.ffmultiplier.game

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dev.touyou.ffmultiplier.FFApplication
import com.dev.touyou.ffmultiplier.data.normalizedName
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** 画面回転や広告表示で Activity が作り直されてもゲームが初期化されないよう ViewModel に持つ。 */
class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as FFApplication
    private val engine = GameEngine()

    // engine は Compose から観測できないので、変更のたびに版を上げて再描画させる
    private var version by mutableIntStateOf(0)

    val leftText: String get() = version.let { engine.current?.let { hexDigit(it.left) } ?: "" }
    val rightText: String get() = version.let { engine.current?.let { hexDigit(it.right) } ?: "" }
    val inputText: String get() = version.let { engine.input.ifEmpty { "--" } }
    val score: Int get() = version.let { engine.score }
    val remaining: Int get() = version.let { engine.remaining }

    /** 直近の判定結果のトースト。表示ごとに id を変えて再アニメーションさせる。 */
    var toast by mutableStateOf<Toast?>(null)
        private set
    var result by mutableStateOf<GameResult?>(null)
        private set
    var askName by mutableStateOf(false)
        private set

    data class Toast(val id: Long, val text: String)
    data class GameResult(val score: Int, val isHighScore: Boolean)

    private val timer = viewModelScope.launch {
        while (!engine.isFinished) {
            delay(1000)
            engine.tick()
            version++
        }
        finish()
    }

    /** 途中で閉じたら記録せずに止める。 */
    fun abandon() {
        timer.cancel()
    }

    fun tapNumber(value: Int) {
        engine.tapNumber(value)
        version++
    }

    fun delete() {
        engine.delete()
        version++
    }

    fun done() {
        if (engine.isFinished) return
        val accepted = engine.done()
        toast = Toast(System.nanoTime(), if (accepted) "accepted" else "failed")
        version++
    }

    fun clearToast(id: Long) {
        if (toast?.id == id) toast = null
    }

    fun registerName(raw: String) {
        askName = false
        val name = normalizedName(raw)
        if (name.isEmpty()) return
        val score = engine.score
        viewModelScope.launch {
            app.scoreStore.setPlayerName(name)
            app.rankingService.register(name, score)
        }
    }

    fun dismissNamePrompt() {
        askName = false
    }

    private suspend fun finish() {
        val score = engine.score
        val store = app.scoreStore
        store.add(score)
        // 保存後の最高スコアが今回以下ならハイスコア
        val isHighScore = (store.highScore() ?: score) <= score
        if (isHighScore) {
            val name = store.playerName()
            if (name != null) {
                viewModelScope.launch { app.rankingService.register(name, score) }
            } else {
                askName = true
            }
        }
        result = GameResult(score, isHighScore)
    }
}
