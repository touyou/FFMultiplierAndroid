package com.dev.touyou.ffmultiplier.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.dev.touyou.ffmultiplier.R
import com.dev.touyou.ffmultiplier.ScreenshotFixture
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/** Google Mobile Ads の起動とインタースティシャルの load / show。 */
object AdManager {
    private var interstitial: InterstitialAd? = null

    /** SDK を起動したか。バナーはこれが true になってから置く(先に load すると同意前にリクエストが飛ぶ)。 */
    var isStarted by mutableStateOf(false)
        private set

    /** 同意が取れていれば SDK を起動し、インタースティシャルを事前ロードする。何度呼んでもよい。 */
    fun startIfAllowed(context: Context) {
        if (isStarted || !ConsentManager.canRequestAds) return
        isStarted = true
        MobileAds.initialize(context) {}
        loadInterstitial(context)
    }

    private fun loadInterstitial(context: Context) {
        val appContext = context.applicationContext
        InterstitialAd.load(
            appContext,
            appContext.getString(R.string.interstitial_ad_unit_id),
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitial = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.w("AdManager", "interstitial failed to load: ${error.message}")
                    interstitial = null
                }
            },
        )
    }

    /** ロード済みなら表示し、次回用に読み直す。 */
    fun showInterstitial(activity: Activity) {
        if (ScreenshotFixture.active) return
        val ad = interstitial ?: return
        interstitial = null
        ad.show(activity)
        loadInterstitial(activity)
    }
}
