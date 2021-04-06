package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.MessageSquareContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareMsgBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/617:27
 *    desc   :
 *    version: 1.0
 */
class MessageSquareModel : BaseModel(), MessageSquareContract.Model {
    override fun squareLists(params: HashMap<String, Any>): Observable<BaseResp<MutableList<SquareMsgBean>>> {
        return RetrofitHelper.service.squareLists(params)
    }

    override fun markSquareRead(params: HashMap<String, Any>): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.markSquareRead(params)
    }

    override fun delSquareMsg(params: HashMap<String, Any>): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.delSquareMsg(params)
    }

}