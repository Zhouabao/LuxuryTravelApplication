package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.flyco.tablayout.listener.OnTabSelectListener
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.databinding.ActivityMyVisitBinding
import com.sdy.luxurytravelapplication.ui.adapter.MainPager2Adapter
import com.sdy.luxurytravelapplication.ui.fragment.MyTodayVisitFragment
import org.jetbrains.anko.startActivity

/**
 *  我的访客
 */
class MyVisitActivity : BaseActivity<ActivityMyVisitBinding>() {
    private val from by lazy { intent.getIntExtra("from", FROM_ME) }

    companion object {
        const val FROM_ME = 2
        const val FROM_TOP_RECOMMEND = 3

        fun start(
            context: Context,
            isVip: Boolean,
            todayCnt: Int,
            all: Int,
            freeShow: Boolean,
            from: Int
        ) {
            context.startActivity<MyVisitActivity>(
                "isVip" to isVip,
                "today" to todayCnt,
                "all" to all,
                "freeShow" to freeShow,
                "from" to from
            )

        }
    }

    override fun initData() {


    }

    //fragment栈管理
    private val mStack = arrayListOf<Fragment>()
    private val titles by lazy {
        if (from == FROM_TOP_RECOMMEND) arrayOf(
            getString(R.string.today_visit) + "\t${intent.getIntExtra("today", 0)}",
            getString(R.string.all_visir) + "\t${intent.getIntExtra("all", 0)}"
        ) else {
            arrayOf(getString(R.string.all_visir))
        }
    }


    override fun initView() {
        binding.apply {
            barCl.actionbarTitle.text = "看过我的"
            barCl.btnBack.setOnClickListener {
                finish()
            }
        }
        initFragment()
    }

    private fun initFragment() {
        if (from == FROM_TOP_RECOMMEND)
            mStack.add(
                MyTodayVisitFragment(
                    intent.getBooleanExtra("freeShow", false),
                    MyTodayVisitFragment.FROM_TODAY, 0,
                    intent.getIntExtra("todayExplosure", 0), 0
                )
            )
        //今日来访
        mStack.add(
            MyTodayVisitFragment(
                intent.getBooleanExtra("freeShow", false),
                MyTodayVisitFragment.FROM_ME,
                intent.getIntExtra("today", 0),
                0,
                intent.getIntExtra("all", 0)
            )
        )
        binding.apply {
            //所有来访
            vpMyVisit.adapter = MainPager2Adapter(this@MyVisitActivity, mStack)
            if (from == FROM_TOP_RECOMMEND) {
                vpMyVisit.offscreenPageLimit = 2
                tabMyVisit.setTabData(titles)
                tabMyVisit.isVisible = true
                tabMyVisit.setOnTabSelectListener(object : OnTabSelectListener {
                    override fun onTabSelect(position: Int) {
                        vpMyVisit.currentItem = position
                    }

                    override fun onTabReselect(position: Int) {
                    }

                })
                tabMyVisit.currentTab = 0
                tabMyVisit.setOnTabSelectListener(object : OnTabSelectListener {
                    override fun onTabSelect(position: Int) {

                    }

                    override fun onTabReselect(position: Int) {
                        vpMyVisit.currentItem = position
                    }

                })
            } else {
                vpMyVisit.offscreenPageLimit = 1
                tabMyVisit.isVisible = false
            }

            vpMyVisit.currentItem = 0

            vpMyVisit.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (tabMyVisit.isVisible)
                        tabMyVisit.currentTab = position
                }
            })
        }
    }

    override fun start() {
    }


}