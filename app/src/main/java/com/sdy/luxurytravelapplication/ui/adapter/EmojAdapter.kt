package com.sdy.luxurytravelapplication.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemEmojBinding
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class EmojAdapter : BaseBindingQuickAdapter<String, ItemEmojBinding>(R.layout.item_emoj) {
    override fun convert(binding: ItemEmojBinding, position: Int, item: String) {
        binding.apply {
            val params = root.layoutParams as RecyclerView.LayoutParams
            params.width = ((ScreenUtils.getScreenWidth() - SizeUtils.dp2px(15F)) / 8f).toInt()
            params.height = (SizeUtils.dp2px(250F) / 5F).toInt()
            root.layoutParams = params
            emojTv.text = item
        }
    }
}
