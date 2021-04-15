package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.mvp.contract.InviteCodeContract
import com.sdy.luxurytravelapplication.mvp.model.InviteCodeModel

/**
 *    author : ZFM
 *    date   : 2021/3/229:47
 *    desc   :
 *    version: 1.0
 */
class InviteCodePresenter :
    BasePresenter<InviteCodeContract.Model, InviteCodeContract.View>(),
    InviteCodeContract.Presenter {

    override fun createModel(): InviteCodeContract.Model? = InviteCodeModel()

    override fun checkCode(code: String) {
        mModel?.checkCode(code)?.ss(mModel, mView, true) {
            mView?.checkCode(true)
        }
    }
}