package com.sdy.luxurytravelapplication.mvp.contract

import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.auth.LoginInfo
import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.LoginBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/1911:44
 *    desc   :
 *    version: 1.0
 */
interface OnekeyLoginContract {
    interface Presenter : IPresenter<View> {
        fun loginOrAlloc(params: HashMap<String, Any>)

        fun loginIM(info: LoginInfo)

    }

    interface View : IView {
        fun loginOrAllocResult(data: LoginBean?, success: Boolean)
        fun loginIM(info: LoginInfo?, success: Boolean)

    }

    interface Model : IModel {
        fun loginOrAlloc(params: HashMap<String, Any>): Observable<BaseResp<LoginBean>>

        fun loginIM(info: LoginInfo, callback: RequestCallback<LoginInfo>)
    }
}