package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.ext.sss
import com.sdy.luxurytravelapplication.mvp.contract.MainContract
import com.sdy.luxurytravelapplication.mvp.model.MainModel

/**
 *    author : ZFM
 *    date   : 2021/3/1710:25
 *    desc   :
 *    version: 1.0
 */
class MainPresenter : BasePresenter<MainContract.Model, MainContract.View>(),
    MainContract.Presenter {
    override fun createModel(): MainContract.Model? = MainModel()

    override fun logout() {
        mModel?.logout()?.ss(mModel, mView) {
            mView?.showLogoutSuccess(it.code == 200)
        }

    }

}