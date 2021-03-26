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
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.callback.MyUMAuthCallback
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.event.UpdateContactBookEvent
import com.sdy.luxurytravelapplication.glide.GlideEngine
import com.sdy.luxurytravelapplication.nim.api.NimUIKit
import com.sdy.luxurytravelapplication.nim.attachment.SendGiftAttachment
import com.sdy.luxurytravelapplication.ui.activity.WelcomeActivity
import com.sdy.luxurytravelapplication.utils.UriUtils
import com.sdy.sweetdateapplication.nim.business.session.activity.ChatActivity
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

    fun checkChat(context1: Context, target_accid: String){}


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


    fun getErrorMsg(context: Context): String {
        return if (NetworkUtils.isConnected()) {
            context.getString(R.string.retry_load_error)
        } else {
            context.getString(R.string.retry_net_error)
        }
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