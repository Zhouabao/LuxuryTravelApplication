package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.ContactBookContract
import com.sdy.luxurytravelapplication.mvp.model.ContactBookModel

/**
 *    author : ZFM
 *    date   : 2021/4/1511:49
 *    desc   :
 *    version: 1.0
 */
class ContactBookPresenter:BasePresenter<ContactBookContract.Model,ContactBookContract.View>(),ContactBookContract.Presenter {
    override fun createModel(): ContactBookContract.Model? {
        return ContactBookModel()
    }

    override fun getContactLists() {
        mModel?.getContactLists()?.ssss {
            mView?.onGetContactListResult(it.data)
        }
    }
}