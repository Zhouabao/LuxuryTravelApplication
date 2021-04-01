package com.sdy.luxurytravelapplication.mvp.model

import com.blankj.utilcode.util.SPUtils
import com.qiniu.android.storage.UpCompletionHandler
import com.qiniu.android.storage.UploadOptions
import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.QNUploadManager
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.PublishContract
import com.sdy.luxurytravelapplication.mvp.contract.PublishTravelContract
import com.sdy.luxurytravelapplication.mvp.contract.PublishTravelEndContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.PlanOptionsBean
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 *    author : ZFM
 *    date   : 2021/3/2917:41
 *    desc   :
 *    version: 1.0
 */
class PublishTravelEndlModel : BaseModel(), PublishTravelEndContract.Model {
    override fun uploadFile(
        filePath: String,
        imagePath: String,
        upCompletionHandler: UpCompletionHandler
    ) {

        QNUploadManager.getInstance().put(
            filePath,
            imagePath,
            SPUtils.getInstance(Constants.SPNAME).getString("qntoken"),
            upCompletionHandler, null
        )
    }

    override fun issuePlan(params: HashMap<String, Any>): Observable<BaseResp<Any>> {

        return RetrofitHelper.service.issuePlan(params)
    }


}