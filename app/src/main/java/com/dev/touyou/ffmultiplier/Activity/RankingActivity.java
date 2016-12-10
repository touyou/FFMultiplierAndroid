package com.dev.touyou.ffmultiplier.Activity;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.dev.touyou.ffmultiplier.Adapter.OnlinePagerAdapter;
import com.dev.touyou.ffmultiplier.Fragments.ListFragment;
import com.dev.touyou.ffmultiplier.R;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RankingActivity extends AppCompatActivity implements ListFragment.OnFragmentInteractionListener {

    private TextView rankTextView;
    private int rank = 0;

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
        String articleURL = "PlayStoreのURL";
        String articleTitle = "My rank is " + (rank == 0 ? "--" : rank) + "! Let's play FFMultiplier with me! #FFMultiplier";
        String sharedText = articleTitle + " " + articleURL;

        // builderの生成 ShareCompat.IntentBuilder.from(Context context);
        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(this);
        // アプリ一覧が表示されるDialogのタイトルの設定
        builder.setChooserTitle("Select App");
        // シェアするタイトル
        builder.setSubject(articleTitle);
        // シェアするテキスト
        builder.setText(sharedText);
        // シェアするタイプ（他にもいっぱいあるよ）
        builder.setType("text/plain");
        // Shareアプリ一覧のDialogの表示
        builder.startChooser();
    }

    @Override
    public void setMyRank(final int rank) {
        this.rank = rank;
        Log.d("debug", "set");
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d("debug", "rank setting");
                rankTextView.setText("Your Rank: " + rank);
            }
        });
    }
}
