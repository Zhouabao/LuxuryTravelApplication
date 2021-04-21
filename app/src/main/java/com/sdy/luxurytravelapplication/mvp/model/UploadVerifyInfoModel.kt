package com.sdy.luxurytravelapplication.mvp.model

import com.qiniu.android.storage.UpCompletionHandler
import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.QNUploadManager
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.UploadVerifyInfoContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.utils.RandomUtils
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/2313:45
 *    desc   :
 *    version: 1.0
 */
class UploadVerifyInfoModel : BaseModel(), UploadVerifyInfoContract.Model {
    override fun uploadData(public_type: Int, type: Int, photo: String): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.uploadData(
            hashMapOf<String, Any>(
                "public_type" to public_type,
                "type" to type,
                "photo" to photo
            )
        )

    }

    override fun uploadPhoto(filePath: String,options: UpCompletionHandler) {
        //上传图片
        val key = "${Constants.FILE_NAME_INDEX}${Constants.SWEETHEART}" +
                "${UserManager.accid}/${System.currentTimeMillis()}/${RandomUtils.getRandomString(16)}"
        QNUploadManager.getInstance().put(filePath, key, UserManager.qnToken, options, null)
    }
}