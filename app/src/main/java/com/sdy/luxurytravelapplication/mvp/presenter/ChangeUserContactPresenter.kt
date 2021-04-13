package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.ChangeUserContactContract
import com.sdy.luxurytravelapplication.mvp.model.ChangeUserContactModel

/**
 *    author : ZFM
 *    date   : 2021/4/1320:46
 *    desc   :
 *    version: 1.0
 */
class ChangeUserContactPresenter :
    BasePresenter<ChangeUserContactContract.Model, ChangeUserContactContract.View>(),
    ChangeUserContactContract.Presenter {
    override fun createModel(): ChangeUserContactContract.Model? {

        return ChangeUserContactModel()
    }

    override fun getContact() {

        mModel?.getContact()?.ssss {
            mView?.onGetContactResult(it.data)
        }
    }

    override fun setContact(contact_way: Int, contact_way_content: String, contact_way_hide: Int) {
        mModel?.setContact(contact_way, contact_way_content, contact_way_hide)?.ssss {
            mView?.onSetContactResult(it.code == 200)
        }
    }
}