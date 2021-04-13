package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.MyCommentContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.MyCommentList
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/1219:43
 *    desc   :
 *    version: 1.0
 */
class MyCommentModel:BaseModel(),MyCommentContract.Model {
    override fun myCommentList(params: HashMap<String, Any>): Observable<BaseResp<MyCommentList?>> {

        return RetrofitHelper.service.myCommentList(params)
    }

    override fun deleteComment(
        params: HashMap<String, Any>,
        position: Int
    ): Observable<BaseResp<Any>> {
      return RetrofitHelper.service.destoryComment(params)
    }
}