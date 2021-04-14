package com.sdy.luxurytravelapplication.mvp.presenter

import android.util.Log
import com.qiniu.android.storage.UpCompletionHandler
import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.VideoIntroduceContract
import com.sdy.luxurytravelapplication.mvp.model.VideoIntroduceModel
import com.sdy.luxurytravelapplication.utils.ToastUtil
import java.util.*

/**
 *    author : ZFM
 *    date   : 2021/4/1411:13
 *    desc   :
 *    version: 1.0
 */
class VideoIntroducePresenter :
    BasePresenter<VideoIntroduceContract.Model, VideoIntroduceContract.View>(),
    VideoIntroduceContract.Presenter {
    override fun createModel(): VideoIntroduceContract.Model? {

        return VideoIntroduceModel()
    }

    override fun uploadMv(params: HashMap<String, Any>) {
        mModel?.uploadMv(params)?.ss(mModel, mView, true) {
            mView?.uploadMvResult(it.code == 200)
        }
    }

    override fun getVideoNormal() {
        mModel?.getVideoNormal()?.ssss {
            mView?.getVideoNormalResult(it.data)
        }
    }

    override fun uploadProfile(filePath: String, type: Int, normal_id: Int) {
        mView?.showLoading()
        mModel?.uploadProfile(filePath, UpCompletionHandler { key, info, response ->
            Log.d("OkHttp", "key=$key\ninfo=$info\nresponse=$response")
            if (info != null && info.isOK) {
                //视频上传成功
                uploadMv(
                    hashMapOf(
                        "mv_url" to key,
                        "type" to type,
                        "normal_id" to normal_id
                    )
                )
            } else {
                ToastUtil.toast("视频提交失败，请重新进入录制")
            }
        })
    }
}