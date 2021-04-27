package com.sdy.luxurytravelapplication.ui.adapter

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemTravelCitySmallBinding
import com.sdy.luxurytravelapplication.mvp.model.bean.TravelCityBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

/**
 *    author : ZFM
 *    date   : 2021/3/319:51
 *    desc   :
 *    version: 1.0
 */
class TravelCityAdapterSmall() :
    BaseBindingQuickAdapter<TravelCityBean, ItemTravelCitySmallBinding>(R.layout.item_travel_city_small) {
    override fun convert(binding: ItemTravelCitySmallBinding, position: Int, item: TravelCityBean) {
        binding.apply {
            val params = root.layoutParams as RecyclerView.LayoutParams
            params.leftMargin = if (position == 0) {
                SizeUtils.dp2px(15F)
            } else {
                SizeUtils.dp2px(10F)
            }
            params.rightMargin = if (data.size >= 5 && position == data.size - 1) {
                SizeUtils.dp2px(71F)
            } else {
                0
            }
            cityCnt.text = "${item.city_name} ${item.cnt}"
            if (item.checked) {
                root.setBackgroundResource(R.drawable.stroke_green_13dp)
                cityCnt.setTextColor(Color.parseColor("#FF1ED0A7"))
            } else {
                root.setBackgroundResource(R.drawable.stroke_b6bcc6_13dp)
                cityCnt.setTextColor(Color.parseColor("#ffb6bcc6"))
            }

        }

    }
}