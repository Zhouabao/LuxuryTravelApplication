package com.sdy.luxurytravelapplication.mvp.model

import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.auth.LoginInfo
import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.OnekeyLoginContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.LoginBean
import com.sdy.luxurytravelapplication.nim.api.NimUIKit
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/1911:46
 *    desc   :
 *    version: 1.0
 */
class OnekeyLoginModel : BaseModel(), OnekeyLoginContract.Model {
    override fun loginOrAlloc(params: HashMap<String, Any>): Observable<BaseResp<LoginBean?>> {
        return RetrofitHelper.service.loginOrAlloc(params)
    }

    override fun loginIM(info: LoginInfo, callback: RequestCallback<LoginInfo>) {
        NimUIKit.login(info, callback)

    }
}