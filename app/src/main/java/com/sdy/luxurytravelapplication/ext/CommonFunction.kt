package com.sdy.luxurytravelapplication.ext

import android.app.Activity
import android.content.Context
import android.os.Handler
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.netease.nimlib.sdk.msg.model.RecentContact
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.callback.MyUMAuthCallback
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.event.*
import com.sdy.luxurytravelapplication.glide.GlideEngine
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.liveface.FaceLivenessExpActivity
import com.sdy.luxurytravelapplication.mvp.model.bean.SendTipBean
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareBean
import com.sdy.luxurytravelapplication.mvp.model.bean.TravelPlanBean
import com.sdy.luxurytravelapplication.nim.api.NimUIKit
import com.sdy.luxurytravelapplication.nim.attachment.*
import com.sdy.luxurytravelapplication.nim.business.session.activity.ChatActivity
import com.sdy.luxurytravelapplication.nim.impl.cache.DemoCache
import com.sdy.luxurytravelapplication.ui.activity.*
import com.sdy.luxurytravelapplication.ui.dialog.*
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.utils.UriUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException

/**
 *    author : ZFM
 *    date   : 2021/3/1811:01
 *    desc   :
 *    version: 1.0
 */

object CommonFunction {

    /**
     * 退出登录
     */
    fun loginOut(activity: Context) {
        NimUIKit.logout()
        NIMClient.getService(AuthService::class.java).logout()
        UserManager.clearLoginData()
//        ActivityUtils.finishOtherActivities(WelcomeActivity::class.java,false)
        val intent = activity.intentFor<WelcomeActivity>().clearTask().newTask()
        activity.startActivity(intent)
    }


    /**
     * 不是会员先弹充值
     * 不是好友就赠送礼物
     * 是好友就直接跳聊天界面
     * 201解锁联系方式前置判断
     * 	201 拉起充值会员
     * 	206 是好友进聊天
     * 	200 拉起礼物列表
     * 	208 充值高级会员（女性设置了聊天权限）
     */
    fun checkChat(context1: Context, target_accid: String) {
        val loading = LoadingDialog()
        loading.show()
        RetrofitHelper.service.checkChat(hashMapOf("target_accid" to target_accid))
            .ssss { t ->
                loading.dismiss()
                when (t.code) {
                    200 -> {
                        if (t.data != null)
                            ChatUpOpenPtVipDialog(
                                context1,
                                target_accid,
                                ChatUpOpenPtVipDialog.TYPE_CHAT,
                                t.data!!
                            ).show()
                    }
                    201 -> {//开通门槛会员
                        PurchaseFootActivity.start(context1)
                    }
                    206 -> {
                        if (ActivityUtils.getTopActivity() !is ChatActivity) {
                            ChatActivity.start(context1, target_accid)
                        } else {
                            EventBus.getDefault().post(HideChatLlEvent())
                        }
                    }
                    207 -> { //女性对男性搭讪
                        //随机发送一条招呼文本消息
                        val chatUpAttachment = ChatUpAttachment(t.msg)
                        val msg = MessageBuilder.createCustomMessage(
                            target_accid,
                            SessionTypeEnum.P2P,
                            chatUpAttachment
                        )

                        NIMClient.getService(MsgService::class.java).sendMessage(msg, false)
                            .setCallback(object : RequestCallback<Void> {
                                override fun onSuccess(p0: Void?) {
                                    if (ActivityUtils.getTopActivity() !is ChatActivity)
                                        Handler().postDelayed({
                                            ChatActivity.start(context1, target_accid)
                                        }, 500L)
                                }

                                override fun onFailed(p0: Int) {
                                }

                                override fun onException(p0: Throwable?) {
                                }

                            })
                    }
                    400 -> {
                        ToastUtil.toast(t.msg)
                    }
                    401 -> {//女性未认证
                        VerifyThenChatDialog(context1).show()
                    }
                }
            }

    }


