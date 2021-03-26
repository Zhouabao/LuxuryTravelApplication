package com.sdy.luxurytravelapplication.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemMatchDetailImgBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.VideoJson
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class ListSquareImgsAdapter(private var fullScreenWidth: Boolean = false, var spanCnt: Int = 3) :
    BaseBindingQuickAdapter<VideoJson, ItemMatchDetailImgBinding>(
        R.layout.item_match_detail_img
    ) {
    override fun convert(binding: ItemMatchDetailImgBinding, position: Int, item: VideoJson) {

        binding.apply {
            if (!fullScreenWidth) {
//            if (datas.size == 1) {
//                val layoutParams = holder.itemView.ivUser.layoutParams as RecyclerView.LayoutParams
//                layoutParams.width = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(30F)
//                layoutParams.height = layoutParams.width
//                layoutParams.leftMargin = SizeUtils.dp2px(15F)
//                layoutParams.rightMargin = SizeUtils.dp2px(15F)
//
//                holder.itemView.ivUser.layoutParams = layoutParams
//                GlideUtil.loadRoundImgCenterCrop(
//                    mContext,
//                    datas[position].url,
//                    holder.itemView.ivUser,
//                    SizeUtils.dp2px(5F)
//                )
//
//            } else {
                val layoutParams = binding.ivUser.layoutParams as RecyclerView.LayoutParams
                layoutParams.width =
                    (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(15 * 2F + 6 * (spanCnt - 1))) / spanCnt
                layoutParams.leftMargin = SizeUtils.dp2px(6F)
                layoutParams.topMargin = SizeUtils.dp2px(6F)
                layoutParams.height = layoutParams.width
//                layoutParams.leftMargin = SizeUtils.dp2px(15F)
//                if (position == datas.size - 1) {
//                    layoutParams.rightMargin = SizeUtils.dp2px(15F)
//                }
                binding.ivUser.layoutParams = layoutParams
                binding.ivUser.setBackgroundResource(R.drawable.default_image_15dp)
                GlideUtil.loadRoundImgCenterCrop(
                    context,
                    item.url,
                    binding.ivUser,
                    SizeUtils.dp2px(15F)
                )

//            }

            } else {
                val layoutParams = binding.ivUser.layoutParams as RecyclerView.LayoutParams
                layoutParams.width = ScreenUtils.getScreenWidth()
                layoutParams.height = ScreenUtils.getScreenHeight()
                binding.ivUser.layoutParams = layoutParams

                GlideUtil.loadRoundImgCenterCrop(context, item.url, binding.ivUser, 0)

            }
        }
    }

}
