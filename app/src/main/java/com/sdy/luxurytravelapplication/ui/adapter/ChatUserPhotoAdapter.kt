package com.sdy.luxurytravelapplication.ui.adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemChatUserPhotoBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

/**
 *    author : ZFM
 *    date   : 2020/8/2814:31
 *    desc   :
 *    version: 1.0
 */
class ChatUserPhotoAdapter :
    BaseBindingQuickAdapter<String, ItemChatUserPhotoBinding>(R.layout.item_chat_user_photo) {
    public var leftCnt = 0
    override fun convert(binding: ItemChatUserPhotoBinding, position: Int, item: String) {
        binding.apply {
            val params1 = root.layoutParams as RecyclerView.LayoutParams
            params1.leftMargin = if (position == 0) {
                SizeUtils.dp2px(15F)
            } else {
                SizeUtils.dp2px(5F)
            }

            params1.rightMargin =
                if (position == data.size - 1) {
                    SizeUtils.dp2px(15F)
                } else {
                    SizeUtils.dp2px(0F)
                }
            params1.width = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(15 * 2 + 5 * 4F)) / 5
            params1.height = params1.width
            root.layoutParams = params1


            GlideUtil.loadRoundImgCenterCrop(
                context,
                item,
                userPhoto,
                SizeUtils.dp2px(10F)
            )
            userPhotoCnt.isVisible = position == data.size - 1 && leftCnt > 0
            userPhotoCnt.text = "+$leftCnt"
        }

    }
}