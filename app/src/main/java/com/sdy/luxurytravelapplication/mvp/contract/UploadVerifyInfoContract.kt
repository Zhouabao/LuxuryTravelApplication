package com.sdy.luxurytravelapplication.mvp.contract

import com.qiniu.android.storage.UpCompletionHandler
import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/2313:43
 *    desc   :
 *    version: 1.0
 */
interface UploadVerifyInfoContract {


    interface View : IView {

        fun uploadImgResult(success: Boolean, key: String, index: Int)

        fun uploadDataResult(success: Boolean)
    }

    interface Presenter : IPresenter<View> {
        fun uploadData(public_type: Int, type: Int, photo: String)
        fun uploadPhoto(filePath: String, index: Int = 0)
    }

    interface Model : IModel {
        fun uploadData(public_type: Int, type: Int, photo: String): Observable<BaseResp<Any>>
        fun uploadPhoto(filePath: String,  options: UpCompletionHandler)
    }

}