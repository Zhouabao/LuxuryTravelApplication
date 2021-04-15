package com.sdy.luxurytravelapplication.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.TimeUtils
import com.kongzue.dialog.v3.MessageDialog
import com.luck.picture.lib.PictureSelector
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityRegisterInfoOneBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.contract.LoginInfoContract
import com.sdy.luxurytravelapplication.mvp.model.bean.SetPersonalBean
import com.sdy.luxurytravelapplication.mvp.presenter.LoginInfoPresenter
import com.sdy.luxurytravelapplication.utils.RandomUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * 注册个人信息
 */
class RegisterInfoOneActivity :
    BaseMvpActivity<LoginInfoContract.View, LoginInfoContract.Presenter, ActivityRegisterInfoOneBinding>(),
    LoginInfoContract.View, View.OnClickListener {

    companion object {
        const val REQUEST_LOGIN_AVATOR = 1000

    }

    override fun createPresenter(): LoginInfoContract.Presenter = LoginInfoPresenter()

    override fun initData() {
        binding.apply {
            ClickUtils.applySingleDebouncing(
                arrayOf(
                    loginAvator,
                    loginBirthday,
                    loginGender,
                    loginNextBtn
                ), this@RegisterInfoOneActivity
            )
        }
    }

    override fun start() {

    }


    private var showGenderTip = false
    private val params by lazy { hashMapOf<String, Any>() }
    override fun onClick(v: View) {
        when (v) {
            binding.loginAvator -> {
                CommonFunction.onTakePhoto(
                    this,
                    1,
                    REQUEST_LOGIN_AVATOR,
                    cropEnable = true,
                    aspect_ratio_x = 1,
                    aspect_ratio_y = 1
                )
            }
            binding.loginBirthday -> {
                showBirthdayPicker()
            }
            binding.loginGender -> {
                showGenderPicker()
            }
            binding.loginNextBtn -> {
                //如果没有提醒过行呗 就先
                if (!showGenderTip) {
                    MessageDialog.show(
                        this, getString(R.string.notice),
                        getString(R.string.gender_cannot_change), getString(R.string.i_know)
                    ).setOnOkButtonClickListener { baseDialog, v ->
                        showGenderTip = true
                        false
                    }
                } else {
                    // 上传个人信息 跳转到自我介绍
                    val key =
                        "${Constants.FILE_NAME_INDEX}${Constants.AVATOR}${UserManager.accid}/" +
                                "${System.currentTimeMillis()}/${RandomUtils.getRandomString(
                                    16
                                )}"
                    mPresenter?.uploadAvatar(avatarPath, key)
                }

            }
        }
    }

    /**
     * 展示条件选择器
     */
    private val genders by lazy {
        mutableListOf(
            getString(R.string.i_am_male),
            getString(R.string.i_am_female)
        )
    }

    private fun showGenderPicker() {
        //条件选择器
        val pvOptions = OptionsPickerBuilder(this,
            OnOptionsSelectListener { options1, options2, options3, v ->
                //选过性别并且和之前的不一样，就删除
                binding.loginGender.text = genders[options1]
                params["gender"] = options1 + 1
                checkConfirmEnable()
            })
            .setSubmitText(getString(R.string.confirm))
            .setCancelText(getString(R.string.cancel))
            .setTitleText(getString(R.string.please_choose_your_gender))
            .setTitleColor(resources.getColor(R.color.colorEdittext))
            .setTitleSize(16)
            .setContentTextSize(20)
            .setDecorView(window.decorView.findViewById(android.R.id.content) as ViewGroup)
            .setSubmitColor(resources.getColor(R.color.colorAccent))
            .setOutSideCancelable(false)
            .build<String>()
        pvOptions.setPicker(genders)
        pvOptions.show()
    }


    /**
     * 展示日历
     */
    //错误使用案例： startDate.set(2013,1,1);  endDate.set(2020,12,1);
    //正确使用案例： startDate.set(2013,0,1);  endDate.set(2020,11,1);
    private fun showBirthdayPicker() {
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        startDate.set(endDate.get(Calendar.YEAR) - 50, 0, 1)
        endDate.set(
            endDate.get(Calendar.YEAR) - 16, endDate.get(Calendar.MONTH), endDate.get(
                Calendar.DATE
            )
        )
        val clOptions = TimePickerBuilder(this, OnTimeSelectListener { date, v ->
            //            getZodiac
            binding.loginBirthday.text = TimeUtils.date2String(
                date,
                SimpleDateFormat("yyyy/MM/dd")
            )
            params["birth"] = TimeUtils.date2Millis(date) / 1000L
            checkConfirmEnable()
        })
            .setRangDate(startDate, endDate)
            .setDate(endDate)
            .setTitleText(getString(R.string.gender))
            .setTitleColor(Color.BLACK)//标题文字颜色
            .build()
        clOptions.show()
    }


    private var avatarPath = ""
    private fun checkConfirmEnable() {
        binding.loginNextBtn.isEnabled =
            binding.loginBirthday.text.isNotEmpty()
                    && binding.loginGender.text.isNotEmpty()
                    && avatarPath.isNotEmpty()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                //选择照片
                REQUEST_LOGIN_AVATOR -> {
                    if (data != null) {
                        avatarPath = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P
                            && !PictureSelector.obtainMultipleResult(data)[0].androidQToPath.isNullOrEmpty()
                        ) {
                            PictureSelector.obtainMultipleResult(data)[0].androidQToPath
                        } else {
                            PictureSelector.obtainMultipleResult(data)[0].path
                        }
                        GlideUtil.loadAvatorImg(this, avatarPath, binding.loginAvator)
                        checkConfirmEnable()
                    }
                }
            }
        }

    }

    override fun setPersonalResult(setPersonalBean: SetPersonalBean?, success: Boolean) {
        if (success) {
            setPersonalBean.apply {
                UserManager.living_btn = this!!.living_btn
                RegisterInfoTwoActivity.start(this@RegisterInfoOneActivity)
            }
        }

    }

    override fun uploadAvatarResult(filePath: String?, success: Boolean) {
        if (success) {
            params["avatar"] = filePath!!
            mPresenter?.setPersonal(params)
        }
    }
}