package com.dev.touyou.ffmultiplier.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dev.touyou.ffmultiplier.R
import com.dev.touyou.ffmultiplier.ads.BannerAd

@Composable
fun HomeScreen(
    onTimeAttack: () -> Unit,
    onLocalScore: () -> Unit,
    onOnlineRanking: () -> Unit,
    onSettings: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().background(FFColor.white).windowInsetsPadding(WindowInsets.safeDrawing),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
        ) {
            Image(
                painterResource(R.drawable.key_visual),
                contentDescription = "FFMultiplier",
                contentScale = ContentScale.Fit,
                modifier = Modifier.heightIn(max = 200.dp).padding(top = 32.dp, bottom = 24.dp),
            )
            val buttonModifier = Modifier.widthIn(max = 400.dp).fillMaxWidth()
            FFButton("time attack", onClick = onTimeAttack, modifier = buttonModifier)
            FFButton("local score", onClick = onLocalScore, modifier = buttonModifier, prominent = false)
            FFButton("online ranking", onClick = onOnlineRanking, modifier = buttonModifier, prominent = false)
            FFButton("settings", onClick = onSettings, modifier = buttonModifier, prominent = false)
            Spacer(Modifier.heightIn(min = 24.dp))
        }
        BannerAd()
    }
}
