package com.dev.touyou.ffmultiplier

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dev.touyou.ffmultiplier.ads.AdManager
import com.dev.touyou.ffmultiplier.ads.ConsentManager
import com.dev.touyou.ffmultiplier.game.GameViewModel
import com.dev.touyou.ffmultiplier.ui.FFTheme
import com.dev.touyou.ffmultiplier.ui.GameScreen
import com.dev.touyou.ffmultiplier.ui.HomeScreen
import com.dev.touyou.ffmultiplier.ui.LocalScoreScreen
import com.dev.touyou.ffmultiplier.ui.OnlineRankingScreen
import com.dev.touyou.ffmultiplier.ui.SettingsScreen
import com.dev.touyou.ffmultiplier.ui.TutorialDialog

private enum class Screen { Home, Game, LocalScore, OnlineRanking, Settings }

class MainActivity : ComponentActivity() {
    // AppFunctions の startTimeAttack から開かれたとき true。ゲームを始めたら false に戻す
    private val startRequested = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        // 同意フォームは Activity が要るので、広告の起動もここで同意のあとに行う
        if (!ScreenshotFixture.active) {
            ConsentManager.gatherConsent(this) { AdManager.startIfAllowed(applicationContext) }
        }
        // 作り直しやプロセス終了後の復帰では、システムが元の intent をもう一度渡してくるので読まない
        if (savedInstanceState == null) consumeStartRequest(intent)
        setContent {
            FFTheme {
                App(
                    app = application as FFApplication,
                    startRequested = startRequested.value,
                    onStartConsumed = { startRequested.value = false },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        consumeStartRequest(intent)
    }

    // 同じ Activity の中で intent を読み直したときに始め直さないよう、受けたら action を消す
    private fun consumeStartRequest(intent: Intent) {
        if (intent.action != ACTION_START_TIME_ATTACK) return
        intent.action = null
        startRequested.value = true
    }

    companion object {
        const val ACTION_START_TIME_ATTACK = "com.dev.touyou.ffmultiplier.action.START_TIME_ATTACK"
    }
}

@Composable
private fun App(app: FFApplication, startRequested: Boolean, onStartConsumed: () -> Unit) {
    var screen by rememberSaveable { mutableStateOf(Screen.Home) }
    var showTutorial by rememberSaveable { mutableStateOf(false) }
    // ViewModel は Activity に紐づくため、1 回ごとに別の key で新しいゲームを作る
    var gameCount by rememberSaveable { mutableIntStateOf(0) }
    // 起動要求が来たとき、今のゲームが結果表示まで進んでいるかを見るために持つ
    var currentGame by remember { mutableStateOf<GameViewModel?>(null) }

    LaunchedEffect(Unit) {
        if (app.scoreStore.consumeFirstLaunch()) showTutorial = true
        app.rankingService.migrateIfNeeded()
    }

    LaunchedEffect(startRequested) {
        if (startRequested) {
            // 遊んでいる途中なら今のゲームを続ける(key を変えると前のゲームのタイマーが裏で残り、スコアを保存してしまう)。
            // 結果表示中ならタイマーは止まっているので、新しいゲームを始める
            if (screen != Screen.Game || currentGame?.result != null) gameCount++
            screen = Screen.Game
            onStartConsumed()
        }
    }

    val goHome = { screen = Screen.Home }
    BackHandler(enabled = screen != Screen.Home, onBack = goHome)

    when (screen) {
        Screen.Home -> HomeScreen(
            onTimeAttack = {
                gameCount++
                screen = Screen.Game
            },
            onLocalScore = { screen = Screen.LocalScore },
            onOnlineRanking = { screen = Screen.OnlineRanking },
            onSettings = { screen = Screen.Settings },
        )
        Screen.Game -> {
            val vm: GameViewModel = viewModel(key = "game$gameCount")
            SideEffect { currentGame = vm }
            GameScreen(onExit = goHome, vm = vm)
        }
        Screen.LocalScore -> LocalScoreScreen(onClose = goHome)
        Screen.OnlineRanking -> OnlineRankingScreen(onClose = goHome)
        Screen.Settings -> SettingsScreen(onClose = goHome)
    }

    if (showTutorial && screen == Screen.Home) TutorialDialog(onExit = { showTutorial = false })
}
