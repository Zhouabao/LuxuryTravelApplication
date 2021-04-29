package com.sdy.luxurytravelapplication.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.blankj.utilcode.util.SpanUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityChangeAccountBinding
import com.sdy.luxurytravelapplication.mvp.contract.ChangeAccountContract
import com.sdy.luxurytravelapplication.mvp.model.bean.LoginOffCauseBeans
import com.sdy.luxurytravelapplication.mvp.presenter.ChangeAccountPresenter
import com.sdy.luxurytravelapplication.ui.dialog.LoginOffDialog
import org.jetbrains.anko.startActivityForResult

/**
 * 变更账号
 */
class ChangeAccountActivity :
    BaseMvpActivity<ChangeAccountContract.View, ChangeAccountContract.Presenter, ActivityChangeAccountBinding>(),
    ChangeAccountContract.View, View.OnClickListener {


    override fun createPresenter(): ChangeAccountContract.Presenter {
        return ChangeAccountPresenter()
    }

    override fun initData() {

        binding.apply {

            binding.barCl.actionbarTitle.text = getString(R.string.account_change_title)
            binding.barCl.btnBack.setOnClickListener(this@ChangeAccountActivity)
            verifycodeBtn.setOnClickListener(this@ChangeAccountActivity)
            confirmChangeBtn.setOnClickListener(this@ChangeAccountActivity)
            countryCodeTv.setOnClickListener(this@ChangeAccountActivity)
            loginOff.setOnClickListener(this@ChangeAccountActivity)

            loginOff.text = SpanUtils.with(loginOff)
                .append(getString(R.string.account_login_offtip1))
                .append(getString(R.string.account_login_offtip2))
                .setForegroundColor(resources.getColor(R.color.colorAccent))
                .append(getString(R.string.account_login_offtip3))
                .create()

            newPhoneEt.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(editable: Editable) {
                    checkConfirmBtnEnable()

                    if (editable.isNotEmpty() && editable.length == 11) {
                        countTimer.onFinish()
                        verifycodeBtn.isEnabled = true
                        verifycodeBtn.text = getString(R.string.get_verify_code)
                        verifycodeBtn.setTextColor(resources.getColor(R.color.colorAccent))
                    } else {
                        verifycodeBtn.isEnabled = false
                        verifycodeBtn.setTextColor(Color.parseColor("#ffc6cad4"))
                        verifycodeBtn.text = getString(R.string.get_verify_code)
                    }

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

            })

            newVerifyCodeEt.addTextChangedListener(
                object : TextWatcher {
                    override fun afterTextChanged(editable: Editable) {
                        checkConfirmBtnEnable()

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

    override fun onClick(view: View) {
        when (view) {
            binding.barCl.btnBack -> {
                finish()
            }
            //获取验证码
            binding.verifycodeBtn -> {
                mPresenter?.sendSms(
                    hashMapOf<String, Any>(
                        "phone" to binding.newPhoneEt.text.toString(),
                        "scene" to "register",
                        "region" to countryCode
                    )
                )
            }
            //确认变更
            binding.confirmChangeBtn -> {
                mPresenter?.changeAccount(
                    hashMapOf<String, Any>(
                        "uni_account" to binding.newPhoneEt.text.toString(),
                        "code" to binding.newVerifyCodeEt.text.toString()
                    )
                )
            }
            //注销账号
            binding.loginOff -> {
                mPresenter?.getCauseList()
            }

            //选择区号
            binding.countryCodeTv -> {
                startActivityForResult<CountryCodeActivity>(1002)
            }
        }
    }

    /** 倒计时60秒，一次1秒 */
    private val countTimer by lazy {
        object : CountDownTimer(60 * 1000, 1000) {
            override fun onFinish() {
                binding.verifycodeBtn.text = getString(R.string.reget_verify_code)
                binding.verifycodeBtn.isEnabled = true
            }

            override fun onTick(p0: Long) {
                binding.verifycodeBtn.text = getString(R.string.seconds_resend, p0 / 1000)
                binding.verifycodeBtn.isEnabled = false
            }

        }
    }

    /**
     * 确认按钮是否可以点击
     */
    fun checkConfirmBtnEnable() {
        binding.apply {
            confirmChangeBtn.isEnabled =

                newPhoneEt.text.isNotEmpty() && newPhoneEt.text.length == 11
                        && newVerifyCodeEt.text.isNotEmpty() && newVerifyCodeEt.text.length == 4
        }
    }

    private var countryCode = 86
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1002) {
            countryCode = data?.getIntExtra("code", 86) ?: 86
            binding.countryCodeTv.text = "+$countryCode"
        }
    }


    override fun onChangeAccountResult(result: Boolean) {
        if (result) {
            setResult(
                Activity.RESULT_OK,
                intent.putExtra("phone", binding.newPhoneEt.text.toString())
            )
            finish()
        }

    }


    override fun onSendSmsResult(result: Boolean) {
        countTimer.start()
    }

    override fun onCauseListResult(result: LoginOffCauseBeans) {
        LoginOffDialog(intent.getStringExtra("phone"), result).show()
    }


}