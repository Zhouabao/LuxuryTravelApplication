package com.sdy.luxurytravelapplication.ui.activity

import android.util.Log
import com.chuanglan.shanyan_sdk.OneKeyLoginManager
import com.netease.nimlib.sdk.auth.LoginInfo
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityOnekeyLoginBinding
import com.sdy.luxurytravelapplication.mvp.contract.OnekeyLoginContract
import com.sdy.luxurytravelapplication.mvp.model.bean.LoginBean
import com.sdy.luxurytravelapplication.mvp.presenter.OnekeyLoginPresenter
import com.sdy.luxurytravelapplication.utils.ConfigUtils
import com.sdy.luxurytravelapplication.utils.ToastUtil
import org.json.JSONObject
import java.lang.ref.WeakReference

/**
 * 一键登录
 *
 */
class OnekeyLoginActivity :
    BaseMvpActivity<OnekeyLoginContract.View, OnekeyLoginContract.Presenter, ActivityOnekeyLoginBinding>(),
    OnekeyLoginContract.View {
    override fun createPresenter(): OnekeyLoginContract.Presenter = OnekeyLoginPresenter()
    private var syCode = 0

    companion object {
        public var weakrefrece: WeakReference<OnekeyLoginActivity>? = null
    }


    override fun initData() {
        syCode = intent.getIntExtra("syCode", 0)
        weakrefrece = WeakReference(this)


        //判断是否有登录
        if (UserManager.token.isNotEmpty()) {//token不为空说明登录过
            if (UserManager.isUserInfoMade()) {//是否填写过用户信息
                MainActivity.startToMain(this)
                finish()
            } else {
                UserManager.clearLoginData()
                //                startActivity<SetInfoActivity>()
            }
        }

        OneKeyLoginManager.getInstance().setAuthThemeConfig(ConfigUtils.getCJSConfig(this))
        openLoginActivity()
    }


    private fun openLoginActivity() {
        //拉起授权页方法
        OneKeyLoginManager.getInstance().openLoginAuth(false,
            //getOpenLoginAuthStatus
            { code, result ->
                when (code) {
                    1000 -> {
                        //拉起授权页成功
                        Log.e(
                            "VVV",
                            "拉起授权页成功： _code==$code   _result==$result"
                        )
                    }
                    else -> {
                        //拉起授权页失败
                        PhoneActivity.startToPhone(this)
                        Log.e(
                            "VVV",
                            "拉起授权页失败： _code==$code   _result==$result"
                        )
                    }

                }

            },
            //OneKeyLoginListener
            { code, result ->
                when (code) {
                    1011 -> {//点击返回，用户取消免密登录
                        OneKeyLoginManager.getInstance().finishAuthActivity()
                        OneKeyLoginManager.getInstance().removeAllListener()
                        finish()
                    }
                    1000 -> {//一键登录成功，解析result，可得到网络请求参数
                        Log.e("VVV", "用户点击登录获取token成功： _code==$code   _result==$result")
                        mPresenter?.loginOrAlloc(
                            hashMapOf(
                                "flash_token" to JSONObject(result).optString("token"),
                                "type" to VerifycodeActivity.TYPE_LOGIN_SY
                            )
                        )

                    }
                    else -> {
                        Log.e("VVV", "用户点击登录获取token失败： _code==$code   _result==$result")
                    }
                }

            })


    }

    override fun start() {
    }

    private var data: LoginBean? = null
    override fun loginOrAllocResult(data: LoginBean?, code: Int) {
        if (code == 200) {
            this.data = data
            mPresenter?.loginIM(LoginInfo(data!!.accid, data.extra_data.im_token))
        } else if (code == 401) {
            RegisterTooManyActivity.start(data?.countdown_time ?: 0, this)
            OneKeyLoginManager.getInstance().setLoadingVisibility(false)
        } else {
            OneKeyLoginManager.getInstance().setLoadingVisibility(false)
        }
    }

    override fun loginIM(info: LoginInfo?, success: Boolean) {
        if (success) {
            UserManager.startToPersonalInfoActivity(this, info, data!!)
            OneKeyLoginManager.getInstance().setLoadingVisibility(false)
            OneKeyLoginManager.getInstance().finishAuthActivity()
            OneKeyLoginManager.getInstance().removeAllListener()
            finish()
        } else {
            ToastUtil.toast(resources.getString(R.string.login_error))
            OneKeyLoginManager.getInstance().setLoadingVisibility(false)
        }
    }

}