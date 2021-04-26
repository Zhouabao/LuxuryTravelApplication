package com.sdy.luxurytravelapplication.mvp.presenter

import com.qiniu.android.storage.UpCompletionHandler
import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.sss
import com.sdy.luxurytravelapplication.mvp.contract.LoginInfoContract
import com.sdy.luxurytravelapplication.mvp.model.LoginInfoModel

/**
 *    author : ZFM
 *    date   : 2021/3/1810:51
 *    desc   :
 *    version: 1.0
 */
class LoginInfoPresenter : BasePresenter<LoginInfoContract.Model, LoginInfoContract.View>(),
    LoginInfoContract.Presenter {
    override fun createModel(): LoginInfoContract.Model? = LoginInfoModel()
    override fun setPersonal(params: HashMap<String, Any>) {
        mModel?.setPersonal(params)?.sss(mView, false, {
            mView?.hideLoading()
            mView?.setPersonalResult(it.data, true)
        }, {
            mView?.hideLoading()
            mView?.setPersonalResult(null, false)
        })
    }

    override fun uploadAvatar(filePath: String, imageName: String) {
        mView?.showLoading()
        mModel?.uploadAvatar(filePath, imageName,
            UpCompletionHandler { key, info, response ->
                if (!info.isOK) {
                    mView?.hideLoading()
                } else
                    mView?.uploadAvatarResult(key, !key.isNullOrEmpty())
            })
    }
}