package com.sdy.luxurytravelapplication.mvp.presenter

import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.auth.LoginInfo
import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.ext.sss
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.VerifycodeContract
import com.sdy.luxurytravelapplication.mvp.model.VerifycodeModel

/**
 *    author : ZFM
 *    date   : 2021/3/1717:12
 *    desc   :
 *    version: 1.0
 */
class VerifycodePresenter : BasePresenter<VerifycodeContract.Model, VerifycodeContract.View>(),
    VerifycodeContract.Presenter {
    override fun createModel(): VerifycodeContract.Model? = VerifycodeModel()
    override fun sendSms(params: HashMap<String, Any>) {
        mModel?.sendSms(params)?.ssss(mView, false) { mView?.sendSms(it.code, it.data) }
    }

    override fun loginOrAlloc(params: HashMap<String, Any>) {
        mModel?.loginOrAlloc(params)?.ssss (mView, true) {
            mView?.loginOrAllocResult(it.data,it.code,it.msg)
        }

    }

    override fun loginIM(loginInfo: LoginInfo) {
        mModel?.loginIM(loginInfo, object : RequestCallback<LoginInfo> {
            override fun onSuccess(param: LoginInfo) {
                mView?.loginIMResult(loginInfo, true)
            }

            override fun onFailed(code: Int) {
                mView?.loginIMResult(loginInfo, false)
            }

            override fun onException(exception: Throwable?) {
                mView?.loginIMResult(loginInfo, false)
            }
        })
    }

    override fun cancelAccount(params: HashMap<String, Any>) {
        mModel?.cancelAccount(params)?.sss(mView, true,
            { mView?.cancelAccountResult(true) },
            { mView?.cancelAccountResult(false) })
    }
}