package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.MyTravelContract
import com.sdy.luxurytravelapplication.mvp.model.MyTravelModel
import com.sdy.luxurytravelapplication.mvp.model.TravelModel

/**
 *    author : ZFM
 *    date   : 2021/3/3017:13
 *    desc   :
 *    version: 1.0
 */
class MyTravelPresenter : BasePresenter<MyTravelContract.Model, MyTravelContract.View>(),
    MyTravelContract.Presenter {
    override fun createModel(): MyTravelContract.Model? = MyTravelModel()
    override fun checkPlan() {
        mModel?.checkPlan()?.ss(mModel,mView, true) {
            mView?.checkPlan(it.code == 200)
        }

    }

    override fun planList(params: HashMap<String, Any>) {
        mModel?.planList(params)?.ssss(mView, true) {
            mView?.planList(it.code == 200, it.data ?: mutableListOf())
        }
    }
}