package com.dev.touyou.ffmultiplier

import android.app.Application
import com.dev.touyou.ffmultiplier.ads.AdManager
import com.dev.touyou.ffmultiplier.data.RankingService
import com.dev.touyou.ffmultiplier.data.ScoreStore

class FFApplication : Application() {
    lateinit var scoreStore: ScoreStore
        private set
    lateinit var rankingService: RankingService
        private set

    override fun onCreate() {
        super.onCreate()
        scoreStore = ScoreStore(this)
        rankingService = RankingService(this, scoreStore)
        AdManager.start(this)
        AdManager.loadInterstitial(this)
    }
}
