package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.JoinLuxuryContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/219:41
 *    desc   :
 *    version: 1.0
 */
class JoinLuxuryModel:BaseModel() ,JoinLuxuryContract.Model{
    override fun joinSweetApply(): Observable<BaseResp<Any>> {

        return RetrofitHelper.service.joinSweetApply(hashMapOf())
    }
}