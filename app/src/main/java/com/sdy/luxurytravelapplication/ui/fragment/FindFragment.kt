package com.sdy.luxurytravelapplication.ui.fragment

import android.view.View
import androidx.fragment.app.Fragment
import com.flyco.tablayout.listener.OnTabSelectListener
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseFragment
import com.sdy.luxurytravelapplication.databinding.FragmentFindBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * 發現
 */
class FindFragment : BaseFragment<FragmentFindBinding>() {
    private val fragments by lazy {
        arrayListOf<Fragment>(
            FindContentFragment(FindContentFragment.TYPE_RECOMMEND),
            FindContentFragment(FindContentFragment.TYPE_NEARBY),
            FindContentFragment(FindContentFragment.TYPE_NEWEST)
        )
    }
    private val titles by lazy {
        arrayOf(
            getString(R.string.tab_recommend),
            getString(R.string.tab_nearby),
            getString(R.string.tab_sweet)
        )
    }

    override fun initView(view: View) {
        binding.apply {
            tabFind.setTabData(titles, activity!!, content.id, fragments)
            tabFind.setOnTabSelectListener(object : OnTabSelectListener {
                override fun onTabSelect(position: Int) {
                }

                override fun onTabReselect(position: Int) {
                }

            })
            tabFind.currentTab = 0
        }

    }

    override fun lazyLoad() {


    }
}