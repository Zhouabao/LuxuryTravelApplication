package com.sdy.luxurytravelapplication.ui.adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemPeopleRecommendTopContentBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.IndexTopBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

/**
 *    author : ZFM
 *    date   : 2021/3/2215:12
 *    desc   :
 *    version: 1.0
 */
class PeopleRecommendTopAdapter :
    BaseBindingQuickAdapter<IndexTopBean, ItemPeopleRecommendTopContentBinding>(R.layout.item_people_recommend_top_content) {
    override fun convert(
        binding: ItemPeopleRecommendTopContentBinding,
        position: Int,
        item: IndexTopBean
    ) {
        binding.apply {
            val itemView = binding.root
            val params = itemView.layoutParams as RecyclerView.LayoutParams
            if (position == data.size - 1) {
                params.rightMargin = SizeUtils.dp2px(15F)
            }

            GlideUtil.loadRoundImgCenterCrop(
                context,
                item.avatar,
                userAvator,
                SizeUtils.dp2px(5f)
            )

            userMvBtn.isVisible = item.source_type == 1
        }
    }
}