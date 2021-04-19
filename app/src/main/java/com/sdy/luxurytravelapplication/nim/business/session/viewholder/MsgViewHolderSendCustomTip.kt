package com.sdy.luxurytravelapplication.nim.business.session.viewholder

import android.content.Intent
import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import com.blankj.utilcode.util.SpanUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.NimMessageItemCustomTipBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.liveface.FaceLivenessExpActivity
import com.sdy.luxurytravelapplication.nim.attachment.SendCustomTipAttachment
import com.sdy.luxurytravelapplication.nim.business.module.list.MsgAdapter
import com.sdy.luxurytravelapplication.ui.activity.SettingsActivity
import java.util.*

/**
 *    author : ZFM
 *    date   : 2020/10/2011:15
 *    desc   :
 *    version: 1.0
 */
class MsgViewHolderSendCustomTip(adapter: MsgAdapter) : MsgViewHolderBase(adapter) {
    override val contentResId: Int
        get() = R.layout.nim_message_item_custom_tip

    private val attachment by lazy { message.attachment as SendCustomTipAttachment }
    private val binding by lazy { NimMessageItemCustomTipBinding.bind(view) }
    override fun inflateContentView() {
    }


    override fun bindContentView() {
        binding.apply {
            when (attachment.showType) {
                SendCustomTipAttachment.CUSTOME_TIP_EXCHANGE_FOR_ASSISTANT -> {
                    val tokenizer = StringTokenizer(
                        attachment.content,
                        context.getString(R.string.verify_now)
                    )
                    try {
                        SpanUtils.with(messageItemNotificationLabel).append(tokenizer.nextToken())
                            .setForegroundColor(Color.parseColor("#FFC5C6C8"))
                            .append(context.getString(R.string.verify_now))
                            .setClickSpan(object : ClickableSpan() {
                                override fun updateDrawState(ds: TextPaint) {
                                    ds.color = Color.parseColor("#FF6796FA")
                                    ds.isUnderlineText = false
                                }

                                override fun onClick(widget: View) {
                                    CommonFunction.startToFace(
                                        context,
                                        FaceLivenessExpActivity.TYPE_ACCOUNT_NORMAL,
                                        -1
                                    )
                                }
                            })
                            .setForegroundColor(Color.parseColor("#FF6796FA"))
                            .append(tokenizer.nextToken())
                            .setForegroundColor(Color.parseColor("#FFC5C6C8"))
                            .create()
                    } catch (e: Exception) {
                        messageItemNotificationLabel.setText(attachment.content)
                        e.printStackTrace()
                    }
                }
                SendCustomTipAttachment.CUSTOME_TIP_PRIVICY_SETTINGS ->
                    SpanUtils.with(messageItemNotificationLabel)
                        .append(context.getString(R.string.too_many_message_1))
                        .setForegroundColor(Color.parseColor("#FFC5C6C8"))
                        .append(context.getString(R.string.too_many_message_2))
                        .setClickSpan(object : ClickableSpan() {
                            override fun updateDrawState(ds: TextPaint) {
                                ds.color = Color.parseColor("#FF6796FA")
                                ds.isUnderlineText = false
                            }

                            override fun onClick(widget: View) {
                                val intent = Intent(context, SettingsActivity::class.java)
                                context.startActivity(intent)
                            }
                        })
                        .setForegroundColor(Color.parseColor("#FF6796FA"))
                        .append(context.getString(R.string.too_many_message_3))
                        .setForegroundColor(Color.parseColor("#FFC5C6C8"))
                        .create()
                SendCustomTipAttachment.CUSTOME_TIP_CHARGE_PT_VIP ->
                    SpanUtils.with(messageItemNotificationLabel)
                        .append(context.getString(R.string.free_chat_message1))
                        .setForegroundColor(Color.parseColor("#FFC5C6C8"))
                        .append(context.getString(R.string.free_chat_message2))
                        .setClickSpan(object : ClickableSpan() {
                            override fun updateDrawState(ds: TextPaint) {
                                ds.color = Color.parseColor("#FF6796FA")
                                ds.isUnderlineText = false
                            }

                            override fun onClick(widget: View) {
                                CommonFunction.startToVip(context)
                            }
                        })
                        .setForegroundColor(Color.parseColor("#FF6796FA"))
                        .append(context.getString(R.string.free_chat_message3))
                        .setForegroundColor(Color.parseColor("#FFC5C6C8"))
                        .create()
                else -> messageItemNotificationLabel.setText(attachment.content)
            }
        }
    }

    override val isShowBubble: Boolean get() = false
    override val isShowHeadImage: Boolean get() = false
    override val isShowTime: Boolean get() = false

    override fun shouldDisplayNick(): Boolean {
        return false
    }


}