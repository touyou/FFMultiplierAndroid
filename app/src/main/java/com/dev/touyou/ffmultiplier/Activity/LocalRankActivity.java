package com.dev.touyou.ffmultiplier.Activity;

import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.dev.touyou.ffmultiplier.Fragments.ListFragment;
import com.dev.touyou.ffmultiplier.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LocalRankActivity extends AppCompatActivity implements ListFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_rank);

        ListFragment listFragment = ListFragment.newInstance("local_mode");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.localListContainer, listFragment);
        transaction.commit();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
