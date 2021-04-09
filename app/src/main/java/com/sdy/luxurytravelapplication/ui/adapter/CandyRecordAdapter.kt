package com.sdy.luxurytravelapplication.ui.adapter

import android.graphics.Color
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemRecordCandyBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.BillBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class CandyRecordAdapter :
    BaseBindingQuickAdapter<BillBean, ItemRecordCandyBinding>(R.layout.item_record_candy) {
    override fun convert(binding: ItemRecordCandyBinding, position: Int, item: BillBean) {
        binding.apply {
            GlideUtil.loadCircleImg(context, item.icon, recordImg)
            recordTime.text = item.create_time

            if (item.affect_candy > 0) {
                recordMoney.setTextColor(Color.parseColor("#FF1ED0A7"))
                recordMoney.text = "+${item.affect_candy}"
            } else {
                recordMoney.text = "${item.affect_candy}"
                recordMoney.setTextColor(Color.parseColor("#FF333333"))
            }
            recordContent.text = "${item.intro}"
            recordType.text = "${item.type_title}"

        }

    }

}
