package com.sdy.luxurytravelapplication.ui.adapter

import android.graphics.Color
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ClickUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ItemVipPowerBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.ChargeWayBean
import com.sdy.luxurytravelapplication.mvp.model.bean.VipPurchaseBean
import com.sdy.luxurytravelapplication.ui.dialog.ConfirmPayCandyDialog
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class VipPowerAdapter :
    BaseBindingQuickAdapter<VipPurchaseBean, ItemVipPowerBinding>(R.layout.item_vip_power) {
    companion object {
        const val TYPE_VIP = 0
        const val TYPE_PT_VIP = 1
    }

    override fun convert(binding: ItemVipPowerBinding, position: Int, item: VipPurchaseBean) {
        binding.apply {
            GlideUtil.loadAvatorImg(context, UserManager.avatar, vipAvatar)
            if (item.isvip) {
                vipState.text = item.vip_express
            } else {
                vipState.text = "暂未激活"
            }
            vipDescr.text = item.vip_save_str
            powerCnt1.text = "拥有${item.icon_list.size}项特权"
            priceRv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            val priceAdapter = VipChargeAdapter(position)
            priceRv.adapter = priceAdapter
            var promotePos = -1
            for (tdata in item.list.withIndex()) {
                if (tdata.value.is_promote) {
                    promotePos = tdata.index
                }
            }
            if (promotePos == -1) {
                promotePos = 0
                item.list[0].is_promote = true
            }
            priceAdapter.setNewInstance(item.list)
            priceAdapter.setOnItemClickListener { _, view, position ->
                for (tdata in priceAdapter.data) {
                    tdata.is_promote = tdata == priceAdapter.data[position]
                }
                buyBtn.text = "¥${if (priceAdapter.data[position].type == 1) {
                    priceAdapter.data[position].original_price
                } else {
                    priceAdapter.data[position].discount_price
                }} ${if (item.isvip) {
                    context.getString(R.string.vip_renew)
                } else {
                    context.getString(R.string.vip_buy)
                }}${if (position == TYPE_VIP) {
                    context.getString(R.string.vip_gold)
                } else {
                    context.getString(R.string.vip_connection_card)
                }}"
                priceAdapter.notifyDataSetChanged()

            }
            val vipPowerAdapter = PowerDescrAdapter(position)
            val manager = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
            powerRv.layoutManager = manager
            powerRv.adapter = vipPowerAdapter
            vipPowerAdapter.setNewInstance(item.icon_list)



            if (position == TYPE_VIP) {
                powerCnt.text = "尊享${item.icon_list.size}项特权"
                buyBtn.text = "¥${if (item.list[promotePos].type == 1) {
                    item.list[promotePos].original_price
                } else {
                    item.list[promotePos].discount_price
                }}\t获取黄金会员"
                vipBg.setImageResource(R.drawable.icon_vip_bg)
                powerLeftBg.setImageResource(R.drawable.icon_vip_left_bg)
                powerRightBg.setImageResource(R.drawable.icon_vip_right_bg)
                buyBtn.setBackgroundResource(R.drawable.gradient_buy_vip_bg)
                powerCnt1.setTextColor(Color.parseColor("#FFF2B710"))
                vipDescr.setTextColor(Color.parseColor("#ffa6651b"))
                powerCnt.setTextColor(Color.parseColor("#ffa6651b"))
                vipState.setTextColor(Color.parseColor("#ffa6651b"))

            } else {
                powerCnt.text = "尊享定制特权"
                buyBtn.text = "¥${if (item.list[promotePos].type == 1) {
                    item.list[promotePos].original_price
                } else {
                    item.list[promotePos].discount_price
                }}\t获取至尊直联卡"
                vipBg.setImageResource(R.drawable.icon_ptvip_bg)
                powerLeftBg.setImageResource(R.drawable.icon_ptvip_left_bg)
                powerRightBg.setImageResource(R.drawable.icon_ptvip_right_bg)
                buyBtn.setBackgroundResource(R.drawable.gradient_buy_ptvip_bg)
                powerCnt1.setTextColor(Color.parseColor("#FFF95925"))
                vipDescr.setTextColor(Color.parseColor("#FFDA4819"))
                powerCnt.setTextColor(Color.parseColor("#FFDA4819"))
                vipState.setTextColor(Color.parseColor("#FFDA4819"))
            }

            ClickUtils.applySingleDebouncing(buyBtn) {
                var position: ChargeWayBean? = null
                for (data in priceAdapter.data) {
                    if (data.is_promote) {
                        position = data
                        break
                    }
                }
                if (position != null)
                    ConfirmPayCandyDialog(context, position, item.paylist).show()
            }

        }
    }

}
