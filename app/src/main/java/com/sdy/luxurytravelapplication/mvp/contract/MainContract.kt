package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.AllMsgCount
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
        fun onMsgListResult(allMsgCount: AllMsgCount)
        fun startupRecord()
    }

    interface Presenter : IPresenter<View> {
        fun msgList()
        fun startupRecord()
    }

    interface Model : IModel {
        fun msgList(): Observable<BaseResp<AllMsgCount>>
        fun startupRecord(): Observable<BaseResp<Any>>
    }
}