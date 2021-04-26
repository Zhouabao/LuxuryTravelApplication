package com.sdy.luxurytravelapplication.ui.adapter

import android.view.View
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
import com.sdy.luxurytravelapplication.widgets.FindAudioView
import org.jetbrains.anko.backgroundColorResource

/**
 *    author : ZFM
 *    date   : 2021/3/319:51
 *    desc   :
 *    version: 1.0
 */
class TravelAdapter(val formDetail: Boolean = false ,val type : Int = 0) :
    BaseBindingQuickAdapter<TravelPlanBean, ItemTravelBinding>(R.layout.item_travel) {
    val myAudioView by lazy { mutableListOf<FindAudioView?>() }

    override fun convert(binding: ItemTravelBinding, position: Int, item: TravelPlanBean) {
        binding.apply {
            ClickUtils.applySingleDebouncing(arrayOf(root, signUpBtn, userAvatar)) {
                when (it) {
                    root -> {
                        if (!formDetail)
                            TravelDetailActivity.start(context, item)
                    }
                    signUpBtn -> {
                        CommonFunction.checkApplyForDating(context, item)
                    }
                    userAvatar -> {
                        TargetUserActivity.start(context, item.accid)
                    }
                }
            }

            if(type==1){
                //个人中心的旅行
                bg.setBackgroundResource(R.drawable.icon_my_travel_bg)
                userAvatar.isVisible = false
                travelCost.isVisible = false
                travelRequire.isVisible = false
                travelAimbition.isVisible = false
                signUpBtn.isVisible = false
                detailsTv.isVisible = true
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
            if (item.content_type == 1) {
                travelDescr.text = item.content
                travelAduio.isVisible = false
                travelDescr.isVisible = true
                myAudioView.add(null)
            } else {
                travelAduio.isVisible = true
                travelDescr.isVisible = false
                travelAduio.prepareAudio(item.content, item.duration, item.id, item.content_type, false)
                myAudioView.add(travelAduio)
            }

        }

    }


    fun resetMyAudioViews() {
        for (myaudio in myAudioView) {
            myaudio?.release()
        }
    }

    fun notifySomeOneAudioView(positionId: Int) {
        for (myaudio in myAudioView.withIndex()) {
            if (myaudio?.value?.positionId != positionId) {
                myaudio?.value?.release()
            }
        }
    }
}