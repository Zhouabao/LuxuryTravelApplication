package com.sdy.luxurytravelapplication.nim.business.session.viewholder

import android.graphics.Color
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ClickUtils
import com.kongzue.dialog.util.TextInfo
import com.kongzue.dialog.v3.MessageDialog
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.NimMessageSendGiftBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.nim.attachment.SendGiftAttachment
import com.sdy.luxurytravelapplication.nim.attachment.SendWechatAttachment
import com.sdy.luxurytravelapplication.nim.business.module.list.MsgAdapter
import com.sdy.luxurytravelapplication.viewbinding.bindViewWithGeneric

/**
 *    author : ZFM
 *    date   : 2020/10/2011:15
 *    desc   :
 *    version: 1.0
 */
class MsgViewHolderSendGift(msgAdapter1: MsgAdapter) : MsgViewHolderBase(msgAdapter1) {
    private val attachment by lazy { message.attachment as SendGiftAttachment }
    override val contentResId: Int
        get() = R.layout.nim_message_send_gift

    private lateinit var binding: NimMessageSendGiftBinding
    override fun inflateContentView() {
        binding = bindViewWithGeneric(view)
    }
    override fun bindContentView() {
        val giftType = attachment.giftType

        val status =
            if (message.localExtension != null && message.localExtension[SendGiftAttachment.KEY_STATUS] != null)
                message.localExtension[SendGiftAttachment.KEY_STATUS]
            else if (message.remoteExtension != null && message.remoteExtension[SendGiftAttachment.KEY_STATUS] != null) {
                message.remoteExtension[SendGiftAttachment.KEY_STATUS]
            } else {
                SendGiftAttachment.STATUS_WAIT
            }
        if (isReceivedMessage) {
            binding.giftReceviceStatus.gravity = Gravity.LEFT
            binding.receiverLl.isVisible = true
            when (status) { //0未领取 1已领取 2已退回
                SendGiftAttachment.STATUS_WAIT -> {
                    binding.giftReceviceStatus.isVisible = false
                    binding.acceptBtn.isVisible = true
                    binding.declineBtn.text = "拒绝"
                    binding.declineBtn.isEnabled = true
                    binding.giftStutasIv.isVisible = false
                    binding.refuseCover.isVisible = false


                }
                SendGiftAttachment.STATUS_RECEIVED -> {
                   binding.giftReceviceStatus.isVisible = false
                   binding.acceptBtn.isVisible = false
                   binding.declineBtn.text = "已接受"
                   binding.declineBtn.isEnabled = false
                   binding.giftStutasIv.isVisible = false
                   binding.refuseCover.isVisible = false

                }
                SendGiftAttachment.STATUS_RETURNED -> {
                   binding.giftReceviceStatus.isVisible = false
                   binding.acceptBtn.isVisible = false
                   binding.declineBtn.text = "已拒绝"
                   binding.declineBtn.isEnabled = false
                   binding.giftStutasIv.isVisible = true
                   binding.refuseCover.isVisible = true
                   binding.giftStutasIv.setImageResource(R.drawable.icon_gift_declined)

                }
                SendGiftAttachment.STATUS_TIMEOUT -> {
                   binding.giftReceviceStatus.isVisible = false
                   binding.giftReceviceStatus.text = "超时已退回"
                   binding.receiverLl.isVisible = false
                   binding.giftStutasIv.isVisible = true
                   binding.refuseCover.isVisible = true
                   binding.giftStutasIv.setImageResource(R.drawable.icon_gift_timeout)
                }
            }

            //接受礼物
           ClickUtils.applySingleDebouncing(binding.acceptBtn) {
                getGift(giftType)


            }

            //拒绝礼物
           ClickUtils.applySingleDebouncing(binding.declineBtn) {
                MessageDialog.show(
                    ActivityUtils.getTopActivity() as AppCompatActivity,
                    msgAdapter.container.activity.getString(R.string.notice),
                    msgAdapter.container.activity.getString(R.string.refuse_will_return),
                    msgAdapter.container.activity.getString(R.string.cancel),
                    msgAdapter.container.activity.getString(R.string.still_refuse)
                )
                    .setButtonPositiveTextInfo(TextInfo().setFontColor(Color.parseColor("#FF94AEFF")))
                    .setButtonTextInfo(TextInfo().setFontColor(Color.parseColor("#FFC6CAD4")))
                    .setOnOkButtonClickListener { baseDialog, v ->
                        false
                    }
                    .setOnCancelButtonClickListener { baseDialog, v ->
                        refundGift()
                        false
                    }

            }
        } else {
           binding.giftReceviceStatus.gravity = Gravity.RIGHT
//            setFrameGravity(view.giftViewCl, Gravity.RIGHT)
           binding.receiverLl.isVisible = false
           binding.giftReceviceStatus.isVisible = true
            when (status) { //0未领取 1已领取 2已退回
                SendGiftAttachment.STATUS_WAIT -> {
                   binding.giftReceviceStatus.text =
                        if (giftType == SendGiftAttachment.GIFT_TYPE_CONTACT) {
                            context.getString(R.string.contact_gift_wait_public)
                        } else {
                            context.getString(R.string.contact_gift_wait_get)
                        }
                   binding.giftStutasIv.isVisible = false
                   binding.refuseCover.isVisible = false

                }
                SendGiftAttachment.STATUS_RECEIVED -> {
                   binding.giftReceviceStatus.text =
                        context.getString(R.string.contact_gift_wait_accepted)
                   binding.giftStutasIv.isVisible = false
                   binding.refuseCover.isVisible = false

                }
                SendGiftAttachment.STATUS_RETURNED -> {
                   binding.giftReceviceStatus.text =
                        if (giftType == SendGiftAttachment.GIFT_TYPE_CONTACT) {
                            context.getString(R.string.contact_gift_refuse_public)
                        } else {
                            context.getString(R.string.contact_gift_returned)
                        }
                   binding.giftStutasIv.isVisible = true
                   binding.refuseCover.isVisible = true

                   binding.giftStutasIv.setImageResource(R.drawable.icon_gift_returned)
                }
                SendGiftAttachment.STATUS_TIMEOUT -> {
                   binding.giftReceviceStatus.text =
                        if (giftType == SendGiftAttachment.GIFT_TYPE_CONTACT) {
                            context.getString(R.string.contact_gift_return_timeout)
                        } else {
                            context.getString(R.string.contact_gift_returned_timeout)
                        }
                   binding.giftStutasIv.isVisible = true
                   binding.refuseCover.isVisible = true
                   binding.giftStutasIv.setImageResource(R.drawable.icon_gift_returned)
                }

            }
        }

        GlideUtil.loadImg(context, attachment.giftIcon,binding.giftIcon)
       binding.giftName.text = attachment.giftName
       binding.giftAmount.text = "${attachment.giftAmount}"


    }

//    private fun sendNotification(message: IMMessage, status: Int) {
//        val customNotification = CustomNotification()
//        customNotification.sessionId = msgAdapter.container.account
//        customNotification.sessionType = msgAdapter.container.sessionType
//        val config = CustomNotificationConfig()
//        config.enablePush = false
//        config.enableUnreadCount = false
//        customNotification.config = config
//
//
//        val notification = NotificationGiftBean(status, message.uuid)
//        val msgBean = CustomerMsgBean(100, notification, "礼物状态修改")
//        customNotification.content = Gson().toJson(msgBean)
//        NIMClient.getService(MsgService::class.java).sendCustomNotification(customNotification)
//    }


