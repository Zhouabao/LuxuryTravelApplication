package com.sdy.luxurytravelapplication.mvp.contract

import com.qiniu.android.storage.UpCompletionHandler
import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/715:05
 *    desc   :
 *    version: 1.0
 */
interface ReportReasonUploadContract {
    interface View : IView {

        fun uploadImgResult(ok: Boolean, key: String, index: Int)

        fun addReportResult(success: Boolean, msg: String)

    }

    interface Presenter : IPresenter<View> {
        fun uploadPhoto(filePath: String, index: Int = 0)

        /**
         * 添加举报
         * type	1通话举报 2主页举报 3聊天内容举报 4广场动态举报 5广场评论举报
         * content  当type为3和5 为举报内容 为4 广场动态的id
         * photo 举报图片json串
         * case_type 返回举报类型【色情涉黄/广告或垃圾信息。。。】
         */
        fun addReport(
            photo: String = "",
            content: String = "",
            case_type: String,
            target_accid: String
        )
    }

    interface Model : IModel {

        fun uploadPhoto(
            filePath: String, index: Int = 0,
            upCompletionHandler: UpCompletionHandler
        )

        fun addReport(
            photo: String = "",
            content: String = "",
            case_type: String,
            target_accid: String
        ): Observable<BaseResp<Any>>
    }
}