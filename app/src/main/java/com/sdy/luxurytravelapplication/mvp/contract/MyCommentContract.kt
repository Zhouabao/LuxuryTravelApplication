package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.MyCommentList
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/1219:42
 *    desc   :
 *    version: 1.0
 */
interface MyCommentContract {
    interface View : IView {
        fun onGetCommentListResult(data: MyCommentList?)
        fun onDeleteCommentResult(success:Boolean,position: Int)
    }

    interface Presenter : IPresenter<View> {
        /**
         * 获取评论列表
         */
        fun myCommentList(params: HashMap<String, Any>)
        /**
         * 删除评论
         */
        fun deleteComment(params: HashMap<String, Any>, position: Int)
    }

    interface Model : IModel {
        /**
         * 获取评论列表
         */
        fun myCommentList(params: HashMap<String, Any>):Observable<BaseResp<MyCommentList?>>
        /**
         * 删除评论
         */
        fun deleteComment(params: HashMap<String, Any>, position: Int):Observable<BaseResp<Any>>
    }
}