    /**
     * 领取礼物
     */
    fun getGift(giftType: Int) {
        val params = hashMapOf<String, Any>()
        params["order_id"] = attachment.orderId
        RetrofitHelper.service.getGift(params).ssss(null,false) {
            if (it.code == 200) {
                CommonFunction.updateMessageExtension(
                    message,
                    SendGiftAttachment.KEY_STATUS,
                    SendGiftAttachment.STATUS_RECEIVED
                )
                msgAdapter.notifyItemChanged(layoutPosition)
    //                CommonFunction.sendGiftNotification(
    //                    message,
    //                    SendGiftAttachment.STATUS_RECEIVED
    //                )


                if (giftType == SendGiftAttachment.GIFT_TYPE_CONTACT) {
                    // 如果是联系方式 直接领取并发送联系方式
                    val attachment = SendWechatAttachment()
                    attachment.url = it.data.contact_way
                    val messge = MessageBuilder.createCustomMessage(
                        msgAdapter.container.account,
                        SessionTypeEnum.P2P,
                        attachment
                    )
                    msgAdapter.container.proxy.sendMessage(messge)
                } else {
    //                    ShowReceiveGiftDialog(msgAdapter.container, attachment).show()
                }
            } else if (it.code == 400) {
                CommonFunction.updateMessageExtension(
                    message,
                    SendGiftAttachment.KEY_STATUS,
                    SendGiftAttachment.STATUS_TIMEOUT
                )
                msgAdapter.notifyItemChanged(layoutPosition)
    //                CommonFunction.sendGiftNotification(
    //                    message,
    //                    SendGiftAttachment.STATUS_TIMEOUT
    //                )
            }
        }


    }

    /**
     * 拒绝领取礼物
     */
    fun refundGift() {
        val params = hashMapOf<String, Any>()
        params["order_id"] = attachment.orderId
        RetrofitHelper.service.refundGift(params).ssss(onResult = {

            if (it.code == 200 || it.code == 400) {
                CommonFunction.updateMessageExtension(
                    message,
                    SendGiftAttachment.KEY_STATUS,
                    SendGiftAttachment.STATUS_RETURNED
                )
                msgAdapter.notifyItemChanged(layoutPosition)
//                CommonFunction.sendGiftNotification(
//                    message,
//                    SendGiftAttachment.STATUS_RETURNED
//                )

            }
        })
    }


    override fun onItemClick() {
    }

}