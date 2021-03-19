package com.sdy.luxurytravelapplication.adapter

import android.widget.ImageView
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

/**
 *    author : ZFM
 *    date   : 2021/3/1916:58
 *    desc   :
 *    version: 1.0
 */
class GuideBannerAdapter : BaseBannerAdapter<String>() {

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_guide_banner
    }

    override fun bindData(
        holder: BaseViewHolder<String>,
        data: String,
        position: Int,
        pageSize: Int
    ) {
        val bannerImager = holder.findViewById<ImageView>(R.id.bannerImage)

        GlideUtil.loadImgCenterCrop(holder.itemView.context, data, bannerImager)
    }
}