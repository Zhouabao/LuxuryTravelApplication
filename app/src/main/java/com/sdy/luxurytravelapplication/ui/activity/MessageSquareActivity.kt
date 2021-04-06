package com.sdy.luxurytravelapplication.ui.activity

import androidx.fragment.app.Fragment
import com.flyco.tablayout.listener.OnTabSelectListener
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.ActivityMessageSquareBinding
import com.sdy.luxurytravelapplication.ui.adapter.MainPager2Adapter
import com.sdy.luxurytravelapplication.ui.fragment.SquareZanFragment

/**
 * 发现消息列表
 *
 **/
class MessageSquareActivity :
    BaseActivity<ActivityMessageSquareBinding>() {

    private val fragments by lazy {
        arrayListOf<Fragment>(
            SquareZanFragment(SquareZanFragment.TYPE_ZAN),
            SquareZanFragment(SquareZanFragment.TYPE_COMMNET)
        )
    }

    override fun initData() {


    }

    override fun initView() {
        binding.apply {
            barCl.actionbarTitle.text = "广场消息"
            barCl.btnBack.setOnClickListener {
                finish()
            }

            tabSquare.setTabData(arrayOf("点赞", "评论"))
            vpSquare.adapter = MainPager2Adapter(this@MessageSquareActivity, fragments)
            vpSquare.isUserInputEnabled = false
            tabSquare.setOnTabSelectListener(object : OnTabSelectListener {
                override fun onTabSelect(position: Int) {
                    vpSquare.currentItem = position
                }

                override fun onTabReselect(position: Int) {
                }

            })
            tabSquare.currentTab = 0
            vpSquare.currentItem = 0
        }
    }

    override fun start() {
    }


}