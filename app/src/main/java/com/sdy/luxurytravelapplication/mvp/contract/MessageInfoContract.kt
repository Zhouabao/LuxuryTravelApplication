package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/712:24
 *    desc   :
 *    version: 1.0
 */
interface MessageInfoContract {
    interface View : IView {

        fun removeStarResult(success: Boolean)

        fun delFriendResult(success: Boolean)

        fun addStarTargetResult(success: Boolean)
    }

    interface Presenter : IPresenter<View> {
        /**
         * 添加星标
         */
        fun addStarTarget(target_accid: String)


        /**
         * 删除好友
         */
        fun dissolutionFriend(target_accid: String)

        /**
         * 移除星标
         */
        fun removeStarTarget(target_accid: String)
    }

    interface Model : IModel {
        /**
         * 添加星标
         */
        fun addStarTarget(target_accid: String): Observable<BaseResp<Any>>


        /**
         * 删除好友
         */
        fun dissolutionFriend(target_accid: String): Observable<BaseResp<Any>>

        /**
         * 移除星标
         */
        fun removeStarTarget(target_accid: String): Observable<BaseResp<Any>>

    }
}