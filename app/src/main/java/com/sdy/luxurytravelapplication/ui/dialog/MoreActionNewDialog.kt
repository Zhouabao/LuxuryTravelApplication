package com.sdy.luxurytravelapplication.ui.dialog

import android.graphics.Bitmap
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ClipboardUtils
import com.kongzue.dialog.util.BaseDialog
import com.kongzue.dialog.util.DialogSettings
import com.kongzue.dialog.v3.ShareDialog
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareBean
import com.sdy.luxurytravelapplication.ui.activity.ContactBookActivity
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMVideo
import com.umeng.socialize.media.UMWeb
import com.umeng.socialize.media.UMusic

/**
 *    author : ZFM
 *    date   : 2021/3/1911:22
 *    desc   :
 *    version: 1.0
 */
class MoreActionNewDialog(
    val context: AppCompatActivity,
    var squareBean: SquareBean? = null,
    var url: String = "",
    var type: Int = TYPE_SHARE_SQUARE,
    var title: String = "",
    var content: String = "",
    var pic: String = "",
    var shareCallback: ShareCallBack? = null
){
    interface ShareCallBack {
        fun delete()
        fun report()
    }

    companion object {
        val TYPE_SHARE_SQUARE = 1
        val TYPE_SHARE_VIP_URL = 2
    }

    private val umShareAPI by lazy { UMShareAPI.get(context) }

    /**
     * 封装分享
     */
    private fun shareToThirdParty(platformConfig: SHARE_MEDIA) {
        if (type == TYPE_SHARE_VIP_URL) {
            shareWeb(platformConfig)
        } else {
            shareSquare(platformConfig)
        }
    }


    /**
     * 分享动态
    const val PIC = 1
    const val VIDEO = 2
    const val AUDIO = 3
     */
    private fun shareSquare(platformConfig: SHARE_MEDIA) {
        if (squareBean?.type == SquareBean.PIC) {
            //多图上传,需要带文字描述
            if (!squareBean?.photo_json.isNullOrEmpty()) {
                val image = UMImage(context, squareBean?.photo_json?.get(0)?.url)//
                //大小压缩，默认为大小压缩，适合普通很大的图
                image.compressStyle = UMImage.CompressStyle.SCALE
                image.compressFormat = Bitmap.CompressFormat.JPEG
//                image.title =
//                    context.getString(R.string.send_a_pic_in_app, squareBean?.nickname.toString())
//                image.description = if (!squareBean?.descr.isNullOrEmpty()) {
//                    squareBean?.descr
//                } else context.getString(R.string.hurry_to_see_this)
                ShareAction(ActivityUtils.getTopActivity())
                    .setPlatform(platformConfig)
                    .withText(
                        if (!squareBean?.descr.isNullOrEmpty()) {
                            squareBean?.descr
                        } else context.getString(R.string.hurry_to_see_this)
                    )//分享内容
                    .withMedia(image)//多张图片
                    .setCallback(callback)
                    .share()

            } else {            //文本分享
                val web = UMWeb("http://")
                web.title = context.getString(
                    R.string.send_a_square_in_app,
                    squareBean?.nickname.toString()
                )//标题
                web.setThumb(UMImage(context, squareBean?.avatar ?: ""))  //缩略图
                web.description = squareBean?.descr ?: ""//描述
                if (platformConfig == SHARE_MEDIA.QQ) {
                    ShareAction(ActivityUtils.getTopActivity())
                        .setPlatform(platformConfig)
                        .withText(squareBean?.descr ?: "")
                        .withMedia(web)
                        .setCallback(callback)
                        .share()
                } else {
                    ShareAction(ActivityUtils.getTopActivity())
                        .setPlatform(platformConfig)
                        .withText(squareBean?.descr ?: "")
                        .setCallback(callback)
                        .share()
                }


            }
        } else if (squareBean?.type == SquareBean.VIDEO) {//视频分享
            val video = UMVideo(squareBean?.video_json?.get(0)?.url)//
            val thumbImg = UMImage(context, squareBean?.cover_url ?: "")
            //大小压缩，默认为大小压缩，适合普通很大的图
            thumbImg.compressStyle = UMImage.CompressStyle.SCALE
            thumbImg.compressFormat = Bitmap.CompressFormat.PNG
            video.setThumb(thumbImg)
            video.title =
                context.getString(R.string.send_a_video_in_app, squareBean?.nickname.toString())
            video.description = if (!squareBean?.descr.isNullOrEmpty()) {
                squareBean?.descr
            } else context.getString(R.string.hurry_to_see_this)
            ShareAction(ActivityUtils.getTopActivity())
                .setPlatform(platformConfig)
                .withMedia(video)
                .setCallback(callback)
                .share()
        } else if (squareBean?.type == SquareBean.AUDIO) {
            val audio = UMusic(squareBean?.audio_json?.get(0)?.url)
            audio.setThumb(UMImage(context, squareBean?.avatar ?: ""))
            audio.setmTargetUrl(squareBean?.audio_json?.get(0)?.url)
            audio.title =
                context.getString(R.string.send_a_audio_in_app, squareBean?.nickname.toString())
            audio.description = if (!squareBean?.descr.isNullOrEmpty()) {
                squareBean?.descr
            } else context.getString(R.string.hurry_to_see_this)
            ShareAction(ActivityUtils.getTopActivity())
                .setPlatform(platformConfig)
                .withText(squareBean?.descr ?: "")
                .withMedia(audio)
                .setCallback(callback)
                .share()
        }
    }


    /**
     * 链接分享
     */
    private fun shareWeb(platformConfig: SHARE_MEDIA) {
        val web = UMWeb(url)
        web.title = title
        web.description = content
        web.setThumb(UMImage(context, pic)) //缩略图
        ShareAction(ActivityUtils.getTopActivity())
            .setPlatform(platformConfig)
            .withMedia(web)
            .setCallback(callback)
            .share()
    }


    /*第三方平台分享回调*/
    private val callback = object : UMShareListener {
        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        override fun onResult(p0: SHARE_MEDIA) {
            Log.d("share===", "onresult ${p0?.getName()}================")
            if (type == TYPE_SHARE_SQUARE)
                addShare(p0)
//            else
//                dismiss()
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        override fun onCancel(p0: SHARE_MEDIA?) {
            Log.d("share===", "cancel ${p0?.getName()}================")

//            dismiss()
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        override fun onError(p0: SHARE_MEDIA, p1: Throwable) {
            Log.d("share===", "onerror ${p0.getName()}================${p1.message ?: ""}")
            ToastUtil.toast(context.getString(R.string.share_fail))
        }

        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        override fun onStart(p0: SHARE_MEDIA) {
            Log.d("share===", "onStart ${p0.getName()}================")
        }

    }


    /*-------------------------分享成功回调----------------------------*/
    private fun addShare(sharePlat: SHARE_MEDIA) {
        val params = hashMapOf<String, Any>()
        params["square_id"] = squareBean?.id ?: 0
        RetrofitHelper.service.addShare(params).ssss {
            ToastUtil.toast(it.msg)
//            dismiss()
        }
    }

    public fun showDialog() {
        DialogSettings.init()
//        DialogSettings.checkRenderscriptSupport(context)
        DialogSettings.style = DialogSettings.STYLE.STYLE_MIUI
        val itemList = arrayListOf<ShareDialog.Item>()
        itemList.add(ShareDialog.Item(context, R.drawable.icon_share_wechat, "微信"))
        itemList.add(ShareDialog.Item(context, R.drawable.icon_share_wechat_circle, "朋友圈"))
        itemList.add(ShareDialog.Item(context, R.drawable.icon_share_qq, "QQ"))
        itemList.add(ShareDialog.Item(context, R.drawable.icon_share_qq_zone, "QQ空间"))
//        itemList.add(ShareDialog.Item(context, R.drawable.icon_share_weibo, "微博"))
        if (type == TYPE_SHARE_SQUARE) {
            itemList.add(ShareDialog.Item(context, R.drawable.icon_share_friend, "尤玩好友"))
            if (!squareBean?.descr.isNullOrEmpty())
                itemList.add(ShareDialog.Item(context, R.drawable.icon_share_copy, "复制"))
            itemList.add(ShareDialog.Item(context, R.drawable.icon_share_copy, "举报"))
            if (squareBean?.accid == UserManager.accid)
                itemList.add(ShareDialog.Item(context, R.drawable.icon_share_delete, "删除"))
        }

        ShareDialog.show(context, itemList) { shareDialog, index, item ->
            when (item.text.toString()) {
                "微信" -> {
                    shareToThirdParty(SHARE_MEDIA.WEIXIN)
                }
                "朋友圈" -> {
                    shareToThirdParty(SHARE_MEDIA.WEIXIN_CIRCLE)
                }
                "QQ" -> {
                    shareToThirdParty(SHARE_MEDIA.QQ)
                }
                "QQ空间" -> {
                    shareToThirdParty(SHARE_MEDIA.QZONE)
                }

                "尤玩好友" -> {
                    if (squareBean != null) {
                        ContactBookActivity.start(context, squareBean!!)
                    }
                }
                "复制" -> {
                    ClipboardUtils.copyText(squareBean?.descr)
                    ToastUtil.toast(context.getString(R.string.has_copy_to_board))
                }
                "举报" -> {
                    shareCallback?.report()
                }
                "删除" -> {
                    shareCallback?.delete()
                }
            }
            false
        }
            .setTitle("更多")
            .setAlign(BaseDialog.ALIGN.BOTTOM)
            .setOnDismissListener {
                umShareAPI.release()
            }

    }

}