    /**
     * 验证联系方式解锁
     * 	400 toast错误
     * 	202 高级会员充值
     * 	222 （已经解锁过了）
     * 	200 amount 解锁旅券 isplatinumvip 是否铂金会员true是 false不是
     *
     */
    fun checkUnlockContact(context: Context, target_accid: String, gender: Int) {
        val loading = LoadingDialog()
        loading.show()
        RetrofitHelper.service
            .checkUnlockContact(hashMapOf("target_accid" to target_accid))
            .ssss { t ->
                loading.dismiss()
                when (t.code) {
                    200 -> {//amount 解锁旅券 isplatinumvip 是否铂金会员true是 false不是
                        ChatUpOpenPtVipDialog(
                            context,
                            target_accid,
                            ChatUpOpenPtVipDialog.TYPE_CONTACT,
                            t.data!!
                        ).show()
                    }

                    201 -> {
                        PurchaseFootActivity.start(context)
                    }
                    222 -> {
                        if (ActivityUtils.getTopActivity() !is ChatActivity)
                            Handler().postDelayed({
                                ChatActivity.start(context, target_accid)
                            }, 500L)
                    }

                    207 -> { //女性解锁男性聊天方式
                        if (ActivityUtils.getTopActivity() !is ChatActivity)
                            if (t.msg.isNullOrEmpty()) {
                                Handler().postDelayed({
                                    ChatActivity.start(context, target_accid)
                                }, 500L)
                            } else {
                                //随机发送一条招呼文本消息
                                val chatUpAttachment = ChatUpAttachment(t.msg)
                                val msg = MessageBuilder.createCustomMessage(
                                    target_accid,
                                    SessionTypeEnum.P2P,
                                    chatUpAttachment
                                )
                                NIMClient.getService(MsgService::class.java)
                                    .sendMessage(msg, false)
                                    .setCallback(object : RequestCallback<Void> {
                                        override fun onSuccess(p0: Void?) {
                                            Handler().postDelayed({
                                                ChatActivity.start(context, target_accid)
                                            }, 500L)
                                        }

                                        override fun onFailed(p0: Int) {
                                        }

                                        override fun onException(p0: Throwable?) {
                                        }

                                    })
                            }

                    }
                    401 -> {//女性未认证
                        VerifyThenChatDialog(
                            context,
                            VerifyThenChatDialog.FROM_CONTACT_VERIFY
                        ).show()
                    }
                    else -> {
                        ToastUtil.toast(t.msg)
                    }
                }
            }
    }

    /**
     * 验证视频介绍解锁
     * 400 错误toast
     * 201 男性不是门槛会员
     * 222 （铂金会元/已经解锁视频 返回isnew_friend true是新好友 false 不是新建立 mv_url 视频地址 ）
     * 200 amount 旅券数 isplatinumvip 是否铂金会员
     */
    fun checkUnlockIntroduceVideo(context: Context, target_accid: String, mv_cover_url: String) {
        val waitDialog = LoadingDialog()
        waitDialog.show()
        RetrofitHelper.service
            .checkUnlockMv(hashMapOf("target_accid" to target_accid))
            .ssss { t ->
                waitDialog.dismiss()
                when (t.code) {
                    222 -> {//铂金会员解锁成功/已经解锁过了 isnew_friend 是否新好友
                        PlayVideoDialog(t.data?.mv_url ?: "").show()
                    }
                    200 -> {//amount 解锁旅券 isplatinumvip 是否铂金会员true是 false不是
                        VideoOpenPtVipDialog(mv_cover_url).show()
                    }
                    201 -> {
                        PurchaseFootActivity.start(context)
                    }
                    else -> {
                        ToastUtil.toast(t.msg)
                    }
                }

            }
    }


