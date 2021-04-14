package com.sdy.luxurytravelapplication.mvp.model

import com.qiniu.android.storage.UpCompletionHandler
import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.QNUploadManager
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.VideoIntroduceContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.CopyMvBean
import com.sdy.luxurytravelapplication.utils.RandomUtils
import io.reactivex.Observable
import java.util.*

/**
 *    author : ZFM
 *    date   : 2021/4/1411:12
 *    desc   :
 *    version: 1.0
 */
class VideoIntroduceModel : BaseModel(), VideoIntroduceContract.Model {
    override fun uploadMv(params: HashMap<String, Any>): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.uploadMv(params)

    }

    override fun getVideoNormal(): Observable<BaseResp<CopyMvBean?>> {
        return RetrofitHelper.service.normalMv(hashMapOf())
    }

    override fun uploadProfile(filePath: String, options: UpCompletionHandler) {
        val fileKey = "${Constants.FILE_NAME_INDEX}${Constants.VIDEOFACE}${UserManager.accid}/" +
                "${System.currentTimeMillis()}/${RandomUtils.getRandomString(16)}"
        QNUploadManager.getInstance().put(filePath, fileKey, UserManager.qnToken, options, null)
    }
}