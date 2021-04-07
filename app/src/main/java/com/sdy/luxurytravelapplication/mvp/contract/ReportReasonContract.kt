package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/714:40
 *    desc   :
 *    version: 1.0
 */
interface ReportReasonContract {
    interface View : IView {
        fun onGetReportMsgResult(b: Boolean, msg: MutableList<String>)
    }

    interface Presenter : IPresenter<View> {
        /**
         * 获取举报类型
         */
        fun getReportMsg()
    }

    interface Model : IModel {
        /**
         * 获取举报类型
         */
        fun getReportMsg(): Observable<BaseResp<MutableList<String>>>
    }
}