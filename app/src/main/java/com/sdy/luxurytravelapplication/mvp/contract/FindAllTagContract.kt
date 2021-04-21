package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareTagBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/2215:59
 *    desc   :
 *    version: 1.0
 */
interface FindAllTagContract {
    interface View : IView {
        /**
         * 获取广场列表
         */
        fun onGetSquareTagResult(data: MutableList<SquareTagBean>?, result: Boolean)

    }

    interface Presenter : IPresenter<View> {
        fun squareTopicList()
    }

    interface Model : IModel {
        fun squareTopicList(): Observable<BaseResp<MutableList<SquareTagBean>?>>
    }
}