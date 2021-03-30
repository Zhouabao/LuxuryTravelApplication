package com.sdy.luxurytravelapplication.ui.adapter

import android.graphics.Color
import android.view.View
import com.amap.api.services.core.PoiItem
import com.blankj.utilcode.util.SpanUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemLocationBinding
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class LocationAdapter(var checkPosition: Int = 0) :
    BaseBindingQuickAdapter<PoiItem, ItemLocationBinding>(R.layout.item_location) {
    override fun convert(binding: ItemLocationBinding, position: Int, item: PoiItem) {

        binding.apply {
            if (position == checkPosition) {
                locationName.setTextColor(context.resources.getColor(R.color.colorAccent))
                locationChooseImg.visibility = View.VISIBLE
            } else {
                locationName.setTextColor(context.resources.getColor(R.color.color333))
                locationChooseImg.visibility = View.GONE
            }


            SpanUtils.with(locationName)
                .append("${(item.title ?: "")}")
                .append(
                    "${if (item.snippet.isNotEmpty()) {
                        "\n${item.snippet}"
                    } else {
                        ""
                    }}"
                )
                .setForegroundColor(Color.parseColor("#FFC8C8C8"))
                .setFontSize(12, true)
                .create()
        }
    }


}
