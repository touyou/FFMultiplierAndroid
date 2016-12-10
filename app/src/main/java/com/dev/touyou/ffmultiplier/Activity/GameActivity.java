package com.dev.touyou.ffmultiplier.Activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.dev.touyou.ffmultiplier.Fragments.GameFragment;
import com.dev.touyou.ffmultiplier.R;

public class GameActivity extends AppCompatActivity implements GameFragment.GameFragmentListener {

    private Fragment gameFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gameFragment = new GameFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.gameContainer, gameFragment);
        transaction.commit();
    }

    @Override
    public void onDestroyActivity() {
        finish();
    }
}
