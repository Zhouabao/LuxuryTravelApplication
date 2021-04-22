package com.sdy.luxurytravelapplication.ui.adapter

import androidx.core.view.isVisible
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemTargetBigPhotoBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.Myinfo
import com.sdy.luxurytravelapplication.mvp.model.bean.UserPhotoBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

/**
 * 对方个人页大图模式
 */
class TargetBigPhotoAdapter :
    BaseBindingQuickAdapter<UserPhotoBean, ItemTargetBigPhotoBinding>(R.layout.item_target_big_photo) {
    lateinit var myinfo: Myinfo
    lateinit var target_accid: String
    override fun convert(
        binding: ItemTargetBigPhotoBinding,
        position: Int,
        item: UserPhotoBean
    ) {
        val autoPlay =
            myinfo.personal_auto_play && (myinfo.residue_auto_count > 0 || myinfo.isgoldvip)
        binding.apply {
            userPhoto.isVisible = !item.isVideo || !autoPlay
            userVideo.isVisible = item.isVideo && item.checked && autoPlay
            userVideoBtn.isVisible = item.isVideo && !autoPlay
            if (userVideoBtn.isVisible) {
                GlideUtil.loadImgBlurCenterCrop(
                    context,
                    item.avatar,
                    userPhoto,
                    SizeUtils.dp2px(20F),
                    RoundedCornersTransformation.CornerType.BOTTOM
                )
            }
            if (item.isVideo) {
                userVideo.apply {
                    setUp(item.mv_detail_url, false, "")
                    setThumbImageView(item.avatar)
                    if (item.checked && autoPlay) {
                        startPlayLogic()
                    }

                }
            } else {
                GlideUtil.loadRoundImgCenterCrop(
                    context,
                    item.avatar,
                    userPhoto,
                    SizeUtils.dp2px(20F),
                    RoundedCornersTransformation.CornerType.BOTTOM
                )
            }
        }
    }


}
