package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.CandyRecordContract
import com.sdy.luxurytravelapplication.mvp.model.CandyRecordModel

/**
 *    author : ZFM
 *    date   : 2021/4/911:52
 *    desc   :
 *    version: 1.0
 */
class CandyRecordPresenter : BasePresenter<CandyRecordContract.Model, CandyRecordContract.View>(),
    CandyRecordContract.Presenter {
    override fun createModel(): CandyRecordContract.Model? = CandyRecordModel()
    override fun myBillList(params: HashMap<String, Any>) {
        mModel?.myBillList(params)?.ssss {
            mView?.onMyBillList(it.code == 200, it.data ?: mutableListOf())
        }
    }
}