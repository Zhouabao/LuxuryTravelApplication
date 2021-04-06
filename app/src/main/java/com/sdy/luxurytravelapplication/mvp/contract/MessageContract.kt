package com.sdy.luxurytravelapplication.mvp.contract

import com.netease.nimlib.sdk.RequestCallbackWrapper
import com.netease.nimlib.sdk.msg.model.RecentContact
import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.MessageListBean1
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/614:48
 *    desc   :
 *    version: 1.0
 */
interface MessageContract {
    interface View : IView {
        fun messageCensus(data: MessageListBean1?)
        fun getRecentContacts(result: MutableList<RecentContact>)
    }

    interface Presenter : IPresenter<View> {

        fun messageCensus()

        fun getRecentContacts()
    }

    interface Model : IModel {
        fun getRecentContacts(requestCallback: RequestCallbackWrapper<MutableList<RecentContact>>)

        fun messageCensus(): Observable<BaseResp<MessageListBean1?>>
    }
}