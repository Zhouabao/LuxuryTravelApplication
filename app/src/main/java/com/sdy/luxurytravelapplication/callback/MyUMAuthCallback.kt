package com.sdy.luxurytravelapplication.callback

import android.content.Context
import android.util.Log
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.chuanglan.shanyan_sdk.OneKeyLoginManager
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.auth.LoginInfo
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.model.bean.LoginBean
import com.sdy.luxurytravelapplication.nim.api.NimUIKit
import com.sdy.luxurytravelapplication.ui.activity.PhoneActivity
import com.sdy.luxurytravelapplication.ui.activity.VerifycodeActivity
import com.sdy.luxurytravelapplication.ui.dialog.LoadingDialog
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.bean.SHARE_MEDIA

/**
 *    author : ZFM
 *    date   : 2021/3/1911:33
 *    desc   :
 *    version: 1.0
 */
class MyUMAuthCallback(val context: Context) : UMAuthListener {
    private var openid = ""
    private var type = -1
    private var nickname = ""

    private val loading by lazy { LoadingDialog() }
    override fun onComplete(p0: SHARE_MEDIA, p1: Int, p2: MutableMap<String, String>?) {
        loading.dismiss()
        LogUtils.d("onComplete===$p0,$p1,$p2")
        openid = p2?.get("uid") ?: ""
        nickname = p2?.get("name") ?: ""
        type = when (p0) {
            SHARE_MEDIA.QQ -> {
                VerifycodeActivity.TYPE_LOGIN_QQ
            }
            SHARE_MEDIA.WEIXIN -> {
                VerifycodeActivity.TYPE_LOGIN_WECHAT
            }
            else -> {
                -1
            }
        }
        loginOrAlloc(openid, nickname, type)
    }

    override fun onCancel(p0: SHARE_MEDIA?, p1: Int) {
        LogUtils.d("onCancel===$p0,$p1")
        loading.dismiss()

    }

    override fun onError(p0: SHARE_MEDIA?, p1: Int, p2: Throwable?) {
        LogUtils.d("onError===$p0,$p1,$p2")
        loading.dismiss()

    }

    override fun onStart(p0: SHARE_MEDIA?) {
        LogUtils.d("onstart===$p0")
        loading.show()

    }


    /**
     * 对比验证码是否正确，正确即登录
     * flash_token[string]	是	闪验时传的token参数
    openid[string]	是	三方登录的唯一id
    nickname
     */
    private fun loginOrAlloc(openid: String = "", nickname: String = "", type: Int) {
        if (!NetworkUtils.isAvailable()) {
            return
        }

        val params = hashMapOf<String, Any>()
        if (openid.isNotEmpty()) {
            params["openid"] = openid
            params["nickname"] = nickname
        }
        params["type"] = type

        RetrofitHelper.service.loginOrAlloc(params).ssss {
            onConfirmVerifyCode(it.code, it.data)
        }
    }


    private var data: LoginBean? = null
    fun onConfirmVerifyCode(code: Int, data: LoginBean?) {
        when (code) {
            202 -> {//首次三方登錄
                if (openid.isNotEmpty() && type != -1)
                    PhoneActivity.startToPhone(context, type, openid, nickname)
            }
            200 -> { //登錄成功
                this.data = data
                loginIM(LoginInfo(data!!.accid, data.extra_data.im_token))
            }
            401 -> {//注冊人數過多

            }
            else -> {
                OneKeyLoginManager.getInstance().setLoadingVisibility(false)
            }
        }
    }


    /**
     * 登录IM
     */
    private fun loginIM(info: LoginInfo) {
        val callback = object : RequestCallback<LoginInfo> {
            override fun onSuccess(param: LoginInfo) {
                onIMLoginResult(param, true)
            }

            override fun onFailed(code: Int) {
                Log.d("OkHttp", "=====$code")
                onIMLoginResult(null, false)
            }

            override fun onException(exception: Throwable?) {
                Log.d("OkHttp", exception.toString())
            }

        }
        NimUIKit.login(info, callback)

    }

    fun onIMLoginResult(loginInfo: LoginInfo?, success: Boolean) {
        if (success && data != null) {
            UserManager.startToPersonalInfoActivity(context, loginInfo, data!!)
            OneKeyLoginManager.getInstance().finishAuthActivity()
            OneKeyLoginManager.getInstance().removeAllListener()
        } else {
            ToastUtil.toast(context.resources.getString(R.string.login_error))
            OneKeyLoginManager.getInstance().setLoadingVisibility(false)
        }
    }
}