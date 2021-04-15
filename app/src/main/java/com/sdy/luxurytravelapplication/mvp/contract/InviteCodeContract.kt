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
interface InviteCodeContract {
    interface Presenter : IPresenter<View> {
        fun checkCode(code: String)

    }

    interface View : IView {
        fun checkCode(success: Boolean)

    }

    interface Model : IModel {
        fun checkCode(code: String): Observable<BaseResp<Any>>

    }
}