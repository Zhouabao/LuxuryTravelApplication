package com.sdy.luxurytravelapplication.ui.adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemVerifyUploadInfoBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.UploadInfoBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class VerifyUploadInfoAdapter :
    BaseBindingQuickAdapter<UploadInfoBean, ItemVerifyUploadInfoBinding>(R.layout.item_verify_upload_info) {
    override fun convert(
        binding: ItemVerifyUploadInfoBinding,
        position: Int,
        item: UploadInfoBean
    ) {
        binding.apply {
            val params = root.layoutParams as RecyclerView.LayoutParams
            params.width = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(10 * 2 + 15 * 2F)) / 3
            params.height = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(10 * 2 + 15 * 2F)) / 3

            if (item.chooseIcon.isNullOrEmpty()) {
                infoIV.setImageResource(item.defaultIcon)
            } else {
                GlideUtil.loadRoundImgCenterCrop(
                    context,
                    item.chooseIcon,
                    infoIV,
                    SizeUtils.dp2px(10F)
                )
            }
            infoDelete.isVisible = item.chooseIcon.isNotEmpty()


        }

    }

}
