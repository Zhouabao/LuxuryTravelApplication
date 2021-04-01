package com.sdy.luxurytravelapplication.mvp.presenter

import com.qiniu.android.http.ResponseInfo
import com.qiniu.android.storage.UpCompletionHandler
import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.mvp.contract.PublishTravelEndContract
import com.sdy.luxurytravelapplication.mvp.model.PublishTravelEndlModel
import org.json.JSONObject

/**
 *    author : ZFM
 *    date   : 2021/3/2917:41
 *    desc   :
 *    version: 1.0
 */
class PublishTravelEndPresenter :
    BasePresenter<PublishTravelEndContract.Model, PublishTravelEndContract.View>(),
    PublishTravelEndContract.Presenter {
    override fun createModel(): PublishTravelEndContract.Model? = PublishTravelEndlModel()
    override fun uploadFile(
        filePath: String,
        imagePath: String
    ) {
        mView?.showLoading()
        mModel?.uploadFile(filePath, imagePath, object : UpCompletionHandler {
            override fun complete(key: String, info: ResponseInfo?, response: JSONObject?) {
                if (info != null && info.isOK) {
                    mView?.uploadFile(true, key)
                } else {
                    mView?.uploadFile(false, "")
                    mView?.hideLoading()
                }
            }

        })
    }

    override fun issuePlan(params: HashMap<String, Any>) {
        mModel?.issuePlan(params)?.ss(mModel, mView, false) {
            mView?.issuePlan(it.code == 200)
            mView?.hideLoading()
        }
    }

}