package com.sdy.luxurytravelapplication.mvp.presenter

import com.netease.nimlib.sdk.RequestCallbackWrapper
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.msg.model.RecentContact
import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.MessageContract
import com.sdy.luxurytravelapplication.mvp.model.MessageModel

/**
 *    author : ZFM
 *    date   : 2021/4/614:49
 *    desc   :
 *    version: 1.0
 */
class MessagePresenter : BasePresenter<MessageContract.Model, MessageContract.View>(),
    MessageContract.Presenter {

    override fun createModel(): MessageContract.Model? = MessageModel()
    override fun messageCensus() {
        mModel?.messageCensus()?.ssss {
            mView?.messageCensus(it.data)
        }

    }

    override fun getRecentContacts() {
        mModel?.getRecentContacts(object : RequestCallbackWrapper<MutableList<RecentContact>>() {
            override fun onResult(
                code: Int,
                result: MutableList<RecentContact>?,
                exception: Throwable?
            ) {
                if (code != ResponseCode.RES_SUCCESS.toInt() || result == null) {
                    return
                }
                mView?.getRecentContacts(result)
            }

        })

    }
}