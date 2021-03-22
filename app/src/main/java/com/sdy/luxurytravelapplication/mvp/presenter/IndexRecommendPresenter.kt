package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.mvp.contract.IndexRecommendContract
import com.sdy.luxurytravelapplication.mvp.model.IndexRecommendModel

/**
 *    author : ZFM
 *    date   : 2021/3/2216:00
 *    desc   :
 *    version: 1.0
 */
class IndexRecommendPresenter :
    BasePresenter<IndexRecommendContract.Model, IndexRecommendContract.View>(),
    IndexRecommendContract.Presenter {
    override fun createModel(): IndexRecommendContract.Model?  = IndexRecommendModel()
}