package com.sdy.luxurytravelapplication.mvp.model

import com.qiniu.android.storage.UpCompletionHandler
import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.QNUploadManager
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.ReportReasonUploadContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.utils.RandomUtils
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/715:06
 *    desc   :
 *    version: 1.0
 */
class ReportReasonUploadModel : BaseModel(), ReportReasonUploadContract.Model {
    override fun uploadPhoto(
        filePath: String,
        index: Int,
        upCompletionHandler: UpCompletionHandler
    ) {
        //上传图片
        val key =
            "${Constants.FILE_NAME_INDEX}${Constants.CHATREPORT}${UserManager.accid}/${System.currentTimeMillis()}/${RandomUtils.getRandomString(
                16
            )}"
        QNUploadManager.getInstance()
            .put(filePath, key, UserManager.qnToken, upCompletionHandler, null)

    }

    override fun addReport(
        photo: String,
        content: String,
        case_type: String,
        target_accid: String
    ): Observable<BaseResp<Any>> {
        val params = hashMapOf<String, Any>(
            "case_type" to case_type,
            "type" to 2,
            "target_accid" to target_accid
        )

        if (photo.isNotEmpty()) {
            params["photo"] = photo
        }
        if (content.isNotEmpty()) {
            params["content"] = content
        }
        return RetrofitHelper.service.addReport(params)
    }
}