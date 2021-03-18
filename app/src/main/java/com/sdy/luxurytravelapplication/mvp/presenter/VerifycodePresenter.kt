package com.sdy.luxurytravelapplication.mvp.presenter

import com.netease.nimlib.sdk.auth.LoginInfo
import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.ext.sss
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
        mModel?.sendSms(params)?.sss(mView, false,
            { mView?.sendSms(true) },
            { mView?.sendSms(false) })
    }

    override fun loginOrAlloc(params: HashMap<String, Any>) {
        mModel?.loginOrAlloc(params)?.ss(mModel, mView, true) {
            mView?.loginOrAllocResult(it.data)
        }

    }

    override fun loginIM(loginInfo: LoginInfo) {
        val result = mModel?.loginIM()
        mView?.loginIMResult(result, result != null)
    }

    override fun cancelAccount(params: HashMap<String, Any>) {
        mModel?.cancelAccount(params)?.sss(mView, true,
            { mView?.cancelAccountResult(true) },
            { mView?.cancelAccountResult(false) })
    }
}