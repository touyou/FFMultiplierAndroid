package com.dev.touyou.ffmultiplier.Adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.dev.touyou.ffmultiplier.Fragments.ListFragment

/**
 * Created by touyou on 2016/12/10.
 */
class OnlinePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                val topListFragment = ListFragment.newInstance("online_top")
                return topListFragment
            }
            1 -> {
                val nearListFragment = ListFragment.newInstance("online_near")
                return nearListFragment
            }
        }
        return null
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        if (position == 0) {
            return "Top50"
        } else {
            return "Nearby"
        }
    }
}
