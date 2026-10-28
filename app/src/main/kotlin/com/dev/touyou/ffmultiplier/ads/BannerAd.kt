package com.dev.touyou.ffmultiplier.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dev.touyou.ffmultiplier.R
import com.dev.touyou.ffmultiplier.ScreenshotFixture
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/** 画面下部の標準バナー(320x50)。iOS 版と同じくレイアウトを圧迫しない固定サイズにする。 */
@Composable
fun BannerAd(modifier: Modifier = Modifier) {
    if (ScreenshotFixture.active) return
    val context = LocalContext.current
    val adUnitId = stringResource(R.string.banner_ad_unit_id)
    val adView = remember {
        AdView(context).apply {
            setAdSize(AdSize.BANNER)
            this.adUnitId = adUnitId
            loadAd(AdRequest.Builder().build())
        }
    }
    DisposableEffect(adView) {
        onDispose { adView.destroy() }
    }
    AndroidView(factory = { adView }, modifier = modifier.fillMaxWidth().height(50.dp))
}
