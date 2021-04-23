package com.sdy.luxurytravelapplication.mvp.model

import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.auth.LoginInfo
import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.VerifycodeContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.LoginBean
import com.sdy.luxurytravelapplication.mvp.model.bean.RegisterTooManyBean
import com.sdy.luxurytravelapplication.nim.api.NimUIKit
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/1717:15
 *    desc   :
 *    version: 1.0
 */
class VerifycodeModel : BaseModel(), VerifycodeContract.Model {
    override fun sendSms(params: HashMap<String, Any>): Observable<BaseResp<RegisterTooManyBean?>> {
        return RetrofitHelper.service.sendSms(params)
    }

    override fun loginOrAlloc(params: HashMap<String, Any>): Observable<BaseResp<LoginBean?>> {
        return RetrofitHelper.service.loginOrAlloc(params)

    }

    override fun cancelAccount(params: HashMap<String, Any>): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.cancelAccount(params)

    }

    override fun loginIM(loginInfo: LoginInfo, callback: RequestCallback<LoginInfo>) {
        NimUIKit.login(loginInfo, callback)
    }
}