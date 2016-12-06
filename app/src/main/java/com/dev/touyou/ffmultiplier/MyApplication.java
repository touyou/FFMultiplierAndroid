package com.dev.touyou.ffmultiplier;

import android.app.Application;
import io.realm.Realm;

/**
 * Created by touyou on 2016/12/06.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
