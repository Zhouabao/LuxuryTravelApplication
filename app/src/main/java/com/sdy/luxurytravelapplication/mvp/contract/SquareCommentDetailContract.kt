package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.AllCommentBean
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/2514:41
 *    desc   :
 *    version: 1.0
 */
interface SquareCommentDetailContract {
    interface View : IView {

        fun onGetCommentListResult(allCommentBean: AllCommentBean?)

        fun onGetSquareLikeResult(result: Boolean)

        fun onLikeCommentResult(data: BaseResp<Any>,position:Int)

        fun onDeleteCommentResult(data: BaseResp<Any>,position:Int)

        fun onReportCommentResult(data: BaseResp<Any>,position:Int)

        fun onGetSquareCollectResult(data: BaseResp<Any>)

        fun onGetSquareReport(data: BaseResp<Any>)

        fun onAddCommentResult(data: BaseResp<Any>, result: Boolean)

        fun onRemoveMySquareResult(b: Boolean)

        fun onGetSquareInfoResults(data: SquareBean?)
    }

    interface Presenter : IPresenter<View> {
        fun getCommentLike(params: HashMap<String, Any>, position: Int)

        fun getSquareInfo(params: HashMap<String, Any>)

        fun getCommentList(params: HashMap<String, Any>, refresh: Boolean)

        fun getSquareLike(params: HashMap<String, Any>, auto: Boolean = false)

        fun getSquareCollect(params: HashMap<String, Any>)

        fun getSquareReport(params: HashMap<String, Any>)

        fun addComment(params: HashMap<String, Any>)

        fun deleteComment(params: HashMap<String, Any>, position: Int)
        fun commentReport(params: HashMap<String, Any>, position: Int)

        fun removeMySquare(params: HashMap<String, Any>)

    }
    interface Model : IModel {
        fun getCommentLike(params: HashMap<String, Any>, position: Int):Observable<BaseResp<Any>>

        fun getSquareInfo(params: HashMap<String, Any>):Observable<BaseResp<SquareBean?>>

        fun getCommentList(params: HashMap<String, Any>):Observable<BaseResp<AllCommentBean?>>

        fun getSquareLike(params: HashMap<String, Any>, auto: Boolean = false):Observable<BaseResp<Any>>

        fun getSquareCollect(params: HashMap<String, Any>):Observable<BaseResp<Any>>

        fun getSquareReport(params: HashMap<String, Any>):Observable<BaseResp<Any>>

        fun addComment(params: HashMap<String, Any>):Observable<BaseResp<Any>>

        fun deleteComment(params: HashMap<String, Any>, position: Int):Observable<BaseResp<Any>>

        fun commentReport(params: HashMap<String, Any>, position: Int):Observable<BaseResp<Any>>

        fun removeMySquare(params: HashMap<String, Any>):Observable<BaseResp<Any>>


    }
}