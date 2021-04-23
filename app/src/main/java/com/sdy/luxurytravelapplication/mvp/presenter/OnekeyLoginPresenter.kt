package com.sdy.luxurytravelapplication.mvp.presenter

import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.auth.LoginInfo
import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.OnekeyLoginContract
import com.sdy.luxurytravelapplication.mvp.model.OnekeyLoginModel

/**
 *    author : ZFM
 *    date   : 2021/3/1911:45
 *    desc   :
 *    version: 1.0
 */
class OnekeyLoginPresenter : BasePresenter<OnekeyLoginContract.Model, OnekeyLoginContract.View>(),
    OnekeyLoginContract.Presenter {

    override fun createModel(): OnekeyLoginContract.Model? = OnekeyLoginModel()
    override fun loginOrAlloc(params: HashMap<String, Any>) {
        mModel?.loginOrAlloc(params)?.ssss(mView, true) {
            mView?.loginOrAllocResult(it.data, it.code)
        }

    }

    override fun loginIM(info: LoginInfo) {

        mModel?.loginIM(info, object : RequestCallback<LoginInfo> {
            override fun onSuccess(param: LoginInfo?) {
                mView?.loginIM(info, true)
            }

            override fun onFailed(code: Int) {
                mView?.loginIM(info, false)
            }

            override fun onException(exception: Throwable?) {
                mView?.loginIM(info, false)
            }
        })

    }
}