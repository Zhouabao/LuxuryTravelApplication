package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import android.view.KeyEvent
import android.view.View
import com.blankj.utilcode.util.ClickUtils
import com.jyn.vcview.VerificationCodeView
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityInviteCodeBinding
import com.sdy.luxurytravelapplication.mvp.contract.InviteCodeContract
import com.sdy.luxurytravelapplication.mvp.model.bean.MoreMatchBean
import com.sdy.luxurytravelapplication.mvp.presenter.InviteCodePresenter
import org.jetbrains.anko.startActivity

/**
 * 请输入验证码
 */
class InviteCodeActivity :
    BaseMvpActivity<InviteCodeContract.View, InviteCodeContract.Presenter, ActivityInviteCodeBinding>(),
    InviteCodeContract.View,
    VerificationCodeView.OnCodeFinishListener {
    private val moreMatchBean by lazy { intent.getSerializableExtra("moreMatchBean") as MoreMatchBean }

    companion object {
        fun start(context: Context, moreMatchBean: MoreMatchBean) {
            context.startActivity<InviteCodeActivity>("moreMatchBean" to moreMatchBean)
        }
    }

    override fun initData() {
        ClickUtils.applySingleDebouncing(arrayOf(binding.nextBtn, binding.noInviteCodeBtn)) {
            when (it) {
                binding.nextBtn -> {
                    moreMatchBean.apply {
                        UserManager.savePersonalInfo(avatar, birth, gender, nickname)
                    }
                    MainActivity.startToMain(this, true)
                }
                binding.noInviteCodeBtn -> {
                    PurchaseFootActivity.start(
                        this,
                        moreMatchBean,
                        PurchaseFootActivity.FROM_REGISTER_OPEN_VIP
                    )
                }
            }
        }

        binding.inviteCodeView.onCodeFinishListener = this

    }


    override fun start() {
    }

    override fun onComplete(view: View?, content: String) {
        mPresenter?.checkCode(content)
    }

    override fun onTextChange(view: View?, content: String) {
        binding.nextBtn.isEnabled = content.length == 4
    }

    override fun createPresenter(): InviteCodeContract.Presenter {
        return InviteCodePresenter()
    }

    override fun checkCode(success: Boolean) {
        MainActivity.startToMain(this)
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return keyCode == KeyEvent.KEYCODE_BACK
    }
}