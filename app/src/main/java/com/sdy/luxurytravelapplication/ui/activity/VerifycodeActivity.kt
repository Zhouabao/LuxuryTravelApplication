package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import android.os.CountDownTimer
import android.view.View
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.SpanUtils
import com.jyn.vcview.VerificationCodeView
import com.netease.nimlib.sdk.auth.LoginInfo
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityVerifycodeBinding
import com.sdy.luxurytravelapplication.mvp.contract.VerifycodeContract
import com.sdy.luxurytravelapplication.mvp.model.bean.LoginBean
import com.sdy.luxurytravelapplication.mvp.model.bean.RegisterTooManyBean
import com.sdy.luxurytravelapplication.mvp.presenter.VerifycodePresenter
import com.sdy.luxurytravelapplication.ui.dialog.LoginOffSuccessDialog
import com.sdy.luxurytravelapplication.utils.ToastUtil
import org.jetbrains.anko.startActivity

/**
 * 获取验证码
 */
class VerifycodeActivity :
    BaseMvpActivity<VerifycodeContract.View, VerifycodeContract.Presenter, ActivityVerifycodeBinding>(),
    VerifycodeContract.View, VerificationCodeView.OnCodeFinishListener {
    override fun createPresenter(): VerifycodeContract.Presenter = VerifycodePresenter()

    companion object {
        const val TYPE_LOGIN_PHONE = 1 //手机登录
        const val REGISTER_APPLE = 2//苹果
        const val TYPE_LOGIN_SY = 3//闪验登录
        const val TYPE_LOGIN_QQ = 4//QQ登录
        const val TYPE_LOGIN_WECHAT = 5//微信登录
        const val TYPE_LOGIN_SINA = 6//sina登录
        const val TYPE_LOGIN_OFF = 7//注销

        fun startToVerifyCode(
            context: Context,
            phone: String = "",
            wxCode: String = "",
            type: Int = -1,
            nickname: String = ""
        ) {
            context.startActivity<VerifycodeActivity>(
                "phone" to phone,
                "openid" to wxCode,
                "nickname" to nickname,
                "type" to type
            )

        }
    }

    private val phone by lazy { intent.getStringExtra("phone") ?: "" }
    private val openid by lazy { intent.getStringExtra("openid") ?: "" }
    private val nickname by lazy { intent.getStringExtra("nickname") ?: "" }
    private val type by lazy { intent.getIntExtra("type", TYPE_LOGIN_PHONE) }

    private val timer by lazy {
        object : CountDownTimer(60 * 1000, 1000) {
            override fun onFinish() {
                SpanUtils.with(binding.resendBtn)
                    .append(getString(R.string.reget))
                    .setForegroundColor(resources.getColor(R.color.colorAccent))
                    .create()
                binding.resendBtn.isEnabled = true
            }

            override fun onTick(millisUntilFinished: Long) {
                SpanUtils.with(binding.resendBtn)
                    .append(getString(R.string.after_some_seconds, millisUntilFinished / 1000))
                    .append(getString(R.string.re_resend))
                    .create()
                binding.resendBtn.isEnabled = false
            }

        }
    }

    override fun initData() {
        binding.apply {
            verifycodeEt.onCodeFinishListener = this@VerifycodeActivity
            sendVerifcodeTip.text = getString(R.string.verify_code_has_sent, phone)
            ClickUtils.applySingleDebouncing(resendBtn) {
                mPresenter?.sendSms(hashMapOf("phone" to phone, "scene" to "register"))
            }
        }
    }


    override fun start() {
        mPresenter?.sendSms(hashMapOf("phone" to phone, "scene" to "register"))
    }

    override fun sendSms(code: Int, data: RegisterTooManyBean?) {
        if (code == 200) {
            timer.cancel()
            timer.start()
            KeyboardUtils.showSoftInput(binding.verifycodeEt.getChildAt(0))
            binding.verifycodeEt.onFocusChange(binding.verifycodeEt, true)
        } else if (code == 401) {
            RegisterTooManyActivity.start(data?.countdown_time ?: 0, this)

            SpanUtils.with(binding.resendBtn)
                .append(getString(R.string.reget))
                .setForegroundColor(resources.getColor(R.color.colorAccent))
                .setBold()
                .create()
            binding.resendBtn.isEnabled = true
        } else {
            SpanUtils.with(binding.resendBtn)
                .append(getString(R.string.reget))
                .setForegroundColor(resources.getColor(R.color.colorAccent))
                .setBold()
                .create()
            binding.resendBtn.isEnabled = true
        }
    }

    private lateinit var data: LoginBean
    override fun loginOrAllocResult(data: LoginBean?, code: Int, msg: String) {
        if (code == 200) {
            this.data = data!!
            mPresenter?.loginIM(LoginInfo(data.accid, data.extra_data.im_token))
        } else if (code == 401) {
            RegisterTooManyActivity.start(data?.countdown_time ?: 0, this)
        } else {
            ToastUtil.toast(msg)
        }
    }

    override fun cancelAccountResult(success: Boolean) {
        if (success) {
            LoginOffSuccessDialog().show()
        }
    }

    override fun loginIMResult(loginInfo: LoginInfo?, success: Boolean) {
        if (success) {
            binding.verifycodeEt.isEnabled = true
            KeyboardUtils.hideSoftInputByToggle(this)
            UserManager.startToPersonalInfoActivity(this, loginInfo, data)
        } else {
            ToastUtil.toast(getString(R.string.login_fail))
        }
    }


    override fun onComplete(view: View?, content: String) {
        if (type == TYPE_LOGIN_OFF) {//注销
            val params = hashMapOf<String, Any>(
                "uni_account" to phone,
                "code" to content,
                "descr" to intent.getStringExtra("descr")!!
            )
            mPresenter?.cancelAccount(params)
        } else {//登录
            val params = hashMapOf<String, Any>()
            params["uni_account"] = phone
            params["type"] = type
            params["code"] = content
            if (openid.isNotEmpty()) {
                params["openid"] = openid
                params["nickname"] = nickname
            }
            mPresenter?.loginOrAlloc(params)
        }

    }

    override fun onTextChange(view: View?, content: String?) {
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}