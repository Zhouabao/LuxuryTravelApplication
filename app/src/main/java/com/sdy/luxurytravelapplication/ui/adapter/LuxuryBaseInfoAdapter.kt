package com.sdy.luxurytravelapplication.ui.adapter

import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemIndexBaseInfoBinding
import com.sdy.luxurytravelapplication.databinding.ItemTargetBaseInfoBinding
import com.sdy.luxurytravelapplication.mvp.model.bean.DetailUserInfoBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class LuxuryBaseInfoAdapter :
    BaseBindingQuickAdapter<String, ItemIndexBaseInfoBinding>(R.layout.item_index_base_info) {
    override fun convert(
        binding: ItemIndexBaseInfoBinding,
        position: Int,
        item: String
    ) {

        binding.apply {
            root.text = item
        }
    }


}
