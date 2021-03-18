package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.databinding.ActivityPhoneBinding
import org.jetbrains.anko.startActivity

/**
 * 电话号码页面
 */
class PhoneActivity : BaseActivity<ActivityPhoneBinding>() {

    private val openid by lazy { intent.getStringExtra("openid") }
    private val nickname by lazy { intent.getStringExtra("nickname") }
    private val type by lazy { intent.getIntExtra("type", VerifycodeActivity.TYPE_LOGIN_PHONE) }

    companion object {
        @JvmOverloads
        fun startToPhone(
            context: Context,
            type: Int = VerifycodeActivity.TYPE_LOGIN_PHONE,
            openid: String = "",
            nickname: String = ""
        ) {
            context.startActivity<PhoneActivity>(
                "openid" to openid,
                "type" to type,
                "nickname" to nickname
            )
        }
    }

    override fun initData() {

    }

    override fun initView() {
        binding.apply {
            phoneEt.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (phoneCodeBtn.text == "+86") {
                        nextBtn.isEnabled = s?.length == 11
                        if (s?.length == 11) {
                            KeyboardUtils.hideSoftInput(this@PhoneActivity)
                        }

                    } else {
                        nextBtn.isEnabled = s?.length ?: 0 > 0
                    }
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
            ClickUtils.applySingleDebouncing(nextBtn) {
                VerifycodeActivity.startToVerifyCode(
                    this@PhoneActivity,
                    binding.phoneEt.text.toString(), openid?:"", type, nickname?:""
                )
            }
        }

    }

    override fun start() {

    }


}