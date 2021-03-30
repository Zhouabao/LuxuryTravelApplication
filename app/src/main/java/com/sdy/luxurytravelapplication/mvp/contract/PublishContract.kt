package com.sdy.luxurytravelapplication.mvp.contract

import com.qiniu.android.storage.UpCompletionHandler
import com.qiniu.android.storage.UploadOptions
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
interface PublishContract {
    interface View : IView {

        fun onSquareAnnounceResult(type: Int, b: Boolean, code: Int = 0)

        fun onQnUploadResult(success: Boolean, type: Int, key: String)

        /**
         * 设置用户封禁
         */
        fun onCheckBlockResult(result: Boolean)
    }

    interface Presenter : IPresenter<View> {
        fun uploadFile(
            totalCount: Int,
            currentCount: Int,
            filePath: String,
            imagePath: String,
            type: Int
        )

        fun publishContent(
            type: Int,
            params: HashMap<String, Any>,
            keyList: MutableList<String> = mutableListOf()
        )

        fun checkBlock()
    }

    interface Model : IModel {
        fun uploadFile(
            totalCount: Int,
            currentCount: Int,
            filePath: String,
            imagePath: String,
            type: Int,
            upCompletionHandler: UpCompletionHandler,
            uploadOptions: UploadOptions
        )

        fun announce(type: Int, params: HashMap<String, Any>): Observable<BaseResp<Any>>
        fun checkBlock():Observable<BaseResp<Any?>>
    }
}