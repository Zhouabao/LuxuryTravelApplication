package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.CustomChatUpContentContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/1919:16
 *    desc   :
 *    version: 1.0
 */
class CustomChatUpContentModel : BaseModel(), CustomChatUpContentContract.Model {
    override fun saveChatupMsg(aboutme: String): Observable<BaseResp<Any>> {

        return RetrofitHelper.service.saveChatupMsg(hashMapOf("content" to aboutme))
    }
}