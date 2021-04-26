package com.sdy.luxurytravelapplication.ui.adapter

import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemTargetBaseInfoBinding
import com.sdy.luxurytravelapplication.mvp.model.bean.DetailUserInfoBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class TargetBaseInfoAdapter :
    BaseBindingQuickAdapter<String, ItemTargetBaseInfoBinding>(R.layout.item_target_base_info) {
    override fun convert(
        binding: ItemTargetBaseInfoBinding,
        position: Int,
        item: String
    ) {

        binding.apply {
            root.text = item
        }
    }


}
