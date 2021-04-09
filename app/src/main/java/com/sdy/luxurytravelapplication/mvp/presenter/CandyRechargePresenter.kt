package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.CandyRechargeContract
import com.sdy.luxurytravelapplication.mvp.model.CandyRechargeModel

/**
 *    author : ZFM
 *    date   : 2021/4/719:30
 *    desc   :
 *    version: 1.0
 */
class CandyRechargePresenter :
    BasePresenter<CandyRechargeContract.Model, CandyRechargeContract.View>(),
    CandyRechargeContract.Presenter {
    override fun createModel(): CandyRechargeContract.Model? = CandyRechargeModel()
    override fun giftRechargeList() {
        mModel?.giftRechargeList()?.ssss{
            mView?.giftRechargeList(it.data)
        }
    }

    override fun myCadny() {

        mModel?.myCadny()?.ssss (mView, true) { mView?.onMyCadnyResult(it.data) }
    }
}