package com.sdy.luxurytravelapplication.mvp.model

import com.qiniu.android.storage.UpCompletionHandler
import com.qiniu.android.storage.UploadOptions
import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.constant.QNUploadManager
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.PublishContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 *    author : ZFM
 *    date   : 2021/3/2917:41
 *    desc   :
 *    version: 1.0
 */
class PublishModel : BaseModel(), PublishContract.Model {
    override fun uploadFile(
        totalCount: Int,
        currentCount: Int,
        filePath: String,
        imagePath: String,
        type: Int,
        upCompletionHandler: UpCompletionHandler, uploadOptions: UploadOptions
    ) {
        QNUploadManager.getInstance()
            .put(filePath, imagePath, UserManager.qnToken, upCompletionHandler, uploadOptions)
    }

    override fun announce(type: Int, params: HashMap<String, Any>): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.squareAnnounce(params)
    }

    override fun checkBlock(): Observable<BaseResp<Any?>> {
        return RetrofitHelper.service.checkBlock(hashMapOf())
    }
}