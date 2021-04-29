package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.http.exception.ErrorStatus
import com.sdy.luxurytravelapplication.mvp.contract.RegisterInfoTwoContract
import com.sdy.luxurytravelapplication.mvp.model.RegisterInfoTwoModel

/**
 *    author : ZFM
 *    date   : 2021/3/1710:53
 *    desc   :
 *    version: 1.0
 */
class RegisterInfoTwoPresenter :
    BasePresenter<RegisterInfoTwoContract.Model, RegisterInfoTwoContract.View>(),
    RegisterInfoTwoContract.Presenter {
    override fun createModel(): RegisterInfoTwoContract.Model? = RegisterInfoTwoModel()
    override fun setPersonal(params: HashMap<String, Any>) {
        mModel?.setPersonal(params)?.ss(mModel, mView,true) {
            mView?.apply {
                if (it.code != ErrorStatus.SUCCESS) {
                    setPersonalFail()
                } else {
                    setPersonalSuccess(it.data)
                }
            }
        }
    }

    override fun getRegisterProcessType() {
        mModel?.getRegisterProcessType()?.ss(mModel, mView,true) {
            mView?.apply {
                if (it.code == 200) {
                    getRegisterProcessType(it.data?: mutableListOf())
                }

            }
        }
    }
}