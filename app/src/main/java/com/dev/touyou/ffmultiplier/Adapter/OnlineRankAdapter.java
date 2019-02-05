package com.dev.touyou.ffmultiplier.Adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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
    boolean topMode = true;
    int myPos = 0;

    public OnlineRankAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setScoreList(ArrayList<OnlineScoreItem> scoreList) {
        this.scoreList = scoreList;
    }

    public void setTopMode(boolean topMode) {
        this.topMode = topMode;
    }

    public void setMyPos(int myPos) {
        this.myPos = myPos;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    public void add(final OnlineScoreItem item) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (scoreList.add(item)) {
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getCount() {
        return Math.min(50, scoreList.size());
    }

    @Override
    public Object getItem(int i) {
       if (topMode || myPos < 25) {
            return scoreList.get(i);
       } else {
            return scoreList.get(i + myPos - 25);
       }
    }

    @Override
    public long getItemId(int i) {
        return scoreList.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = layoutInflater.inflate(R.layout.online_score_item, viewGroup, false);

        OnlineScoreItem score;
        if (topMode || myPos < 25) {
            score = scoreList.get(i);
        } else {
            score = scoreList.get(i + myPos - 25);
        }

        String name = score.getName();
        if (name == null || name.length() == 0) {
            name = "No Name";
        }

        ((TextView) view.findViewById(R.id.onlineNameTextView)).setText(name);
        ((TextView) view.findViewById(R.id.onlinePointTextView)).setText(score.getScore()+" points");
        ((TextView) view.findViewById(R.id.onlineRankTextView)).setText(score.getRank()+". ");

        return view;
    }
}
