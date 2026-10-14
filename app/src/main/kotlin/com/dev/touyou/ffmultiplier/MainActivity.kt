package com.dev.touyou.ffmultiplier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dev.touyou.ffmultiplier.ui.FFTheme
import com.dev.touyou.ffmultiplier.ui.GameScreen
import com.dev.touyou.ffmultiplier.ui.HomeScreen
import com.dev.touyou.ffmultiplier.ui.LocalScoreScreen
import com.dev.touyou.ffmultiplier.ui.OnlineRankingScreen
import com.dev.touyou.ffmultiplier.ui.SettingsScreen
import com.dev.touyou.ffmultiplier.ui.TutorialDialog

private enum class Screen { Home, Game, LocalScore, OnlineRanking, Settings }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            FFTheme { App(application as FFApplication) }
        }
    }
}

@Composable
private fun App(app: FFApplication) {
    var screen by rememberSaveable { mutableStateOf(Screen.Home) }
    var showTutorial by rememberSaveable { mutableStateOf(false) }
    // ViewModel は Activity に紐づくため、1 回ごとに別の key で新しいゲームを作る
    var gameCount by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        if (app.scoreStore.consumeFirstLaunch()) showTutorial = true
        app.rankingService.migrateIfNeeded()
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
        Screen.Game -> GameScreen(onExit = goHome, vm = viewModel(key = "game$gameCount"))
        Screen.LocalScore -> LocalScoreScreen(onClose = goHome)
        Screen.OnlineRanking -> OnlineRankingScreen(onClose = goHome)
        Screen.Settings -> SettingsScreen(onClose = goHome)
    }

    if (showTutorial && screen == Screen.Home) TutorialDialog(onExit = { showTutorial = false })
}
