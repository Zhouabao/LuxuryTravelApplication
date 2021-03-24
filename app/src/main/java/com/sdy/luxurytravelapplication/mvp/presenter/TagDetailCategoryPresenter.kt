package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.TagDetailCategoryContract
import com.sdy.luxurytravelapplication.mvp.model.TagDetailCategoryModel

/**
 *    author : ZFM
 *    date   : 2021/3/2419:45
 *    desc   :
 *    version: 1.0
 */
class TagDetailCategoryPresenter :
    BasePresenter<TagDetailCategoryContract.Model, TagDetailCategoryContract.View>(),
    TagDetailCategoryContract.Presenter {
    override fun createModel(): TagDetailCategoryContract.Model? = TagDetailCategoryModel()
    override fun squareTagInfo(params: HashMap<String, Any>) {
        mModel?.squareTagInfo(params)?.ssss(mView, false) {
            mView?.onGetSquareRecommendResult(it.data, it.code == 200)
        }
    }

    override fun checkBlock() {
        mModel?.checkBlock()?.ssss {
            mView?.onCheckBlockResult(it.code==200)
        }
    }
}