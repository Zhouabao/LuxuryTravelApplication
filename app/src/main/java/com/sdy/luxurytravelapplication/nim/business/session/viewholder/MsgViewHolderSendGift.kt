package com.sdy.luxurytravelapplication.nim.business.session.viewholder

import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ClickUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.NimMessageSendGiftBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.nim.attachment.SendGiftAttachment
import com.sdy.luxurytravelapplication.nim.business.module.list.MsgAdapter

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
        binding =
            NimMessageSendGiftBinding.inflate(LayoutInflater.from(context), contentContainer, true)
    }

    override fun bindContentView() {
        val giftReceiveStatus: Int = attachment.giftStatus
        binding.revokeStatus.isVisible =
            giftReceiveStatus != SendGiftAttachment.GIFT_RECEIVE_STATUS_NORMAL
        binding.giftTitle.text = "旅券礼物待开启"
        if (isReceivedMessage) {
            if (giftReceiveStatus == SendGiftAttachment.GIFT_RECEIVE_STATUS_NORMAL) {
                binding.giftType.setText(R.string.gift_receive_wait_open)
            } else if (giftReceiveStatus == SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_OPEN) {
                binding.giftType.setText(R.string.gift_receive_has_received)
            } else if (giftReceiveStatus == SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_RETURNED) {
                binding.giftType.setText(R.string.gift_receive_has_revoked)
            }
        } else {
            if (giftReceiveStatus == SendGiftAttachment.GIFT_RECEIVE_STATUS_NORMAL) {
                binding.giftType.setText(R.string.gift_send_wait_open)
            } else if (giftReceiveStatus == SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_OPEN) {
                binding.giftType.setText(R.string.gift_send_has_been_received)
            } else if (giftReceiveStatus == SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_RETURNED) {
                binding.giftType.setText(R.string.gift_send_has_been_revoked)
            }
        }

        ClickUtils.applySingleDebouncing(binding.root) {
            CommonFunction.openGiftLetter(
                isReceivedMessage,
                attachment.giftStatus,
                attachment.orderId,
                context,
                message.sessionId
            )
        }

    }

    override fun leftBackground(): Int {
//        if (attachment.giftStatus == SendGiftAttachment.GIFT_RECEIVE_STATUS_NORMAL)
        return R.drawable.shape_rectangle_stroke_eceff4_15dp
//        else
//            return R.drawable.shape_gift_revoked_15dp

    }

    override fun rightBackground(): Int {
//        if (attachment.giftStatus == SendGiftAttachment.GIFT_RECEIVE_STATUS_NORMAL)
        return R.drawable.shape_rectangle_stroke_eceff4_15dp
//        else
//            return R.drawable.shape_gift_revoked_15dp
    }

    override fun onItemClick() {
    }

}