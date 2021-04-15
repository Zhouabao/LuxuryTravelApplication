package com.sdy.luxurytravelapplication.ui.adapter

import android.view.View
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemContactBookBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.ContactBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class ContactAdapter :
    BaseBindingQuickAdapter<ContactBean, ItemContactBookBinding>(R.layout.item_contact_book) {
    override fun convert(binding: ItemContactBookBinding, position: Int, item: ContactBean) {
        binding.apply {
            //因为添加了头部 所以位置要移动
            if ((position == 1 || data[position - 1 - 1].index != item.index)) {
                tvIndex.visibility = View.VISIBLE
                tvIndex.text = item.index
                friendDivider.visibility = View.GONE
            } else {
                tvIndex.visibility = View.GONE
                friendDivider.visibility = View.VISIBLE

            }

            GlideUtil.loadImg(context, item.avatar, friendIcon)
            friendName.text = "${item.nickname}"
        }

    }

}
