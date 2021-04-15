package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.InviteCodeContract
import com.sdy.luxurytravelapplication.mvp.contract.PurchaseFootContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.ChargeWayBeans
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/229:48
 *    desc   :
 *    version: 1.0
 */
class InviteCodeModel : BaseModel(), InviteCodeContract.Model {


    override fun checkCode(code: String): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.checkCode(hashMapOf("code" to code))

    }
}