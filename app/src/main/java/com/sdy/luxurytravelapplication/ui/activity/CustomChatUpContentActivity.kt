package com.sdy.luxurytravelapplication.ui.activity

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.kongzue.dialog.v3.TipDialog
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityCustomChatUpContentBinding
import com.sdy.luxurytravelapplication.mvp.contract.CustomChatUpContentContract
import com.sdy.luxurytravelapplication.mvp.presenter.CustomChatUpContentPresenter

/**
 * 自定义搭讪语
 */
class CustomChatUpContentActivity :
    BaseMvpActivity<CustomChatUpContentContract.View, CustomChatUpContentContract.Presenter, ActivityCustomChatUpContentBinding>(),
    CustomChatUpContentContract.View, View.OnClickListener {

    override fun createPresenter(): CustomChatUpContentContract.Presenter {
        return CustomChatUpContentPresenter()
    }

    override fun initData() {

        binding.apply {
            barCl.actionbarTitle.text = getString(R.string.custom_chatup)
            ClickUtils.applySingleDebouncing(
                arrayOf(barCl.btnBack, barCl.rightTextBtn),
                this@CustomChatUpContentActivity
            )
            barCl.rightTextBtn.isVisible = true
            barCl.rightTextBtn.setBackgroundResource(R.drawable.selector_button_14dp)
            barCl.rightTextBtn.text = getString(R.string.save)
            barCl.rightTextBtn.isEnabled = false


            chatupEt.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    checkSaveEnable()
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
            })

        }
    }

    override fun start() {
    }


    fun checkSaveEnable() {
        binding.barCl.rightTextBtn.isEnabled = binding.chatupEt.text.trim().isNotEmpty()
    }


    override fun onClick(view: View) {
        when (view) {
            binding.barCl.btnBack -> {
                finish()
            }
            binding.barCl.rightTextBtn -> {
                mPresenter?.saveChatupMsg(binding.chatupEt.text.trim().toString())
                KeyboardUtils.hideSoftInputByToggle(this)
            }

        }
    }

    override fun onSaveChatupMsg(success: Boolean, msg: String) {
        if (success) {
            TipDialog.show(this, "保存成功", TipDialog.TYPE.SUCCESS)
        } else {
            binding.chatupErrorTv.isVisible = true
            binding.chatupErrorTv.text = msg
        }
    }
}