    /**
     * 验证报名约会
     * 	code 202 对方设置高级会员 206是好友，已经报名 207 报名成功返回数据（id，title，dating_title，icon） 200 400错误信息  401
     */
    fun checkApplyForDating(context1: Context, datingBean: TravelPlanBean) {
        val waitDialog = LoadingDialog()
        waitDialog.show()
        RetrofitHelper.service
            .checkDatingapply(hashMapOf("dating_id" to datingBean.id))
            .ssss { t ->
                waitDialog.dismiss()
                when (t.code) {
                    200 -> {//amount 解锁旅券 isplatinumvip 是否铂金会员true是 false不是
                        DatingOpenPtVipDialog(
                            context1,
                            DatingOpenPtVipDialog.TYPE_DATING_APPLYFOR,
                            t.data,
                            datingBean
                        ).show()
                    }
                    202 -> {//202 对方设置高级会员
                        DatingOpenPtVipDialog(
                            context1,
                            DatingOpenPtVipDialog.TYPE_DATING_APPLYFOR_PRIVACY,
                            t.data,
                            datingBean
                        ).show()
                    }
                    206 -> {// 206是好友，已经报名
                        ChatActivity.start(context1, datingBean.accid)
                    }
                    207 -> {//207 报名成功返回数据（id，title，dating_title，icon）
                        val attachment =
                            ChatDatingAttachment(
                                t.data!!.content,
                                t.data!!.icon,
                                t.data!!.datingId
                            )
                        val message = MessageBuilder.createCustomMessage(
                            datingBean.accid,
                            SessionTypeEnum.P2P,
                            "",
                            attachment,
                            CustomMessageConfig()
                        )
                        NIMClient.getService(MsgService::class.java).sendMessage(message, false)
                            .setCallback(object : RequestCallback<Void?> {
                                override fun onSuccess(param: Void?) {
                                    EventBus.getDefault().post(UpdateApproveEvent())
                                    EventBus.getDefault().post(UpdateHiEvent())
                                    EventBus.getDefault().post(UpdateAccostListEvent())
                                    if (ActivityUtils.getTopActivity() !is ChatActivity) {
                                        Handler().postDelayed({
                                            ChatActivity.start(
                                                ActivityUtils.getTopActivity(),
                                                datingBean.accid
                                            )
                                        }, 400L)
                                    } else {
                                        EventBus.getDefault().post(UpdateSendGiftEvent(message))
                                    }
                                }

                                override fun onFailed(code: Int) {
                                }

                                override fun onException(exception: Throwable) {
                                }
                            })
                    }
                    401 -> {//女性未认证
                        VerifyThenChatDialog(
                            context1,
                            VerifyThenChatDialog.FROM_APPLY_DATING
                        ).show()

                    }
                    else -> {
                        ToastUtil.toast(t.msg)
                    }
                }

            }
    }


    /**
     * 打开礼物信封
     * attachment.getOrderId()
     *
     */
    fun openGiftLetter(
        isReceive: Boolean,
        giftStatus: Int,
        order_id: Int,
        context: Context,
        target_accid: String
    ) {
        RetrofitHelper.service
            .checkGiftState(hashMapOf("order_id" to order_id))
            .ssss { t ->
                if (t.code == 200) {
                    ReceiveCandyGiftDialog(
                        isReceive,
                        giftStatus,
                        t.data!!,
                        order_id,
                        context, target_accid
                    ).show()
                }
            }
    }


    /**
     * 发送自定义tip消息
     */
    fun sendTips(target_accid: String, retTipsArr: MutableList<SendTipBean>) {
        for (tip in retTipsArr) {
            val attachment = SendCustomTipAttachment(tip.content, tip.showType, tip.ifSendUserShow)
            val tip =
                MessageBuilder.createCustomMessage(
                    target_accid,
                    SessionTypeEnum.P2P,
                    attachment
                )
            val config = CustomMessageConfig()
            config.enableUnreadCount = false
            config.enablePush = false
            tip.config = config
            NIMClient.getService(MsgService::class.java).sendMessage(tip, false)
                .setCallback(object :
                    RequestCallback<Void?> {
                    override fun onSuccess(param: Void?) {
                        //更新消息列表
                        EventBus.getDefault().post(UpdateSendGiftEvent(tip))
                    }

                    override fun onFailed(code: Int) {
                    }

                    override fun onException(exception: Throwable) {

                    }
                })
        }
    }


    fun startToFace(
        context: Context,
        type: Int = 0,
        requestCode: Int = -1
    ) {
        if (requestCode != -1)
            FaceLivenessExpActivity.startActivity(context as Activity, type, requestCode)
        else
            FaceLivenessExpActivity.startActivity(context, type)
    }

    /**
     * 录制视频介绍
     */
    fun startToVideoIntroduce(
        context: Context,
        requestCode: Int = -1
    ) {
        VideoIntroduceActivity.start(context, requestCode)
    }

    /**
     * 支付结果回调数据
     */
    fun payResultNotify(context: Context) {
        if (ActivityUtils.getTopActivity() is PurchaseFootActivity) {
            EventBus.getDefault().post(CloseRegVipEvent(true))
        } else {
            EventBus.getDefault().post(RefreshGoldEvent())
            EventBus.getDefault().postSticky(UserCenterEvent(true))
            EventBus.getDefault().post(CloseDialogEvent())
            EventBus.getDefault().post(UpdateLuxuryEvent())
            //刷新顶部精选数据
            EventBus.getDefault().post(TopCardEvent(true))
        }


    }


