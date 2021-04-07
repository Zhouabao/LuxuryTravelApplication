package com.sdy.luxurytravelapplication.ui.adapter

import android.graphics.Color
import android.text.style.ClickableSpan
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.SpanUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemSendGiftBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.SendGiftBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class SendGiftAdapter(val isGrid: Boolean = false) :
    BaseBindingQuickAdapter<SendGiftBean, ItemSendGiftBinding>(R.layout.item_send_gift) {
    override fun convert(binding: ItemSendGiftBinding, position: Int, item: SendGiftBean) {
        binding.apply {


            if (isGrid) {
                val params = root.layoutParams as RecyclerView.LayoutParams
                if (position == data.size - 1) {
                    params.rightMargin = SizeUtils.dp2px(10F)
                } else {
                    params.rightMargin = SizeUtils.dp2px(0f)
                }
                params.leftMargin = SizeUtils.dp2px(5F)
            } else {
                val params = root.layoutParams as RecyclerView.LayoutParams
                if (data.size == 1) {
                    params.width = SizeUtils.dp2px(141F)
                    params.height = SizeUtils.dp2px(141F)
                } else {
                    params.width = SizeUtils.dp2px(95F)
                    params.height = SizeUtils.dp2px(111F)
                    if (position == data.size - 1) {
                        params.rightMargin = SizeUtils.dp2px(15F)
                    } else {
                        params.rightMargin = SizeUtils.dp2px(0f)
                    }
                    params.leftMargin = SizeUtils.dp2px(15F)

                }


            }


            if (item.checked) {
                root.setBackgroundResource(R.drawable.shape_rectangle_stroke_green_10dp)
            } else {
                if (isGrid)
                    root.setBackgroundColor(Color.WHITE)
                else
                    root.setBackgroundResource(R.drawable.shape_rectangle_stroke_eceff4_10dp)
            }


            GlideUtil.loadRoundImgCenterCrop(context, item.icon, giftIcon, SizeUtils.dp2px(10F))
            giftName.text = item.title
            giftMoney.text = "${item.amount}"
            SpanUtils.with(giftMoney)
                .append("${item.amount}")
                .setBold()
                .append("\t充值")
                .setClickSpan(object :ClickableSpan(){
                    override fun onClick(widget: View) {


                    }

                })
        }

    }

}
