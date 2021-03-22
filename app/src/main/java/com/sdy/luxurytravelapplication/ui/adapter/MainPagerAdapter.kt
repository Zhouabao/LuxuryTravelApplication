package com.sdy.luxurytravelapplication.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 *    author : ZFM
 *    date   : 2021/3/2211:54
 *    desc   :
 *    version: 1.0
 */
class MainPagerAdapter @JvmOverloads constructor(
    fragmentManager: FragmentManager,
    private val fragments: List<Fragment>,
    private var titles: Array<String>? = null
) :
    FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        if (!titles.isNullOrEmpty() && titles!!.size > position)
            return titles!![position]
        else return null
    }
}
