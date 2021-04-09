package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.ChargeWayBeans
import com.sdy.luxurytravelapplication.mvp.model.bean.PullWithdrawBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/719:30
 *    desc   :
 *    version: 1.0
 */
interface CandyRechargeContract {
    interface View : IView {
        fun giftRechargeList(data: ChargeWayBeans?)
        fun onMyCadnyResult(candyCount: PullWithdrawBean?)
    }

    interface Presenter : IPresenter<View> {
        fun giftRechargeList()

        /**
         * 查询我的糖果
         */
        fun myCadny()
    }

    interface Model : IModel {
        /**
         * 查询我的糖果
         */
        fun myCadny(): Observable<BaseResp<PullWithdrawBean?>>
        fun giftRechargeList(): Observable<BaseResp<ChargeWayBeans?>>
    }
}