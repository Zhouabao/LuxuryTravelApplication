package com.sdy.luxurytravelapplication.ui.adapter

import android.widget.ImageView
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
interface CloseCallBack{
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
        GlideUtil.loadRoundImgCenterCrop(
            binding.root.context,
            data,
            binding.bannerImage,
            SizeUtils.dp2px(15F)
        )
        binding.bannerClose.setOnClickListener {
            closeCallBack.close()
        }
    }
}