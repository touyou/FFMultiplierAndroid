package com.dev.touyou.ffmultiplier.Adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.dev.touyou.ffmultiplier.Model.OnlineScoreItem
import com.dev.touyou.ffmultiplier.R

import java.util.ArrayList

/**
 * Created by touyou on 2016/12/10.
 */
class OnlineRankAdapter(internal var context: Context) : BaseAdapter() {
    internal var layoutInflater: LayoutInflater
    internal var scoreList: ArrayList<OnlineScoreItem>
    internal var topMode = true
    internal var myPos = 0

    init {
        this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.scoreList = java.util.ArrayList<OnlineScoreItem>()
    }

    fun setScoreList(scoreList: ArrayList<OnlineScoreItem>) {
        this.scoreList = scoreList
    }

    fun setTopMode(topMode: Boolean) {
        this.topMode = topMode
    }

    fun setMyPos(myPos: Int) {
        this.myPos = myPos
        Handler(Looper.getMainLooper()).post { notifyDataSetChanged() }
    }

    fun add(item: OnlineScoreItem) {
        Handler(Looper.getMainLooper()).post {
            if (scoreList.add(item)) {
                notifyDataSetChanged()
            }
        }
    }

    override fun getCount(): Int {
        return Math.min(50, scoreList.size)
    }

    override fun getItem(i: Int): Any {
        if (topMode || myPos < 25) {
            return scoreList[i]
        } else {
            return scoreList[i + myPos - 25]
        }
    }

    override fun getItemId(i: Int): Long {
        return scoreList[i].id
    }

    override fun getView(i: Int, view: View, viewGroup: ViewGroup): View {
        var view = view
        view = layoutInflater.inflate(R.layout.online_score_item, viewGroup, false)

        val score: OnlineScoreItem
        if (topMode || myPos < 25) {
            score = scoreList[i]
        } else {
            score = scoreList[i + myPos - 25]
        }

        (view.findViewById(R.id.onlineNameTextView) as TextView).text = score.name
        (view.findViewById(R.id.onlinePointTextView) as TextView).text = score.score.toString() + " points"
        (view.findViewById(R.id.onlineRankTextView) as TextView).text = score.rank.toString() + ". "

        return view
    }
}
