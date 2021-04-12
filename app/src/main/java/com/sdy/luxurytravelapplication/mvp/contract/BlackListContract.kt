package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.BlackBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/1212:18
 *    desc   :
 *    version: 1.0
 */
interface BlackListContract {
    interface View : IView {
        fun onMyShieldingListResult(data: MutableList<BlackBean>?)

        fun onRemoveBlockResult(success: Boolean, position: Int)
    }

    interface Presenter : IPresenter<View> {
        /**
         * 获取黑名单list
         */
        fun myShieldingList(params: HashMap<String, Any>)

        /**
         * 解除拉黑
         */
        fun removeBlock(params: HashMap<String, Any>, position: Int)
    }

    interface Model : IModel {
        /**
         * 获取黑名单list
         */
        fun myShieldingList(params: HashMap<String, Any>): Observable<BaseResp<MutableList<BlackBean>?>>


        /**
         * 解除拉黑
         */
        fun removeBlock(params: HashMap<String, Any>): Observable<BaseResp<Any>>
    }
}