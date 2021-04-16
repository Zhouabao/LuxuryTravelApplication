package com.sdy.luxurytravelapplication.ui.adapter

import androidx.core.view.isVisible
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemTargetBigPhotoBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.UserPhotoBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

/**
 * 对方个人页大图模式
 */
class TargetBigPhotoAdapter :
    BaseBindingQuickAdapter<UserPhotoBean, ItemTargetBigPhotoBinding>(R.layout.item_target_big_photo) {
    override fun convert(
        binding: ItemTargetBigPhotoBinding,
        position: Int,
        item: UserPhotoBean
    ) {
        binding.apply {
//            val params = root.layoutParams as RecyclerView.LayoutParams
//            params.width = ScreenUtils.getScreenWidth()
//            params.height = (5 / 4F * params.width).toInt()
            userPhoto.isVisible = !item.isVideo
            userVideo.isVisible = item.isVideo && item.checked
            if (item.isVideo && item.checked) {
                userVideo.apply {
                    setUp(item.mv_detail_url, false, "")
                    backButton.isVisible = false
                    titleTextView.isVisible = false
                    fullscreenButton.isVisible = false
                    setIsTouchWiget(false)
                    startPlayLogic()
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
