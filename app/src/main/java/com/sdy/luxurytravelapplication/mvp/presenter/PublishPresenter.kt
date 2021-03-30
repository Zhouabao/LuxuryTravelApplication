package com.sdy.luxurytravelapplication.mvp.presenter

import android.util.Log
import com.google.gson.Gson
import com.qiniu.android.storage.UpCompletionHandler
import com.qiniu.android.storage.UpProgressHandler
import com.qiniu.android.storage.UploadOptions
import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.event.UploadEvent
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.ext.startAndResult
import com.sdy.luxurytravelapplication.mvp.contract.PublishContract
import com.sdy.luxurytravelapplication.mvp.model.PublishModel
import com.sdy.luxurytravelapplication.utils.ToastUtil
import org.greenrobot.eventbus.EventBus

/**
 *    author : ZFM
 *    date   : 2021/3/2917:41
 *    desc   :
 *    version: 1.0
 */
class PublishPresenter : BasePresenter<PublishContract.Model, PublishContract.View>(),
    PublishContract.Presenter {
    override fun createModel(): PublishContract.Model? = PublishModel()
    override fun uploadFile(
        totalCount: Int,
        currentCount: Int,
        filePath: String,
        imagePath: String,
        type: Int
    ) {
        mModel?.uploadFile(
            totalCount,
            currentCount,
            filePath,
            imagePath,
            type,
            UpCompletionHandler { key, info, response ->
                if (info != null && !info.isOK) {
                    mView?.onSquareAnnounceResult(1, false)
                }
            }, UploadOptions(
                null, null, false,
                UpProgressHandler { key, percent ->
                    Log.d("OkHttp", "=============percent = $percent============")
                    EventBus.getDefault().postSticky(UploadEvent(totalCount, currentCount, percent))

                    if (percent == 1.0)
                        mView?.onQnUploadResult(true, type, key)
                }, UserManager.cancellationHandler
            )

        )

    }

    override fun publishContent(
        type: Int,
        params: HashMap<String, Any>,
        keyList: MutableList<String>
    ) {
        params["comment"] = Gson().toJson(keyList)
        mModel?.announce(type, params)?.startAndResult(mView, false, {
            if (it.code == 200) {
                if (type == 0) {
                    EventBus.getDefault().postSticky(UploadEvent(1, 1, 1.0))
                }
                mView?.onSquareAnnounceResult(type, true, 200)
            } else {
                mView?.onSquareAnnounceResult(type, false, it.code)
            }
        }, {
            if (type == 0) {
                EventBus.getDefault().postSticky(UploadEvent(1, 1, 0.0))
            }
        })
    }

    override fun checkBlock() {
        mModel?.checkBlock()?.ssss(mView, true) {
            mView?.onCheckBlockResult(it.code == 200)
            if (it.code != 200) {
                ToastUtil.toast(it.msg)
            }
        }
    }

}