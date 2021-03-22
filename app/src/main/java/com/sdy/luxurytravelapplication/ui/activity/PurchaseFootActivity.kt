package com.sdy.luxurytravelapplication.ui.activity

import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SpanUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityPurchaseFootBinding
import com.sdy.luxurytravelapplication.mvp.contract.PurchaseFootContract
import com.sdy.luxurytravelapplication.mvp.model.bean.ChargeWayBean
import com.sdy.luxurytravelapplication.mvp.model.bean.ChargeWayBeans
import com.sdy.luxurytravelapplication.mvp.model.bean.PaywayBean
import com.sdy.luxurytravelapplication.mvp.presenter.PurchaseFootPresenter
import com.sdy.luxurytravelapplication.ui.dialog.WhyPayDialog

/**
 * 购买会员邀请码
 */
class PurchaseFootActivity :
    BaseMvpActivity<PurchaseFootContract.View, PurchaseFootContract.Presenter, ActivityPurchaseFootBinding>(),
    PurchaseFootContract.View {
    override fun createPresenter(): PurchaseFootContract.Presenter = PurchaseFootPresenter()

    override fun initData() {

        binding.apply {
            ClickUtils.applySingleDebouncing(arrayOf(buyBtn,whyPayBtn)){
                when (it) {
                    //购买邀请码
                    buyBtn->{
                        //todo 拉起购买弹窗

                    }

                    //为什么要付费
                    whyPayBtn->{
                        WhyPayDialog(this@PurchaseFootActivity).show()
                    }
                }
            }
        }
    }

    override fun start() {
    }


    private var chargeWayBeans: MutableList<ChargeWayBean> = mutableListOf()
    private var payways: MutableList<PaywayBean> = mutableListOf()
    override fun getThresholdResult(chargeWayBeans: ChargeWayBeans) {
        this.chargeWayBeans = chargeWayBeans.list ?: mutableListOf()
        setPurchaseType()
        payways.addAll(chargeWayBeans.paylist ?: mutableListOf())
    }

    private fun setPurchaseType() {
        if (chargeWayBeans.isNotEmpty()) {
            SpanUtils.with(binding.footOrignalPrice1)
                .append("¥${chargeWayBeans[0].original_price}")
                .setFontSize(12, true)
                .setBold()
                .setStrikethrough()
                .create()
            binding.footNowPrice.text ="${if (chargeWayBeans[0].type == 1) {
                chargeWayBeans[0].original_price
            } else {
                chargeWayBeans[0].discount_price
            }}"
        }


    }
}