package com.sdy.luxurytravelapplication.nim.business.session.viewholder

import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemChatDatingBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.nim.attachment.ChatDatingAttachment
import com.sdy.luxurytravelapplication.nim.business.module.list.MsgAdapter
import com.sdy.luxurytravelapplication.ui.activity.TravelDetailActivity

/**
 *    author : ZFM
 *    date   : 2020/10/2011:15
 *    desc   :
 *    version: 1.0
 */
class MsgViewHolderChatDating(msgAdapter1: MsgAdapter) : MsgViewHolderBase(msgAdapter1) {
    private val attachment by lazy { message.attachment as ChatDatingAttachment }

    private lateinit var datingContent // 分享文字内容
            : TextView 
    private lateinit var datingName // 分享文字内容
            : TextView 
    private lateinit var datingImg // 分享的图片
            : ImageView 
    override val contentResId: Int
        get() = R.layout.item_chat_dating

//    private lateinit var binding: ItemChatDatingBinding
    override fun inflateContentView() {
//        binding = ItemChatDatingBinding.inflate(LayoutInflater.from(context), contentContainer, true)
        // 初始化数据

        datingName = findViewById(R.id.datingName)
        datingContent = findViewById(R.id.datingContent)
        datingImg = findViewById(R.id.datingImg)
    }

    override fun bindContentView() {
//        binding.apply {
            datingContent.setText(attachment.content)
            GlideUtil.loadImg(
                context,
                attachment.img,
                datingImg
            )
//        }
    }

    override fun leftBackground(): Int {
        return R.drawable.shape_rectangle_stroke_eceff4_15dp
    }

    override fun rightBackground(): Int {
        return R.drawable.shape_rectangle_stroke_eceff4_15dp
    }

    override fun onItemClick() {
        TravelDetailActivity.start(context, dating_id = attachment.datingId)
    }

}


