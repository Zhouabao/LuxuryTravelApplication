package com.sdy.luxurytravelapplication.ui.adapter

import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemFatePersonBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.IndexBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class FateAdapter : BaseBindingQuickAdapter<IndexBean, ItemFatePersonBinding>() {
    override fun convert(binding: ItemFatePersonBinding, position: Int, item: IndexBean) {
        binding.apply {
            GlideUtil.loadCircleImg(context, item.avatar, fateIcon)
            if (item.checked) {
              fateStatus.setImageResource(R.drawable.icon_pay_checked)
            } else {
              fateStatus.setImageResource(R.drawable.icon_pay_normal)
            }
        }

    }


}
