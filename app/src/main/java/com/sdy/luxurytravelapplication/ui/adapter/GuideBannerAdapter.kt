package com.sdy.luxurytravelapplication.ui.adapter

import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemGuideBannerBinding
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

    private val lotties = arrayListOf<LottieAnimationView>()
    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_guide_banner
    }

    override fun bindData(
        holder: BaseViewHolder<BannerGuideBean>,
        data: BannerGuideBean,
        position: Int,
        pageSize: Int
    ) {
        val binding = ItemGuideBannerBinding.bind(holder.itemView)
        binding.apply {
            if (data.fileName.isNotEmpty()) {
                bannerAnimation.isVisible = true
                bannerImage.isVisible = false
                view1.isVisible = true
                bannerAnimation.imageAssetsFolder = data.imageName
                bannerAnimation.setAnimation(data.fileName)
                lotties.add(bannerAnimation)
            } else {
                view1.isVisible = false
                bannerAnimation.isVisible = false
                bannerImage.isVisible = true
                GlideUtil.loadImg(root.context, data.image, bannerImage)
            }
            bannerTitle.text = data.title
            bannerContent.text = data.descr
        }
    }

    fun clearAnimation() {
        lotties.forEach {
            it.clearAnimation()
        }
    }
}