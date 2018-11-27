package com.dev.touyou.ffmultiplier;

import android.app.Application;
import com.google.android.gms.ads.MobileAds;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by touyou on 2016/12/06.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("myrealm.realm").schemaVersion(2).build();
        Realm.setDefaultConfiguration(config);
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, "ca-app-pub-2853999389157478~5749714867");
        CalligraphyConfig.initDefault(
                new CalligraphyConfig.Builder()
                .setDefaultFont(R.font.futura_normal)
                .build());
    }
}
