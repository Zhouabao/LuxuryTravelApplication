package com.sdy.luxurytravelapplication.ui.activity

import android.app.Activity
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityChangeUserContactBinding
import com.sdy.luxurytravelapplication.event.UserCenterContactEvent
import com.sdy.luxurytravelapplication.mvp.contract.ChangeUserContactContract
import com.sdy.luxurytravelapplication.mvp.model.bean.ContactWayBean
import com.sdy.luxurytravelapplication.mvp.presenter.ChangeUserContactPresenter
import com.sdy.luxurytravelapplication.utils.ToastUtil
import org.greenrobot.eventbus.EventBus

/**
 * 更改用户联系方式
 */
class ChangeUserContactActivity :
    BaseMvpActivity<ChangeUserContactContract.View, ChangeUserContactContract.Presenter, ActivityChangeUserContactBinding>(),
    ChangeUserContactContract.View,
    View.OnClickListener {

    override fun createPresenter(): ChangeUserContactContract.Presenter {
        return ChangeUserContactPresenter()
    }

    override fun initData() {
        binding.apply {

            barCl.rightTextBtn.isVisible = true
            barCl.rightTextBtn.text = getString(R.string.save)
            barCl.rightTextBtn.setTextColor(Color.WHITE)
            barCl.rightTextBtn.setBackgroundResource(R.drawable.selector_button_14dp)
            barCl.actionbarTitle.text = getString(R.string.contact_title)

            ClickUtils.applySingleDebouncing(arrayOf(contactImg,contactImgMore,barCl.btnBack,barCl.rightTextBtn),this@ChangeUserContactActivity)

            contactEt.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    barCl.rightTextBtn.isEnabled = s.toString().isNotEmpty()
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
        mPresenter?.getContact()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.contactImgMore, R.id.contactImg -> {
                KeyboardUtils.hideSoftInput(this)
                showContactPicker()
            }
            R.id.rightTextBtn -> {
                mPresenter?.setContact(
                    contactWay,
                    binding.contactEt.text.trim().toString(),
                    //	是否显示 1显示 2隐藏
                    if (binding.switchShowContact.isChecked) {
                        2
                    } else {
                        1
                    }
                )
            }
            R.id.btnBack -> {
                finish()
            }
        }
    }

    /**
     * 展示联系方式
     * 1 电话 2 微信 3 qq 99隐藏
     */
    private var contactWay = 1 //1 电话 2 微信 3 qq 99隐藏
    private val contactWays by lazy {
        mutableListOf(
            getString(R.string.contact_phone),
            getString(R.string.contact_wechat),
            getString(R.string.contact_QQ)
        )
    }
    private val contactWaysIcon by lazy {

        mutableListOf(
            R.drawable.icon_contact_phone,
            R.drawable.icon_contact_wechat,
            R.drawable.icon_contact_qq
        )
    }

    private fun showContactPicker() {
        //条件选择器
        val pvOptions = OptionsPickerBuilder(this,
            OnOptionsSelectListener { options1, options2, options3, v ->
                binding.contactImg.setImageResource(contactWaysIcon[options1])
                contactWay = options1 + 1
            })
            .setSubmitText(getString(R.string.ok))
            .setTitleText(getString(R.string.contact_picker_title))
            .setTitleColor(resources.getColor(R.color.colorBlack))
            .setTitleSize(16)
            .setDividerColor(resources.getColor(R.color.colorDivider))
            .setContentTextSize(20)
            .setDecorView(window.decorView.findViewById(android.R.id.content) as ViewGroup)
            .setSubmitColor(resources.getColor(R.color.colorAccent))
            .build<String>()

        pvOptions.setPicker(contactWays)
        pvOptions.setSelectOptions(1)
        pvOptions.show()
    }

    override fun onGetContactResult(data: ContactWayBean?) {
        if (data != null) {
            binding.apply {
                //	1 显示 2隐藏
                if (data.contact_way != 0 && data.contact_way != 99) {
                    contactWay = data.contact_way
                    contactImg.setImageResource(contactWaysIcon[data.contact_way - 1])
                    contactEt.setText(data.contact_way_content)
                    contactEt.setSelection(contactEt.text.length)
                }

                //  是隐藏就开，默认不隐藏( 1 显示 2隐藏)
                switchShowContact.isChecked = data.contact_way_hide == 2
                getCandy.isVisible = !data.contact_way_str.isNullOrEmpty()
                getCandy.text = data.contact_way_str
            }
        }

    }

    override fun onSetContactResult(success: Boolean) {
        if (success) {
            ToastUtil.toast(getString(R.string.contact_change_success))
            if (UserManager.gender == 2) {
                EventBus.getDefault().post(UserCenterContactEvent(contactWay))
            }
            setResult(Activity.RESULT_OK, intent.putExtra("contact", contactWay))
            finish()
        }
    }

}