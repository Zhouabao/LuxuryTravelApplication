package com.sdy.luxurytravelapplication.mvp.model

import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallbackWrapper
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.model.RecentContact
import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.AccostListContract
import com.sdy.luxurytravelapplication.mvp.model.bean.AccostListBean
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/616:42
 *    desc   :
 *    version: 1.0
 */
class AccostListModel : BaseModel(), AccostListContract.Model {
    override fun getRecentContacts(requestCallback: RequestCallbackWrapper<MutableList<RecentContact>>) {
        NIMClient.getService(MsgService::class.java)
            .queryRecentContacts()
            .setCallback(requestCallback)
    }

    override fun chatupList(params: HashMap<String, Any>): Observable<BaseResp<AccostListBean?>> {
        return RetrofitHelper.service.chatupList(params)
    }

    override fun delChat(params: HashMap<String, Any>, position: Int): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.delChat(params)
    }
}