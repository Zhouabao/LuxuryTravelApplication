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
import com.sdy.luxurytravelapplication.mvp.contract.PublishTravelContract
import com.sdy.luxurytravelapplication.mvp.model.PublishModel
import com.sdy.luxurytravelapplication.mvp.model.PublishTravelModel
import com.sdy.luxurytravelapplication.utils.ToastUtil
import org.greenrobot.eventbus.EventBus

/**
 *    author : ZFM
 *    date   : 2021/3/2917:41
 *    desc   :
 *    version: 1.0
 */
class PublishTravelPresenter : BasePresenter<PublishTravelContract.Model, PublishTravelContract.View>(),
    PublishTravelContract.Presenter {
    override fun createModel(): PublishTravelContract.Model? = PublishTravelModel()
    override fun planOptions() {

        mModel?.planOptions()?.ssss(mView){
            mView?.planOptions(it.data)
        }
    }


}