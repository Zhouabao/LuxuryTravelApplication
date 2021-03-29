package com.sdy.luxurytravelapplication.ui.adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.module.DraggableModule
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemChoosePhotoBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.MediaBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class ChoosePhotosAdapter(type: Int) :
    BaseBindingQuickAdapter<MediaBean, ItemChoosePhotoBinding>(R.layout.item_choose_photo),
    DraggableModule {
    override fun convert(binding: ItemChoosePhotoBinding, position: Int, item: MediaBean) {

        binding.apply {
            val params = root.layoutParams as RecyclerView.LayoutParams
            params.width = SizeUtils.dp2px(70F)
            params.height = SizeUtils.dp2px(70F)
            params.leftMargin = if (position == 0) {
                SizeUtils.dp2px(15F)
            } else {
                SizeUtils.dp2px(10F)
            }
            params.rightMargin = if (position == data.size - 1) {
                SizeUtils.dp2px(15F)
            } else 0
            root.layoutParams = params



            chooseVideoDuration.isVisible = false
            choosePhotoDel.isVisible = item.ischecked
            choosePhotoDel.setImageResource(R.drawable.icon_delete)

            GlideUtil.loadRoundImgCenterCrop(
                context,
                item.filePath,
                choosePhoto,
                SizeUtils.dp2px(15F)
            )
        }

    }

}
