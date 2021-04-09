package com.sdy.luxurytravelapplication.ui.activity

import android.graphics.Color
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.RegexUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityBindAlipayAccountBinding
import com.sdy.luxurytravelapplication.event.GetAlipayAccountEvent
import com.sdy.luxurytravelapplication.mvp.contract.BindAlipayAccountContract
import com.sdy.luxurytravelapplication.mvp.model.bean.Alipay
import com.sdy.luxurytravelapplication.mvp.presenter.BindAlipayAccountPresenter
import org.greenrobot.eventbus.EventBus

/**
 * 绑定支付宝
 */
class BindAlipayAccountActivity :
    BaseMvpActivity<BindAlipayAccountContract.View, BindAlipayAccountContract.Presenter, ActivityBindAlipayAccountBinding>(),
    BindAlipayAccountContract.View, TextWatcher {
    override fun createPresenter(): BindAlipayAccountContract.Presenter {
        return BindAlipayAccountPresenter()
    }

    private val alipay by lazy { intent.getSerializableExtra("alipay") as Alipay? }

    override fun initData() {
        binding.apply {
            barCl.btnBack.setOnClickListener {
                finish()
            }
            barCl.actionbarTitle.text = getString(R.string.alipay_bind)
            barCl.rightTextBtn.isVisible = true
            barCl.rightTextBtn.text = getString(R.string.save)
            barCl.rightTextBtn.setTextColor(Color.WHITE)
            barCl.rightTextBtn.setBackgroundResource(R.drawable.selector_button_14dp)
            barCl.rightTextBtn.isEnabled = alipay!=null

            etTelephone.filters = arrayOf(InputFilter.LengthFilter(11))

            etAlipayAccount.addTextChangedListener(this@BindAlipayAccountActivity)
            etAlipayName.addTextChangedListener(this@BindAlipayAccountActivity)
            etTelephone.addTextChangedListener(this@BindAlipayAccountActivity)

            ClickUtils.applySingleDebouncing(barCl.rightTextBtn) {
                val params = hashMapOf<String, Any>(
                    "ali_account" to etAlipayAccount.text.trim().toString(),
                    "nickname" to etAlipayName.text.toString(),
                    "phone" to etTelephone.text.toString()
                )
                mPresenter?.saveWithdrawAccount(params)
            }

            if (alipay != null) {
                etAlipayAccount.setText(alipay?.ali_account)
                etAlipayAccount.setSelection((alipay?.ali_account ?: "").length)
                etAlipayName.setText(alipay?.nickname)
                etAlipayName.setSelection((alipay?.nickname ?: "").length)
                etTelephone.setText(alipay?.phone)
                etTelephone.setSelection((alipay?.phone ?: "").length)
                checkConfirm()
            }
        }
    }

    private fun checkConfirm() {
        binding.apply {
            barCl.rightTextBtn.isEnabled = etAlipayAccount.text.trim().isNotEmpty()
                    && !RegexUtils.isZh(etAlipayAccount.text.trim().toString())
                    && etAlipayName.text.trim().isNotEmpty()
                    && (etTelephone.text.trim().isNotEmpty() && etTelephone.text.length == 11)

        }
    }

    override fun start() {
    }

    override fun afterTextChanged(s: Editable?) {
        checkConfirm()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }


    override fun onPause() {
        super.onPause()
        KeyboardUtils.hideSoftInput(binding.etAlipayAccount)
    }

    override fun onResume() {
        super.onResume()
        binding.etAlipayAccount.postDelayed(
            { KeyboardUtils.showSoftInput(binding.etAlipayAccount) },
            200L
        )
    }

    override fun saveWithdrawAccountResult(success: Boolean, data: Alipay?) {
        if (success) {
            EventBus.getDefault().post(GetAlipayAccountEvent(data!!))
            finish()
        }
    }
}