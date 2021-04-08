package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.RecommendSquareListBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/2411:20
 *    desc   :
 *    version: 1.0
 */
interface MyFindContentContract {

    interface View : IView {
        fun onGetSquareRecommendResult(data: RecommendSquareListBean?, b: Boolean)
        fun onCheckBlockResult(b: Boolean)

    }

    interface Presenter : IPresenter<View> {
        fun squareEliteList(params: HashMap<String, Any>)
        fun checkBlock()
    }

    interface Model : IModel {
        fun squareEliteList(params: HashMap<String, Any>): Observable<BaseResp<RecommendSquareListBean?>>
        fun checkBlock():Observable<BaseResp<Any?>>
    }
}