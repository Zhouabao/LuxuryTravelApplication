package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.MyCollectionAndLikeContract
import com.sdy.luxurytravelapplication.mvp.model.MyCollectionAndLikeModel

/**
 *    author : ZFM
 *    date   : 2021/3/2411:38
 *    desc   :
 *    version: 1.0
 */
class MyCollectionAndLikePresenter : BasePresenter<MyCollectionAndLikeContract.Model, MyCollectionAndLikeContract.View>(),
    MyCollectionAndLikeContract.Presenter {

    override fun createModel(): MyCollectionAndLikeContract.Model?  = MyCollectionAndLikeModel()
    override fun getMySquare(params: HashMap<String, Any>) {
        mModel?.getMySquare(params)?.ssss(mView,false){
            if (it.code == 200) {
                mView?.onGetSquareListResult(it.data, true)
            } else {
                mView?.onGetSquareListResult(it.data, false)
            }
        }
    }
}