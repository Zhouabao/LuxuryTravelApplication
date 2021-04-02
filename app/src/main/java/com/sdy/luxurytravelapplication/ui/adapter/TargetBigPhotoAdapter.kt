package com.sdy.luxurytravelapplication.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemTargetBigPhotoBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

/**
 * 对方个人页大图模式
 */
class TargetBigPhotoAdapter :
    BaseBindingQuickAdapter<String, ItemTargetBigPhotoBinding>(R.layout.item_target_big_photo) {
    override fun convert(
        binding: ItemTargetBigPhotoBinding,
        position: Int,
        item: String
    ) {
        binding.apply {
//            val params = root.layoutParams as RecyclerView.LayoutParams
//            params.width = ScreenUtils.getScreenWidth()
//            params.height = (5 / 4F * params.width).toInt()
            GlideUtil.loadRoundImgCenterCrop(
                context,
                item,
                root,
                SizeUtils.dp2px(20F),
                RoundedCornersTransformation.CornerType.BOTTOM
            )
        }
    }


}
