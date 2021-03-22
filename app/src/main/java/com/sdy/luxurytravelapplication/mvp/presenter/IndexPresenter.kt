package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.mvp.contract.IndexContract
import com.sdy.luxurytravelapplication.mvp.model.IndexModel

/**
 *    author : ZFM
 *    date   : 2021/3/2214:20
 *    desc   :
 *    version: 1.0
 */
class IndexPresenter : BasePresenter<IndexContract.Model, IndexContract.View>(),
    IndexContract.Presenter {

    override fun createModel(): IndexContract.Model? = IndexModel()
    override fun indexTop(params: HashMap<String, Any>) {
        mModel?.indexTop(params)?.ss(mModel,mView,false) {
            mView?.indexTop(it.data)
        }
    }
}