package com.sdy.luxurytravelapplication.ext

import android.app.Activity
import android.content.Context
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.TimeUtils
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.style.PictureCropParameterStyle
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.netease.nimlib.sdk.msg.model.RecentContact
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.callback.MyUMAuthCallback
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.event.CloseDialogEvent
import com.sdy.luxurytravelapplication.event.RefreshGoldEvent
import com.sdy.luxurytravelapplication.glide.GlideEngine
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.model.bean.SendGiftBean
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareBean
import com.sdy.luxurytravelapplication.nim.api.NimUIKit
import com.sdy.luxurytravelapplication.nim.attachment.SendGiftAttachment
import com.sdy.luxurytravelapplication.nim.attachment.ShareSquareAttachment
import com.sdy.luxurytravelapplication.nim.business.module.Container
import com.sdy.luxurytravelapplication.nim.business.session.activity.ChatActivity
import com.sdy.luxurytravelapplication.nim.impl.cache.DemoCache
import com.sdy.luxurytravelapplication.ui.activity.CandyRechargeActivity
import com.sdy.luxurytravelapplication.ui.activity.WelcomeActivity
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.utils.UriUtils
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

/**
 *    author : ZFM
 *    date   : 2021/3/1811:01
 *    desc   :
 *    version: 1.0
 */

object CommonFunction {

    fun checkChat(context1: Context, target_accid: String) {}


    /**
     * 退出登录
     */
    fun loginOut(activity: Context) {
        NimUIKit.logout()
        NIMClient.getService(AuthService::class.java).logout()
        UserManager.clearLoginData()
        val intent = activity.intentFor<WelcomeActivity>().clearTask().newTask()
        activity.startActivity(intent)
    }


    /**
     * 发送礼物消息
     */
    fun sendGift(
        target_accid: String,
        sendGiftBean: SendGiftBean,
        giftType: Int,
        container: Container? = null
    ) {
        val params = hashMapOf<String, Any>()
        params["target_accid"] = target_accid
        params["gift_id"] = sendGiftBean.id
        RetrofitHelper.service
            .giveGift(params)
            .ssss {
                when (it.code) {
                    200 -> {
                        EventBus.getDefault().post(RefreshGoldEvent())
                        /**
                         * 发送礼物消息
                         */
                        val attachment = SendGiftAttachment()
                        attachment.orderId = it.data.order_id
                        attachment.giftId = sendGiftBean.id
                        attachment.giftIcon = sendGiftBean.icon
                        attachment.giftName = sendGiftBean.title
                        attachment.giftAmount = sendGiftBean.amount
                        attachment.giftType = giftType
                        val extension = hashMapOf<String, Any>()
                        extension[SendGiftAttachment.KEY_STATUS] =
                            SendGiftAttachment.STATUS_WAIT
                        val giftMessage =
                            MessageBuilder.createCustomMessage(
                                target_accid,
                                SessionTypeEnum.P2P,
                                attachment
                            )
                        giftMessage.localExtension = extension
                        if (container != null) {
                            if (container.proxy.sendMessage(giftMessage)) {
//                                    sendGiftNotification(giftMessage, SendGiftAttachment.STATUS_WAIT)
                                EventBus.getDefault().post(CloseDialogEvent())
                                upMsgGiftId(giftMessage.uuid, it.data.order_id)
                            }
                        } else {
                            NIMClient.getService(MsgService::class.java)
                                .sendMessage(giftMessage, true)
                                .setCallback(object : RequestCallback<Void?> {
                                    override fun onSuccess(param: Void?) {
//                                            sendGiftNotification(giftMessage,SendGiftAttachment.STATUS_WAIT)
                                        EventBus.getDefault().post(CloseDialogEvent())
                                        ChatActivity.start(
                                            ActivityUtils.getTopActivity(),
                                            target_accid
                                        )
                                        upMsgGiftId(giftMessage.uuid, it.data.order_id)

                                    }

                                    override fun onFailed(code: Int) {
                                        ToastUtil.toast(
                                            ActivityUtils.getTopActivity()
                                                .getString(R.string.gift_send_fail)
                                        )

                                    }

                                    override fun onException(exception: Throwable) {
                                    }
                                })
                        }

                    }
                    419 -> { //糖果余额不足
                       CandyRechargeActivity.gotoCandyRecharge(ActivityUtils.getTopActivity())
                    }
                    else -> {
                        ToastUtil.toast(it.msg)
                    }
                }
            }
    }

