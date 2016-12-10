package com.dev.touyou.ffmultiplier.Activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.dev.touyou.ffmultiplier.Fragments.GameFragment;
import com.dev.touyou.ffmultiplier.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class GameActivity extends AppCompatActivity implements GameFragment.GameFragmentListener {

    private Fragment gameFragment;
    private InterstitialAd interstitialAd;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameFragment = new GameFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.gameContainer, gameFragment);
        transaction.commit();

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.interst_ads_id));

        final AdRequest adRequest = new AdRequest.Builder().build();
        Thread adThread = new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        interstitialAd.loadAd(adRequest);
                    }
                });
            }
        });
        adThread.start();
    }

    @Override
    public void onDestroyActivity() {
        finish();
    }

    @Override
    public void loadAds() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
    }
}
