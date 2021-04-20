package com.sdy.luxurytravelapplication.ui.adapter

import androidx.core.view.isVisible
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemTargetSmallPhotoBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.MatchBean
import com.sdy.luxurytravelapplication.mvp.model.bean.UserPhotoBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

/**
 * 对方个人页小图模式
 */
class TargetSmallPhotoAdapter :
    BaseBindingQuickAdapter<UserPhotoBean, ItemTargetSmallPhotoBinding>(R.layout.item_target_small_photo) {
     lateinit var matchBean: MatchBean
    override fun convert(
        binding: ItemTargetSmallPhotoBinding,
        position: Int,
        item: UserPhotoBean
    ) {
        binding.apply {
            GlideUtil.loadRoundImgCenterCrop(
                context,
                item.avatar,
                smallImg,
                SizeUtils.dp2px(10F)
            )
            checkedView.isVisible = item.checked
            videoLogo.isVisible = item.isVideo

            if (item.isVideo && item.checked) {
                ClickUtils.applySingleDebouncing(root){
                    CommonFunction.checkUnlockIntroduceVideo(context, matchBean.accid)
                }

            }
        }
    }


}
