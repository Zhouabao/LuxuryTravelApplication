package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import android.graphics.Color
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.BarUtils
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityVipPowerBinding
import com.sdy.luxurytravelapplication.mvp.contract.VipChargeContract
import com.sdy.luxurytravelapplication.mvp.model.bean.ChargeWayBeans
import com.sdy.luxurytravelapplication.mvp.model.bean.VipPurchaseBean
import com.sdy.luxurytravelapplication.mvp.presenter.VipChargePresenter
import com.sdy.luxurytravelapplication.ui.adapter.VipPowerAdapter
import org.jetbrains.anko.startActivity

/**
 * 会员权益及购买页面
 */
class VipChargeActivity :
    BaseMvpActivity<VipChargeContract.View, VipChargeContract.Presenter, ActivityVipPowerBinding>(),
    VipChargeContract.View {
    companion object {
        fun start(context: Context) {
            context.startActivity<VipChargeActivity>()
        }

    }

    private val adapter by lazy { VipPowerAdapter() }


    override fun createPresenter(): VipChargeContract.Presenter {
        return VipChargePresenter()

    }

    override fun initData() {
        binding.apply {
            BarUtils.addMarginTopEqualStatusBarHeight(backBtn)
            rvVip.offscreenPageLimit = 2
            rvVip.isUserInputEnabled = false
            rvVip.adapter = adapter
            rvVip.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    checkTab(position)
                }
            })
            tabVip.setOnClickListener {
                checkTab(0)
                rvVip.currentItem = 0
            }
            tabPtVip.setOnClickListener {
                checkTab(1)
                rvVip.currentItem = 1
            }
        }
    }

    private fun checkTab(position: Int) {
        binding.apply {
            when (position) {
                0 -> {
                    tabVip.textSize = 16F
                    tabVip.setTextColor(Color.WHITE)
                    tabVip.paint.isFakeBoldText = true
                    tabPtVip.textSize = 14F
                    tabPtVip.setTextColor(Color.parseColor("#FF8A909F"))
                    tabPtVip.paint.isFakeBoldText = false

                }
                1 -> {
                    tabPtVip.textSize = 16F
                    tabPtVip.setTextColor(Color.WHITE)
                    tabPtVip.paint.isFakeBoldText = true
                    tabVip.textSize = 14F
                    tabVip.setTextColor(Color.parseColor("#FF8A909F"))
                    tabVip.paint.isFakeBoldText = false
                }
            }
        }
    }

    override fun start() {
        mPresenter?.getThreshold()
    }

    override fun getChargeDataResult(data: ChargeWayBeans) {
        adapter.addData(
            VipPurchaseBean(
                data.pt_icon_list,
                data.pt_list,
                data.paylist,
                data.isplatinum,
                data.platinum_vip_express,
                data.platinum_save_str
            )
        )
        adapter.addData(
            VipPurchaseBean(
                data.direct_icon_list,
                data.direct_list,
                data.paylist,
                data.isdirect,
                data.direct_vip_express,
                data.direct_save_str
            )
        )
    }


}