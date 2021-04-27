package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.UserCenterContract
import com.sdy.luxurytravelapplication.mvp.model.UserCenterModel

/**
 *    author : ZFM
 *    date   : 2021/4/810:55
 *    desc   :
 *    version: 1.0
 */
class UserCenterPresenter : BasePresenter<UserCenterContract.Model, UserCenterContract.View>(),
    UserCenterContract.Presenter {
    override fun createModel(): UserCenterContract.Model? {
        return UserCenterModel()
    }

    override fun myInfoCandy() {

        mModel?.myInfoCandy()?.ssss(mView,false) {
            mView?.onGetMyInfoResult(it.data)
        }
    }
}