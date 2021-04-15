package com.sdy.luxurytravelapplication.ui.adapter

import android.view.View
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemContactBookBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.ContactBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class ContactStarAdapter(var star: Boolean = true) :
    BaseBindingQuickAdapter<ContactBean, ItemContactBookBinding>(R.layout.item_contact_book) {
    override fun convert(binding: ItemContactBookBinding, position: Int, item: ContactBean) {
        binding.apply {
            if (position == 0 && star) {
                tvIndex.visibility = View.VISIBLE
                tvIndex.text = context.getString(R.string.star_friend)
            } else {
                tvIndex.visibility = View.GONE
            }

            GlideUtil.loadImg(context, item.avatar, friendIcon)
            friendName.text = item.nickname
        }
    }

}
