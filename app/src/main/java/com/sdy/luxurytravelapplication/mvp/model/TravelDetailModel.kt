package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.TravelContract
import com.sdy.luxurytravelapplication.mvp.contract.TravelDetailContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.TravelPlanBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/3017:13
 *    desc   :
 *    version: 1.0
 */
class TravelDetailModel : BaseModel(), TravelDetailContract.Model {
    override fun planInfo(dating_id: Int): Observable<BaseResp<TravelPlanBean?>> {
        return RetrofitHelper.service.planInfo(hashMapOf("dating_id" to dating_id))
    }

}