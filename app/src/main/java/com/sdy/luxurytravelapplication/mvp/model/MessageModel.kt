package com.sdy.luxurytravelapplication.mvp.model

import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallbackWrapper
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.model.RecentContact
import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.MessageContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.MessageListBean1
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/614:50
 *    desc   :
 *    version: 1.0
 */
class MessageModel : BaseModel(), MessageContract.Model {
    override fun getRecentContacts(requestCallback: RequestCallbackWrapper<MutableList<RecentContact>>) {
      NIMClient.getService(MsgService::class.java)
          .queryRecentContacts()
          .setCallback(requestCallback)
    }

    override fun messageCensus(): Observable<BaseResp<MessageListBean1?>> {

        return  RetrofitHelper.service.messageCensus(hashMapOf())
    }
}