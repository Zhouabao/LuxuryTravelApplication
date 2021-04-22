package com.sdy.luxurytravelapplication.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemMessageChatupBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.AccostBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class MessageCenterAllAdapter :
    BaseBindingQuickAdapter<AccostBean, ItemMessageChatupBinding>(R.layout.item_message_chatup) {
    override fun convert(binding: ItemMessageChatupBinding, position: Int, item: AccostBean) {
        binding.apply {
            GlideUtil.loadRoundImgCenterCrop(
                context,
                item.avatar,
                accostUserIv,
                SizeUtils.dp2px(15F)
            )
            if (position == 0) {
                (root.layoutParams as RecyclerView.LayoutParams).leftMargin = SizeUtils.dp2px(15F)
            } else {
                (root.layoutParams as RecyclerView.LayoutParams).leftMargin = 0
            }

            if (position == data.lastIndex) {
                (root.layoutParams as RecyclerView.LayoutParams).rightMargin =
                    SizeUtils.dp2px(36 + 10F)
            }
            if (NIMClient.getService(MsgService::class.java)
                    .queryRecentContact(item.accid, SessionTypeEnum.P2P) != null
                && NIMClient.getService(MsgService::class.java)
                    .queryRecentContact(item.accid, SessionTypeEnum.P2P).unreadCount > 0
            ) {
                accostGiftIv.visibility = View.VISIBLE
            } else {
                accostGiftIv.visibility = View.INVISIBLE
            }
        }

    }

}
