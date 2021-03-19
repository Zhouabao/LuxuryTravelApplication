package com.sdy.luxurytravelapplication.ui.activity

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.LogUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityRegisterInfoTwoBinding
import com.sdy.luxurytravelapplication.mvp.contract.RegisterInfoTwoContract
import com.sdy.luxurytravelapplication.mvp.model.bean.RegisterFileBean
import com.sdy.luxurytravelapplication.mvp.model.bean.SetPersonalBean
import com.sdy.luxurytravelapplication.mvp.presenter.RegisterInfoTwoPresenter

/**
 * 登录注册页面
 */
class RegisterInfoTwoActivity :
    BaseMvpActivity<RegisterInfoTwoContract.View, RegisterInfoTwoContract.Presenter, ActivityRegisterInfoTwoBinding>(),
    RegisterInfoTwoContract.View, View.OnClickListener {
    override fun createPresenter(): RegisterInfoTwoContract.Presenter = RegisterInfoTwoPresenter()

    override fun initData() {
        binding.apply {
            ClickUtils.applySingleDebouncing(
                arrayOf(travelAim, travelKeepTime, travelPatener, nextBtn),
                this@RegisterInfoTwoActivity
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
                showPickerView(binding.travelAim, travelAims, "", "")
            }
            binding.travelPatener -> {
                showPickerView(binding.travelPatener, travelPartners, "", "")
            }
            binding.travelKeepTime -> {
                showPickerView(binding.travelKeepTime, travelTimes, "", "")
            }
            binding.nextBtn -> {
                mPresenter?.setPersonal(params)
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
        title: String,
        paramName: String
    ) {
        val pvOptions = OptionsPickerBuilder(this) { options1, _, _, _ ->
            textview.text = data[options1]
            params[paramName] = data[options1]
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

    override fun setPersonalSuccess(personalBean: SetPersonalBean) {

        GetMoreMatchActivity.start(this, personalBean.people_amount)
//        UserManager.savePersonalInfo()
    }

    override fun setPersonalFail() {

    }

    override fun getRegisterProcessType(data: RegisterFileBean) {
        LogUtils.d(data)

    }


    private fun showGenderPicker(
        textview: TextView,
        title: String,
        data: Array<String>,
        paramName: String
    ) {
        //条件选择器
        val pvOptions = OptionsPickerBuilder(this,
            OnOptionsSelectListener { options1, options2, options3, v ->
                //选过性别并且和之前的不一样，就删除
                textview.text = data[options1]
                params[paramName] = options1 + 1
                checkConfirmEnable()
            })
            .setSubmitText(getString(R.string.confirm))
            .setCancelText(getString(R.string.cancel))
            .setTitleText(title)
            .setTitleColor(resources.getColor(R.color.colorEdittext))
            .setTitleSize(16)
            .setContentTextSize(20)
            .setDecorView(window.decorView.findViewById(android.R.id.content) as ViewGroup)
            .setSubmitColor(resources.getColor(R.color.colorAccent))
            .setOutSideCancelable(false)
            .build<String>()
        pvOptions.setPicker(data.toList())
        pvOptions.show()
    }

    private fun checkConfirmEnable() {
        binding.nextBtn.isEnabled = binding.nicknameEt.text.isNotEmpty()
                && binding.travelAim.text.isNotEmpty()
                && binding.travelKeepTime.text.isNotEmpty()
                && binding.travelPatener.text.isNotEmpty()
    }

}