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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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
        // 半開きの折りたたみでは折り目に沿って、それ以外は横長なら左右・縦長なら上下に分ける。
        // タブレットではマスが大きくなりすぎて指の移動が長くなるので、iOS の iPad と同じ上限で中央に寄せる
        HingeSplit(
            modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
            first = { sideBySide ->
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    DisplaySection(vm, exit, showActions = !sideBySide, Modifier.widthIn(max = 600.dp).fillMaxSize())
                }
            },
            // 左右に分けたときは DELETE / DONE をキーパッドの上に移し、入力から確定までの指の移動を短くする
            second = { sideBySide ->
                Column(
                    Modifier.fillMaxSize().background(FFColor.blackBackground),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    if (sideBySide) ActionRow(vm, Modifier.widthIn(max = 600.dp).fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp))
                    Keypad(vm, Modifier.widthIn(max = 600.dp).heightIn(max = 680.dp).fillMaxWidth().weight(1f, fill = false).fillMaxHeight())
                }
            },
        )

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
private fun DisplaySection(vm: GameViewModel, onExit: () -> Unit, showActions: Boolean, modifier: Modifier) {
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
        // 狭いペインではカードの最小幅のまま並べて全体を縮める(はみ出すとキーパッドの下に潜る)
        BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            val scale = (maxWidth / OperandRowWidth).coerceAtMost(1f)
            Row(
                Modifier.requiredWidth(OperandRowWidth).scale(scale),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            ) {
                Operand(vm.leftText, Modifier.testTag("operandLeft"))
                Text("×", fontSize = 41.sp, color = FFColor.white)
                Operand(vm.rightText, Modifier.testTag("operandRight"))
            }
        }
        Text(
            vm.inputText,
            fontFamily = FFFont.dseg7,
            fontSize = 40.sp,
            color = FFColor.white,
            modifier = Modifier.padding(top = 16.dp),
        )
        Spacer(Modifier.weight(1f))
        if (showActions) ActionRow(vm, Modifier.fillMaxWidth().padding(bottom = 12.dp))
    }
}

@Composable
private fun ActionRow(vm: GameViewModel, modifier: Modifier) {
    // 操作側のペインが狭いときは DELETE / DONE を記号だけにし、さらに狭ければスコアをボタンの上に出す
    BoxWithConstraints(modifier) {
        val compact = maxWidth < 320.dp
        if (maxWidth < 200.dp) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                ScoreBadge(vm.score)
                Row(
                    Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    ActionButton("DELETE", Icons.Filled.Close, FFColor.red, vm::delete, compact = true)
                    ActionButton("DONE", Icons.Filled.Check, FFColor.green, vm::done, compact = true)
                }
            }
        } else {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ActionButton("DELETE", Icons.Filled.Close, FFColor.red, vm::delete, compact)
                ScoreBadge(vm.score)
                ActionButton("DONE", Icons.Filled.Check, FFColor.green, vm::done, compact)
            }
        }
    }
}

@Composable
private fun ScoreBadge(score: Int) {
    Text(
        score.toString(),
        fontFamily = FFFont.dseg7,
        fontSize = 20.sp,
        color = FFColor.white,
        modifier = Modifier
            .background(FFColor.lightGrayBackground, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
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
private fun ActionButton(
    title: String,
    icon: ImageVector,
    background: Color,
    onClick: () -> Unit,
    compact: Boolean = false,
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = background, contentColor = Color.White),
        contentPadding = if (compact) PaddingValues(horizontal = 12.dp) else ButtonDefaults.ContentPadding,
    ) {
        Icon(icon, contentDescription = if (compact) title else null, modifier = Modifier.size(18.dp))
        if (!compact) {
            Text(title, fontFamily = FFFont.jostBold, fontSize = 16.sp, modifier = Modifier.padding(start = 6.dp))
        }
    }
}

@Composable
private fun Keypad(vm: GameViewModel, modifier: Modifier) {
    BoxWithConstraints(modifier.background(FFColor.blackBackground)) {
        // 折りたたみの半開きなどで縦が短いと、キーの行が 36sp の数字より低くなって上下の数字が重なる。
        // キー 1 つの大きさに収まるよう、字の大きさを 36sp を上限に縮める
        val digitSize = with(LocalDensity.current) {
            minOf(36.sp.toDp(), maxHeight / 4 * 0.6f, maxWidth / 4 * 0.6f).toSp()
        }
        Column(Modifier.fillMaxSize()) {
            repeat(4) { row ->
                Row(Modifier.weight(1f).fillMaxWidth()) {
                    repeat(4) { col ->
                        val value = row * 4 + col
                        Box(
                            Modifier.weight(1f).fillMaxSize().clickable { vm.tapNumber(value) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                hexDigit(value),
                                fontFamily = FFFont.dseg7,
                                fontSize = digitSize,
                                color = FFColor.gray,
                                maxLines = 1,
                            )
                        }
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
                fontFamily = FFFont.jostBold,
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
        Text("Result", fontFamily = FFFont.jostBold, fontSize = 22.sp, color = FFColor.blackReversible)
        if (isHighScore) {
            Row(
                Modifier.background(FFColor.red, CircleShape).padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Text("High Score", fontFamily = FFFont.jostBold, fontSize = 15.sp, color = Color.White, modifier = Modifier.padding(start = 6.dp))
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

// カード 2 枚(最小 80dp)と「×」、その間隔を足した問題の行の幅
private val OperandRowWidth = 220.dp
