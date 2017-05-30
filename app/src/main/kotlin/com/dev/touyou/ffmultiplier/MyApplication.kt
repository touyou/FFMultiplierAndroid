package com.dev.touyou.ffmultiplier

import android.app.Application
import com.google.android.gms.ads.MobileAds
import io.realm.Realm
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

/**
 * Created by touyou on 2016/12/06.
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        MobileAds.initialize(this, "ca-app-pub-2853999389157478~5749714867")
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath(resources.getString(R.string.futura_font))
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }
}
