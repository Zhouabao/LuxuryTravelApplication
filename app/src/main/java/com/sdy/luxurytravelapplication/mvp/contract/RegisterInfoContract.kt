package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.RegisterFileBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/1710:49
 *    desc   :
 *    version: 1.0
 */
interface RegisterInfoContract {
    interface View : IView {
        fun registerSuccess(success: Boolean)
        fun registerFail()

        fun getRegisterProcessType(data: RegisterFileBean)
    }

    interface Presenter : IPresenter<View> {
        fun register(params: HashMap<String, String>)

        fun getRegisterProcessType()
    }

    interface Model : IModel {
        fun register(params: HashMap<String, String>): Observable<BaseResp<Any>>
        fun getRegisterProcessType(): Observable<BaseResp<RegisterFileBean>>
    }
}