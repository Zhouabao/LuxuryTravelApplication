package com.sdy.luxurytravelapplication.ui.adapter

import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemVideoVerifyBannerBinding
import com.sdy.luxurytravelapplication.mvp.model.bean.VideoVerifyBannerBean
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class VideoVerifyHolderView : BaseBannerAdapter<VideoVerifyBannerBean>() {

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_video_verify_banner
    }

    override fun bindData(
        holder: BaseViewHolder<VideoVerifyBannerBean>,
        data: VideoVerifyBannerBean,
        position: Int,
        pageSize: Int
    ) {
        val viewBinding = ItemVideoVerifyBannerBinding.bind(holder.itemView)
        viewBinding.apply {
            iv1.setImageResource(data.icon)
            tv1.text = data.content
            tv2.text = data.title
        }
    }
}