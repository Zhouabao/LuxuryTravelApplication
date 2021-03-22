package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.ChargeWayBeans
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/1917:57
 *    desc   :
 *    version: 1.0
 */
interface PurchaseFootContract {
    interface Presenter : IPresenter<View> {

        fun getThreshold(params: HashMap<String, Any>)
    }

    interface View : IView {
        fun getThresholdResult(chargeWayBeans: ChargeWayBeans)
    }

    interface Model : IModel {

        fun getThreshold(params: HashMap<String, Any>): Observable<BaseResp<ChargeWayBeans>>
    }
}