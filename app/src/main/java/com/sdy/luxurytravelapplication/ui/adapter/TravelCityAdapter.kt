package com.sdy.luxurytravelapplication.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemTravelCityBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.TravelCityBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

/**
 *    author : ZFM
 *    date   : 2021/3/319:51
 *    desc   :
 *    version: 1.0
 */
class TravelCityAdapter(var grid: Boolean = false) :
    BaseBindingQuickAdapter<TravelCityBean, ItemTravelCityBinding>(R.layout.item_travel_city) {
    override fun convert(binding: ItemTravelCityBinding, position: Int, item: TravelCityBean) {
        binding.apply {
            val params = root.layoutParams as RecyclerView.LayoutParams
            if (grid) {
                params.bottomMargin = SizeUtils.dp2px(15F)
                params.leftMargin = if (position % 5 == 0) {
                    SizeUtils.dp2px(15F)
                } else {
                    SizeUtils.dp2px(15F)
                }
            } else {
                params.width = SizeUtils.dp2px(61F)
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

            }

            cityCnt.text = "${item.city_cnt}"
            cityName.text = item.city_name
            GlideUtil.loadRoundImgCenterCrop(
                context,
                item.city_image,
                cityImage,
                SizeUtils.dp2px(12F)
            )

            if (item.checked) {
                itemBg.setBackgroundResource(R.drawable.shape_rectangle_green85_12dp)
            } else {
                itemBg.setBackgroundResource(R.drawable.shape_rectangle_black30_12dp)
            }

        }

    }
}