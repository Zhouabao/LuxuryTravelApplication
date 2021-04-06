package com.sdy.luxurytravelapplication.ui.adapter

import android.view.View
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemMessageListBinding
import com.sdy.luxurytravelapplication.databinding.ItemMessageListHeadBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.MessageListBean
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class MessageListHeadAdapter :
    BaseBindingQuickAdapter<MessageListBean, ItemMessageListBinding>(R.layout.item_message_list) {
    override fun convert(
        binding: ItemMessageListBinding,
        position: Int,
        item: MessageListBean
    ) {
        binding.apply {
//            if (position == 0) {
//                root.isCanLeftSwipe = false
//                root.isCanRightSwipe = false
//            }
            root.isCanLeftSwipe = false
            root.isCanRightSwipe = false
            GlideUtil.loadRoundImgCenterCrop(context,item.icon,msgIcon,SizeUtils.dp2px(15F))
            msgTitle.text = item.title
            text.text = item.msg
            latelyTime.text = item.time
            if (item.count == 0) {
                newCount.visibility = View.GONE
            } else {
                newCount.visibility = View.VISIBLE
                newCount.text = "${item.count}"
            }
        }

    }

}
