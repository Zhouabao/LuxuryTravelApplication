package com.sdy.luxurytravelapplication.nim.business.session.viewholder

import android.util.Log
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ClickUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.NimMessageSendGiftBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.nim.attachment.SendGiftAttachment
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
        binding = NimMessageSendGiftBinding.inflate(LayoutInflater.from(context), contentContainer, true)
    }

    override fun bindContentView() {
        val giftReceiveStatus: Int = attachment.giftStatus
        if (isReceivedMessage) {
            if (giftReceiveStatus == SendGiftAttachment.GIFT_RECEIVE_STATUS_NORMAL) {
                binding.giftTitle.setText(R.string.open_gift)
                binding.giftType.setText(R.string.gift_wait_open)
            } else if (giftReceiveStatus == SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_OPEN) {
                binding.giftTitle.setText(R.string.gift_has_opend)
                binding.giftType.setText(R.string.gift_has_received)
            } else if (giftReceiveStatus == SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_RETURNED) {
                binding.giftTitle.setText(R.string.gift_revoke)
                binding.giftType.setText(R.string.revoke_cause_time_out)
            }
            binding.giftImg.isVisible = false
            binding.rightGiftImg.isVisible = true
        } else {
            if (giftReceiveStatus == SendGiftAttachment.GIFT_RECEIVE_STATUS_NORMAL) {
                binding.giftTitle.setText(R.string.wait_open)
                binding.giftType.setText(R.string.gift_wait_open_my)
            } else if (giftReceiveStatus == SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_OPEN) {
                binding.giftTitle.setText(R.string.gift_has_opend)
                binding.giftType.setText(R.string.gift_has_been_received)
            } else if (giftReceiveStatus == SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_RETURNED) {
                binding.giftTitle.setText(R.string.revoke_cause_time_out)
                binding.giftType.setText(R.string.gift_has_been_revoked)
            }
            binding.giftImg.isVisible = true
            binding.rightGiftImg.isVisible = false
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
        return R.drawable.shape_rectangle_stroke_eceff4_15dp
    }

    override fun rightBackground(): Int{
        return R.drawable.shape_rectangle_stroke_eceff4_15dp
    }

    override fun onItemClick() {
    }

}