package com.sdy.luxurytravelapplication.ui.adapter

import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemTargetBaseInfoBinding
import com.sdy.luxurytravelapplication.mvp.model.bean.DetailUserInfoBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class TargetBaseInfoAdapter :
    BaseBindingQuickAdapter<DetailUserInfoBean, ItemTargetBaseInfoBinding>(R.layout.item_target_base_info) {
    override fun convert(
        binding: ItemTargetBaseInfoBinding,
        position: Int,
        item: DetailUserInfoBean
    ) {

        binding.apply {
            root.text = if (item.title == "身高") {
                item.title + item.content + "cm"
            } else if (item.title == "体重") {
                item.title + item.content + "kg"
            } else {
                item.content
            }
        }
    }


}
