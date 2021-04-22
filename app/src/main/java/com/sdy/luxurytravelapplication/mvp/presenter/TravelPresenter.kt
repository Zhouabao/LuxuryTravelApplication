package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.TravelContract
import com.sdy.luxurytravelapplication.mvp.model.TravelModel

/**
 *    author : ZFM
 *    date   : 2021/3/3017:13
 *    desc   :
 *    version: 1.0
 */
class TravelPresenter : BasePresenter<TravelContract.Model, TravelContract.View>(),
    TravelContract.Presenter {
    override fun createModel(): TravelContract.Model? = TravelModel()


    override fun planList(params: HashMap<String, Any>) {
        mModel?.planList(params)?.ssss(mView, true) {
            mView?.planList(it.code == 200, it.data ?: mutableListOf())
        }
    }
}