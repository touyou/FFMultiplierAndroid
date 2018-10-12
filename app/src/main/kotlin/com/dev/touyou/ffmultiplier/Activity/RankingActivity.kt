package com.dev.touyou.ffmultiplier.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.support.v4.app.FragmentManager
import android.support.v4.app.ShareCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.dev.touyou.ffmultiplier.Adapter.OnlinePagerAdapter
import com.dev.touyou.ffmultiplier.Fragments.ListFragment
import com.dev.touyou.ffmultiplier.Model.DatabaseScore
import com.dev.touyou.ffmultiplier.Model.ScoreModel
import com.dev.touyou.ffmultiplier.R
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class RankingActivity : AppCompatActivity(), ListFragment.OnFragmentInteractionListener {

    private var rankTextView: TextView? = null
    private var rank = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        val fragmentManager = getSupportFragmentManager()
        val viewPager = findViewById(R.id.pagerView) as ViewPager
        val adapter = OnlinePagerAdapter(fragmentManager)
        viewPager.setAdapter(adapter)

        rankTextView = findViewById(R.id.yourRankTextView) as TextView
    }

    override protected fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    fun registerBtn(v: View) {
        val realm = Realm.getDefaultInstance()
        val results = realm.where(ScoreModel::class.java).sort("score", Sort.DESCENDING).findAll()
        if (results.size === 0) return
        val score = results.first()!!.score
        val sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference()
        if (sp.getString("name", null) == null) {
            val inflater = LayoutInflater.from(getApplicationContext())
            val dialog = inflater.inflate(R.layout.input_dialog, null)
            val editText = dialog.findViewById(R.id.editNameText) as EditText

            AlertDialog.Builder(getApplicationContext()).setTitle("please set your name").setView(dialog).setPositiveButton("ok") { dialogInterface, i ->
                val userName = editText.text.toString()
                // スコアを登録
                val adIdThread = Thread(Runnable {
                    val adInfo: AdvertisingIdClient.Info
                    try {
                        adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext())
                        val id = adInfo.getId()
                        val databaseScore = DatabaseScore(userName, score)
                        ref.child("scores").child(id).setValue(databaseScore)
                    } catch (e: Exception) {
                    }
                })
                adIdThread.start()
                sp.edit().putString("name", userName).commit()
            }.show()
        } else {
            val userName = sp.getString("name", null)
            // スコアを送信
            val adIdThread = Thread(Runnable {
                val adInfo: AdvertisingIdClient.Info
                try {
                    adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext())
                    val id = adInfo.getId()
                    val databaseScore = DatabaseScore(userName, score)
                    ref.child("scores").child(id).setValue(databaseScore)
                } catch (e: Exception) {
                }
            })
            adIdThread.start()
        }
    }

    fun shareBtn(v: View) {
        val articleURL = "https://play.google.com/store/apps/details?id=com.dev.touyou.ffmultiplier"
        val articleTitle = "My rank is " + (if (rank == 0) "--" else rank) + "! Let's play FFMultiplier with me! #FFMultiplier"
        val sharedText = articleTitle + " " + articleURL

        // builderの生成 ShareCompat.IntentBuilder.from(Context context);
        val builder = ShareCompat.IntentBuilder.from(this)
        // アプリ一覧が表示されるDialogのタイトルの設定
        builder.setChooserTitle("Select App")
        // シェアするタイトル
        builder.setSubject(articleTitle)
        // シェアするテキスト
        builder.setText(sharedText)
        // シェアするタイプ（他にもいっぱいあるよ）
        builder.setType("text/plain")
        // Shareアプリ一覧のDialogの表示
        builder.startChooser()
    }

    override fun setMyRank(rank: Int) {
        this.rank = rank
        Handler(Looper.getMainLooper()).post { rankTextView!!.text = "Your Rank: " + if (rank == 0) "--" else rank }
    }
}
