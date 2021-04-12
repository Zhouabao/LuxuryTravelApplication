package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.mvp.contract.AccountAboutContract
import com.sdy.luxurytravelapplication.mvp.model.AccountAboutModel

/**
 *    author : ZFM
 *    date   : 2021/4/1215:29
 *    desc   :
 *    version: 1.0
 */
class AccountAboutPresenter :
    BasePresenter<AccountAboutContract.Model, AccountAboutContract.View>(),
    AccountAboutContract.Presenter {
    override fun createModel(): AccountAboutContract.Model? {
        return AccountAboutModel()
    }

    override fun getAccountInfo() {
        mModel?.getAccountInfo()?.ss(mModel, mView, true) {
            mView?.getAccountResult(it.data)
        }
    }

    override fun unbundWeChat() {
        mModel?.unbundWeChat()?.ss(mModel, mView, true) {
            mView?.unbundWeChatResult(true)
        }
    }

    override fun bundWeChat(wxcode: String) {
        mModel?.bundWeChat(wxcode)?.ss(mModel,mView,true) {
            mView?.bundWeChatResult(it.data)
        }
    }
}