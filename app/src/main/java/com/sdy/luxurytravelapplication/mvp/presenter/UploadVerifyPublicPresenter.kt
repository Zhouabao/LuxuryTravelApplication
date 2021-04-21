package com.sdy.luxurytravelapplication.mvp.presenter

import com.qiniu.android.storage.UpCompletionHandler
import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.UploadVerifyPublicContract
import com.sdy.luxurytravelapplication.mvp.model.UploadVerifyPublicModel
import com.sdy.luxurytravelapplication.utils.ToastUtil

/**
 *    author : ZFM
 *    date   : 2021/3/2317:11
 *    desc   :
 *    version: 1.0
 */
class UploadVerifyPublicPresenter :
    BasePresenter<UploadVerifyPublicContract.Model, UploadVerifyPublicContract.View>(),
    UploadVerifyPublicContract.Presenter {
    override fun createModel(): UploadVerifyPublicContract.Model? = UploadVerifyPublicModel()

    override fun uploadData(public_type: Int, type: Int, photo: String, content: String) {

        mModel?.uploadData(public_type, type, photo, content)?.ssss {
            mView?.hideLoading()
            mView?.uploadDataResult(it.code == 200)
            if (it.code != 200)
                ToastUtil.toast(it.msg)
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

    override fun getPicTpl(type: Int) {
        mModel?.getPicTpl(type)?.ss {
            mView?.getPicTplResult(it.data)
        }
    }
}