package com.dev.touyou.ffmultiplier.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.dev.touyou.ffmultiplier.R;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-2853999389157478~5749714867");
    }

    public void toGameView(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
