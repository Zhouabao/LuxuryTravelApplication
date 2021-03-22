package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.PurchaseFootContract
import com.sdy.luxurytravelapplication.utils.ToastUtil

/**
 *    author : ZFM
 *    date   : 2021/3/229:47
 *    desc   :
 *    version: 1.0
 */
class PurchaseFootPresenter :
    BasePresenter<PurchaseFootContract.Model, PurchaseFootContract.View>(),
    PurchaseFootContract.Presenter {
    override fun getThreshold(params: HashMap<String, Any>) {
        mModel?.getThreshold(params)?.ssss(mView, false) {
            if (it.code == 200)
                mView?.getThresholdResult(it.data)
            else
                ToastUtil.toast(it.msg)
        }
    }
}