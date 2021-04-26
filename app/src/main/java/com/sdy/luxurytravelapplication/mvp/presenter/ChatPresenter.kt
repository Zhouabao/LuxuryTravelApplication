package com.sdy.luxurytravelapplication.mvp.presenter

import com.netease.nimlib.sdk.msg.model.IMMessage
import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.ChatContract
import com.sdy.luxurytravelapplication.mvp.model.ChatModel

/**
 *    author : ZFM
 *    date   : 2021/3/1817:01
 *    desc   :
 *    version: 1.0
 */
class ChatPresenter : BasePresenter<ChatContract.Model, ChatContract.View>(),
    ChatContract.Presenter {
    override fun createModel(): ChatContract.Model? = ChatModel()
//    override fun focus(target_accid: String, state: Int) {
//        mModel?.focus(target_accid, state)?.sss(mView, false, {
//            mView?.focusResult(true, state == 1)
//        }, {
//            mView?.focusResult(false, state == 1)
//        })
//    }

    override fun getTargetInfo(accid: String) {
        mModel?.getTargetInfo(accid)?.ssss(mView, true) {
            mView?.getTargetInfoResult(it.data, it.code,it.msg)
        }
    }

    override fun sendMsgRequest(
        content: IMMessage,
        target_accid: String,
        qnMediaUrl: String,
        islocation: Boolean
    ) {

        mModel?.sendMsgRequest(content, target_accid, qnMediaUrl, islocation)?.ssss(mView, false) {
            mView?.sendMsgResult(it.code, it.data, it.msg, content)
        }
    }

    override fun aideSendMsg(imMessage: IMMessage) {
        mModel?.aideSendMsg(imMessage)?.ssss(mView, false) {
        }
    }

    override fun addReport(imMessage: IMMessage) {
        mModel?.addReport(imMessage)?.ssss {
            mView?.addReport(it.msg)
        }
    }

    override fun uploadImgToQN(content: IMMessage, target_accid: String, imageUrl: String) {
        val key = mModel?.uploadImgToQN(content, target_accid, imageUrl)
        mView?.uploadImgToQNResult(!key.isNullOrEmpty(), key ?: "", content, target_accid)

    }
}