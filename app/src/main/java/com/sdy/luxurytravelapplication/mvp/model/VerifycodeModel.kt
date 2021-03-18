package com.sdy.luxurytravelapplication.mvp.model

import android.app.Activity
import android.util.Log
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.auth.LoginInfo
import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.VerifycodeContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.LoginBean
import com.sina.weibo.sdk.web.view.LoadingBar
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/1717:15
 *    desc   :
 *    version: 1.0
 */
class VerifycodeModel : BaseModel(), VerifycodeContract.Model {
    override fun sendSms(params: HashMap<String, Any>): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.sendSms(params)
    }

    override fun loginOrAlloc(params: HashMap<String, Any>): Observable<BaseResp<LoginBean>> {
        return RetrofitHelper.service.loginOrAlloc(params)

    }

    override fun cancelAccount(params: HashMap<String, Any>): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.cancelAccount(params)

    }

    override fun loginIM(): LoginInfo? {
//        NimUIKit.login(info, object : RequestCallback<LoginInfo> {
//            override fun onSuccess(param: LoginInfo) {
//                if (mvi.isDestroyed)
//                    return
//
//                loading.dismiss()
//                mView.onIMLoginResult(param, true)
//            }
//
//            override fun onFailed(code: Int) {
//                loading.dismiss()
//                Log.d("OkHttp", "=====$code")
//                mView.onIMLoginResult(null, false)
//            }
//
//            override fun onException(exception: Throwable?) {
//                loading.dismiss()
//                Log.d("OkHttp", exception.toString())
//            }
//
//        })

        return LoginInfo("","")
    }
}