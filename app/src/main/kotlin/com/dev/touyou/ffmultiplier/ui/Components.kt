package com.dev.touyou.ffmultiplier.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.dev.touyou.ffmultiplier"

fun shareText(context: Context, text: String) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "$text $PLAY_STORE_URL")
    }
    context.startActivity(Intent.createChooser(send, null))
}

/** 塗りの大きいボタン。prominent でないものは枠の薄い色で控えめにする。 */
@Composable
fun FFButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = FFColor.green,
    prominent: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = if (prominent) {
            // ダークの ff_green は明るいので白文字だと読めない。onPrimary はダークで黒になる
            ButtonDefaults.buttonColors(containerColor = tint, contentColor = MaterialTheme.colorScheme.onPrimary)
        } else {
            // 半透明のままだとダークの明るい背景(ff_white)に溶けるので、ff_white_background の上で不透明にする
            ButtonDefaults.buttonColors(
                containerColor = tint.copy(alpha = 0.18f).compositeOver(FFColor.whiteBackground),
                contentColor = tint,
            )
        },
        contentPadding = ButtonDefaults.ContentPadding,
    ) {
        Text(text, fontFamily = FFFont.futuraBold, fontSize = 17.sp, modifier = Modifier.padding(vertical = 6.dp))
    }
}

/** ローカル / オンラインのランキング一覧。空なら "No Data"。 */
@Composable
fun <T> ScoreList(
    rows: List<T>,
    modifier: Modifier = Modifier,
    line: (T) -> String,
) {
    if (rows.isEmpty()) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No Data", fontFamily = FFFont.futuraBold, fontSize = 20.sp, color = FFColor.white)
        }
        return
    }
    LazyColumn(modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(rows) { item ->
            Text(
                line(item),
                fontFamily = FFFont.futuraMedium,
                fontSize = 16.sp,
                color = FFColor.white,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            )
        }
    }
}

/** ランキング用の名前入力。OK で normalizedName 前の生文字列を返す。 */
@Composable
fun NameDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("register name") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("please set your username")
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    placeholder = { Text("user name") },
                )
            }
        },
        confirmButton = { TextButton(onClick = { onConfirm(name) }) { Text("OK") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

