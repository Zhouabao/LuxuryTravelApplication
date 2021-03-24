package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.FindContentContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.RecommendSquareListBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/2411:37
 *    desc   :
 *    version: 1.0
 */
class FindContentModel:BaseModel(),FindContentContract.Model {
    override fun squareEliteList(params: HashMap<String, Any>): Observable<BaseResp<RecommendSquareListBean?>> {
        return RetrofitHelper.service.squareEliteList(params)
    }
}