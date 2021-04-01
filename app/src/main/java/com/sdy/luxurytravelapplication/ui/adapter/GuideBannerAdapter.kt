package com.sdy.luxurytravelapplication.ui.adapter

import android.widget.ImageView
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.BannerGuideBean
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

/**
 *    author : ZFM
 *    date   : 2021/3/1916:58
 *    desc   :
 *    version: 1.0
 */
class GuideBannerAdapter : BaseBannerAdapter<BannerGuideBean>() {

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_guide_banner
    }

    override fun bindData(
        holder: BaseViewHolder<BannerGuideBean>,
        data: BannerGuideBean,
        position: Int,
        pageSize: Int
    ) {
        val bannerImager = holder.findViewById<ImageView>(R.id.bannerImage)

        GlideUtil.loadImg(holder.itemView.context, data.image, bannerImager)
    }
}