package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.VipChargeContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.ChargeWayBeans
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/819:02
 *    desc   :
 *    version: 1.0
 */
class VipChargeModel : BaseModel(), VipChargeContract.Model {
    override fun getThreshold(): Observable<BaseResp<ChargeWayBeans>> {
        return RetrofitHelper.service.getThreshold(hashMapOf())

    }
}