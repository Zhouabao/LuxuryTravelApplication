package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.RecommendSquareListBean
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareBannerBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/2411:20
 *    desc   :
 *    version: 1.0
 */
interface FindContentContract {

    interface View : IView {
        fun onGetSquareRecommendResult(data: RecommendSquareListBean?, b: Boolean)
//        fun onCheckBlockResult(banner: SquareBannerBean, b: Boolean)

    }

    interface Presenter : IPresenter<View> {
        fun squareEliteList(params: HashMap<String, Any>,type:Int)
//        fun checkBlock()
    }

    interface Model : IModel {
        fun squareEliteList(params: HashMap<String, Any>,type:Int): Observable<BaseResp<RecommendSquareListBean?>>
    }
}