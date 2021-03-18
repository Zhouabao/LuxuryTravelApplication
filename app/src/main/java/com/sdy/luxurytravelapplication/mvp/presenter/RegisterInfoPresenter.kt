package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.http.exception.ErrorStatus
import com.sdy.luxurytravelapplication.mvp.contract.RegisterInfoContract
import com.sdy.luxurytravelapplication.mvp.model.RegisterInfoModel

/**
 *    author : ZFM
 *    date   : 2021/3/1710:53
 *    desc   :
 *    version: 1.0
 */
class RegisterInfoPresenter :
    BasePresenter<RegisterInfoContract.Model, RegisterInfoContract.View>(),
    RegisterInfoContract.Presenter {
    override fun createModel(): RegisterInfoContract.Model? = RegisterInfoModel()
    override fun register(params: HashMap<String, String>) {
        mModel?.register(params)?.ss(mModel, mView) {
            mView?.apply {
                if (it.code != ErrorStatus.SUCCESS) {
                    registerFail()
                } else {
                    registerSuccess(true)
                }
            }
        }
    }

    override fun getRegisterProcessType() {
        mModel?.getRegisterProcessType()?.ss(mModel, mView) {
            mView?.apply {
                if (it.code == 200) {
                    getRegisterProcessType(it.data)
                }

            }
        }
    }
}