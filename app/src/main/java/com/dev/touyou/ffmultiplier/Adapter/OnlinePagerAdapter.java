package com.dev.touyou.ffmultiplier.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.dev.touyou.ffmultiplier.Fragments.ListFragment;

/**
 * Created by touyou on 2016/12/10.
 */
public class OnlinePagerAdapter extends FragmentPagerAdapter {
    public OnlinePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ListFragment topListFragment = ListFragment.newInstance("online_top");
                return topListFragment;
            case 1:
                ListFragment nearListFragment = ListFragment.newInstance("online_near");
                return nearListFragment;

        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Top50";
        } else {
            return "Nearby";
        }
    }
}
