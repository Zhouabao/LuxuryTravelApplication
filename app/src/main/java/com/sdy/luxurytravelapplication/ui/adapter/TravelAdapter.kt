package com.sdy.luxurytravelapplication.ui.adapter

import androidx.core.view.isVisible
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemTravelBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.TravelPlanBean
import com.sdy.luxurytravelapplication.ui.activity.TargetUserActivity
import com.sdy.luxurytravelapplication.ui.activity.TravelDetailActivity
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

/**
 *    author : ZFM
 *    date   : 2021/3/319:51
 *    desc   :
 *    version: 1.0
 */
class TravelAdapter(val formDetail: Boolean = false) :
    BaseBindingQuickAdapter<TravelPlanBean, ItemTravelBinding>(R.layout.item_travel) {
    override fun convert(binding: ItemTravelBinding, position: Int, item: TravelPlanBean) {
        binding.apply {
            ClickUtils.applySingleDebouncing(arrayOf(root, chatBtn,userAvatar)) {
                when (it) {
                    root -> {
                        if (!formDetail)
                            TravelDetailActivity.start(context, item)
                    }
                    chatBtn -> {
                        CommonFunction.checkApplyForDating(context, item)
                    }
                    userAvatar->{
                        TargetUserActivity.start(context, item.accid)
                    }
                }
            }

            GlideUtil.loadRoundImgCenterCrop(context, item.avatar, userAvatar, SizeUtils.dp2px(15F))
            travelTitle.text = item.dating_title
            travelCost.text = "费用：${item.cost_type}/${item.cost_money}"
            travelRequire.text = "要求：${item.dating_target}"
            travelAimbition.text = "目的：${item.purpose}"
            travelProvince.text = item.rise_province
            travelAddress.text = item.rise_city
            travelDestProvince.text = item.goal_province
            travelDestAddress.text = item.goal_city
            if (item.content.isNotEmpty()) {
                travelDescr.text = item.content
                travelAduio.isVisible = false
                travelDescr.isVisible = true
            } else {
                travelAduio.isVisible = true
                travelDescr.isVisible = false
            }


        }

    }
}