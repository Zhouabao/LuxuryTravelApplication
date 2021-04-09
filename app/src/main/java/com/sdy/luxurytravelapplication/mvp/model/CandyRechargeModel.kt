package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.CandyRechargeContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.ChargeWayBeans
import com.sdy.luxurytravelapplication.mvp.model.bean.PullWithdrawBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/719:30
 *    desc   :
 *    version: 1.0
 */
class CandyRechargeModel : BaseModel(), CandyRechargeContract.Model {
    override fun myCadny(): Observable<BaseResp<PullWithdrawBean?>> {
        return RetrofitHelper.service.myCadny(hashMapOf())
    }

    override fun giftRechargeList(): Observable<BaseResp<ChargeWayBeans?>> {
        return RetrofitHelper.service.candyRechargeList(hashMapOf())
    }
}