package com.sdy.luxurytravelapplication.ui.adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemMyPhotoBinding
import com.sdy.luxurytravelapplication.databinding.ItemMyPhotoTakeBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.MyPhotoBean

class UserPhotoAdapter : BaseMultiItemQuickAdapter<MyPhotoBean, BaseViewHolder>(),
    DraggableModule {

    init {
        addItemType(MyPhotoBean.COVER, R.layout.item_my_photo_take)
        addItemType(MyPhotoBean.PHOTO, R.layout.item_my_photo)
    }

    override fun convert(holder: BaseViewHolder, item: MyPhotoBean) {
        when (holder.itemViewType) {
            MyPhotoBean.COVER -> {
                val binding = ItemMyPhotoTakeBinding.bind(holder.itemView)
                val params = binding.root.layoutParams as RecyclerView.LayoutParams
                params.width = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(15 * 2F + 9 * 2)) / 3
                params.height = params.width
                binding.root.layoutParams = params
            }
            MyPhotoBean.PHOTO -> {
                val binding = ItemMyPhotoBinding.bind(holder.itemView)
                val params = binding.root.layoutParams as RecyclerView.LayoutParams
                params.width = (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(15 * 2F + 10 * 2)) / 3
                params.height = params.width
                binding.root.layoutParams = params


                binding.myAvatorTip.isVisible = holder.layoutPosition == 0
                GlideUtil.loadRoundImgCenterCrop(
                    context,
                    item.url,
                    binding.myPhoto,
                    SizeUtils.dp2px(15F)
                )
            }
        }
    }
}
