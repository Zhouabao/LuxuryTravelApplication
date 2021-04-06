package com.sdy.luxurytravelapplication.mvp.contract

import com.netease.nimlib.sdk.RequestCallbackWrapper
import com.netease.nimlib.sdk.msg.model.RecentContact
import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.AccostBean
import com.sdy.luxurytravelapplication.mvp.model.bean.AccostListBean
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/616:41
 *    desc   :
 *    version: 1.0
 */
interface AccostListContract {
    interface View : IView {
        fun chatupList(data: MutableList<AccostBean>?)
        fun delChat(success: Boolean, position: Int)
        fun getRecentContacts(data: MutableList<AccostBean>,result: MutableList<RecentContact>)

    }

    interface Presenter : IPresenter<View> {
        fun chatupList(params: HashMap<String, Any>)
        fun delChat(params: HashMap<String, Any>, position: Int)
        fun getRecentContacts(data: MutableList<AccostBean>)
    }

    interface Model : IModel {
        fun getRecentContacts(requestCallback: RequestCallbackWrapper<MutableList<RecentContact>>)

        fun chatupList(params: HashMap<String, Any>): Observable<BaseResp<AccostListBean?>>
        fun delChat(params: HashMap<String, Any>, position: Int): Observable<BaseResp<Any>>
    }
}