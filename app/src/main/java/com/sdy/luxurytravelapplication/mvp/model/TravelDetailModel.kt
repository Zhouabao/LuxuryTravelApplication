package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.TravelDetailContract
import com.sdy.luxurytravelapplication.mvp.model.bean.AllCommentBean
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

    override fun getcommentPlan(params: HashMap<String, Any>): Observable<BaseResp<AllCommentBean?>> {

        return RetrofitHelper.service.getcommentPlan(params)
    }

    override fun addCommentPlan(params: HashMap<String, Any>): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.addCommentPlan(params)

    }

    override fun commentPlanLike(params: HashMap<String, Any>): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.commentPlanLike(params)
    }

    override fun delCommentPlan(id: Int): Observable<BaseResp<Any>> {

        return RetrofitHelper.service.delCommentPlan(hashMapOf("id" to id))
    }

    override fun commentReport(
        params: HashMap<String, Any>,
        position: Int
    ): Observable<BaseResp<Any>> {

        return RetrofitHelper.service.addReport(params)
    }

}