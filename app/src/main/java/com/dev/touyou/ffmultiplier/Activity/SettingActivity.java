package com.dev.touyou.ffmultiplier.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.dev.touyou.ffmultiplier.R;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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
        finish();
    }

    public void tappedPrivacyPolicy(View v) {
        Uri uri = Uri.parse("https://www.freeprivacypolicy.com/privacy/view/ea9d5d7e7bdf287b2c8b37a25a7a4c11");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
