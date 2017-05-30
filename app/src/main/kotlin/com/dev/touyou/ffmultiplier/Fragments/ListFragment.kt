package com.dev.touyou.ffmultiplier.Fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ListView
import com.dev.touyou.ffmultiplier.Adapter.LocalRankAdapter
import com.dev.touyou.ffmultiplier.Adapter.OnlineRankAdapter
import com.dev.touyou.ffmultiplier.Model.DatabaseScore
import com.dev.touyou.ffmultiplier.Model.LocalScoreItem
import com.dev.touyou.ffmultiplier.Model.OnlineScoreItem
import com.dev.touyou.ffmultiplier.Model.ScoreModel
import com.dev.touyou.ffmultiplier.R
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.database.*
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort

import java.util.*

import android.content.ContentValues.TAG

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var mParam: String? = null
    var listView: ListView

    private val database: FirebaseDatabase
    private val ref: DatabaseReference
    private var listener: ValueEventListener? = null

    private var mListener: OnFragmentInteractionListener? = null

    init {
        // Required empty public constructor
        database = FirebaseDatabase.getInstance()
        ref = database.getReference()
    }

    fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        if (getArguments() != null) {
            mParam = getArguments().getString(ARG_PARAM)
        }
    }

    fun onCreateView(inflater: LayoutInflater, container: ViewGroup,
                     savedInstanceState: Bundle): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.listView) as ListView

        when (mParam) {
            "local_mode" -> setLocalMode()
            "online_top" -> setOnline("top")
            "online_near" -> setOnline("near")
            else -> {
            }
        }
    }

    fun onDestroy() {
        if (listener != null) ref.removeEventListener(listener)
        super.onDestroy()
    }

    private fun setLocalMode() {
        val arrayList = ArrayList<LocalScoreItem>()
        val localRankAdapter = LocalRankAdapter(getActivity())
        localRankAdapter.setScoreList(arrayList)
        listView.adapter = localRankAdapter

        val realm = Realm.getDefaultInstance()
        val results = realm.where(ScoreModel::class.java).findAllSorted("score", Sort.DESCENDING)
        if (results.size() > 0) {
            var nowValue = results.first()
            arrayList.add(convertLocalScore(1, nowValue))
            var r = 1
            for (i in 1..Math.min(50, results.size()) - 1) {
                if (results.get(i).getScore() !== nowValue.getScore()) {
                    r = i + 1
                    nowValue = results.get(i)
                }
                arrayList.add(convertLocalScore(r, results.get(i)))
            }
        }
        localRankAdapter.notifyDataSetChanged()
    }

    private fun convertLocalScore(i: Int, s: ScoreModel): LocalScoreItem {
        val localScoreItem = LocalScoreItem()
        localScoreItem.rank = i
        localScoreItem.date = s.date
        localScoreItem.score = s.score
        return localScoreItem
    }

    private fun setOnline(mode: String) {

        val arrayList = ArrayList<OnlineScoreItem>()
        val onlineRankAdapter = OnlineRankAdapter(getActivity())
        onlineRankAdapter.setScoreList(arrayList)
        onlineRankAdapter.setTopMode(mode === "top")
        listView.adapter = onlineRankAdapter

        listener = object : ValueEventListener() {
            fun onDataChange(dataSnapshot: DataSnapshot) {
                val map = dataSnapshot.getValue(object : GenericTypeIndicator<Map<String, DatabaseScore>>() {
                    fun hashCode(): Int {
                        return super.hashCode()
                    }
                })
                // Map.Entryのリストを作成
                val entries = ArrayList<Entry<String, DatabaseScore>>(map.entries)
                // 比較用
                Collections.sort<Entry<String, DatabaseScore>>(entries) { o1, o2 ->
                    val o2score = Integer.valueOf(o2.value.getScore())
                    val o1score = Integer.valueOf(o1.value.getScore())
                    o2score!!.compareTo(o1score)
                }

                onlineRankAdapter.setScoreList(ArrayList<OnlineScoreItem>())
                if (mListener != null) mListener!!.setMyRank(0)

                val adIdThread = Thread(Runnable {
                    val adInfo: AdvertisingIdClient.Info
                    try {
                        adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getContext())
                        val id = adInfo.getId()
                        var r = 0
                        var pos = 0
                        var nextScore = -10
                        for (e in entries) {
                            if (nextScore != e.value.getScore()) {
                                r = pos + 1
                                nextScore = e.value.getScore()
                            }
                            onlineRankAdapter.add(convertOnlineScore(r, e.value))
                            if (id.compareTo(e.key.toString()) == 0) {
                                mListener!!.setMyRank(r)
                                onlineRankAdapter.setMyPos(r)
                            }
                            pos++
                        }
                    } catch (e: Exception) {
                    }
                })
                adIdThread.start()
            }

            fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        ref.child("scores").orderByPriority().addValueEventListener(listener)
    }

    private fun convertOnlineScore(i: Int, databaseScore: DatabaseScore): OnlineScoreItem {
        val ret = OnlineScoreItem()
        ret.name = databaseScore.name
        ret.score = databaseScore.score
        ret.rank = i
        return ret
    }

    fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun setMyRank(rank: Int)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM = "local_mode"

        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String): ListFragment {
            val fragment = ListFragment()
            val args = Bundle()
            args.putString(ARG_PARAM, param1)
            fragment.setArguments(args)
            return fragment
        }
    }
}
