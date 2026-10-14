package com.dev.touyou.ffmultiplier.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.dev.touyou.ffmultiplier.R
import kotlinx.coroutines.launch

private val pages = listOf(
    R.drawable.tutorial1, R.drawable.tutorial2, R.drawable.tutorial3,
    R.drawable.tutorial4, R.drawable.tutorial5, R.drawable.tutorial6,
)

/** 遊び方のページング表示(初回起動時と設定の HELP から)。 */
@Composable
fun TutorialDialog(onExit: () -> Unit) {
    val pager = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()
    Dialog(onDismissRequest = onExit) {
        Column(
            Modifier
                .widthIn(max = 320.dp)
                .background(FFColor.whiteBackground, RoundedCornerShape(20.dp))
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                painterResource(R.drawable.ffmultiicon),
                contentDescription = null,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)),
            )
            HorizontalPager(pager, Modifier.fillMaxWidth().heightIn(max = 320.dp)) { page ->
                Image(
                    painterResource(pages[page]),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(pages.size) { index ->
                    val color = if (index == pager.currentPage) FFColor.green else FFColor.gray
                    Box(Modifier.size(8.dp).background(color, CircleShape))
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TextButton(
                    onClick = { scope.launch { pager.animateScrollToPage(pager.currentPage - 1) } },
                    enabled = pager.currentPage > 0,
                ) { Text("👈", fontSize = 24.sp) }
                FFButton("EXIT", onClick = onExit)
                TextButton(
                    onClick = { scope.launch { pager.animateScrollToPage(pager.currentPage + 1) } },
                    enabled = pager.currentPage < pages.lastIndex,
                ) { Text("👉", fontSize = 24.sp) }
            }
        }
    }
}
