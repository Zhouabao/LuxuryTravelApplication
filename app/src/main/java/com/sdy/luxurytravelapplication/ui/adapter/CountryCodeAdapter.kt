package com.sdy.luxurytravelapplication.ui.adapter

import android.view.View
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemCountryCodeBinding
import com.sdy.luxurytravelapplication.mvp.model.bean.CountryCodeBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class CountryCodeAdapter :
    BaseBindingQuickAdapter<CountryCodeBean, ItemCountryCodeBinding>(R.layout.item_country_code) {
    override fun convert(binding: ItemCountryCodeBinding, position: Int, item: CountryCodeBean) {
        binding.apply {
            //因为添加了头部 所以位置要移动
            if ((position == 0 || data[position - 1].index != item.index)) {
                tvIndex.visibility = View.VISIBLE
                friendDivider0.visibility = View.VISIBLE
                tvIndex.text = item.index
                friendDivider.visibility = View.GONE
            } else {
                tvIndex.visibility = View.GONE
                friendDivider0.visibility = View.GONE
                friendDivider.visibility = View.VISIBLE
            }


            friendName.text = "${item.sc}\t+${item.code}"
        }

    }

}
