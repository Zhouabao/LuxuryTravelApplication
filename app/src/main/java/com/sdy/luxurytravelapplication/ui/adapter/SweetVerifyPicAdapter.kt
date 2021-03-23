package com.sdy.luxurytravelapplication.ui.adapter

import androidx.core.view.isVisible
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemSweetVerifyPicBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.SweetUploadBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

/**
 *    author : ZFM
 *    date   : 2021/3/2316:44
 *    desc   :
 *    version: 1.0
 */
class SweetVerifyPicAdapter :
    BaseBindingQuickAdapter<SweetUploadBean, ItemSweetVerifyPicBinding>(R.layout.item_sweet_verify_pic) {
    override fun convert(binding: ItemSweetVerifyPicBinding, position: Int, item: SweetUploadBean) {
        binding.apply {
//            val params = root.layoutParams
//            params.width = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(15 * 2F + 10 * 2)) / 3
//            params.height = params.width
            if (item.url == "") {
                sweetPicDelete.isVisible = false
                if (item.defacultIcon == 0)
                    sweetPic.setImageResource(R.drawable.icon_upload_public)
                else
                    sweetPic.setImageResource(item.defacultIcon)
            } else {
                sweetPicDelete.isVisible = true
                GlideUtil.loadRoundImgCenterCrop(
                    context,
                    item.url,
                    sweetPic,
                    SizeUtils.dp2px(10F)
                )
            }
        }


    }
}