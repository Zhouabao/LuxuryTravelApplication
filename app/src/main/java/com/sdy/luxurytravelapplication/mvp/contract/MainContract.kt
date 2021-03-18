package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/1616:58
 *    desc   :
 *    version: 1.0
 */
interface MainContract {

    interface View : IView {
        fun showLogoutSuccess(success: Boolean)
        fun showUserInfo(bean: Any)
    }

    interface Presenter : IPresenter<View> {
        fun logout()
        fun getUserInfo()
    }

    interface Model : IModel {
        fun logout(): Observable<BaseResp<Any>>
        fun getUserInfo(): Observable<BaseResp<Any>>
    }
}