package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.MyInviteBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/1416:47
 *    desc   :
 *    version: 1.0
 */
interface MyInviteContract {
    interface View : IView {
        fun myInvite(data: MyInviteBean?)
    }

    interface Presenter : IPresenter<View> {
        fun myInvite()
    }

    interface Model : IModel {
        fun myInvite(): Observable<BaseResp<MyInviteBean?>>
    }
}