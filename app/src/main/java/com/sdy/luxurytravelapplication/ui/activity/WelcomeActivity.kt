package com.sdy.luxurytravelapplication.ui.activity

import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PermissionUtils
import com.chuanglan.shanyan_sdk.OneKeyLoginManager
import com.sdy.luxurytravelapplication.app.TravelApp
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityWelcomeBinding
import com.sdy.luxurytravelapplication.ui.dialog.PrivacyDialog
import com.sdy.luxurytravelapplication.utils.AMapManager

/**
 * 启动页面
 */
class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>() {


    override fun initData() {

    }
    private val privacyDialog by lazy {
        PrivacyDialog()
    }
    override fun initView() {

        if (UserManager.alertProtocol) {
            initLogin()
        } else {
            privacyDialog.show()
            privacyDialog.binding.agree.setOnClickListener {
                privacyDialog.dismiss()
                UserManager.alertProtocol = true
                initLogin()
            }
        }
    }

    override fun start() {
    }




    private fun initLogin() {
        PermissionUtils.permissionGroup(
            PermissionConstants.LOCATION,
            PermissionConstants.STORAGE,
            PermissionConstants.PHONE
        )
            .callback { isAllGranted, granted, deniedForever, denied ->
                startToLogin()
            }
            .rationale { activity, shouldRequest -> shouldRequest.again(true) }
            .request()
    }

    private fun startToLogin() {
        //首先获取一次定位
        AMapManager.initLocation(TravelApp.context)

        //闪验预取号
        OneKeyLoginManager.getInstance().getPhoneInfo { code, result ->
            binding.root.postDelayed({
                LogUtils.d("$code,$result")
                LoginActivity.startToLogin(this, code)
                finish()
            }, 1000L)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AMapManager.destory()
    }
}