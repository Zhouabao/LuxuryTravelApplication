package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.ContactWayBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/1320:46
 *    desc   :
 *    version: 1.0
 */
interface ChangeUserContactContract {
    interface View : IView {
        fun onGetContactResult(data: ContactWayBean?)

        fun onSetContactResult(success: Boolean)
    }

    interface Presenter : IPresenter<View> {
        /**
         * 获取我的联系方式
         *
         */
        fun getContact()

        /**
         * 设置我的联系方式
         */
        fun setContact(contact_way: Int, contact_way_content: String, contact_way_hide: Int)
    }

    interface Model:IModel{
        /**
         * 获取我的联系方式
         *
         */
        fun getContact():Observable<BaseResp<ContactWayBean?>>

        /**
         * 设置我的联系方式
         */
        fun setContact(contact_way: Int, contact_way_content: String, contact_way_hide: Int):Observable<BaseResp<Any?>>

    }
}