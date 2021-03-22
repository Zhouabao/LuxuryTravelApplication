package com.sdy.luxurytravelapplication.ui.adapter

import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemIndexLuxuryBinding
import com.sdy.luxurytravelapplication.databinding.ItemIndexRecommendBinding
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

/**
 *    author : ZFM
 *    date   : 2021/3/2216:08
 *    desc   :
 *    version: 1.0
 */
class IndexLuxuryAdapter :
    BaseBindingQuickAdapter<String, ItemIndexLuxuryBinding>(R.layout.item_index_luxury) {
    override fun convert(binding: ItemIndexLuxuryBinding, position: Int, item: String) {
    }
}