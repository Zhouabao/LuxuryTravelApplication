package com.sdy.luxurytravelapplication.ui.activity

import android.app.Activity
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.google.gson.Gson
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityRegisterInfoTwoBinding
import com.sdy.luxurytravelapplication.mvp.contract.RegisterInfoTwoContract
import com.sdy.luxurytravelapplication.mvp.model.bean.AnswerBean
import com.sdy.luxurytravelapplication.mvp.model.bean.FindTagBean
import com.sdy.luxurytravelapplication.mvp.model.bean.MoreMatchBean
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

    private val wants = arrayListOf(0,0,0)
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
                    0
                )
            }
            binding.travelPatener -> {
                showPickerView(
                    binding.travelPatener,
                    travelAims[1].child,
                    travelAims[1].title,
                    1
                )
            }
            binding.travelKeepTime -> {
                showPickerView(
                    binding.travelKeepTime,
                    travelAims[2].child,
                    travelAims[2].title,
                    2
                )
            }
            binding.nextBtn -> {
                params["nickname"] = binding.nicknameEt.text
                params["find_id"] = Gson().toJson(wants)
                mPresenter?.setPersonal(params)
            }
        }
    }

    private val travelAims by lazy { arrayListOf<AnswerBean>() }
    private val params by lazy { hashMapOf<String, Any>() }

    private fun showPickerView(
        textview: TextView,
        data: MutableList<FindTagBean>,
        title: String,
        index: Int
    ) {
        KeyboardUtils.hideSoftInput(this)
        val pvOptions = OptionsPickerBuilder(this) { options1, _, _, _ ->
            textview.text = data[options1].title
            wants[index] = data[options1].id
            checkConfirmEnable()
        }
            .setTitleText(title)
            .setTitleColor(resources.getColor(R.color.color_333333))
            .setOutSideCancelable(true)
            .setSubmitColor(resources.getColor(R.color.colorAccent))
            .setCancelColor(resources.getColor(R.color.color_c6cad4))
            .setTitleSize(16)
            .setTitleBgColor(Color.WHITE)
            .build<FindTagBean>()
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

    override fun getRegisterProcessType(data: MutableList<AnswerBean>) {
        if (data.isNotEmpty()) {
            travelAims.addAll(data)
            binding.apply {
                travelAim.hint = data[0].title
                travelPatener.hint = data[1].title
                travelKeepTime.hint = data[2].title
            }
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


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return keyCode == KeyEvent.KEYCODE_BACK
    }
}