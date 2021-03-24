package com.sdy.luxurytravelapplication.ui.adapter

import android.widget.LinearLayout
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemSweetNormalPicBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

/**
 *    author : ZFM
 *    date   : 2021/3/1916:58
 *    desc   :
 *    version: 1.0
 */
class VerifyNormalAdapter(private val closeCallBack: CloseCallBack) : BaseBannerAdapter<String>() {
    interface CloseCallBack {
        fun close()
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_sweet_normal_pic
    }

    override fun bindData(
        holder: BaseViewHolder<String>,
        data: String,
        position: Int,
        pageSize: Int
    ) {
        val binding = ItemSweetNormalPicBinding.bind(holder.itemView)
        val params = binding.bannerImage.layoutParams as LinearLayout.LayoutParams
        params.width = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(37 * 2F)
        params.height = params.width / 3 * 4
        binding.bannerImage.layoutParams = params
        GlideUtil.loadRoundImgCenterCrop(
            binding.root.context,
            data,
            binding.bannerImage,
            SizeUtils.dp2px(10F)
        )
        binding.bannerClose.setOnClickListener {
            closeCallBack.close()
        }
    }
}