    /**
     * 支付会员
     */
    fun startToVip(context: Context, source_type: Int = -1, position: Int = 0) {
        VipChargeActivity.start(context)
    }

    /**
     * 门槛付费
     */
    fun startToFootPrice(context: Context) {
        context.startActivity<InviteCodeActivity>()
    }

    /**
     * 跳转到旅券充值
     */
    fun gotoCandyRecharge(context: Context) {
        CandyRechargeActivity.gotoCandyRecharge(context)
//        RechargeCandyDialog(context).show()
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
        if (ActivityUtils.isActivityExistsInStack(TargetUserActivity::class.java))
            ActivityUtils.finishActivity(TargetUserActivity::class.java)
        if (ActivityUtils.isActivityExistsInStack(MessageInfoActivity::class.java))
            ActivityUtils.finishActivity(MessageInfoActivity::class.java)
        //更新通讯录
        if (ActivityUtils.isActivityExistsInStack(ContactBookActivity::class.java))
            EventBus.getDefault().post(UpdateContactBookEvent())
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
                            DemoCache.getContext().getString(R.string.msg_gift)
                        }

                        is ContactAttachment -> {
                            ActivityUtils.getTopActivity().getString(R.string.content_contact)
                        }
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
            is ChatDatingAttachment -> DemoCache.getContext().getString(R.string.msg_dating_apply)
            is SendGiftAttachment -> DemoCache.getContext().getString(R.string.msg_gift)
            is ContactAttachment -> (item.attachment as ContactAttachment).contactContent
            is ContactCandyAttachment -> {
                DemoCache.getContext().getString(R.string.msg_unlock_contact)
            }
            is SendCustomTipAttachment -> if ((item.attachment as SendCustomTipAttachment).content.isNullOrEmpty()) {
                DemoCache.getContext().getString(R.string.msg_prompt)
            } else {
                (item.attachment as SendCustomTipAttachment).content
            }
            is ChatUpAttachment -> {
                (item.attachment as ChatUpAttachment).chatUpContent
            }
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


    fun initVideo(gsyVideoPlayer: StandardGSYVideoPlayer, url: String) {
        gsyVideoPlayer.titleTextView.isVisible = false
        gsyVideoPlayer.backButton.isVisible = true
        gsyVideoPlayer.backButton.setImageResource(R.drawable.icon_close_transparent)
        gsyVideoPlayer.setIsTouchWiget(false)

        gsyVideoPlayer.setUp(url, true, "")
        gsyVideoPlayer.startPlayLogic()
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
        cropEnable: Boolean = false,
        aspect_ratio_x: Int = 4,
        aspect_ratio_y: Int = 5
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
            .withAspectRatio(aspect_ratio_x, aspect_ratio_y)
            .isAndroidQTransform(true)//是否需要处理Android Q 拷贝至应用沙盒的操作
            .compressSavePath(UriUtils.getCacheDir(context))
            .cameraFileName("${TimeUtils.getNowMills()}.png")
            .compress(compress)
            .loadImageEngine(GlideEngine.createGlideEngine())// 自定义图片加载引擎
            .compressSavePath(UriUtils.getCacheDir(context))
            .forResult(requestCode)
    }


    /**
     * 字符串 千位符
     *
     * @param num
     * @return
     */
    fun num2thousand(num: String): String {
        var numStr = ""
        val nf = NumberFormat.getInstance()
        try {
            val df = DecimalFormat("#,###")
            numStr = df.format(nf.parse(num))
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return numStr
    }

    fun checkPublishDating(context: Context) {
        val loadingDialog = LoadingDialog()
        loadingDialog.show()
        RetrofitHelper.service.checkPlan(hashMapOf()).ssss { t ->
            loadingDialog.dismiss()
            when (t.code) {
                200 -> {//amount 解锁糖果 isplatinumvip 是否铂金会员true是 false不是
                    if (t.data?.is_publish == true)
                        context.startActivity<PublishTravelActivity>()
                    else
                        context.startActivity<PublishTravelBeforeActivity>()
                }
                201 -> {
                    startToFootPrice(context)
                }
                202 -> {
                    DatingOpenPtVipDialog(
                        context,
                        DatingOpenPtVipDialog.TYPE_DATING_PUBLISH
                    ).show()
                }
                203 -> {
                    TodayHasDatingDialog(context).show()

                }
                else -> {
                    ToastUtil.toast(t.msg)
                }
            }

        }

    }


}

