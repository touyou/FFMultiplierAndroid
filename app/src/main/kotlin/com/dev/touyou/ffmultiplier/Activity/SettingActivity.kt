package com.dev.touyou.ffmultiplier.Activity

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.dev.touyou.ffmultiplier.R
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

import java.util.HashMap

class SettingActivity : AppCompatActivity() {

    private var editText: EditText? = null
    private var name: String? = null

    private var sp: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        editText = findViewById(R.id.nameEditText) as EditText
        sp = PreferenceManager.getDefaultSharedPreferences(this)
        name = sp!!.getString("name", null)
        if (name != null) {
            editText!!.setText(name)
        }
    }

    override protected fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    fun tappedSaveBtn(v: View) {
        name = editText!!.text.toString()
        sp!!.edit().putString("name", name).commit()
        finish()
    }
}
