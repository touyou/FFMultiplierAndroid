package com.dev.touyou.ffmultiplier.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.dev.touyou.ffmultiplier.Model.LocalScoreItem
import com.dev.touyou.ffmultiplier.R

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Locale

/**
 * Created by touyou on 2016/12/10.
 */
class LocalRankAdapter(internal var context: Context) : BaseAdapter() {
    internal var layoutInflater: LayoutInflater
    internal var scoreList: ArrayList<LocalScoreItem>

    init {
        this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.scoreList = java.util.ArrayList<LocalScoreItem>()
    }

    fun setScoreList(scoreList: ArrayList<LocalScoreItem>) {
        this.scoreList = scoreList
    }

    override fun getCount(): Int {
        return scoreList.size
    }

    override fun getItem(i: Int): Any {
        return scoreList[i]
    }

    override fun getItemId(i: Int): Long {
        return scoreList[i].id
    }

    override fun getView(i: Int, view: View, viewGroup: ViewGroup): View {
        var view = view
        view = layoutInflater.inflate(R.layout.local_score_item, viewGroup, false)

        (view.findViewById(R.id.localRankTextView) as TextView).text = scoreList[i].rank.toString() + ". "
        (view.findViewById(R.id.localNameTextView) as TextView).text = scoreList[i].score.toString() + " points"
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        (view.findViewById(R.id.localDateTextView) as TextView).text = dateFormat.format(scoreList[i].date)

        return view
    }
}
