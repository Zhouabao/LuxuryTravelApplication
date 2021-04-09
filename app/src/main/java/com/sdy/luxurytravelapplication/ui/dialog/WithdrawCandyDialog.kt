package com.sdy.luxurytravelapplication.ui.dialog

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.DialogWithdrawCandyBinding
import com.sdy.luxurytravelapplication.event.GetAlipayAccountEvent
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.model.bean.PullWithdrawBean
import com.sdy.luxurytravelapplication.ui.activity.BindAlipayAccountActivity
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivity

class WithdrawCandyDialog : BaseBindingDialog<DialogWithdrawCandyBinding>(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        initView()
        pullWithdraw()
    }

    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.BOTTOM)
        val params = window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        params?.windowAnimations = R.style.MyDialogBottomAnimation

        window?.attributes = params
        setCanceledOnTouchOutside(true)
    }


    private fun initView() {
        binding.apply {

            inputWithdrawMoney.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (!s.isNullOrEmpty())
                        if (s.toString().toFloat() > pullWithdrawBean?.money_amount ?: 0F) {
                            ToastUtil.toast(
                                context.getString(
                                    R.string.withdraw_max,
                                    pullWithdrawBean?.money_amount ?: 0F
                                )
                            )
                            inputWithdrawMoney.setText("")
                        }
                    checkWithdrawEnable()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

            })

            withdrawAll.setOnClickListener(this@WithdrawCandyDialog)
            wirteAlipayAcount.setOnClickListener(this@WithdrawCandyDialog)
            confirmWithdraw.setOnClickListener(this@WithdrawCandyDialog)
            successBtn.setOnClickListener(this@WithdrawCandyDialog)
        }
    }


    fun checkWithdrawEnable() {
        binding.apply {
            confirmWithdraw.isEnabled = (pullWithdrawBean?.money_amount ?: 0F > 0)
                    && !inputWithdrawMoney.text.trim().isNullOrEmpty()
                    && !wirteAlipayAcount.text.trim().isNullOrEmpty()
                    &&
                    if (inputWithdrawMoney.text.isNullOrEmpty()) {
                        0F
                    } else {
                        inputWithdrawMoney.text.toString().toFloat()
                    } <= (pullWithdrawBean?.money_amount ?: 0F)
        }
    }

    override fun show() {
        super.show()
        EventBus.getDefault().register(this)
    }

    override fun dismiss() {
        super.dismiss()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGetAlipayAccountEvent(event: GetAlipayAccountEvent) {
        pullWithdrawBean?.alipay = event.account
        binding.wirteAlipayAcount.text = event.account.ali_account
        checkWithdrawEnable()
    }

    override fun onClick(v: View) {
        binding.apply {
            when (v.id) {
                R.id.withdrawAll -> {//全部提现
                    inputWithdrawMoney.setText(
                        "${pullWithdrawBean?.money_amount}"
                    )
                    inputWithdrawMoney.setSelection(inputWithdrawMoney.text.length)
                }
                R.id.wirteAlipayAcount -> {//填写支付宝账户
                    context.startActivity<BindAlipayAccountActivity>("alipay" to pullWithdrawBean?.alipay)
                }
                R.id.successBtn -> {//提现成功
                    dismiss()
                }
                R.id.confirmWithdraw -> {//确认提现
                    withdraw()
                }
            }
        }
    }

    private var pullWithdrawBean: PullWithdrawBean? = null

    /**
     * 拉起提现
     */
    fun pullWithdraw() {
        RetrofitHelper.service
            .myCadny(hashMapOf())
            .ssss { t ->
                if (t.code == 200) {
                    pullWithdrawBean = t.data
                    binding.withdrawMoney.text = context.getString(
                        R.string.can_withdraw,
                        pullWithdrawBean?.money_amount
                    )
                    if (pullWithdrawBean?.alipay != null)
                        binding.wirteAlipayAcount.text = pullWithdrawBean?.alipay?.ali_account
                    checkWithdrawEnable()
                }
            }

    }


    /**
     * 提现
     */
    fun withdraw() {
        binding.apply {
            val params = hashMapOf<String, Any>(
                "amount" to inputWithdrawMoney.text.toString().toFloat()
            )
            RetrofitHelper.service
                .withdraw(params)
                .ssss { t ->
                    if (t.code == 200) {
                        withdrawCl.isVisible = false
                        withdrawSuccessCl.isVisible = true
                        withdrawID.text = "${t.data?.trade_no}"
                        withdrawTime.text = "${t.data?.create_tme}"
                        withdrawCandy.text = t.data?.candy_amount.toString() + context.getString(
                            R.string.count_unit
                        )
                        withdrawMoney1.text = "¥${t.data?.money_amount}"
                    } else {
                        ToastUtil.toast(t.msg)
                    }
                }
        }

    }
}
