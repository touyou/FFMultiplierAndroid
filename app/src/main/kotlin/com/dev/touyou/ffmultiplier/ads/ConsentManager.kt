package com.dev.touyou.ffmultiplier.ads

import android.app.Activity
import android.content.pm.ApplicationInfo
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

/**
 * Google User Messaging Platform (UMP) のラッパー。EEA・英国などで必要な広告の同意フォームを
 * 起動時に出し、設定画面から同意を変えられるようにする。フォームの中身は AdMob 側で作る。
 * 同意が取れる(`canRequestAds`)までは広告 SDK を起動しない。
 */
object ConsentManager {
    /** 設定画面に「PRIVACY SETTINGS」を出す必要があるか。 */
    var isPrivacyOptionsRequired by mutableStateOf(false)
        private set

    private lateinit var info: ConsentInformation

    val canRequestAds: Boolean
        get() = ::info.isInitialized && info.canRequestAds()

    /**
     * 同意情報を更新し、必要ならフォームを出す。終わったら(失敗しても) [onDone] を呼ぶ。
     * [onDone] は前回の同意で先に呼ばれることがあるので、フォームが閉じたのを待つなら [onSettled] を使う。
     */
    fun gatherConsent(activity: Activity, onSettled: () -> Unit = {}, onDone: () -> Unit) {
        info = UserMessagingPlatform.getConsentInformation(activity)
        val params = ConsentRequestParameters.Builder().apply {
            // debug ビルドで intent extra `umpDebugEEA` を付けると EEA からのアクセスとして振る舞う
            val debuggable = (activity.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
            if (debuggable && activity.intent.getBooleanExtra("umpDebugEEA", false)) {
                setConsentDebugSettings(
                    ConsentDebugSettings.Builder(activity)
                        .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                        .build(),
                )
            }
        }.build()

        info.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { error ->
                    error?.let { Log.w("ConsentManager", "consent form: ${it.message}") }
                    refresh()
                    onDone()
                    onSettled()
                }
            },
            { error ->
                Log.w("ConsentManager", "consent info update: ${error.message}")
                refresh()
                onDone()
                onSettled()
            },
        )
        // 前回の起動で同意済みなら、更新の完了を待たずに広告を始められる
        if (info.canRequestAds()) onDone()
    }

    /** 同意の変更・撤回のフォームを出す。 */
    fun showPrivacyOptionsForm(activity: Activity, onDone: () -> Unit) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { error ->
            error?.let { Log.w("ConsentManager", "privacy options form: ${it.message}") }
            refresh()
            onDone()
        }
    }

    private fun refresh() {
        isPrivacyOptionsRequired =
            info.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
    }
}
