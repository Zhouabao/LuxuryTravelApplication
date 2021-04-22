package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.ext.ssss
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
    override fun createModel(): IndexRecommendContract.Model? = IndexRecommendModel()
    override fun recommendIndex(params: HashMap<String, Any>,type:Int) {
        mModel?.recommendIndex(params,type)?.ssss {
            mView?.recommendIndex(it.data)
        }

    }

    override fun todayRecommend() {
        mModel?.todayRecommend()?.ss(isShowLoading = false) {
            mView?.onTodayRecommendResult(it.data)
        }
    }
}