package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.CandyRecordContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.BillBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/911:52
 *    desc   :
 *    version: 1.0
 */
class CandyRecordModel:BaseModel(),CandyRecordContract.Model {
    override fun myBillList(params: HashMap<String, Any>): Observable<BaseResp<MutableList<BillBean>?>> {

        return RetrofitHelper.service.myBillList(params)
    }
}