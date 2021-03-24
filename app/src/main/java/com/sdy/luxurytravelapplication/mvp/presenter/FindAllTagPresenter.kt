package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.FindAllTagContract
import com.sdy.luxurytravelapplication.mvp.model.FindAllTagModel

/**
 *    author : ZFM
 *    date   : 2021/3/2216:00
 *    desc   :
 *    version: 1.0
 */
class FindAllTagPresenter :
    BasePresenter<FindAllTagContract.Model, FindAllTagContract.View>(),
    FindAllTagContract.Presenter {
    override fun createModel(): FindAllTagContract.Model? = FindAllTagModel()
    override fun squareTagList() {
        mModel?.squareTagList()?.ssss(mView, false) {
            mView?.onGetSquareTagResult(it.data, it.code == 200)
        }
    }
}