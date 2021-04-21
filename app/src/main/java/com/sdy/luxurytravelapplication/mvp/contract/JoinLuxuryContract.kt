package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/219:41
 *    desc   :
 *    version: 1.0
 */
interface JoinLuxuryContract {
    interface View : IView {
        fun joinSweetApplyResult(success: Boolean)
    }

    interface Model : IModel {
        fun joinSweetApply(): Observable<BaseResp<Any>>
    }

    interface Presenter : IPresenter<View> {
        fun joinSweetApply()

    }
}