package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import android.graphics.Typeface
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SpanUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityPurchaseFootBinding
import com.sdy.luxurytravelapplication.event.CloseRegVipEvent
import com.sdy.luxurytravelapplication.mvp.contract.PurchaseFootContract
import com.sdy.luxurytravelapplication.mvp.model.bean.ChargeWayBean
import com.sdy.luxurytravelapplication.mvp.model.bean.ChargeWayBeans
import com.sdy.luxurytravelapplication.mvp.model.bean.MoreMatchBean
import com.sdy.luxurytravelapplication.mvp.model.bean.PaywayBean
import com.sdy.luxurytravelapplication.mvp.presenter.PurchaseFootPresenter
import com.sdy.luxurytravelapplication.ui.dialog.ConfirmPayCandyDialog
import com.sdy.luxurytravelapplication.ui.dialog.WhyPayDialog
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivity

/**
 * 购买会员邀请码
 */
class PurchaseFootActivity :
    BaseMvpActivity<PurchaseFootContract.View, PurchaseFootContract.Presenter, ActivityPurchaseFootBinding>(),
    PurchaseFootContract.View {
    private val moreMatchBean by lazy { intent.getSerializableExtra("moreMatchBean") as MoreMatchBean? }
    val from by lazy { intent.getIntExtra("from", FROM_REGISTER_OPEN_VIP) }

    companion object {
        const val FROM_REGISTER_OPEN_VIP = 1//注册开通vip页面
        const val FROM_P2P_CHAT = 3 //详细聊天页面
        fun start(context: Context, moreMatchBean: MoreMatchBean, from: Int) {
            context.startActivity<PurchaseFootActivity>(
                "moreMatchBean" to moreMatchBean,
                "from" to from
            )
        }
    }

    override fun createPresenter(): PurchaseFootContract.Presenter = PurchaseFootPresenter()

    override fun initData() {
        binding.apply {
            ClickUtils.applySingleDebouncing(arrayOf(buyBtn, whyPayBtn)) {
                when (it) {
                    //购买邀请码
                    buyBtn -> {
                        //todo 拉起购买弹窗
                        if (chargeWayBeans.isNotEmpty())
                            ConfirmPayCandyDialog(
                                this@PurchaseFootActivity,
                                chargeWayBeans[0],
                                payways
                            ).show()
                    }

                    //为什么要付费
                    whyPayBtn -> {
                        WhyPayDialog(this@PurchaseFootActivity).show()
                    }
                }
            }
        }
    }

    override fun start() {
        mPresenter?.getThreshold(hashMapOf())
    }


    private var chargeWayBeans: MutableList<ChargeWayBean> = mutableListOf()
    private var payways: MutableList<PaywayBean> = mutableListOf()
    override fun getThresholdResult(chargeWayBeans: ChargeWayBeans) {
        this.chargeWayBeans = chargeWayBeans.list
        setPurchaseType()
        payways.addAll(chargeWayBeans.paylist)
        if (chargeWayBeans.invite_code_residue > 0) {
            binding.buyBtn.isEnabled = true
            binding.buyBtn.text = "购买邀请码"
        } else {
            binding.buyBtn.text = "今日已抢完"
            binding.buyBtn.isEnabled = false
        }
        SpanUtils.with(binding.leftCodeTv)
            .append("${chargeWayBeans.invite_code_residue}")
            .setForegroundColor(resources.getColor(R.color.colorAccent))
            .setTypeface(Typeface.createFromAsset(assets, "DIN_Alternate_Bold.ttf"))
            .append("/")
            .append("${chargeWayBeans.invite_code_all}")
            .setForegroundColor(resources.getColor(R.color.colorAccent))
            .create()


    }

    private fun setPurchaseType() {
        if (chargeWayBeans.isNotEmpty()) {
            SpanUtils.with(binding.footOrignalPrice1)
                .append("¥${chargeWayBeans[0].original_price}")
                .setFontSize(12, true)
                .setBold()
                .setStrikethrough()
                .create()
            binding.footNowPrice.text = "¥${if (chargeWayBeans[0].type == 1) {
                chargeWayBeans[0].original_price
            } else {
                chargeWayBeans[0].discount_price
            }}"
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCloseDialogEvent(event: CloseRegVipEvent) {
        if (from == FROM_REGISTER_OPEN_VIP) {
            if (event.paySuccess) {
                moreMatchBean?.apply {
                    UserManager.savePersonalInfo(avatar, birth, gender, nickname)
                }
                MainActivity.startToMain(this)
            }
        } else {
            finish()
        }

    }
}