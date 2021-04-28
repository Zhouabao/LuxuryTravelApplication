package com.sdy.luxurytravelapplication.ui.adapter

import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemDetailImgViewpagerBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.VideoJson
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class SquareDetailImgsAdaper : BaseBannerAdapter<VideoJson>() {
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_detail_img_viewpager

    }

    override fun bindData(
        holder: BaseViewHolder<VideoJson>,
        data: VideoJson,
        position: Int,
        pageSize: Int
    ) {
        val binding = ItemDetailImgViewpagerBinding.bind(holder.itemView)
        binding.apply {
            GlideUtil.loadImg(root.context, data.url, root)
        }
    }

}
