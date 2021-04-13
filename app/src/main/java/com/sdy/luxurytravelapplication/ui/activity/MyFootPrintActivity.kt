package com.sdy.luxurytravelapplication.ui.activity

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.flyco.tablayout.listener.OnTabSelectListener
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.databinding.ActivityMyFootPrintBinding
import com.sdy.luxurytravelapplication.ui.adapter.MainPager2Adapter
import com.sdy.luxurytravelapplication.ui.fragment.FindContentFragment
import com.sdy.luxurytravelapplication.ui.fragment.MyCollectionAndLikeFragment
import com.sdy.luxurytravelapplication.ui.fragment.MyCommentFragment
import org.greenrobot.eventbus.EventBus

/**
 * 我的足迹：我的点赞 、评论、收藏
 */
class MyFootPrintActivity : BaseActivity<ActivityMyFootPrintBinding>() {


    //fragment栈管理
    private val mStack = arrayListOf<Fragment>()
    private val titles by lazy {
        arrayOf(
            getString(R.string.tab_zan), getString(R.string.tab_comment)
        )
    }


    /*
      初始化Fragment栈管理
   */
    private fun initFragment() {
        binding.apply {
            mStack.add(MyCollectionAndLikeFragment(MyCollectionAndLikeFragment.TYPE_LIKE))   //我的点赞
            mStack.add(MyCommentFragment())   //我的评论
            vpMyFootPrint.adapter = MainPager2Adapter(this@MyFootPrintActivity, mStack)
            vpMyFootPrint.offscreenPageLimit = 2
            tabMyFootprint.setTabData(titles)
            tabMyFootprint.setOnTabSelectListener(object : OnTabSelectListener {
                override fun onTabSelect(position: Int) {
                    vpMyFootPrint.currentItem = position
                }

                override fun onTabReselect(position: Int) {
                }

            })
            vpMyFootPrint.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tabMyFootprint.currentTab = position
                }
            })

            vpMyFootPrint.currentItem = 0
            tabMyFootprint.currentTab = 0
        }
    }


    override fun finish() {
        setResult(Activity.RESULT_OK, intent)
        super.finish()
    }


    override fun initData() {

    }

    override fun start() {
    }

    override fun initView() {
        binding.apply {
            barCl.actionbarTitle.text = getString(R.string.my_foot_print)
            barCl.btnBack.setOnClickListener {
                finish()
            }
        }
        initFragment()
    }

}