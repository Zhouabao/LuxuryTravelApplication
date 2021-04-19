package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/1919:15
 *    desc   :
 *    version: 1.0
 */
interface CustomChatUpContentContract {
    interface View : IView {
        fun onSaveChatupMsg(success: Boolean,msg:String)
    }

    interface Presenter : IPresenter<View> {
        /**
         * 保存注册信息
         */
        fun saveChatupMsg(aboutme: String)
    }

    interface Model : IModel {
        /**
         * 保存注册信息
         */
        fun saveChatupMsg(aboutme: String): Observable<BaseResp<Any>>
    }
}