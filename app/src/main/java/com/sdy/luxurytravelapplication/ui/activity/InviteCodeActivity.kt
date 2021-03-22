package com.sdy.luxurytravelapplication.ui.activity

import android.view.View
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.jyn.vcview.VerificationCodeView
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.databinding.ActivityInviteCodeBinding
import org.jetbrains.anko.startActivity

/**
 * 请输入验证码
 */
class InviteCodeActivity : BaseActivity<ActivityInviteCodeBinding>(),
    VerificationCodeView.OnCodeFinishListener {

    override fun initData() {

    }

    override fun initView() {
        ClickUtils.applySingleDebouncing(arrayOf(binding.nextBtn, binding.noInviteCodeBtn)) {
            when (it) {
                binding.nextBtn -> {
                    MainActivity.startToMain(this,true)
                }
                binding.noInviteCodeBtn -> {
                    startActivity<PurchaseFootActivity>()
                }
            }
        }

        binding.inviteCodeView.onCodeFinishListener = this

    }

    override fun onResume() {
        super.onResume()
        KeyboardUtils.showSoftInput(binding.inviteCodeView.getChildAt(0))
    }

    override fun start() {
    }

    override fun onComplete(view: View?, content: String?) {

    }

    override fun onTextChange(view: View?, content: String) {
        binding.nextBtn.isEnabled = content.length == 4
    }
}