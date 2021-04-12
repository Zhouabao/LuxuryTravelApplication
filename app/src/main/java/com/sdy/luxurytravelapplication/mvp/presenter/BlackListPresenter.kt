package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.BlackListContract
import com.sdy.luxurytravelapplication.mvp.model.BlackListModel

/**
 *    author : ZFM
 *    date   : 2021/4/1212:20
 *    desc   :
 *    version: 1.0
 */
class BlackListPresenter : BasePresenter<BlackListContract.Model, BlackListContract.View>(),
    BlackListContract.Presenter {
    override fun createModel(): BlackListContract.Model? {
        return BlackListModel()
    }

    override fun myShieldingList(params: HashMap<String, Any>) {
        mModel?.myShieldingList(params)?.ssss {
            mView?.onMyShieldingListResult(it.data)
        }
    }

    override fun removeBlock(params: HashMap<String, Any>, position: Int) {
        mModel?.removeBlock(params)?.ssss {
            mView?.onRemoveBlockResult(it.code == 200, position)
        }
    }
}