package com.sdy.luxurytravelapplication.ui.activity

import android.app.Activity
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityRegisterInfoTwoBinding
import com.sdy.luxurytravelapplication.mvp.contract.RegisterInfoTwoContract
import com.sdy.luxurytravelapplication.mvp.model.bean.MoreMatchBean
import com.sdy.luxurytravelapplication.mvp.model.bean.MyTapsBean
import com.sdy.luxurytravelapplication.mvp.model.bean.SetPersonalBean
import com.sdy.luxurytravelapplication.mvp.presenter.RegisterInfoTwoPresenter
import org.jetbrains.anko.startActivity

/**
 * 登录注册页面
 */
class RegisterInfoTwoActivity :
    BaseMvpActivity<RegisterInfoTwoContract.View, RegisterInfoTwoContract.Presenter, ActivityRegisterInfoTwoBinding>(),
    RegisterInfoTwoContract.View, View.OnClickListener {
    override fun createPresenter(): RegisterInfoTwoContract.Presenter = RegisterInfoTwoPresenter()

    companion object {
        fun start(activity: Activity) {
            activity.startActivity<RegisterInfoTwoActivity>()
        }
    }

    override fun initData() {
        binding.apply {
            ClickUtils.applySingleDebouncing(
                arrayOf(travelAim, travelKeepTime, travelPatener, nextBtn),
                this@RegisterInfoTwoActivity
            )
            nicknameEt.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    checkConfirmEnable()
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


    override fun start() {
        mPresenter?.getRegisterProcessType()
    }

    //    "nickname" to
    override fun onClick(v: View) {
        when (v) {
            binding.travelAim -> {
                showPickerView(
                    binding.travelAim,
                    travelAims[0].child,
                    travelAims[0].title,
                    "shelv_want_one"
                )
            }
            binding.travelPatener -> {
                showPickerView(
                    binding.travelPatener,
                    travelAims[1].child,
                    travelAims[1].title,
                    "shelv_want_two"
                )
            }
            binding.travelKeepTime -> {
                showPickerView(
                    binding.travelKeepTime,
                    travelAims[2].child,
                    travelAims[1].title,
                    "shelv_want_three"
                )
            }
            binding.nextBtn -> {
                params["nickname"] = binding.nicknameEt.text
                mPresenter?.setPersonal(params)
            }
        }
    }

    private val travelAims by lazy { arrayListOf<MyTapsBean>() }
    private val params by lazy { hashMapOf<String, Any>() }

    private fun showPickerView(
        textview: TextView,
        data: ArrayList<MyTapsBean>,
        title: String,
        paramName: String
    ) {
        KeyboardUtils.hideSoftInput(this)
        val pvOptions = OptionsPickerBuilder(this) { options1, _, _, _ ->
            textview.text = data[options1].title
            params[paramName] = data[options1]
            checkConfirmEnable()
        }
            .setTitleText(title)
            .setTitleColor(resources.getColor(R.color.color_333333))
            .setOutSideCancelable(true)
            .setSubmitColor(resources.getColor(R.color.colorAccent))
            .setCancelColor(resources.getColor(R.color.color_c6cad4))
            .setTitleSize(16)
            .setTitleBgColor(Color.WHITE)
            .build<MyTapsBean>()
        pvOptions.setPicker(data)
        pvOptions.show()


    }

    override fun setPersonalSuccess(personalBean: SetPersonalBean) {
        personalBean.apply {
            GetMoreMatchActivity.start(
                this@RegisterInfoTwoActivity,
                MoreMatchBean(
                    nickname,
                    gender,
                    birth,
                    avatar,
                    threshold,
                    living_btn,
                    isvip,
                    people_amount,
                    share_btn
                )
            )
        }
    }

    override fun setPersonalFail() {

    }

    override fun getRegisterProcessType(data: MutableList<MyTapsBean>) {
        if (data.isNotEmpty()) {
            travelAims.addAll(data)
        }

    }

    /**
     * 判断按钮是否可以点击
     */
    private fun checkConfirmEnable() {
        binding.nextBtn.isEnabled = binding.nicknameEt.text.isNotEmpty()
                && binding.travelAim.text.isNotEmpty()
                && binding.travelKeepTime.text.isNotEmpty()
                && binding.travelPatener.text.isNotEmpty()
    }

}