package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.BindAlipayAccountContract
import com.sdy.luxurytravelapplication.mvp.model.BindAlipayAccountModel

/**
 *    author : ZFM
 *    date   : 2021/4/915:47
 *    desc   :
 *    version: 1.0
 */
class BindAlipayAccountPresenter :
    BasePresenter<BindAlipayAccountContract.Model, BindAlipayAccountContract.View>(),
    BindAlipayAccountContract.Presenter {
    override fun createModel(): BindAlipayAccountContract.Model? {
        return BindAlipayAccountModel()
    }

    override fun saveWithdrawAccount(params: HashMap<String, Any>) {
        mModel?.saveWithdrawAccount(params)?.ssss {
            mView?.saveWithdrawAccountResult(it.code == 200, it.data)
        }
    }
}