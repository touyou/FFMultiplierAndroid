package com.dev.touyou.ffmultiplier.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dev.touyou.ffmultiplier.ads.AdManager
import com.dev.touyou.ffmultiplier.game.GameViewModel
import com.dev.touyou.ffmultiplier.game.hexDigit
import kotlinx.coroutines.delay

/** タイムアタック本体。表示部(タイマー・問題・入力・操作)と 4×4 の 16 進キーパッド。 */
@Composable
fun GameScreen(onExit: () -> Unit, vm: GameViewModel = viewModel()) {
    val exit = {
        vm.abandon()
        onExit()
    }
    BackHandler(onBack = exit)
    val activity = LocalContext.current as? Activity
    val result = vm.result
    LaunchedEffect(result != null) {
        if (result != null && activity != null) AdManager.showInterstitial(activity)
    }

    Box(Modifier.fillMaxSize().background(FFColor.blackBackground)) {
        BoxWithConstraints(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
            // 横長(折りたたみの半開きやタブレット横向き)では左右に、縦長では上下に分ける
            if (maxWidth > maxHeight) {
                Row(Modifier.fillMaxSize()) {
                    DisplaySection(vm, exit, Modifier.weight(1f).fillMaxSize())
                    Keypad(vm, Modifier.weight(1f).fillMaxSize())
                }
            } else {
                Column(Modifier.fillMaxSize()) {
                    DisplaySection(vm, exit, Modifier.weight(1f).fillMaxWidth())
                    Keypad(vm, Modifier.weight(1f).fillMaxWidth())
                }
            }
        }

        ToastOverlay(vm)

        if (result != null) {
            Box(
                Modifier.fillMaxSize().background(FFColor.blackReversible.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center,
            ) {
                ResultCard(score = result.score, isHighScore = result.isHighScore, onExit = onExit)
            }
        }
        if (vm.askName) {
            NameDialog(onConfirm = vm::registerName, onDismiss = vm::dismissNamePrompt)
        }
    }
}

@Composable
private fun DisplaySection(vm: GameViewModel, onExit: () -> Unit, modifier: Modifier) {
    Column(modifier.padding(horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.fillMaxWidth().padding(top = 8.dp), contentAlignment = Alignment.Center) {
            Text(vm.remaining.toString(), fontFamily = FFFont.dseg7, fontSize = 17.sp, color = FFColor.green)
            IconButton(
                onClick = onExit,
                modifier = Modifier.align(Alignment.CenterStart).size(44.dp),
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White.copy(alpha = 0.15f)),
            ) {
                Icon(Icons.Filled.Close, contentDescription = "Close", tint = FFColor.white)
            }
        }
        Spacer(Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Operand(vm.leftText, Modifier.testTag("operandLeft"))
            Text("×", fontSize = 41.sp, color = FFColor.white)
            Operand(vm.rightText, Modifier.testTag("operandRight"))
        }
        Text(
            vm.inputText,
            fontFamily = FFFont.dseg7,
            fontSize = 40.sp,
            color = FFColor.white,
            modifier = Modifier.padding(top = 16.dp),
        )
        Spacer(Modifier.weight(1f))
        Row(
            Modifier.fillMaxWidth().padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ActionButton("DELETE", Icons.Filled.Close, FFColor.red, vm::delete)
            Text(
                vm.score.toString(),
                fontFamily = FFFont.dseg7,
                fontSize = 20.sp,
                color = FFColor.white,
                modifier = Modifier
                    .background(FFColor.lightGrayBackground, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
            ActionButton("DONE", Icons.Filled.Check, FFColor.green, vm::done)
        }
    }
}

@Composable
private fun Operand(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier
            .sizeIn(minWidth = 80.dp, minHeight = 88.dp)
            .background(FFColor.whiteBackground, RoundedCornerShape(8.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, fontFamily = FFFont.dseg7, fontSize = 48.sp, color = FFColor.blackReversible)
    }
}

@Composable
private fun ActionButton(title: String, icon: ImageVector, background: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = background, contentColor = Color.White),
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Text(title, fontFamily = FFFont.futuraBold, fontSize = 16.sp, modifier = Modifier.padding(start = 6.dp))
    }
}

@Composable
private fun Keypad(vm: GameViewModel, modifier: Modifier) {
    Column(modifier.background(FFColor.blackBackground)) {
        repeat(4) { row ->
            Row(Modifier.weight(1f).fillMaxWidth()) {
                repeat(4) { col ->
                    val value = row * 4 + col
                    Box(
                        Modifier.weight(1f).fillMaxSize().clickable { vm.tapNumber(value) },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(hexDigit(value), fontFamily = FFFont.dseg7, fontSize = 36.sp, color = FFColor.gray)
                    }
                }
            }
        }
    }
}

@Composable
private fun ToastOverlay(vm: GameViewModel) {
    val toast = vm.toast
    LaunchedEffect(toast?.id) {
        if (toast != null) {
            delay(700)
            vm.clearToast(toast.id)
        }
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AnimatedVisibility(visible = toast != null, enter = fadeIn() + scaleIn(), exit = fadeOut()) {
            Text(
                toast?.text.orEmpty(),
                fontFamily = FFFont.futuraBold,
                fontSize = 20.sp,
                color = FFColor.white,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(horizontal = 28.dp, vertical = 14.dp),
            )
        }
    }
}

@Composable
fun ResultCard(score: Int, isHighScore: Boolean, onExit: () -> Unit) {
    val context = LocalContext.current
    Column(
        Modifier
            .padding(horizontal = 24.dp)
            .widthIn(max = 320.dp)
            .background(FFColor.whiteBackground, RoundedCornerShape(28.dp))
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text("Result", fontFamily = FFFont.futuraBold, fontSize = 22.sp, color = FFColor.blackReversible)
        if (isHighScore) {
            Row(
                Modifier.background(FFColor.red, CircleShape).padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Text("High Score", fontFamily = FFFont.futuraBold, fontSize = 15.sp, color = Color.White, modifier = Modifier.padding(start = 6.dp))
            }
        }
        Text(
            score.toString(),
            fontFamily = FFFont.dseg7,
            fontSize = 64.sp,
            color = FFColor.green,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp)
                .background(FFColor.blackBackground, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 20.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FFButton(
                "SHARE",
                onClick = { shareText(context, "I got $score points! Let's play FFMultiplier with me! #FFMultiplier") },
                modifier = Modifier.weight(1f),
            )
            FFButton("EXIT", onClick = onExit, modifier = Modifier.weight(1f), tint = FFColor.gray)
        }
    }
}
