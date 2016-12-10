package com.dev.touyou.ffmultiplier.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.dev.touyou.ffmultiplier.R;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import java.util.HashMap;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {

    private EditText editText;
    private String name;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        editText = (EditText) findViewById(R.id.nameEditText);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        name = sp.getString("name", null);
        if (name != null) {
            editText.setText(name);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void tappedSaveBtn(View v) {
        name = editText.getText().toString();
        sp.edit().putString("name", name).commit();
        // 名前変更時にFirebaseも更新
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference();
        Thread adIdThread = new Thread(new Runnable() {
            @Override
            public void run() {
                AdvertisingIdClient.Info adInfo;
                try {
                    adInfo = AdvertisingIdClient.getAdvertisingIdInfo(SettingActivity.this);
                    final String id = adInfo.getId();
                    ref.child("scores").child(id).child("name").setValue(name);
                } catch (Exception e) {
                }
            }
        });
        adIdThread.start();
        finish();
    }
}
