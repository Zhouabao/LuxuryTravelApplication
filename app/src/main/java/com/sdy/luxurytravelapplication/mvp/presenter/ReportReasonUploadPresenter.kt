package com.sdy.luxurytravelapplication.mvp.presenter

import com.qiniu.android.storage.UpCompletionHandler
import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.ReportReasonUploadContract
import com.sdy.luxurytravelapplication.mvp.model.ReportReasonUploadModel

/**
 *    author : ZFM
 *    date   : 2021/4/715:06
 *    desc   :
 *    version: 1.0
 */
class ReportReasonUploadPresenter :
    BasePresenter<ReportReasonUploadContract.Model, ReportReasonUploadContract.View>(),
    ReportReasonUploadContract.Presenter {
    override fun createModel(): ReportReasonUploadContract.Model? = ReportReasonUploadModel()
    override fun uploadPhoto(filePath: String, index: Int) {
        mView?.showLoading()
        mModel?.uploadPhoto(filePath, index, UpCompletionHandler { key, info, response ->
            if (info != null && info.isOK) {
                mView?.uploadImgResult(info.isOK, key, index)
            } else {
                mView?.uploadImgResult(false, key, index)
                mView?.hideLoading()
            }

        })
    }

    override fun addReport(
        photo: String,
        content: String,
        case_type: String,
        target_accid: String
    ) {
        mModel?.addReport(photo, content, case_type, target_accid)?.ss(mModel,mView,true){
            mView?.addReportResult(true,it.msg)
        }
    }
}