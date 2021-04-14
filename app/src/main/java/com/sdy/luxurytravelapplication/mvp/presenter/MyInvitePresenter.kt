package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.MyInviteContract
import com.sdy.luxurytravelapplication.mvp.model.MyInviteModel

/**
 *    author : ZFM
 *    date   : 2021/4/1416:48
 *    desc   :
 *    version: 1.0
 */
class MyInvitePresenter : BasePresenter<MyInviteContract.Model, MyInviteContract.View>(),
    MyInviteContract.Presenter {
    override fun createModel(): MyInviteContract.Model? {

        return MyInviteModel()
    }

    override fun myInvite() {
        mModel?.myInvite()?.ssss {
            mView?.myInvite(it.data)
        }
    }
}