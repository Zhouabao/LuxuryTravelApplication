package com.sdy.luxurytravelapplication.ui.adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemUserCenterVisitCoverBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter
import jp.wasabeef.glide.transformations.BlurTransformation

class VisitUserAvatorAdater :
    BaseBindingQuickAdapter<String, ItemUserCenterVisitCoverBinding>(R.layout.item_user_center_visit_cover) {
    public var freeShow = false
    public var todayVisitCount = 0
    override fun convert(binding: ItemUserCenterVisitCoverBinding, position: Int, item: String) {
        binding.apply {
            val params = root.layoutParams as RecyclerView.LayoutParams
            params.setMargins(if (position != 0) SizeUtils.dp2px(-15F) else 0, 0, 0, 0)
            params.width = SizeUtils.dp2px(30F)
            params.height = SizeUtils.dp2px(30F)
            root.layoutParams = params
            //如果不是会员，就高斯模糊看过我的
            if (freeShow) {
                GlideUtil.loadImg(context, item, visitCoverImg)
            } else {
                Glide.with(context)
                    .load(item)
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(1, 10)))
                    .into(visitCoverImg)
            }

            if (todayVisitCount > 5 && position == 4) {
                visitCnt.isVisible = true
                visitCnt.text = "+$todayVisitCount"
                visitCoverImg.isVisible = false
            }
        }

    }

}
