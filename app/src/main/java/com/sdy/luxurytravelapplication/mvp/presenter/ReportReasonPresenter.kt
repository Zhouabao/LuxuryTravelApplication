package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.ReportReasonContract
import com.sdy.luxurytravelapplication.mvp.model.ReportReasonModel

/**
 *    author : ZFM
 *    date   : 2021/4/714:40
 *    desc   :
 *    version: 1.0
 */
class ReportReasonPresenter :
    BasePresenter<ReportReasonContract.Model, ReportReasonContract.View>(),
    ReportReasonContract.Presenter {
    override fun createModel(): ReportReasonContract.Model? = ReportReasonModel()
    override fun getReportMsg() {
        mModel?.getReportMsg()?.ssss(mView,true) {
            mView?.onGetReportMsgResult(it.code == 200, it.data)
        }
    }
}