package com.sdy.luxurytravelapplication.ui.adapter

import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemSquareTitleBinding
import com.sdy.luxurytravelapplication.mvp.model.bean.TopicBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class SquareTitleAdapter : BaseBindingQuickAdapter<TopicBean, ItemSquareTitleBinding>(
    R.layout.item_square_title){
    override fun convert(binding: ItemSquareTitleBinding, position: Int, item: TopicBean) {
        binding.squareTitle.text = item.title

    }

}