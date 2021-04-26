package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.AllCommentBean
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.TravelPlanBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/3017:11
 *    desc   :
 *    version: 1.0
 */
interface TravelDetailContract {
    interface View : IView {
        fun planInfo(travelPlanBean: TravelPlanBean?)
        fun getcommentPlan(data: AllCommentBean?)
        fun addCommentPlan(success: Int, msg: String)
        fun commentPlanLike(success: Int, msg: String,position:Int)
        fun delCommentPlan(success: Int, msg: String,position:Int)

        fun commentReport(data: BaseResp<Any>,position:Int)
    }

    interface Presenter : IPresenter<View> {
        fun planInfo(dating_id: Int)
        fun getcommentPlan(params: HashMap<String, Any>)
        fun addCommentPlan(params: HashMap<String, Any>)
        fun commentPlanLike(params: HashMap<String, Any>,position:Int)
        fun delCommentPlan(id: Int,position:Int)
        fun commentReport(params: HashMap<String, Any>, position: Int)
    }

    interface Model : IModel {
        fun planInfo(dating_id: Int): Observable<BaseResp<TravelPlanBean?>>
        fun getcommentPlan(params: HashMap<String, Any>): Observable<BaseResp<AllCommentBean?>>
        fun addCommentPlan(params: HashMap<String, Any>): Observable<BaseResp<Any>>
        fun commentPlanLike(params: HashMap<String, Any>): Observable<BaseResp<Any>>
        fun delCommentPlan(id: Int): Observable<BaseResp<Any>>
        fun commentReport(params: HashMap<String, Any>, position: Int):Observable<BaseResp<Any>>
    }
}