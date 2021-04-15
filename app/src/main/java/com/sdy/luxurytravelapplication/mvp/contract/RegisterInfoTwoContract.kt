package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.MyTapsBean
import com.sdy.luxurytravelapplication.mvp.model.bean.RegisterFileBean
import com.sdy.luxurytravelapplication.mvp.model.bean.SetPersonalBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/1710:49
 *    desc   :
 *    version: 1.0
 */
interface RegisterInfoTwoContract {
    interface View : IView {
        fun setPersonalSuccess(personalBean: SetPersonalBean)
        fun setPersonalFail()

        fun getRegisterProcessType(data: MutableList<MyTapsBean>)
    }

    interface Presenter : IPresenter<View> {
        fun setPersonal(params: HashMap<String, Any>)

        fun getRegisterProcessType()

    }

    interface Model : IModel {
        fun setPersonal(params: HashMap<String, Any>): Observable<BaseResp<SetPersonalBean>>
        fun getRegisterProcessType(): Observable<BaseResp<MutableList<MyTapsBean>?>>
    }
}