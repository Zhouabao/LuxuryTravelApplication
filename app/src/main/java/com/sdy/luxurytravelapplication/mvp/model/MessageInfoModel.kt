package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.MessageInfoContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/712:25
 *    desc   :
 *    version: 1.0
 */
class MessageInfoModel : BaseModel(), MessageInfoContract.Model {
    override fun addStarTarget(target_accid: String): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.addStarTarget(hashMapOf("target_accid" to target_accid))
    }

    override fun dissolutionFriend(target_accid: String): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.removeFriend(hashMapOf("target_accid" to target_accid))
    }

    override fun removeStarTarget(target_accid: String): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.removeStarTarget(hashMapOf("target_accid" to target_accid))
    }

}