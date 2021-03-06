package com.sdy.luxurytravelapplication.mvp.model

import com.netease.nimlib.sdk.msg.attachment.AudioAttachment
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.QNUploadManager
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.ChatContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.ChatInfoBean
import com.sdy.luxurytravelapplication.mvp.model.bean.SendMsgBean
import com.sdy.luxurytravelapplication.utils.RandomUtils
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/1817:02
 *    desc   :
 *    version: 1.0
 */
class ChatModel : BaseModel(), ChatContract.Model {
//    override fun focus(target_accid: String, state: Int): Observable<BaseResp<FocusBean>> {
//        return RetrofitHelper.service.memberFocus(
//            hashMapOf(
//                "target_accid" to target_accid,
//                "type" to state
//            )
//        )
//    }

    override fun getTargetInfo(accid: String): Observable<BaseResp<ChatInfoBean>> {
        return RetrofitHelper.service.getTargetInfo(hashMapOf("target_accid" to accid))

    }

    override fun sendMsgRequest(
        content: IMMessage,
        target_accid: String,
        qnMediaUrl: String,
        islocation: Boolean
    ): Observable<BaseResp<SendMsgBean>> {
        return RetrofitHelper.service.sendMsg(
            hashMapOf<String, Any>(
                "target_accid" to target_accid,
                "type" to if (islocation) {
                    MsgTypeEnum.location.value
                } else {
                    content.msgType.value
                },
                "content" to if (content.msgType == MsgTypeEnum.text) {
                    content.content
                } else {
                    qnMediaUrl
                }
            )
        )
    }

    override fun aideSendMsg(imMessage: IMMessage): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.aideSendMsg(hashMapOf("content" to imMessage.content))
    }

    override fun addReport(imMessage: IMMessage): Observable<BaseResp<Any>> {
        val params = hashMapOf<String, Any>()
        /**
         * type	1通话举报 2主页举报 3聊天内容举报 4广场动态举报 5广场评论举报 8旅行计划评论举报
         * content  当type为3和5 为举报内容 为4 广场动态的id
         * photo 举报图片json串
         * case_type 返回举报类型【色情涉黄/广告或垃圾信息。。。】
         */
        params["type"] = 3
        params["target_accid"] = imMessage.fromAccount
        params["content"] = when (imMessage.msgType) {
            MsgTypeEnum.image -> (imMessage.attachment as ImageAttachment).url
            MsgTypeEnum.video -> (imMessage.attachment as VideoAttachment).url
            MsgTypeEnum.audio -> (imMessage.attachment as AudioAttachment).url
            else -> imMessage.content
        }
        return RetrofitHelper.service.addReport(params)

    }

    override fun uploadImgToQN(content: IMMessage, target_accid: String, imageUrl: String): String {
        var keys =
            "${Constants.FILE_NAME_INDEX}${Constants.CHATCHECK}${UserManager.accid}/${System.currentTimeMillis()}/${RandomUtils.getRandomString(
                16
            )}"
        QNUploadManager.getInstance()
            .put(imageUrl, keys, UserManager.qnToken, { key, info, response ->
                if (!info.isOK) {
                    keys = ""
                }
            }, null)
        return keys
    }
}