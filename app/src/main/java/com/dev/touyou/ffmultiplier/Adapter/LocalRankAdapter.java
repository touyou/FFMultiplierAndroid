package com.dev.touyou.ffmultiplier.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.dev.touyou.ffmultiplier.Model.LocalScoreItem;
import com.dev.touyou.ffmultiplier.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by touyou on 2016/12/10.
 */
public class LocalRankAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<LocalScoreItem> scoreList;

    public LocalRankAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setScoreList(ArrayList<LocalScoreItem> scoreList) {
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
        view = layoutInflater.inflate(R.layout.local_score_item, viewGroup, false);

        ((TextView) view.findViewById(R.id.localRankTextView)).setText(scoreList.get(i).getRank() + ". ");
        ((TextView) view.findViewById(R.id.localNameTextView)).setText(scoreList.get(i).getScore() + " points");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        ((TextView) view.findViewById(R.id.localDateTextView)).setText(dateFormat.format(scoreList.get(i).getDate()));

        return view;
    }
}
