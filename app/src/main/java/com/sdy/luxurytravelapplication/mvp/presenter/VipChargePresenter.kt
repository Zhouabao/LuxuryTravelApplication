package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.mvp.contract.VipChargeContract
import com.sdy.luxurytravelapplication.mvp.model.VipChargeModel

/**
 *    author : ZFM
 *    date   : 2021/4/819:02
 *    desc   :
 *    version: 1.0
 */
class VipChargePresenter : BasePresenter<VipChargeContract.Model, VipChargeContract.View>(),
    VipChargeContract.Presenter {
    override fun createModel(): VipChargeContract.Model? {
        return VipChargeModel()
    }

    override fun getThreshold() {
        mModel?.getThreshold()?.ss(mModel, mView, true) {
            mView?.getChargeDataResult(it.data)
        }
    }
}