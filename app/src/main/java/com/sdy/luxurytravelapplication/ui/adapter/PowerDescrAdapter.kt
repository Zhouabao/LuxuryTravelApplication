package com.sdy.luxurytravelapplication.ui.adapter

import android.graphics.Color
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemPowerDescrBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.VipDescr
import com.sdy.luxurytravelapplication.mvp.model.bean.VipPowerBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class PowerDescrAdapter(val type: Int = 0) :
    BaseBindingQuickAdapter<VipDescr, ItemPowerDescrBinding>(R.layout.item_power_descr) {
    override fun convert(binding: ItemPowerDescrBinding, position: Int, item: VipDescr) {
        binding.apply {
            GlideUtil.loadCircleImg(context, item.icon_vip, powerImg)
            powerTitle.text = item.title
            powerContent.text = item.rule
            if (type == VipPowerBean.TYPE_PT_VIP) {
                powerTitle.setTextColor(Color.parseColor("#FFF95925"))
                powerContent.setTextColor(Color.parseColor("#FF666973"))
            } else {
                powerTitle.setTextColor(Color.parseColor("#FFF2B710"))
                powerContent.setTextColor(Color.parseColor("#FF666973"))
            }
        }


    }

}
