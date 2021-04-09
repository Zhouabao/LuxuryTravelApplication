package com.sdy.luxurytravelapplication.ui.adapter

import android.graphics.Color
import android.graphics.Typeface
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.SpanUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemChargeVipBinding
import com.sdy.luxurytravelapplication.mvp.model.bean.ChargeWayBean
import com.sdy.luxurytravelapplication.mvp.model.bean.VipPowerBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class VipChargeAdapter(val type: Int = 0) :
    BaseBindingQuickAdapter<ChargeWayBean, ItemChargeVipBinding>(R.layout.item_charge_vip) {
    override fun convert(binding: ItemChargeVipBinding, position: Int, item: ChargeWayBean) {
        binding.apply {
            val params = root.layoutParams as RecyclerView.LayoutParams
            params.leftMargin = if (position == 0) {
                SizeUtils.dp2px(15F)
            } else {
                    SizeUtils.dp2px(10F)
            }
            params.rightMargin = if (position == data.size - 1) {
                SizeUtils.dp2px(15F)
            } else {
                0
            }
            root.layoutParams = params

            vipLong.text = item.ename ?: ""
            vipNowPrice.text = "¥${if (item.type == 1) {
                item.original_price
            } else {
                item.discount_price
            }}"
            SpanUtils.with(monthPrice)
                .append("${item.unit_price}")
                .setFontSize(30, true)
                .setTypeface(Typeface.createFromAsset(context.assets, "DIN_Alternate_Bold.ttf"))
                .setBold()
                .append("/月")
                .setFontSize(14, true)
                .create()


            if (type == VipPowerBean.TYPE_PT_VIP) {
                if (item.giving_gold_day.isNotEmpty()) {
                    vipSendCandy.text = item.giving_gold_day
                    vipSendCandy.isVisible = true
                } else {
                    vipSendCandy.isVisible = false
                }
                if (item.is_promote) {
                    vipLong.setTextColor(Color.parseColor("#FFDA4819"))
                    monthPrice.setTextColor(Color.parseColor("#FFDA4819"))
                    vipNowPrice.setTextColor(Color.parseColor("#FFDA4819"))
                    purchaseCl.setBackgroundResource(R.drawable.shape_rectangle_ptvip_selected_15dp)
                    normalCl.setBackgroundResource(R.drawable.shape_rectangle_ptvip_inner_selected_15dp)
                } else {
                    vipLong.setTextColor(Color.parseColor("#FF666973"))
                    monthPrice.setTextColor(Color.parseColor("#FF666973"))
                    vipNowPrice.setTextColor(Color.parseColor("#FF666973"))
                    purchaseCl.setBackgroundResource(R.drawable.shape_rectangle_ptvip_normal_15dp)
                    normalCl.setBackgroundResource(R.drawable.shape_rectangle_ptvip_inner_normal_15dp)
                }
            } else {
                if (item.giving_amount > 0) {
                    vipSendCandy.text = "赠送${item.giving_amount}旅券"
                    vipSendCandy.isVisible = true
                } else {
                    vipSendCandy.isVisible = false
                }
                if (item.is_promote) {
                    vipLong.setTextColor(Color.parseColor("#FFBC8000"))
                    monthPrice.setTextColor(Color.parseColor("#FFBC8000"))
                    vipNowPrice.setTextColor(Color.parseColor("#FFBC8000"))
                    purchaseCl.setBackgroundResource(R.drawable.shape_rectangle_vip_selected_15dp)
                    normalCl.setBackgroundResource(R.drawable.shape_rectangle_vip_inner_selected_15dp)
                } else {
                    vipLong.setTextColor(Color.parseColor("#FF666973"))
                    monthPrice.setTextColor(Color.parseColor("#FF666973"))
                    vipNowPrice.setTextColor(Color.parseColor("#FF666973"))
                    purchaseCl.setBackgroundResource(R.drawable.shape_rectangle_ptvip_normal_15dp)
                    normalCl.setBackgroundResource(R.drawable.shape_rectangle_ptvip_inner_normal_15dp)
                }
            }


        }
    }

}
