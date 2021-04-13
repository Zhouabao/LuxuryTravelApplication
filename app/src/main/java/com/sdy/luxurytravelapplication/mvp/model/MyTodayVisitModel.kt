package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.MyTodayVisitContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.VisitorBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/1220:54
 *    desc   :
 *    version: 1.0
 */
class MyTodayVisitModel : BaseModel(), MyTodayVisitContract.Model {
    override fun myVisitedList(params: HashMap<String, Any>): Observable<BaseResp<MutableList<VisitorBean>?>> {

        return RetrofitHelper.service.myVisitingList(params)
    }

}