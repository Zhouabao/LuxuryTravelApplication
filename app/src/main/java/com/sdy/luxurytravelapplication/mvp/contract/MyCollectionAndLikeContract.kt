package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.RecommendSquareListBean
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareBannerBean
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareListBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/2411:20
 *    desc   :
 *    version: 1.0
 */
interface MyCollectionAndLikeContract {

    interface View : IView {
        fun onGetSquareListResult(data: RecommendSquareListBean?, b: Boolean)
//        fun onCheckBlockResult(banner: SquareBannerBean, b: Boolean)

    }

    interface Presenter : IPresenter<View> {
        fun getMySquare(params: HashMap<String, Any>)
//        fun checkBlock()
    }

    interface Model : IModel {
        fun getMySquare(params: HashMap<String, Any>): Observable<BaseResp<RecommendSquareListBean?>>
    }
}