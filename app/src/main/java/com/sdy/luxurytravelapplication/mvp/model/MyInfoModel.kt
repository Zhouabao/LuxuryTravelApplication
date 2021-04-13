package com.sdy.luxurytravelapplication.mvp.model

import com.google.gson.Gson
import com.qiniu.android.storage.UpCompletionHandler
import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.constant.QNUploadManager
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.MyInfoContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.MyPhotoBean
import com.sdy.luxurytravelapplication.mvp.model.bean.UserInfoSettingBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/1315:15
 *    desc   :
 *    version: 1.0
 */
class MyInfoModel : BaseModel(), MyInfoContract.Model {
    override fun personalInfo(): Observable<BaseResp<UserInfoSettingBean?>> {
        return RetrofitHelper.service.personalInfoCandy(hashMapOf())
    }

    override fun addPhotoV2(
        params: HashMap<String, Any?>,
        photos: MutableList<Int?>,
        type: Int
    ): Observable<BaseResp<Any>> {
        params["photos"] = Gson().toJson(photos)
        return RetrofitHelper.service.addPhotoV2(params)
    }

    override fun addPhotoWall(
        replaceAvator: Boolean,
        key: String
    ): Observable<BaseResp<MyPhotoBean?>> {
        return RetrofitHelper.service.addPhotoWall(hashMapOf("photo" to key))
    }

    override fun uploadProfile(
        filePath: String,
        imagePath: String,
        replaceAvator: Boolean,
        upCompletionHandler: UpCompletionHandler
    ) {
        QNUploadManager.getInstance().put(filePath,imagePath,UserManager.qnToken,upCompletionHandler,null)
    }

}