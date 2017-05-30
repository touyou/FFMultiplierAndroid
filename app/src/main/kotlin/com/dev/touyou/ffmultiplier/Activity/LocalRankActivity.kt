package com.dev.touyou.ffmultiplier.Activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v4.app.FragmentTransaction
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dev.touyou.ffmultiplier.Adapter.LocalRankAdapter
import com.dev.touyou.ffmultiplier.Fragments.ListFragment
import com.dev.touyou.ffmultiplier.Model.DatabaseScore
import com.dev.touyou.ffmultiplier.Model.LocalScoreItem
import com.dev.touyou.ffmultiplier.Model.ScoreModel
import com.dev.touyou.ffmultiplier.R
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.realm.Realm
import io.realm.RealmResults
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

import java.util.ArrayList

class LocalRankActivity : AppCompatActivity(), ListFragment.OnFragmentInteractionListener {

    private var listFragment: ListFragment? = null

    protected fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_local_rank)

        listFragment = ListFragment.newInstance("local_mode")
        val transaction = getSupportFragmentManager().beginTransaction()
        transaction.add(R.id.localListContainer, listFragment)
        transaction.commit()
    }

    protected fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun setMyRank(rank: Int) {}


    fun deleteRankBtn(v: View) {
        val realm = Realm.getDefaultInstance()
        val sp = PreferenceManager.getDefaultSharedPreferences(this)
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference()

        AlertDialog.Builder(this).setTitle("delete your data").setPositiveButton("ok") { dialogInterface, i ->
            // オンラインをリセット
            val adIdThread = Thread(Runnable {
                val adInfo: AdvertisingIdClient.Info
                try {
                    adInfo = AdvertisingIdClient.getAdvertisingIdInfo(this@LocalRankActivity)
                    val id = adInfo.getId()
                    val databaseScore = DatabaseScore(sp.getString("name", null), 0)
                    ref.child("scores").child(id).setValue(databaseScore)
                    val result = realm.where(ScoreModel::class.java).findAll()
                    realm.beginTransaction()
                    result.clear()
                    realm.commitTransaction()
                } catch (e: Exception) {
                }
            })
            adIdThread.start()
        }.setNegativeButton("cancel", null).show()
    }
}
