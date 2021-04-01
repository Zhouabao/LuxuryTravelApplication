package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.PlanOptionsBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/2917:40
 *    desc   :
 *    version: 1.0
 */
interface PublishTravelContract {
    interface View : IView {
        fun planOptions(data: PlanOptionsBean?)

    }

    interface Presenter : IPresenter<View> {
        fun planOptions()
    }

    interface Model : IModel {
        fun planOptions(): Observable<BaseResp<PlanOptionsBean?>>
    }
}