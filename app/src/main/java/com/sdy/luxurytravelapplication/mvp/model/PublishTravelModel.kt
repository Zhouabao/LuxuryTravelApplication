package com.sdy.luxurytravelapplication.mvp.model

import com.qiniu.android.storage.UpCompletionHandler
import com.qiniu.android.storage.UploadOptions
import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.constant.QNUploadManager
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.PublishContract
import com.sdy.luxurytravelapplication.mvp.contract.PublishTravelContract
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
class PublishTravelModel : BaseModel(), PublishTravelContract.Model {
    override fun planOptions(): Observable<BaseResp<PlanOptionsBean?>> {

        return RetrofitHelper.service.planOptions(hashMapOf())
    }

}