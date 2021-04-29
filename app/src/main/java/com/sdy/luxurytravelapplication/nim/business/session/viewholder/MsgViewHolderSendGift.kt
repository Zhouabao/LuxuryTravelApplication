package com.sdy.luxurytravelapplication.nim.business.session.viewholder

import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
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
    private lateinit var giftImg: ImageView
    private lateinit var rightGiftImg: ImageView
    private lateinit var giftTitle //礼物的名字
            : TextView
    private lateinit var giftType //礼物状态类型
            : TextView
    private lateinit var giftStatusBg //礼物背景状态
            : RelativeLayout


    private val attachment by lazy { message.attachment as SendGiftAttachment }
    override val contentResId: Int
        get() = R.layout.nim_message_send_gift

//    private lateinit var binding: NimMessageSendGiftBinding
    override fun inflateContentView() {
        //初始化数据
        giftTitle = findViewById(R.id.giftTitle)
        giftType = findViewById(R.id.giftType)
        giftStatusBg = findViewById(R.id.giftStatusBg)
        giftImg = findViewById(R.id.giftImg)
        rightGiftImg = findViewById(R.id.rightGiftImg)
//        binding = NimMessageSendGiftBinding.inflate(LayoutInflater.from(context), contentContainer, true)
    }

    override fun bindContentView() {
        val giftReceiveStatus: Int = attachment.giftStatus
        apply {

            if (isReceivedMessage) {
                if (giftReceiveStatus == SendGiftAttachment.GIFT_RECEIVE_STATUS_NORMAL) {
                    giftTitle.setText(R.string.open_gift)
                    giftType.setText(R.string.gift_wait_open)
                } else if (giftReceiveStatus == SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_OPEN) {
                    giftTitle.setText(R.string.gift_has_opend)
                    giftType.setText(R.string.gift_has_received)
                } else if (giftReceiveStatus == SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_RETURNED) {
                    giftTitle.setText(R.string.gift_revoke)
                    giftType.setText(R.string.revoke_cause_time_out)
                }
                giftImg.isVisible = false
                rightGiftImg.isVisible = true
            } else {
                if (giftReceiveStatus == SendGiftAttachment.GIFT_RECEIVE_STATUS_NORMAL) {
                    giftTitle.setText(R.string.wait_open)
                    giftType.setText(R.string.gift_wait_open_my)
                } else if (giftReceiveStatus == SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_OPEN) {
                    giftTitle.setText(R.string.gift_has_opend)
                    giftType.setText(R.string.gift_has_been_received)
                } else if (giftReceiveStatus == SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_RETURNED) {
                    giftTitle.setText(R.string.revoke_cause_time_out)
                    giftType.setText(R.string.gift_has_been_revoked)
                }
                giftImg.isVisible = true
                rightGiftImg.isVisible = false
            }

            ClickUtils.applySingleDebouncing(contentContainer) {
                CommonFunction.openGiftLetter(
                    isReceivedMessage,
                    attachment.giftStatus,
                    attachment.orderId,
                    context,
                    message.sessionId
                )
            }
        }

    }

    override fun leftBackground(): Int {
        return R.drawable.shape_rectangle_stroke_eceff4_15dp
    }

    override fun rightBackground(): Int {
        return R.drawable.shape_rectangle_stroke_eceff4_15dp
    }

    override fun onItemClick() {
    }

}