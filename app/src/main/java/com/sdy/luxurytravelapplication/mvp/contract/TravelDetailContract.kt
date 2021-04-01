package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.TravelPlanBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/3017:11
 *    desc   :
 *    version: 1.0
 */
interface TravelDetailContract {
    interface View : IView {
        fun planInfo(travelPlanBean: TravelPlanBean?)
    }

    interface Presenter : IPresenter<View> {
        fun planInfo(dating_id: Int)
    }

    interface Model : IModel {
        fun planInfo(dating_id: Int): Observable<BaseResp<TravelPlanBean?>>
    }
}