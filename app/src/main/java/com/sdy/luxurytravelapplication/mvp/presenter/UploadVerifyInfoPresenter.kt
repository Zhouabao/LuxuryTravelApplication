package com.sdy.luxurytravelapplication.mvp.presenter

import com.qiniu.android.storage.UpCompletionHandler
import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.UploadVerifyInfoContract
import com.sdy.luxurytravelapplication.mvp.model.UploadVerifyInfoModel
import com.sdy.luxurytravelapplication.utils.ToastUtil

/**
 *    author : ZFM
 *    date   : 2021/3/2313:45
 *    desc   :
 *    version: 1.0
 */
class UploadVerifyInfoPresenter :
    BasePresenter<UploadVerifyInfoContract.Model, UploadVerifyInfoContract.View>(),
    UploadVerifyInfoContract.Presenter {
    override fun createModel(): UploadVerifyInfoContract.Model? = UploadVerifyInfoModel()
    override fun uploadData(public_type: Int, type: Int, photo: String) {
        mModel?.uploadData(public_type, type, photo)?.ssss {
            mView?.hideLoading()
            mView?.uploadDataResult(it.code == 200)
            if (it.code != 200) {
                ToastUtil.toast(it.msg)
            }
        }
    }

    override fun uploadPhoto(filePath: String, index: Int) {
        mView?.showLoading()
        mModel?.uploadPhoto(filePath, UpCompletionHandler { key, info, response ->
            if (info != null && info.isOK) {
                mView?.uploadImgResult(info.isOK, key, index)
            } else {
                mView?.hideLoading()
                mView?.uploadImgResult(false, key, index)
            }
        })
    }
}