package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.SquareCommentDetailContract
import com.sdy.luxurytravelapplication.mvp.model.bean.AllCommentBean
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/2514:42
 *    desc   :
 *    version: 1.0
 */
class SquareCommentDetailModel: BaseModel(),SquareCommentDetailContract.Model {
    override fun getCommentLike(
        params: HashMap<String, Any>,
        position: Int
    ): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.commentLikes(params)
    }

    override fun getSquareInfo(params: HashMap<String, Any>): Observable<BaseResp<SquareBean?>> {
       return RetrofitHelper.service.getSquareInfo(params)
    }

    override fun getCommentList(params: HashMap<String, Any>): Observable<BaseResp<AllCommentBean?>> {
        return RetrofitHelper.service.getCommentLists(params)
    }

    override fun getSquareLike(
        params: HashMap<String, Any>,
        auto: Boolean
    ): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.getSquareLike(params)
    }

    override fun getSquareCollect(params: HashMap<String, Any>): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.getSquareCollect(params)
    }

    override fun getSquareReport(params: HashMap<String, Any>): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.getSquareReport(params)
    }

    override fun addComment(params: HashMap<String, Any>): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.addComment(params)
    }

    override fun deleteComment(
        params: HashMap<String, Any>,
        position: Int
    ): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.destoryComment(params)
    }

    override fun commentReport(
        params: HashMap<String, Any>,
        position: Int
    ): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.commentReport(params)
    }

    override fun removeMySquare(params: HashMap<String, Any>): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.removeMySquare(params)
    }
}