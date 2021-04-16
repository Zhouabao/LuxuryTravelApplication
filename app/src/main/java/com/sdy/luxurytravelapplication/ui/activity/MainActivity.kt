package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ClickUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityMainBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.contract.MainContract
import com.sdy.luxurytravelapplication.mvp.presenter.MainPresenter
import com.sdy.luxurytravelapplication.ui.adapter.MainPager2Adapter
import com.sdy.luxurytravelapplication.ui.fragment.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity

class MainActivity :
    BaseMvpActivity<MainContract.View, MainContract.Presenter, ActivityMainBinding>(),
    MainContract.View, View.OnClickListener {
    companion object {
        fun startToMain(context: Context, clearTop: Boolean = true) {
            if (clearTop)
                context.startActivity(context.intentFor<MainActivity>().clearTask().newTask())
            else
                context.startActivity(context.intentFor<MainActivity>())
            context.startActivity<MainActivity>()
//            ActivityUtils.finishToActivity(MainActivity::class.java,false)
//            ActivityUtils.finishOtherActivities(MainActivity::class.java, false)
        }
    }

    override fun createPresenter(): MainContract.Presenter = MainPresenter()


    private val iconUnselectedIds by lazy {
        arrayOf(
            R.drawable.icon_tab_index,
            R.drawable.icon_tab_find,
            R.drawable.icon_tab_travel,
            R.drawable.icon_tab_message
        )
    }
    private val iconSelectedIds by lazy {
        arrayOf(
            R.drawable.icon_tab_index_checked,
            R.drawable.icon_tab_find_checked,
            R.drawable.icon_tab_travel_checked,
            R.drawable.icon_tab_message_checked
        )
    }
    private val titles by lazy {
        arrayOf(
            getString(R.string.tab_index),
            getString(R.string.tab_find),
            getString(R.string.tab_travel),
            getString(R.string.tab_message),
            getString(R.string.tab_mine)
        )
    }

    private val fragments by lazy {
        ArrayList<Fragment>().apply {
            add(IndexFragment())
            add(FindFragment())
            add(TravelFragment())
            add(MessageFragment())
            add(UserCenterFragment())
        }
    }

    override fun initData() {
        GlideUtil.loadAvatorImg(this, UserManager.avatar, binding.tabMine)

//        CompleteInfoDialog().show()
    }


    override fun initView() {
        binding.apply {
            ClickUtils.applySingleDebouncing(
                arrayOf(indexBtn, findBtn, messageBtn, travelBtn, mineBtn),
                this@MainActivity
            )
            vpMain.apply {
                offscreenPageLimit = fragments.size
                isUserInputEnabled = false
                adapter = MainPager2Adapter(this@MainActivity, fragments)
                currentItem = 0
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        updateTabChecked(position)
                    }
                })

            }
        }

    }

    private var checkedPosition = -1
    private fun updateTabChecked(position: Int, fromVp: Boolean = false) {
        if (checkedPosition == position)
            return
        if (position == 4) {
            (fragments[position] as UserCenterFragment).userCenterVisible = true
            if (!(fragments[position] as UserCenterFragment).appbarTop)
                BarUtils.setStatusBarColor(this, resources.getColor(R.color.colorAccent))
        } else {
            BarUtils.setStatusBarColor(this, resources.getColor(R.color.colorWhite))
        }

        if (!fromVp) {
            binding.vpMain.setCurrentItem(position, false)
        }
        when (position) {
            0 -> {
                binding.tabIndexTv.setTextColor(Color.parseColor("#FF1ED0A7"))
                binding.tabIndex.setImageResource(iconSelectedIds[0])


                binding.tabFindTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabFind.setImageResource(iconUnselectedIds[1])
                binding.tabTravelTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabTravel.setImageResource(iconUnselectedIds[2])
                binding.tabMessageTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabMessage.setImageResource(iconUnselectedIds[3])
                binding.tabMineCheckView.isInvisible = true
                binding.tabMineTv.setTextColor(Color.parseColor("#FFCBD0D7"))


            }
            1 -> {
                binding.tabFindTv.setTextColor(Color.parseColor("#FF1ED0A7"))
                binding.tabFind.setImageResource(iconSelectedIds[1])


                binding.tabIndexTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabIndex.setImageResource(iconUnselectedIds[0])
                binding.tabTravelTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabTravel.setImageResource(iconUnselectedIds[2])
                binding.tabMessageTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabMessage.setImageResource(iconUnselectedIds[3])
                binding.tabMineCheckView.isInvisible = true
                binding.tabMineTv.setTextColor(Color.parseColor("#FFCBD0D7"))
            }
            2 -> {
                binding.tabTravelTv.setTextColor(Color.parseColor("#FF1ED0A7"))
                binding.tabTravel.setImageResource(iconSelectedIds[2])

                binding.tabIndexTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabIndex.setImageResource(iconUnselectedIds[0])
                binding.tabFindTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabFind.setImageResource(iconUnselectedIds[1])
                binding.tabMessageTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabMessage.setImageResource(iconUnselectedIds[3])
                binding.tabMineCheckView.isInvisible = true
                binding.tabMineTv.setTextColor(Color.parseColor("#FFCBD0D7"))
            }
            3 -> {
                binding.tabMessageTv.setTextColor(Color.parseColor("#FF1ED0A7"))
                binding.tabMessage.setImageResource(iconSelectedIds[3])


                binding.tabIndexTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabIndex.setImageResource(iconUnselectedIds[0])
                binding.tabFindTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabFind.setImageResource(iconUnselectedIds[1])
                binding.tabTravelTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabTravel.setImageResource(iconUnselectedIds[2])
                binding.tabMineCheckView.isInvisible = true
                binding.tabMineTv.setTextColor(Color.parseColor("#FFCBD0D7"))
            }
            else -> {
                binding.tabMineTv.setTextColor(Color.parseColor("#FF1ED0A7"))
                binding.tabMineCheckView.isInvisible = false


                binding.tabIndexTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabIndex.setImageResource(iconUnselectedIds[0])
                binding.tabFindTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabFind.setImageResource(iconUnselectedIds[1])
                binding.tabTravelTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabTravel.setImageResource(iconUnselectedIds[2])
                binding.tabMessageTv.setTextColor(Color.parseColor("#FFCBD0D7"))
                binding.tabMessage.setImageResource(iconUnselectedIds[3])

            }
        }

        checkedPosition = position


    }


    override fun start() {
    }

    override fun showLogoutSuccess(success: Boolean) {

    }


    override fun onClick(v: View) {
        when (v) {
            binding.indexBtn -> {
                updateTabChecked(0)
            }
            binding.findBtn -> {
                updateTabChecked(1)

            }
            binding.travelBtn -> {
                updateTabChecked(2)

            }
            binding.messageBtn -> {
                updateTabChecked(3)

            }
            binding.mineBtn -> {
                updateTabChecked(4)
            }
        }
    }

}