    /**
     * 上传礼物的id给服务器
     */
    fun upMsgGiftId(uuid: String, order_id: Int) {
        RetrofitHelper.service
            .upGiftMsgId(
                hashMapOf(
                    "im_msg_id" to uuid,
                    "order_id" to order_id
                )
            )
    }


    /**
     * 支付结果回调数据
     */
    fun payResultNotify(context: Context) {
        EventBus.getDefault().post(CloseDialogEvent())

//        if (ActivityUtils.getTopActivity() is VipCenterActivity) {
//            EventBus.getDefault().post(RefreshVipEvent())
//        } else {
            EventBus.getDefault().post(RefreshGoldEvent())
            EventBus.getDefault().post(CloseDialogEvent())
//        }


    }

    /**
     * 三方登录设置
     */
    fun socialLogin(context: Context, shareMedia: SHARE_MEDIA) {
//        val wxapi = WXAPIFactory.createWXAPI(context, null)
//        wxapi.registerApp(Constants.WECHAT_APP_ID)
//        if (!wxapi.isWXAppInstalled) {
//            ToastUtil.toast(context.getString(R.string.unload_wechat))
//            return
//        }
//        val req = SendAuth.Req()
//        req.scope = "snsapi_userinfo"
//        req.state = state
//        wxapi.sendReq(req)
        UMShareAPI.get(context)
            .getPlatformInfo(context as Activity, shareMedia, MyUMAuthCallback(context))
    }


    /**
     * 更新消息的扩展字段
     */
    fun updateMessageExtension(message: IMMessage, statusKey: String, statusValue: Any) {
        val params = message.localExtension ?: hashMapOf()
        params[statusKey] = statusValue
        message.localExtension = params
        NIMClient.getService(MsgService::class.java).updateIMMessage(message)

        if (message.attachment is SendGiftAttachment)
            if (message.direct == MsgDirectionEnum.In) {
                updateRecentExtension(message.fromAccount, params)
            } else {
                updateRecentExtension(message.sessionId, params)
            }
    }

    /**
     * 更新最近联系人的扩展字段
     */
    fun updateRecentExtension(sessionId: String, params: Map<String, Any> = mapOf()) {
        val recent = NIMClient.getService(MsgService::class.java)
            .queryRecentContact(sessionId, SessionTypeEnum.P2P)
        if (recent != null) {
            recent.extension = params
            NIMClient.getService(MsgService::class.java).updateRecentAndNotify(
                recent
            )
        }
    }

    fun dissolveRelationshipLocal(target_accid: String, negative: Boolean = false) {
        NIMClient.getService(MsgService::class.java)
            .deleteRecentContact2(target_accid, SessionTypeEnum.P2P)
        //如果是被动删除，就删除会话
        if (!negative)
            NIMClient.getService(MsgService::class.java)
                .clearChattingHistory(target_accid, SessionTypeEnum.P2P)
        if (ActivityUtils.isActivityExistsInStack(ChatActivity::class.java))
            ActivityUtils.finishActivity(ChatActivity::class.java)
//        if (ActivityUtils.isActivityExistsInStack(TargetUserActivity::class.java))
//            ActivityUtils.finishActivity(TargetUserActivity::class.java)
//        if (ActivityUtils.isActivityExistsInStack(MessageInfoActivity::class.java))
//            ActivityUtils.finishActivity(MessageInfoActivity::class.java)
        //更新通讯录
//        if (ActivityUtils.isActivityExistsInStack(ContactBookActivity::class.java))
//            EventBus.getDefault().post(UpdateContactBookEvent())
    }


