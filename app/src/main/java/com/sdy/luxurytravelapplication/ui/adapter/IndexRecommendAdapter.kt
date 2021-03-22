package com.sdy.luxurytravelapplication.ui.adapter

import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemIndexRecommendBinding
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

/**
 *    author : ZFM
 *    date   : 2021/3/2216:08
 *    desc   :
 *    version: 1.0
 */
class IndexRecommendAdapter :
    BaseBindingQuickAdapter<String, ItemIndexRecommendBinding>(R.layout.item_index_recommend) {
    override fun convert(binding: ItemIndexRecommendBinding, position: Int, item: String) {

        binding.apply {
            if (position % 2 == 0) {
                binding.root.setBackgroundResource(R.drawable.icon_index_recommend_normal_bg)
            } else {
                binding.root.setBackgroundResource(R.drawable.icon_index_recommend_luxury_bg)
            }
        }

    }
}