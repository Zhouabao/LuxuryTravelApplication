package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.MyFindContentContract
import com.sdy.luxurytravelapplication.mvp.model.FindContentModel
import com.sdy.luxurytravelapplication.mvp.model.MyFindContentModel

/**
 *    author : ZFM
 *    date   : 2021/3/2411:38
 *    desc   :
 *    version: 1.0
 */
class MyFindContentPresenter : BasePresenter<MyFindContentContract.Model, MyFindContentContract.View>(),
    MyFindContentContract.Presenter {

    override fun createModel(): MyFindContentContract.Model?  = MyFindContentModel()
    override fun squareEliteList(params: HashMap<String, Any>) {
        mModel?.squareEliteList(params)?.ssss(mView,false){
            if (it.code == 200) {
                mView?.onGetSquareRecommendResult(it.data, true)
            } else {
                mView?.onGetSquareRecommendResult(it.data, false)
            }
        }
    }

    override fun checkBlock() {
        mModel?.checkBlock()?.ss(mModel,mView,true){
            mView?.onCheckBlockResult(true)
        }
    }
}