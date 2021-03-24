package com.sdy.luxurytravelapplication.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemSweetNormalBottomPicBinding
import com.sdy.luxurytravelapplication.databinding.ItemSweetNormalPicBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class SweetNormalPicAdapter :
    BaseBindingQuickAdapter<String, ItemSweetNormalBottomPicBinding>(R.layout.item_sweet_normal_bottom_pic) {
    override fun convert(binding: ItemSweetNormalBottomPicBinding, position: Int, item: String) {

        binding.apply {
                val params = root.layoutParams as RecyclerView.LayoutParams
                params.width = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(30 + 20F)) / 3
                params.height = params.width
                params.leftMargin = if (position == 0) {
                    SizeUtils.dp2px(15F)
                } else {
                    0
                }
                params.rightMargin = if (position == data.size - 1) {
                    SizeUtils.dp2px(15F)
                } else {
                    SizeUtils.dp2px(10F)
                }

            GlideUtil.loadRoundImgCenterCrop(
                context,
                item,
                bannerImage, SizeUtils.dp2px(15F)
            )
        }
    }

}
