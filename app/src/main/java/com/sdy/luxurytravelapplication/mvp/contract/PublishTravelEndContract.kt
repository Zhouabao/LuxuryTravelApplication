package com.sdy.luxurytravelapplication.mvp.contract

import com.qiniu.android.storage.UpCompletionHandler
import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/2917:40
 *    desc   :
 *    version: 1.0
 */
interface PublishTravelEndContract {
    interface View : IView {

        fun uploadFile(success: Boolean, key: String)

        fun issuePlan(success: Boolean)
    }

    interface Presenter : IPresenter<View> {
        fun uploadFile(
            filePath: String,
            imagePath: String
        )

        fun issuePlan(params:HashMap<String,Any>)
    }

    interface Model : IModel {
        fun uploadFile(
            filePath: String,
            imagePath: String,
            upCompletionHandler: UpCompletionHandler
        )

        fun issuePlan(params:HashMap<String,Any>):Observable<BaseResp<Any>>

    }
}