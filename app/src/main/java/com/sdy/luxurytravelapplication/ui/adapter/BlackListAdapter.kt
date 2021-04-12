package com.sdy.luxurytravelapplication.ui.adapter

import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemBlackListBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.BlackBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class BlackListAdapter :
    BaseBindingQuickAdapter<BlackBean, ItemBlackListBinding>(R.layout.item_black_list) {
    override fun convert(binding: ItemBlackListBinding, position: Int, item: BlackBean) {
        binding.apply {
            GlideUtil.loadAvatorImg(context, item.avatar ?: "", friendIcon)
            friendName.text = "${item.nickname}"
        }

    }

}
