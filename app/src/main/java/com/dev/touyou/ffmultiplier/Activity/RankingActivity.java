package com.dev.touyou.ffmultiplier.Activity;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.dev.touyou.ffmultiplier.Adapter.OnlinePagerAdapter;
import com.dev.touyou.ffmultiplier.Fragments.ListFragment;
import com.dev.touyou.ffmultiplier.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RankingActivity extends AppCompatActivity implements ListFragment.OnFragmentInteractionListener {

    private TextView rankTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        FragmentManager fragmentManager = getSupportFragmentManager();
        ViewPager viewPager = (ViewPager) findViewById(R.id.pagerView);
        OnlinePagerAdapter adapter = new OnlinePagerAdapter(fragmentManager);
        viewPager.setAdapter(adapter);

        rankTextView = (TextView) findViewById(R.id.yourRankTextView);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void registerBtn(View v) {

    }

    public void shareBtn(View v) {

    }
}
