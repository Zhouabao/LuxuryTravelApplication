package com.sdy.luxurytravelapplication.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 *    author : ZFM
 *    date   : 2021/3/2211:54
 *    desc   :
 *    version: 1.0
 */
class MainPager2Adapter @JvmOverloads constructor(
    activity: FragmentActivity,
    private val fragments: ArrayList<Fragment>
) :
    FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
        notifyItemChanged(fragments.size - 1)
    }

    fun removeFragment(fragment: Fragment) {
        fragments.remove(fragment)
        notifyDataSetChanged()
    }

}