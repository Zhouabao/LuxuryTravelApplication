package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.FindContentContract
import com.sdy.luxurytravelapplication.mvp.model.FindContentModel

/**
 *    author : ZFM
 *    date   : 2021/3/2411:38
 *    desc   :
 *    version: 1.0
 */
class FindContentPresenter : BasePresenter<FindContentContract.Model, FindContentContract.View>(),
    FindContentContract.Presenter {

    override fun createModel(): FindContentContract.Model? = FindContentModel()
    override fun squareEliteList(params: HashMap<String, Any>, type: Int) {
        mModel?.squareEliteList(params, type)?.ssss(mView, false) {
            if (it.code == 200) {
                mView?.onGetSquareRecommendResult(it.data, true)
            } else {
                mView?.onGetSquareRecommendResult(it.data, false)
            }
        }
    }
}