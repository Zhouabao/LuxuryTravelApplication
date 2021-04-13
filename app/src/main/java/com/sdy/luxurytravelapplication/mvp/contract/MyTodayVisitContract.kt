package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.VisitorBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/1220:54
 *    desc   :
 *    version: 1.0
 */
interface MyTodayVisitContract {
    interface View : IView {

        fun onMyVisitResult(visitor: MutableList<VisitorBean>?)
    }

    interface Presenter : IPresenter<View> {
        /**
         * 1 今日 2所有
         */
        fun myVisitedList(params: HashMap<String, Any>)
    }

    interface Model : IModel {
        /**
         * 1 今日 2所有
         */
        fun myVisitedList(params: HashMap<String, Any>): Observable<BaseResp<MutableList<VisitorBean>?>>
    }
}