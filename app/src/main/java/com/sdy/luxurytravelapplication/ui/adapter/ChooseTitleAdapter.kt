package com.sdy.luxurytravelapplication.ui.adapter

import android.graphics.Color
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemChooseTitleBinding
import com.sdy.luxurytravelapplication.mvp.model.bean.LabelQualityBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class ChooseTitleAdapter :
    BaseBindingQuickAdapter<LabelQualityBean, ItemChooseTitleBinding>(R.layout.item_choose_photo) {
    override fun convert(binding: ItemChooseTitleBinding, position: Int, item: LabelQualityBean) {
        binding.apply {
            if (item.isfuse) {
              titleName.setBackgroundResource(R.drawable.shape_c7f3e9_13dp)
              titleName.setTextColor(context.resources.getColor(R.color.colorAccent))
            } else {
              titleName.setBackgroundResource(R.drawable.shape_background_13dp)
              titleName.setTextColor(Color.parseColor("#FF787C7F"))
            }
          titleName.text = item.content
        }
    }

}
