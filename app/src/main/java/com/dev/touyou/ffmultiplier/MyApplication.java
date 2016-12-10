package com.dev.touyou.ffmultiplier;

import android.app.Application;
import com.google.android.gms.ads.MobileAds;
import io.realm.Realm;

/**
 * Created by touyou on 2016/12/06.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        MobileAds.initialize(this, "ca-app-pub-2853999389157478~5749714867");
    }
}
