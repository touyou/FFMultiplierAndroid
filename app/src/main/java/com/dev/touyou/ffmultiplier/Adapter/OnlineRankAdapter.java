package com.dev.touyou.ffmultiplier.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.dev.touyou.ffmultiplier.Model.OnlineScoreItem;
import com.dev.touyou.ffmultiplier.R;

import java.util.ArrayList;

/**
 * Created by touyou on 2016/12/10.
 */
public class OnlineRankAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<OnlineScoreItem> scoreList;

    public OnlineRankAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setScoreList(ArrayList<OnlineScoreItem> scoreList) {
        this.scoreList = scoreList;
    }

    @Override
    public int getCount() {
        return scoreList.size();
    }

    @Override
    public Object getItem(int i) {
        return scoreList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return scoreList.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(R.layout.online_score_item, viewGroup, false);

        ((TextView) view.findViewById(R.id.onlineNameTextView)).setText(scoreList.get(i).getName());
        ((TextView) view.findViewById(R.id.onlinePointTextView)).setText(scoreList.get(i).getScore()+" points");
        ((TextView) view.findViewById(R.id.onlineRankTextView)).setText(scoreList.get(i).getRank()+". ");

        return view;
    }
}
