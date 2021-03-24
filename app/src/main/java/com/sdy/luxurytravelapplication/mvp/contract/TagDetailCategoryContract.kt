package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.TagSquareListBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/2419:44
 *    desc   :
 *    version: 1.0
 */
interface TagDetailCategoryContract {
    interface Presenter : IPresenter<View> {
        fun squareTagInfo(params: HashMap<String, Any>)
        fun checkBlock()
    }

    interface Model : IModel {
        fun squareTagInfo(params: HashMap<String, Any>): Observable<BaseResp<TagSquareListBean?>>

        fun checkBlock(): Observable<BaseResp<Any?>>
    }

    interface View : IView {

        fun onGetSquareRecommendResult(data: TagSquareListBean?, b: Boolean)

        fun onCheckBlockResult(b: Boolean)
    }
}