package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.MyTravelContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.TravelPlanBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/3017:13
 *    desc   :
 *    version: 1.0
 */
class MyTravelModel : BaseModel(), MyTravelContract.Model {


    override fun planList(params: HashMap<String, Any>): Observable<BaseResp<MutableList<TravelPlanBean>?>> {
        return RetrofitHelper.service.myPlan(params)
    }
}