    /**
     * 返回云信的消息
     */
    fun getRecentContent(
        item: MsgAttachment? = null,
        content: String? = "",
        extionsion: Map<String, Any>,
        msgTypeEnum: MsgTypeEnum,
        remoteExtension: Map<String, Any> = hashMapOf()
    ): String {
        return when (msgTypeEnum) {
            MsgTypeEnum.video -> {
                ActivityUtils.getTopActivity().getString(R.string.msg_video)
            }
            MsgTypeEnum.audio -> {
                ActivityUtils.getTopActivity().getString(R.string.msg_audio)
            }
            MsgTypeEnum.image -> {
//                if (remoteExtension.isNotEmpty() && remoteExtension[LocationActivity.EXTENSION_LATITUDE] != null)
//                    ActivityUtils.getTopActivity().getString(R.string.msg_location)
//                else
                ActivityUtils.getTopActivity().getString(R.string.msg_pic)
            }
            MsgTypeEnum.custom -> {
                if (item == null) {
                    content ?: ""
                } else {
                    when (item) {
//                        is WarmingNoticeAttachment -> ""
//            const val PIC = 1
//        const val VIDEO = 2
//        const val AUDIO = 3
//        const val OFFICIAL_NOTICE = 4
                        is ShareSquareAttachment -> {
                            if (item.shareType == SquareBean.PIC) {
                                ActivityUtils.getTopActivity().getString(R.string.share_pic)
                            } else if (item.shareType == SquareBean.AUDIO) {
                                ActivityUtils.getTopActivity().getString(R.string.share_audio)
                            } else if (item.shareType == SquareBean.VIDEO) {
                                ActivityUtils.getTopActivity().getString(R.string.share_video)
                            } else {
                                ActivityUtils.getTopActivity().getString(R.string.content_share)
                            }
                        }

                        is SendGiftAttachment -> {
                            val giftStatus =
                                if (extionsion.isNotEmpty() && extionsion[SendGiftAttachment.KEY_STATUS] != null) {
                                    extionsion[SendGiftAttachment.KEY_STATUS] as Int
                                } else {
                                    -1
                                }
                            when (giftStatus) {

                                SendGiftAttachment.STATUS_RECEIVED -> {
                                    ActivityUtils.getTopActivity()
                                        .getString(R.string.content_gift_received)
                                }
                                SendGiftAttachment.STATUS_RETURNED -> {
                                    ActivityUtils.getTopActivity()
                                        .getString(R.string.content_gift_refund)
                                }
                                SendGiftAttachment.STATUS_TIMEOUT -> {
                                    ActivityUtils.getTopActivity()
                                        .getString(R.string.content_gift_timeout)
                                }
                                else -> {
//                        SendGiftAttachment.STATUS_WAIT -> {
                                    ActivityUtils.getTopActivity()
                                        .getString(R.string.content_gift_wait_receive)
                                }
//                        else -> {
//                            "『礼物消息』"
//                        }
                            }
                        }

//                        is SendWechatAttachment -> {
//                            ActivityUtils.getTopActivity().getString(R.string.content_contact)
//                        }
                        else -> content ?: ""
                    }
                }
            }
            else -> {
                content ?: ""
            }
        }

    }

    /**
     * 返回云信的消息
     */
    fun getRecentContent(item: RecentContact): String {
        return when (item.attachment) {
//            is ChatHiAttachment ->
//                when ((item.attachment as ChatHiAttachment).showType) {
//                    ChatHiAttachment.CHATHI_CHATUP_FRIEND -> DemoCache.getContext()
//                        .getString(R.string.msg_unlock_chat)
//                    else -> ""
//                }
            is ShareSquareAttachment -> DemoCache.getContext().getString(R.string.msg_share_square)
//            is ChatDatingAttachment -> DemoCache.getContext().getString(R.string.msg_dating_apply)
            is SendGiftAttachment -> DemoCache.getContext().getString(R.string.msg_gift)
//            is ContactAttachment -> (item.attachment as ContactAttachment).contactContent
//            is ContactCandyAttachment -> {
//                DemoCache.getContext().getString(R.string.msg_unlock_contact)
//            }
//            is SendCustomTipAttachment -> if ((item.attachment as SendCustomTipAttachment).content.isNullOrEmpty()) {
//                DemoCache.getContext().getString(R.string.msg_prompt)
//            } else {
//                (item.attachment as SendCustomTipAttachment).content
//            }
//            is ChatUpAttachment -> {
//                (item.attachment as ChatUpAttachment).chatUpContent
//            }
            else -> item.content
        }
    }


