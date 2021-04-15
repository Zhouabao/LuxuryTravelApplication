package com.sdy.luxurytravelapplication.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.DialogShareToFriendsBinding
import com.sdy.luxurytravelapplication.ext.sss
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareBean
import com.sdy.luxurytravelapplication.nim.attachment.ShareSquareAttachment
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog


class ShareToFriendsDialog(
    private var avator: String?,
    private var nickname: String?,
    private var accid: String?,
    private var squareBean: SquareBean
) : BaseBindingDialog<DialogShareToFriendsBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        initView()
    }

    //        const val PIC = 1
    //        const val VIDEO = 2
    //        const val AUDIO = 3
    private fun initView() {
        binding.apply {

            if (accid == Constants.ASSISTANT_ACCID) {
                GlideUtil.loadImg(context, R.drawable.icon_assistant, friendImg)
            } else {
                GlideUtil.loadImg(context, avator ?: "", friendImg)
            }
            friendNick.text = nickname ?: ""
            if (squareBean.type == SquareBean.PIC) { //图片
                if (squareBean.photo_json.isNullOrEmpty()) {
                    friendShareContent.text = squareBean.descr ?: ""
                    friendShareImg.visibility = View.GONE
                    friendShareContent.visibility = View.VISIBLE
                } else {
                    val params = friendShareImg.layoutParams
                    params.height = SizeUtils.dp2px(140F)
                    params.width = LinearLayout.LayoutParams.WRAP_CONTENT
                    friendShareImg.layoutParams = params
                    GlideUtil.loadRoundImgCenterinside(
                        context,
                        squareBean.photo_json?.get(0)?.url ?: "",
                        friendShareImg,
                        0.1F,
                        SizeUtils.dp2px(5F)
                    )
                    friendShareImg.visibility = View.VISIBLE
                    friendShareContent.visibility = View.GONE
                }
            } else if (squareBean.type == SquareBean.VIDEO) {
                val params = friendShareImg.layoutParams
                params.height = SizeUtils.dp2px(140F)
                params.width = LinearLayout.LayoutParams.WRAP_CONTENT
                friendShareImg.layoutParams = params
                GlideUtil.loadRoundImgCenterinside(
                    context,
                    squareBean.cover_url ?: "",
                    friendShareImg,
                    0.1F,
                    SizeUtils.dp2px(5F)
                )
                friendShareImg.visibility = View.VISIBLE
                friendShareContent.visibility = View.GONE
            } else if (squareBean.type == SquareBean.AUDIO) {
                friendShareContent.text = squareBean.descr ?: ""
                friendShareImg.visibility = View.GONE
                friendShareContent.visibility = View.VISIBLE
            }
            cancel.setOnClickListener {
                dismiss()
            }

            ClickUtils.applySingleDebouncing(send) {
                //发送消息
                sendShareMsg()
            }

        }
    }

    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.CENTER)
        val params = window?.attributes
        params?.width = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(15F) * 2
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        params?.windowAnimations = R.style.MyDialogCenterAnimation

        window?.attributes = params
        //点击外部可取消
        setCanceledOnTouchOutside(false)
    }


    /*-------------------------分享成功回调----------------------------*/
    private fun addShare() {
        val params = hashMapOf<String, Any>()
        params["square_id"] = squareBean.id ?: 0
        RetrofitHelper.service
            .addShare(params)
            .sss(onSuccess = { t ->
                if (t.code == 200)
                    ToastUtil.toast(context.getString(R.string.transpond_success))
                else
                    ToastUtil.toast(t.msg)
                dismiss()
                ActivityUtils.getTopActivity().finish()
            }, onError = {
                ToastUtil.toast(context.getString(R.string.transpond_success))
                dismiss()
                ActivityUtils.getTopActivity().finish()
            })

    }


    /*--------------------------消息代理------------------------*/
    private fun sendShareMsg() {
        val shareSquareAttachment = ShareSquareAttachment(
            squareBean.descr ?: "",
            binding.friendMsgBox.text.toString(),
            squareBean.type,
            when (squareBean.type) {
                SquareBean.AUDIO -> {
                    squareBean.avatar
                }
                SquareBean.VIDEO -> {
                    squareBean.cover_url ?: ""
                }
                SquareBean.PIC -> {
                    if (squareBean.photo_json.isNullOrEmpty()) {
                        squareBean.avatar
                    } else squareBean.photo_json!![0].url
                }
                else -> {
                    ""
                }
            }, squareBean.id ?: -1
        )
        val message = MessageBuilder.createCustomMessage(
            accid,
            SessionTypeEnum.P2P,
            "",
            shareSquareAttachment,
            CustomMessageConfig()
        )
        NIMClient.getService(MsgService::class.java).sendMessage(message, false)
            .setCallback(object : RequestCallback<Void?> {
                override fun onSuccess(param: Void?) {

                    addShare()
                }

                override fun onFailed(code: Int) {
                    ToastUtil.toast("转发失败！")
                    dismiss()
                }

                override fun onException(exception: Throwable?) {
                }

            })
    }

}