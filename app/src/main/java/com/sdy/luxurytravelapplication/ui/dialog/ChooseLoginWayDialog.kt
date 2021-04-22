package com.sdy.luxurytravelapplication.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.NetworkUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.DialogChooseLoginWayBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.ui.activity.OnekeyLoginActivity
import com.sdy.luxurytravelapplication.ui.activity.PhoneActivity
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog
import com.umeng.socialize.bean.SHARE_MEDIA
import org.jetbrains.anko.startActivity

/**
 *    author : ZFM
 *    date   : 2021/3/1911:22
 *    desc   :
 *    version: 1.0
 */
class ChooseLoginWayDialog(context: Context, val syCode: Int = 0) :
    BaseBindingDialog<DialogChooseLoginWayBinding>(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        initView()
    }

    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.BOTTOM)
        val params = window?.attributes
//        params?.width = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(15F) * 2
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT

        params?.windowAnimations = R.style.MyDialogBottomAnimation
//        params?.y = SizeUtils.dp2px(15F)
        window?.attributes = params
        //点击外部可取消
        setCanceledOnTouchOutside(true)
    }


    private fun initView() {

        //手机号码登录
        ClickUtils.applySingleDebouncing(binding.loginWithPhoneBtn) {
            if (syCode == 1022) {
                if (!NetworkUtils.getMobileDataEnabled()) {
                    ToastUtil.toast(context.getString(R.string.network_is_not_available))
                    return@applySingleDebouncing
                }
                context.startActivity<OnekeyLoginActivity>()
            } else {
                PhoneActivity.startToPhone(context)
            }
            dismiss()
        }

        //微信登录
        ClickUtils.applySingleDebouncing(binding.loginWithWechatBtn) {
            CommonFunction.socialLogin(context, SHARE_MEDIA.WEIXIN)
            dismiss()
        }

        //QQ登录
        ClickUtils.applySingleDebouncing(binding.loginWithQQBtn) {
            CommonFunction.socialLogin(context, SHARE_MEDIA.QQ)
            dismiss()
        }



        if (syCode == 1022) {
            binding.loginWithPhoneBtn.text = context.getString(R.string.one_key_login)
        } else {
            binding.loginWithPhoneBtn.text = context.getString(R.string.phone_number)
        }


    }
}