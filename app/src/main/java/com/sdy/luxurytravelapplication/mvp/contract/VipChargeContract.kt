package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.ChargeWayBeans
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/819:01
 *    desc   :
 *    version: 1.0
 */
interface VipChargeContract {
    interface View : IView {
        fun getChargeDataResult(data: ChargeWayBeans)
    }

    interface Presenter : IPresenter<View> {
        fun getThreshold()
    }

    interface Model : IModel {
        fun getThreshold(): Observable<BaseResp<ChargeWayBeans>>
    }
}