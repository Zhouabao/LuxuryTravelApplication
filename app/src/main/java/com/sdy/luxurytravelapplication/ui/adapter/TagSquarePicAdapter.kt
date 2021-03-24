package com.sdy.luxurytravelapplication.ui.adapter

import android.text.method.Touch.onTouchEvent
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ActivityUtils.isActivityExistsInStack
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ItemLayoutTagSquareBinding
import com.sdy.luxurytravelapplication.databinding.ItemTitlePicBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.RecommendSquareBean
import com.sdy.luxurytravelapplication.mvp.model.bean.SquarePicBean
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareTagBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class TagSquarePicAdapter(private var spanCount: Int = 3)  :
    BaseBindingQuickAdapter<SquarePicBean, ItemTitlePicBinding>(R.layout.item_title_pic) {
    override fun convert(
        binding: ItemTitlePicBinding,
        position: Int,
        item: SquarePicBean
    ) {
        binding.apply {
            val params = root.layoutParams as RecyclerView.LayoutParams
            params.width = ((ScreenUtils.getScreenWidth() - SizeUtils.dp2px(50F) - SizeUtils.dp2px(3F) * (spanCount - 1)) / spanCount)
            params.height = ((ScreenUtils.getScreenWidth() - SizeUtils.dp2px(50F) - SizeUtils.dp2px(3F) * (spanCount - 1)) / spanCount)
            if (item.cover_url.isNotEmpty()) {
                pic.isVisible = true
                content.isVisible = false
                when (position) {
                    0 -> {
                        GlideUtil.loadRoundImgCenterCrop(
                            context,
                            item.cover_url,
                            pic,SizeUtils.dp2px(15F),RoundedCornersTransformation.CornerType.LEFT
                        )
                    }
                    data.lastIndex -> {
                        GlideUtil.loadRoundImgCenterCrop(
                            context,
                            item.cover_url,
                            pic, SizeUtils.dp2px(15F), RoundedCornersTransformation.CornerType.RIGHT
                        )
                    }
                    else -> {
                        GlideUtil.loadImgCenterCrop(
                            context,
                            item.cover_url,
                            pic
                        )
                    }
                }
            } else {
                content.isVisible = true
                pic.isVisible = false
                content.text = item.descr
            }
        }
    }
}