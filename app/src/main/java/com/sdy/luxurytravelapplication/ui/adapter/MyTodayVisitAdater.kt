package com.sdy.luxurytravelapplication.ui.adapter

import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemTodayVisitBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.VisitorBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter
import jp.wasabeef.glide.transformations.BlurTransformation

class MyTodayVisitAdater(val freeShow: Boolean) :
    BaseBindingQuickAdapter<VisitorBean, ItemTodayVisitBinding>(R.layout.item_today_visit) {
    override fun convert(binding: ItemTodayVisitBinding, position: Int, item: VisitorBean) {
        binding.apply {
            if (freeShow) {
                visitHideName.visibility = View.GONE
                visitHideInfo.visibility = View.GONE
                GlideUtil.loadAvatorImg(context, item.avatar ?: "", visitImg)
            } else {
                visitHideName.visibility = View.VISIBLE
                visitHideInfo.visibility = View.VISIBLE
                Glide.with(context)
                    .load(item.avatar ?: "")
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(1, 10)))
                    .into(visitImg)
            }

            visitName.text = "${item.nickname}"
            visitInfo.text = "${item.age}\t/\t${if (item.gender == 1) {
                context.getString(R.string.gender_man)
            } else {
                context.getString(R.string.gender_woman)
            }}\t/\t${item.constellation}\t/\t${item.distance}"
            visitCount.text = "${item.visitcount}"
            visitVip.isVisible = (item.isvip ?: 0) == 1

        }

    }

}
