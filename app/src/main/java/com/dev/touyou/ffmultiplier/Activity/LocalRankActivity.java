package com.dev.touyou.ffmultiplier.Activity;

import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.dev.touyou.ffmultiplier.Fragments.ListFragment;
import com.dev.touyou.ffmultiplier.R;

public class LocalRankActivity extends AppCompatActivity implements ListFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_rank);

        ListFragment listFragment = ListFragment.newInstance("local_mode");
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.localListContainer, listFragment);
        transaction.commit();
    }
}
