package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.IndexLuxuryContract
import com.sdy.luxurytravelapplication.mvp.model.IndexLuxuryModel

/**
 *    author : ZFM
 *    date   : 2021/3/2216:00
 *    desc   :
 *    version: 1.0
 */
class IndexLuxuryPresenter :
    BasePresenter<IndexLuxuryContract.Model, IndexLuxuryContract.View>(),
    IndexLuxuryContract.Presenter {
    override fun createModel(): IndexLuxuryContract.Model?  = IndexLuxuryModel()
    override fun sweetheart(params: HashMap<String, Any>) {
        mModel?.sweetheart(params)?.ssss {
            mView?.sweetheart(it.data)
        }
    }
}