package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.ContactDataBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/1511:48
 *    desc   :
 *    version: 1.0
 */
interface ContactBookContract {
    interface View : IView {
        fun onGetContactListResult(data: ContactDataBean?)
    }

    interface Presenter : IPresenter<View> {
        /**
         * 获取通讯录
         */
        fun getContactLists()
    }

    interface Model : IModel {
        /**
         * 获取通讯录
         */
        fun getContactLists(): Observable<BaseResp<ContactDataBean?>>
    }
}