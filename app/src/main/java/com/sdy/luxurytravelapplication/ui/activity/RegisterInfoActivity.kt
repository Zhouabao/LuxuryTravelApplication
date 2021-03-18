package com.sdy.luxurytravelapplication.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.LogUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityRegisterInfoBinding
import com.sdy.luxurytravelapplication.mvp.contract.RegisterInfoContract
import com.sdy.luxurytravelapplication.mvp.model.bean.RegisterFileBean
import com.sdy.luxurytravelapplication.mvp.presenter.RegisterInfoPresenter

/**
 * 登录注册页面
 */
class RegisterInfoActivity :
    BaseMvpActivity<RegisterInfoContract.View, RegisterInfoContract.Presenter, ActivityRegisterInfoBinding>(),
    RegisterInfoContract.View, View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun createPresenter(): RegisterInfoContract.Presenter = RegisterInfoPresenter()

    override fun initData() {
        binding.apply {
            ClickUtils.applySingleDebouncing(
                arrayOf(travelAim, travelKeepTime, travelPatener, filterBtn),
                this@RegisterInfoActivity
            )
            nicknameEt.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    checkFiterBtnEnable()
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
        }
    }

    /**
     * 判断按钮是否可以点击
     */
    private fun checkFiterBtnEnable(): Boolean = binding.travelAim.text.isNotEmpty()
            && binding.travelPatener.text.isNotEmpty()
            && binding.travelKeepTime.text.isNotEmpty()
            && binding.nicknameEt.text.isNotEmpty()

    override fun start() {
        mPresenter?.getRegisterProcessType()
    }

    override fun onClick(v: View) {
        when (v) {
            binding.travelAim -> {
            }
            binding.travelPatener -> {
            }
            binding.travelKeepTime -> {
            }
            binding.filterBtn -> {
                //todo 网络请求
                val params = hashMapOf<String, String>()
                mPresenter?.register(params)
            }
        }
    }

    private val travelAims by lazy { arrayOf<String>() }
    private val travelPartners by lazy { arrayOf<String>() }
    private val travelTimes by lazy { arrayOf<String>() }
    private val params by lazy { hashMapOf<String, Any>() }

    private fun showPickerView(
        textview: TextView,
        data: Array<String>,
        title: String
    ) {
        val pvOptions = OptionsPickerBuilder(this) { options1, options2, options3, v ->
            textview.text = data[options1]
            checkFiterBtnEnable()
        }
            .setTitleText(title)
            .setTitleColor(R.color.color_333333)
            .setOutSideCancelable(true)
            .setSubmitColor(R.color.colorAccent)
            .setCancelColor(R.color.color_c6cad4)
            .setTitleSize(16)
            .setTitleBgColor(Color.WHITE)
            .build<String>()
        pvOptions.setPicker(data.asList())
        pvOptions.show()


    }

    override fun registerSuccess(success: Boolean) {


    }

    override fun registerFail() {

    }

    override fun getRegisterProcessType(data: RegisterFileBean) {
        LogUtils.d(data)

    }
}