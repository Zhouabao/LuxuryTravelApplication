package com.sdy.luxurytravelapplication.nim.business.session.panel

import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemActionsLayoutBinding
import com.sdy.luxurytravelapplication.mvp.model.bean.PublishWayBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

/**
 *    author : ZFM
 *    date   : 2020/10/2019:18
 *    desc   :
 *    version: 1.0
 */
class ChatActionAdapter : BaseBindingQuickAdapter<PublishWayBean, ItemActionsLayoutBinding>(R.layout.item_actions_layout) {

    override fun convert(binding: ItemActionsLayoutBinding, position: Int, item: PublishWayBean) {
        if (item.checked) {
            binding.imageView.setImageResource(item.checkedIcon)
        } else {
            binding.imageView.setImageResource(item.normalIcon)
        }
        }
}