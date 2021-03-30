package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.ChooseTitleContract
import com.sdy.luxurytravelapplication.mvp.model.ChooseTitleModel

/**
 *    author : ZFM
 *    date   : 2021/3/3010:51
 *    desc   :
 *    version: 1.0
 */
class ChooseTitlePresenter : BasePresenter<ChooseTitleContract.Model, ChooseTitleContract.View>(),
    ChooseTitleContract.Presenter {
    override fun createModel(): ChooseTitleContract.Model? = ChooseTitleModel()
    override fun getTagTitleList(page: Int) {
        mModel?.getTagTitleList(page)?.ssss(mView, false) {
            mView?.getTagTraitInfoResult(it.code == 200, it.data.list, it.data.limit_cnt ?: 0)
        }

    }
}