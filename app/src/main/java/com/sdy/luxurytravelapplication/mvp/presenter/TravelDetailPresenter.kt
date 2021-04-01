package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.TravelContract
import com.sdy.luxurytravelapplication.mvp.contract.TravelDetailContract
import com.sdy.luxurytravelapplication.mvp.model.TravelDetailModel
import com.sdy.luxurytravelapplication.mvp.model.TravelModel

/**
 *    author : ZFM
 *    date   : 2021/3/3017:13
 *    desc   :
 *    version: 1.0
 */
class TravelDetailPresenter : BasePresenter<TravelDetailContract.Model, TravelDetailContract.View>(),
    TravelDetailContract.Presenter {
    override fun createModel(): TravelDetailContract.Model? = TravelDetailModel()
    override fun planInfo(dating_id: Int) {
        mModel?.planInfo(dating_id)?.ssss(mView){
            mView?.planInfo(it.data)
        }
    }

}