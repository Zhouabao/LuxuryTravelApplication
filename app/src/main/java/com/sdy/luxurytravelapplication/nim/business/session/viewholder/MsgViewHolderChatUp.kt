package com.sdy.luxurytravelapplication.nim.business.session.viewholder

import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ClickUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.nim.attachment.ChatUpAttachment
import com.sdy.luxurytravelapplication.nim.business.module.list.MsgAdapter
import com.sdy.luxurytravelapplication.nim.impl.NimUIKitImpl
import com.sdy.luxurytravelapplication.ui.activity.CustomChatUpContentActivity

/**
 *    author : ZFM
 *    date   : 2020/10/2011:15
 *    desc   :
 *    version: 1.0
 */
class MsgViewHolderChatUp(msgAdapter1: MsgAdapter) : MsgViewHolderBase(msgAdapter1) {
    private val attachment by lazy { message.attachment as ChatUpAttachment }

    private lateinit var contentView: LinearLayout
    private lateinit var chatUpContent: TextView
    private lateinit var customChatUpBtn //自定义搭讪语btn
            : TextView


    override val contentResId: Int
        get() = R.layout.item_chat_up

    //    private lateinit var binding: ItemChatUpBinding
    override fun inflateContentView() {
        //初始化数据

        contentView = findViewById(R.id.contentView)
        chatUpContent = findViewById(R.id.chatUpContent)
        customChatUpBtn = findViewById(R.id.customChatUpBtn)
//        binding = ItemChatUpBinding.inflate(LayoutInflater.from(context), contentContainer, true)
    }

    override fun bindContentView() {

//        binding.apply {
        if (isReceivedMessage) {
//            setGravity(contentView,Gravity.LEFT);
            (chatUpContent.layoutParams as LinearLayout.LayoutParams).gravity = Gravity.LEFT
            chatUpContent.setTextColor(Color.BLACK)
            chatUpContent.setBackgroundResource(NimUIKitImpl.getOptions().messageLeftBackground)
            customChatUpBtn.isVisible = false
        } else {
            (chatUpContent.layoutParams as LinearLayout.LayoutParams).gravity = Gravity.RIGHT
            chatUpContent.setTextColor(Color.WHITE)
            chatUpContent.setBackgroundResource(NimUIKitImpl.getOptions().messageRightBackground)
            customChatUpBtn.isVisible = true
        }

        chatUpContent.text = attachment.chatUpContent
        ClickUtils.applySingleDebouncing(customChatUpBtn) {
            ActivityUtils.startActivity(CustomChatUpContentActivity::class.java)
//            }
        }
    }

    override fun leftBackground(): Int {
        return R.color.transparent
    }

    override fun rightBackground(): Int {
        return R.color.transparent
    }

    override fun onItemClick() {

    }

    override val isShowBubble: Boolean
        get() = true

}


