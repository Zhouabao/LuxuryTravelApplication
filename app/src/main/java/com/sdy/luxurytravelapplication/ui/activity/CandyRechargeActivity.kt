package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import android.graphics.Typeface
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityCandyRechargeBinding
import com.sdy.luxurytravelapplication.event.CloseDialogEvent
import com.sdy.luxurytravelapplication.mvp.contract.CandyRechargeContract
import com.sdy.luxurytravelapplication.mvp.model.bean.ChargeWayBeans
import com.sdy.luxurytravelapplication.mvp.model.bean.PaywayBean
import com.sdy.luxurytravelapplication.mvp.presenter.CandyRechargePresenter
import com.sdy.luxurytravelapplication.ui.adapter.CandyPriceAdapter
import com.sdy.luxurytravelapplication.ui.dialog.ConfirmPayCandyDialog
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivity

/**
 *  * 糖果充值页面
 */
class CandyRechargeActivity :
    BaseMvpActivity<CandyRechargeContract.View, CandyRechargeContract.Presenter, ActivityCandyRechargeBinding>(),
    CandyRechargeContract.View {
    companion object {
        fun gotoCandyRecharge(context: Context) {
            context.startActivity<CandyRechargeActivity>()
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
            barCl.actionbarTitle.text = "充值旅券"
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

        }
    }

    override fun start() {
        mPresenter?.giftRechargeList()
    }

    override fun giftRechargeList(data: ChargeWayBeans?) {
        if (data != null) {
            if (!data.list.isNullOrEmpty()) {
                data.list[0].checked = true
            }
            binding.candyUse.text = data.descr
            binding.candyAmount.text = "${data.mycandy_amount}"
            candyPriceAdapter.addData(data.list ?: mutableListOf())
            payments.addAll(data.paylist ?: mutableListOf())
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCloseDialogEvent(event: CloseDialogEvent) {
        finish()
    }



}