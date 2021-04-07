package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.mvp.contract.UploadVerifyPublicContract
import com.sdy.luxurytravelapplication.mvp.model.UploadVerifyPublicModel

/**
 *    author : ZFM
 *    date   : 2021/3/2317:11
 *    desc   :
 *    version: 1.0
 */
class UploadVerifyPublicPresenter :
    BasePresenter<UploadVerifyPublicContract.Model, UploadVerifyPublicContract.View>(),
    UploadVerifyPublicContract.Presenter {
    override fun createModel(): UploadVerifyPublicContract.Model? =UploadVerifyPublicModel()
}