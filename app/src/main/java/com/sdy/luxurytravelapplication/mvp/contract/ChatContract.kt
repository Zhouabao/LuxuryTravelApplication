package com.sdy.luxurytravelapplication.mvp.contract

import com.netease.nimlib.sdk.msg.model.IMMessage
import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.ChatInfoBean
import com.sdy.luxurytravelapplication.mvp.model.bean.FocusBean
import com.sdy.luxurytravelapplication.mvp.model.bean.SendMsgBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/1816:58
 *    desc   :
 *    version: 1.0
 */
interface ChatContract {
    interface Presenter : IPresenter<View> {

        fun getTargetInfo(accid: String)
        fun sendMsgRequest(
            content: IMMessage,
            target_accid: String,
            qnMediaUrl: String = "",
            islocation: Boolean = false
        )

        fun aideSendMsg(imMessage: IMMessage)

        fun uploadImgToQN(content: IMMessage, target_accid: String, imageUrl: String)
    }

    interface Model : IModel {

        fun getTargetInfo(accid: String): Observable<BaseResp<ChatInfoBean>>
        fun sendMsgRequest(
            content: IMMessage,
            target_accid: String,
            qnMediaUrl: String = "",
            islocation: Boolean = false
        ): Observable<BaseResp<SendMsgBean>>

        fun aideSendMsg(imMessage: IMMessage): Observable<BaseResp<Any>>

        fun uploadImgToQN(content: IMMessage, target_accid: String, imageUrl: String): String
    }

    interface View : IView {


        fun getTargetInfoResult(voiceBean: ChatInfoBean?, code: Int,msg: String)

        fun uploadImgToQNResult(
            isOk: Boolean,
            key: String = "",
            content: IMMessage,
            targetAccid: String
        )

        fun sendMsgResult(
            code: Int,
            residueCountBean: SendMsgBean? = null,
            msg: String,
            content: IMMessage
        )
    }

}