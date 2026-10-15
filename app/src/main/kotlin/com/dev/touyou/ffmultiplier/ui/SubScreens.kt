package com.dev.touyou.ffmultiplier.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.touyou.ffmultiplier.FFApplication
import com.dev.touyou.ffmultiplier.ads.BannerAd
import com.dev.touyou.ffmultiplier.data.LocalScore
import com.dev.touyou.ffmultiplier.data.RankedResult
import com.dev.touyou.ffmultiplier.data.normalizedName
import com.dev.touyou.ffmultiplier.data.windowAround
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

@Composable
private fun app(): FFApplication = LocalContext.current.applicationContext as FFApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FFScaffold(
    title: String,
    background: Color,
    onClose: () -> Unit,
    titleColor: Color = FFColor.white,
    actions: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        containerColor = background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title, fontFamily = FFFont.futuraBold, fontSize = 17.sp) },
                navigationIcon = {
                    IconButton(onClick = onClose) { Icon(Icons.Filled.Close, contentDescription = "Close") }
                },
                actions = { actions() },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = background,
                    titleContentColor = titleColor,
                    navigationIconContentColor = titleColor,
                    actionIconContentColor = titleColor,
                ),
            )
        },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding), content = content)
    }
}

/** 同点同順位でランク付けした上位 50 件。 */
private data class RankedLocal(val rank: Int, val score: LocalScore)

private fun rankLocal(scores: List<LocalScore>): List<RankedLocal> {
    var rank = 0
    var previous: Int? = null
    return scores.sortedWith(compareByDescending<LocalScore> { it.score }.thenByDescending { it.epochMillis })
        .take(50)
        .mapIndexed { index, s ->
            if (s.score != previous) {
                rank = index + 1
                previous = s.score
            }
            RankedLocal(rank, s)
        }
}

@Composable
fun LocalScoreScreen(onClose: () -> Unit) {
    val app = app()
    val scope = rememberCoroutineScope()
    val scores by app.scoreStore.scores.collectAsState(initial = emptyList())
    var confirmDelete by rememberSaveable { mutableStateOf(false) }
    val dateFormat = remember { DateFormat.getDateInstance(DateFormat.MEDIUM) }

    FFScaffold(
        title = "Local Score",
        background = FFColor.brown,
        onClose = onClose,
        actions = {
            IconButton(onClick = { confirmDelete = true }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete All", tint = FFColor.red)
            }
        },
    ) {
        ScoreList(rankLocal(scores)) { "${it.rank}. ${it.score.score} points\n\t date: ${dateFormat.format(Date(it.score.epochMillis))}" }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("delete data") },
            text = { Text("Do you really want to delete all your data?") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    scope.launch {
                        app.scoreStore.playerName()?.let { app.rankingService.reset(it) }
                        app.scoreStore.deleteAll()
                    }
                }) { Text("OK", color = FFColor.red) }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancel") } },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnlineRankingScreen(onClose: () -> Unit) {
    val app = app()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var ranking by remember { mutableStateOf(RankedResult(emptyList(), 0, null)) }
    var isTop by rememberSaveable { mutableStateOf(true) }
    var askName by rememberSaveable { mutableStateOf(false) }

    suspend fun reload() {
        ranking = app.rankingService.fetchRanking()
    }

    suspend fun registerAndReload(name: String) {
        app.scoreStore.highScore()?.let { app.rankingService.register(name, it) }
        reload()
    }

    LaunchedEffect(Unit) { reload() }

    FFScaffold(
        title = "Online Ranking",
        background = FFColor.greenBackground,
        onClose = onClose,
        actions = {
            IconButton(onClick = {
                shareText(context, "My rank is ${ranking.myRank}! Let's play FFMultiplier with me! #FFMultiplier")
            }) { Icon(Icons.Filled.Share, contentDescription = "Share") }
        },
    ) {
        SecondaryTabRow(
            selectedTabIndex = if (isTop) 0 else 1,
            containerColor = Color.Transparent,
            contentColor = FFColor.white,
            modifier = Modifier.padding(horizontal = 24.dp),
        ) {
            Tab(selected = isTop, onClick = { isTop = true }, text = { Text("Top50") })
            Tab(selected = !isTop, onClick = { isTop = false }, text = { Text("Nearby") })
        }
        val shown = if (isTop) ranking.entries.take(50) else windowAround(ranking.entries, ranking.myIndex)
        // タブごとにスクロール位置を持たせる(切り替えても前のタブの位置のまま残らないように)
        key(isTop) {
            ScoreList(shown, Modifier.weight(1f).padding(top = 16.dp)) { "${it.rank}. ${it.score} points\n\t ${it.name}" }
        }
        Text(
            "Your Rank: ${ranking.myRank}",
            fontFamily = FFFont.futuraMedium,
            fontSize = 14.sp,
            color = Color(0xFF555555),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().background(FFColor.whiteBackground).padding(vertical = 6.dp),
        )
        FFButton(
            "REGISTER MY SCORE",
            onClick = {
                scope.launch {
                    val name = app.scoreStore.playerName()
                    if (name != null) registerAndReload(name) else askName = true
                }
            },
            tint = FFColor.blackReversible,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
        )
    }

    if (askName) {
        NameDialog(
            onConfirm = { raw ->
                askName = false
                val name = normalizedName(raw)
                if (name.isNotEmpty()) {
                    scope.launch {
                        app.scoreStore.setPlayerName(name)
                        registerAndReload(name)
                    }
                }
            },
            onDismiss = { askName = false },
        )
    }
}

@Composable
fun SettingsScreen(onClose: () -> Unit) {
    val app = app()
    val scope = rememberCoroutineScope()
    var name by rememberSaveable { mutableStateOf("") }
    var showTutorial by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) { name = app.scoreStore.playerName().orEmpty() }

    FFScaffold(
        title = "Settings",
        background = FFColor.settingGray,
        onClose = onClose,
        titleColor = Color.Black,
        actions = {
            TextButton(onClick = {
                scope.launch {
                    app.scoreStore.setPlayerName(normalizedName(name))
                    onClose()
                }
            }) { Text("Save", fontFamily = FFFont.futuraBold, color = FFColor.green) }
        },
    ) {
        Column(
            Modifier.weight(1f).fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            Column(Modifier.widthIn(max = 480.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("USER NAME", fontFamily = FFFont.futuraBold, fontSize = 14.sp, color = FFColor.darkGray)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    placeholder = { Text("user name") },
                    textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FFFont.futuraMedium, fontSize = 22.sp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FFColor.whiteBackground,
                        unfocusedContainerColor = FFColor.whiteBackground,
                        unfocusedBorderColor = FFColor.gray,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            FFButton(
                "HELP",
                onClick = { showTutorial = true },
                prominent = false,
                modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth(),
            )
        }
        BannerAd()
    }

    if (showTutorial) TutorialDialog(onExit = { showTutorial = false })
}
