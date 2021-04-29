package com.sdy.luxurytravelapplication.ui.adapter

import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemFindRecommendHeadBinding
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareBannerBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class SquareHeadTopicAdapter :
    BaseBindingQuickAdapter<SquareBannerBean, ItemFindRecommendHeadBinding>(R.layout.item_find_recommend_head) {
    override fun convert(
        binding: ItemFindRecommendHeadBinding,
        position: Int,
        item: SquareBannerBean
    ) {
        binding.apply {

            topicTitle.text = item.title
            topicContent.text="${item.all_count}人参与·${item.used_cnt}次浏览"
        }
    }
}