package com.sdy.luxurytravelapplication.mvp.contract

import com.netease.nimlib.sdk.auth.LoginInfo
import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.LoginBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/1717:11
 *    desc   :
 *    version: 1.0
 */
interface VerifycodeContract {
    interface Presenter : IPresenter<View> {
        fun sendSms(params: HashMap<String, Any>)
        fun loginOrAlloc(params: HashMap<String, Any>)
        fun loginIM(loginInfo: LoginInfo)
        fun cancelAccount(params: HashMap<String, Any>)
    }

    interface Model : IModel {
        fun sendSms(params: HashMap<String, Any>): Observable<BaseResp<Any>>
        fun loginOrAlloc(params: HashMap<String, Any>): Observable<BaseResp<LoginBean>>
        fun cancelAccount(params: HashMap<String, Any>): Observable<BaseResp<Any>>
        fun loginIM(): LoginInfo?
    }

    interface View : IView {
        fun sendSms(success: Boolean)
        fun loginOrAllocResult(data: LoginBean)
        fun cancelAccountResult(success: Boolean)
        fun loginIMResult(loginInfo: LoginInfo?, success: Boolean)
    }


}