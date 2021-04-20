package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.TargetUserContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.MatchBean
import com.sdy.luxurytravelapplication.mvp.model.bean.RecommendSquareListBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/117:30
 *    desc   :
 *    version: 1.0
 */
class TargetUserModel : BaseModel(), TargetUserContract.Model {
    override fun getMatchUserInfo(target_accid: String): Observable<BaseResp<MatchBean?>> {
        return RetrofitHelper.service.getMatchUserInfo(hashMapOf("target_accid" to target_accid))
    }

    override fun someoneSquareCandy(params: HashMap<String, Any>): Observable<BaseResp<RecommendSquareListBean?>> {

        return RetrofitHelper.service.someoneSquareCandy(params)
    }

    override fun shieldingFriend(params: HashMap<String, Any>): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.shieldingFriend(params)
    }

    override fun removeBlock(params: HashMap<String, Any>): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.removeBlock(params)
    }

    override fun dissolutionFriend(params: HashMap<String, Any>): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.dissolutionFriend(params)
    }
}