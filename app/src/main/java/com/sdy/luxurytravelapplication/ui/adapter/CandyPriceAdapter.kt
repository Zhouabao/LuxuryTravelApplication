package com.sdy.luxurytravelapplication.ui.adapter

import android.graphics.Typeface
import androidx.core.view.isInvisible
import com.blankj.utilcode.util.SpanUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemCandyPriceBinding
import com.sdy.luxurytravelapplication.mvp.model.bean.ChargeWayBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter
import java.math.BigDecimal

class CandyPriceAdapter :
    BaseBindingQuickAdapter<ChargeWayBean, ItemCandyPriceBinding>(R.layout.item_candy_price) {
    override fun convert(binding: ItemCandyPriceBinding, position: Int, item: ChargeWayBean) {
        binding.apply {
            candyCount.typeface =
                Typeface.createFromAsset(context.assets, "DIN_Alternate_Bold.ttf")
            candyCount.text = "${item.amount}"
            //isfirst
            //不是首冲显示原价
            if (item.isfirst) {
                SpanUtils.with(candyFirstPrice)
                    .append(
                        "¥${item.discount_price}"
                    )
                    .setTypeface(Typeface.createFromAsset(context.assets, "DIN_Alternate_Bold.ttf"))
                    .create()

                SpanUtils.with(candyPrice)
                    .append("${context.getString(R.string.original_price)}${item.original_price}，")
                    .setStrikethrough()
                    .append(context.getString(R.string.first_charge_save))
                    .append("${BigDecimal(item.original_price).minus(BigDecimal(item.discount_price))}")
                    .setForegroundColor(context.resources.getColor(R.color.colorAccent))
                    .create()

            } else {
                SpanUtils.with(candyFirstPrice)
                    .append(
                        "¥${
                        if (BigDecimal(item.discount_price) > BigDecimal.ZERO) {
                            item.discount_price
                        } else {
                            item.original_price
                        }
                        }"
                    )
                    .setTypeface(Typeface.createFromAsset(context.assets, "DIN_Alternate_Bold.ttf"))
                    .create()

                candyPrice.text = context.getString(R.string.cost) +
                        if (BigDecimal(item.discount_price) > BigDecimal.ZERO) {
                            item.discount_price
                        } else {
                            item.original_price
                        } + context.getString(R.string.money_unit)

            }

            candyDiscount.isInvisible = !item.isfirst
        }

    }

}
