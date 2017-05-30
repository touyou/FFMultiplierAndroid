package com.dev.touyou.ffmultiplier.Activity

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dev.touyou.ffmultiplier.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


class MainActivity : AppCompatActivity() {

    private var adView: AdView? = null
    private val handler = Handler()

    protected fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adView = findViewById(R.id.mainBanner) as AdView
        val adRequest = AdRequest.Builder().build()
        val adThread = Thread(Runnable { handler.post { adView!!.loadAd(adRequest) } })
        adThread.start()
    }

    protected fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    fun toGameView(v: View) {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    fun toSettingView(v: View) {
        val intent = Intent(this, SettingActivity::class.java)
        startActivity(intent)
    }

    fun toLocalView(v: View) {
        val intent = Intent(this, LocalRankActivity::class.java)
        startActivity(intent)
    }

    fun toOnlineView(v: View) {
        val intent = Intent(this, RankingActivity::class.java)
        startActivity(intent)
    }
}
