package com.dev.touyou.ffmultiplier.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.dev.touyou.ffmultiplier.Adapter.LocalRankAdapter;
import com.dev.touyou.ffmultiplier.Fragments.ListFragment;
import com.dev.touyou.ffmultiplier.Model.DatabaseScore;
import com.dev.touyou.ffmultiplier.Model.LocalScoreItem;
import com.dev.touyou.ffmultiplier.Model.ScoreModel;
import com.dev.touyou.ffmultiplier.R;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import io.realm.Realm;
import io.realm.RealmResults;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import java.util.ArrayList;

public class LocalRankActivity extends AppCompatActivity implements ListFragment.OnFragmentInteractionListener {

    private ListFragment listFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_rank);

        listFragment = ListFragment.newInstance("local_mode");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.localListContainer, listFragment);
        transaction.commit();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void setMyRank(int rank) {
    }


    public void deleteRankBtn(View v) {
        final Realm realm = Realm.getDefaultInstance();
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference();

        new AlertDialog.Builder(this).setTitle("delete your data").setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // オンラインをリセット
                Thread adIdThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        AdvertisingIdClient.Info adInfo;
                        try {
                            adInfo = AdvertisingIdClient.getAdvertisingIdInfo(LocalRankActivity.this);
                            final String id = adInfo.getId();
                            DatabaseScore databaseScore = new DatabaseScore(sp.getString("name", null), 0);
                            ref.child("scores").child(id).setValue(databaseScore);
                            RealmResults<ScoreModel> result = realm.where(ScoreModel.class).findAll();
                            realm.beginTransaction();
                            result.clear();
                            realm.commitTransaction();
                        } catch (Exception e) {
                        }
                    }
                });
                adIdThread.start();
            }
        }).setNegativeButton("cancel", null).show();
    }
}
