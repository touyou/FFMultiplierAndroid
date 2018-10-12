package com.dev.touyou.ffmultiplier.Activity

import android.app.Fragment
import android.app.FragmentTransaction
import android.content.Context
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.dev.touyou.ffmultiplier.Fragments.GameFragment
import com.dev.touyou.ffmultiplier.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class GameActivity : AppCompatActivity(), GameFragment.GameFragmentListener {

    private var gameFragment: Fragment? = null
    private var interstitialAd: InterstitialAd? = null
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        gameFragment = GameFragment()
        val transaction = getFragmentManager().beginTransaction()
        transaction.add(R.id.gameContainer, gameFragment)
        transaction.commit()

        interstitialAd = InterstitialAd(this)
        interstitialAd!!.setAdUnitId(getResources().getString(R.string.interst_ads_id))

        val adRequest = AdRequest.Builder().build()
        val adThread = Thread(Runnable { handler.post { interstitialAd!!.loadAd(adRequest) } })
        adThread.start()
    }

    override protected fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onDestroyActivity() {
        finish()
    }

    override fun loadAds() {
        if (interstitialAd!!.isLoaded()) {
            interstitialAd!!.show()
        }
    }
}