    fun getErrorMsg(context: Context): String {
        return if (NetworkUtils.isConnected()) {
            context.getString(R.string.retry_load_error)
        } else {
            context.getString(R.string.retry_net_error)
        }
    }


    /**
     * 拍照或者选取照片
     */
    @JvmOverloads
    fun onTakePhoto(
        context: Context,
        maxCount: Int,
        requestCode: Int,
        chooseMode: Int = PictureMimeType.ofImage(),
        compress: Boolean = false,
        showCamera: Boolean = true,
        rotateEnable: Boolean = false,
        cropEnable: Boolean = false,
        minSeconds: Int = 5,
        maxSeconds: Int = 2 * 60,
        aspect_ratio_x: Int = 4,
        aspect_ratio_y: Int = 5
    ) {
        PictureSelector.create(ActivityUtils.getTopActivity())
            .openGallery(chooseMode)
            .maxSelectNum(maxCount)
            .minSelectNum(0)
            .imageSpanCount(4)
            .selectionMode(
                if (maxCount > 1) {
                    PictureConfig.MULTIPLE
                } else {
                    PictureConfig.SINGLE
                }
            )
            .isAndroidQTransform(true)//是否需要处理Android Q 拷贝至应用沙盒的操作
            .previewImage(true)
            .previewVideo(true)
            .isCamera(showCamera)
            .enableCrop(cropEnable)
            .maxVideoSelectNum(1)
            .compressSavePath(UriUtils.getCacheDir(context))
            .compress(compress)
            .videoMaxSecond(maxSeconds)
            .videoMinSecond(minSeconds)
            .minimumCompressSize(100)
            .scaleEnabled(true)
//            .showCropGrid(true)
//            .showCropFrame(true)
            .loadImageEngine(GlideEngine.createGlideEngine())// 自定义图片加载引擎
            .rotateEnabled(rotateEnable)
//            .cropImageWideHigh(4, 5)
            .withAspectRatio(aspect_ratio_x, aspect_ratio_y)
            .compressSavePath(UriUtils.getCacheDir(context))
            .openClickSound(false)
            .isUseCustomCamera(false)
            .forResult(requestCode)
    }

    /**
     * 单独拍照
     */
    fun openCamera(
        context: Context,
        requestCode: Int,
        chooseMode: Int = 1,
        compress: Boolean = false,
        rotateEnable: Boolean = false,
        cropEnable: Boolean = false
    ) {
        // 裁剪主题
        val mCropParameterStyle = PictureCropParameterStyle(
            ContextCompat.getColor(context, R.color.colorBlack),
            ContextCompat.getColor(context, R.color.colorBlack),
            ContextCompat.getColor(context, R.color.colorWhite),
            true
        )

        PictureSelector.create(context as Activity)
            .openCamera(chooseMode)
            .enableCrop(cropEnable)
            .rotateEnabled(rotateEnable)
            .setPictureCropStyle(mCropParameterStyle) // 动态自定义裁剪主题
            .theme(R.style.picture_default_style)
//            .cropImageWideHigh(4, 5)
            .withAspectRatio(4, 5)
            .isAndroidQTransform(true)//是否需要处理Android Q 拷贝至应用沙盒的操作
            .compressSavePath(UriUtils.getCacheDir(context))
            .cameraFileName("${TimeUtils.getNowMills()}.png")
            .compress(compress)
            .loadImageEngine(GlideEngine.createGlideEngine())// 自定义图片加载引擎
            .compressSavePath(UriUtils.getCacheDir(context))
            .forResult(requestCode)
    }
}

