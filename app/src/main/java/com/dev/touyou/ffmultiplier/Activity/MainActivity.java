package com.dev.touyou.ffmultiplier.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.dev.touyou.ffmultiplier.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private AdView adView;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adView = (AdView) findViewById(R.id.mainBanner);
        final AdRequest adRequest = new AdRequest.Builder().build();
        Thread adThread = new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        adView.loadAd(adRequest);
                    }
                });
            }
        });
        adThread.start();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void toGameView(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void toSettingView(View v) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    public void toLocalView(View v) {
        Intent intent = new Intent(this, LocalRankActivity.class);
        startActivity(intent);
    }

    public void toOnlineView(View v) {
        Intent intent = new Intent(this, RankingActivity.class);
        startActivity(intent);
    }
}
