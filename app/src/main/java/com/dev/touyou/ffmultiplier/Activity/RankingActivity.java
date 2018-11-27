package com.dev.touyou.ffmultiplier.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.dev.touyou.ffmultiplier.Adapter.OnlinePagerAdapter;
import com.dev.touyou.ffmultiplier.Fragments.ListFragment;
import com.dev.touyou.ffmultiplier.Model.DatabaseScore;
import com.dev.touyou.ffmultiplier.Model.ScoreModel;
import com.dev.touyou.ffmultiplier.R;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
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
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ScoreModel> results = realm.where(ScoreModel.class).findAll().sort("score", Sort.DESCENDING);
        if (results.size() == 0) return;
        final int score = results.first().getScore();
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference();
        if (sp.getString("name", null) == null) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View dialog = inflater.inflate(R.layout.input_dialog, null);
            final EditText editText = (EditText) dialog.findViewById(R.id.editNameText);

            new AlertDialog.Builder(getApplicationContext()).setTitle("please set your name").setView(dialog).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final String userName = editText.getText().toString();
                    // スコアを登録
                    Thread adIdThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AdvertisingIdClient.Info adInfo;
                            try {
                                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                                final String id = adInfo.getId();
                                DatabaseScore databaseScore = new DatabaseScore(userName, score);
                                ref.child("scores").child(id).setValue(databaseScore);
                            } catch (Exception e) {
                            }
                        }
                    });
                    adIdThread.start();
                    sp.edit().putString("name", userName).commit();
                }
            }).show();
        } else {
            final String userName = sp.getString("name", null);
            // スコアを送信
            Thread adIdThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    AdvertisingIdClient.Info adInfo;
                    try {
                        adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                        final String id = adInfo.getId();
                        DatabaseScore databaseScore = new DatabaseScore(userName, score);
                        ref.child("scores").child(id).setValue(databaseScore);
                    } catch (Exception e) {
                    }
                }
            });
            adIdThread.start();
        }
    }

    public void shareBtn(View v) {
        String articleURL = "https://play.google.com/store/apps/details?id=com.dev.touyou.ffmultiplier";
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
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                rankTextView.setText("Your Rank: " + (rank == 0 ? "--" : rank));
            }
        });
    }
}
