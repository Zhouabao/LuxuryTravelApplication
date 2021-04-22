package com.sdy.luxurytravelapplication.nim.business.emoji

import com.sdy.luxurytravelapplication.databinding.ItemChatEmojBinding
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class ChatEmojAdapter : BaseBindingQuickAdapter<Int, ItemChatEmojBinding>() {
    override fun convert(binding: ItemChatEmojBinding, position: Int, item: Int) {
        binding.chatEmoj.setImageDrawable(EmojiManager.getDisplayDrawable(context, position))
    }


}
