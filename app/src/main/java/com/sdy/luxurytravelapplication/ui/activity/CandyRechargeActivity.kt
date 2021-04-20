package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import android.graphics.Typeface
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ClickUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityCandyRechargeBinding
import com.sdy.luxurytravelapplication.event.CloseDialogEvent
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.mvp.contract.CandyRechargeContract
import com.sdy.luxurytravelapplication.mvp.model.bean.ChargeWayBeans
import com.sdy.luxurytravelapplication.mvp.model.bean.PaywayBean
import com.sdy.luxurytravelapplication.mvp.model.bean.PullWithdrawBean
import com.sdy.luxurytravelapplication.mvp.presenter.CandyRechargePresenter
import com.sdy.luxurytravelapplication.ui.adapter.CandyPriceAdapter
import com.sdy.luxurytravelapplication.ui.dialog.ConfirmPayCandyDialog
import com.sdy.luxurytravelapplication.ui.dialog.WithdrawCandyDialog
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivity

/**
 *  * 旅券充值页面
 */
class CandyRechargeActivity :
    BaseMvpActivity<CandyRechargeContract.View, CandyRechargeContract.Presenter, ActivityCandyRechargeBinding>(),
    CandyRechargeContract.View {
    private val type by lazy { intent.getIntExtra("type", 1) }

    companion object {
        const val TYPE_MINE = -1
        const val TYPE_CHARGE = 1
        const val TYPE_INVITE = 2
        fun gotoCandyRecharge(context: Context, type: Int = TYPE_CHARGE) {
            context.startActivity<CandyRechargeActivity>("type" to type)
//        RechargeCandyDialog(context).show()
        }
    }

    override fun createPresenter(): CandyRechargeContract.Presenter = CandyRechargePresenter()


    private val candyPriceAdapter by lazy { CandyPriceAdapter() }
    private val payments by lazy { mutableListOf<PaywayBean>() }

    override fun initData() {
        binding.apply {
            barCl.btnBack.setOnClickListener {
                finish()
            }
            when (type) {
                TYPE_CHARGE -> {
                    barCl.actionbarTitle.text = "充值旅券"
                    (view1.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio = "375:203"
                }
                TYPE_MINE -> {
                    barCl.actionbarTitle.text = "我的旅券"
                    barCl.rightTextBtn.text = "交易记录"
                    barCl.rightTextBtn.isVisible = true

                    barCl.rightTextBtn.setTextColor(resources.getColor(R.color.color333))
                    ClickUtils.applySingleDebouncing(barCl.rightTextBtn) {
                        startActivity<CandyRecordActivity>()
                    }
                }

            }
            barCl.divider.isVisible = true
            candyAmount.typeface = Typeface.createFromAsset(assets, "DIN_Alternate_Bold.ttf")
            vipChargeRv.layoutManager =
                LinearLayoutManager(this@CandyRechargeActivity, RecyclerView.VERTICAL, false)
            vipChargeRv.adapter = candyPriceAdapter
            candyPriceAdapter.setOnItemClickListener { _, view, position ->
                ConfirmPayCandyDialog(
                    this@CandyRechargeActivity,
                    candyPriceAdapter.data[position],
                    payments
                ).show()
            }
            ClickUtils.applySingleDebouncing(binding.withdrawBtn){
                WithdrawCandyDialog().show()
            }

        }
    }

    override fun start() {
        mPresenter?.myCadny()
    }

    override fun giftRechargeList(data: ChargeWayBeans?) {
        if (data != null) {
            if (!data.list.isNullOrEmpty()) {
                data.list[0].checked = true
            }
            binding.candyUse.text = data.descr
            binding.candyAmount.text = "${data.mycandy_amount}"
            candyPriceAdapter.addData(data.list)
            payments.addAll(data.paylist)
        }
    }

    private var isWithdraw = false
    var mycandy = 0
    override fun onMyCadnyResult(candyCoun: PullWithdrawBean?) {
        if (candyCoun != null) {
            mycandy = candyCoun.candy_amount
            binding.candyAmount.text = CommonFunction.num2thousand("${candyCoun.candy_amount}")

            candyCoun.is_withdraw = true


            binding.withdrawBtn.isVisible = candyCoun.is_withdraw
            binding.candyUse.isVisible = !candyCoun.is_withdraw
            isWithdraw = candyCoun.is_withdraw
            if (isWithdraw) {
                (binding.view1.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
                    "375:203"
            } else {
                (binding.view1.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
                    "375:157"
            }
        }
        mPresenter?.giftRechargeList()

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCloseDialogEvent(event: CloseDialogEvent) {
        finish()
    }


}