package com.sdy.luxurytravelapplication.ui.adapter

import androidx.core.view.isVisible
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemTargetSmallPhotoBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

/**
 * 对方个人页小图模式
 */
class TargetSmallPhotoAdapter :
    BaseBindingQuickAdapter<String, ItemTargetSmallPhotoBinding>(R.layout.item_target_small_photo) {
    override fun convert(
        binding: ItemTargetSmallPhotoBinding,
        position: Int,
        item: String
    ) {
        binding.apply {
            GlideUtil.loadRoundImgCenterCrop(
                context,
                item,
                smallImg,
                SizeUtils.dp2px(10F)
            )
            checkedView.isVisible = position == 0
        }
    }


}
