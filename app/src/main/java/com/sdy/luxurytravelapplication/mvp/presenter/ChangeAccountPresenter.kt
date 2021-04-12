package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.mvp.contract.ChangeAccountContract
import com.sdy.luxurytravelapplication.mvp.model.ChangeAccountModel

/**
 *    author : ZFM
 *    date   : 2021/4/1216:16
 *    desc   :
 *    version: 1.0
 */
class ChangeAccountPresenter :
    BasePresenter<ChangeAccountContract.Model, ChangeAccountContract.View>(),
    ChangeAccountContract.Presenter {
    override fun createModel(): ChangeAccountContract.Model? {
        return ChangeAccountModel()
    }

    override fun changeAccount(params: HashMap<String, Any>) {

        mModel?.changeAccount(params)?.ss(mModel,mView,true) {
            mView?.onChangeAccountResult(true)
        }
    }

    override fun sendSms(params: HashMap<String, Any>) {
        mModel?.sendSms(params)?.ss(mModel,mView,false) {
            mView?.onSendSmsResult(true)
        }
    }

    override fun getCauseList() {
        mModel?.getCauseList()?.ss(mModel,mView,false) {
            mView?.onCauseListResult(it.data)
        }
    }
}