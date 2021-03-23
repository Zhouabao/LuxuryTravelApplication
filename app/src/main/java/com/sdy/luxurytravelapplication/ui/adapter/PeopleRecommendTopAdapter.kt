package com.sdy.luxurytravelapplication.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ItemPeopleRecommendTopContentBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.IndexTopBean
import com.sdy.luxurytravelapplication.ui.dialog.ToBeSelectedDialog
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

/**
 *    author : ZFM
 *    date   : 2021/3/2215:12
 *    desc   :
 *    version: 1.0
 */
class PeopleRecommendTopAdapter :
    BaseBindingQuickAdapter<IndexTopBean, ItemPeopleRecommendTopContentBinding>(R.layout.item_people_recommend_top_content) {
    override fun convert(
        binding: ItemPeopleRecommendTopContentBinding,
        position: Int,
        item: IndexTopBean
    ) {
        binding.apply {
            val itemView = binding.root
            val params = itemView.layoutParams as RecyclerView.LayoutParams
            if (position == data.size - 1) {
                params.rightMargin = SizeUtils.dp2px(15F)
            }
            GlideUtil.loadRoundImgCenterCrop(
                context,
                item.avatar,
                userAvator,
                SizeUtils.dp2px(15f)
            )

//            if (item.accid == UserManager.accid) {
//                userMvBtn.isVisible = false
//                selectedMine.isVisible = true
//                lottieMine.isVisible = true
//            } else {
//                selectedMine.isVisible = false
//                lottieMine.isVisible = false
//                if (UserManager.gender == 1) {
//                    userMvBtn.isVisible = item.source_type == 1
//                } else {
//                    userMvBtn.isVisible = item.isplatinum
//                }
//            }

            ClickUtils.applySingleDebouncing(itemView) {
                if (item.accid == UserManager.accid) {
                    ToBeSelectedDialog(true).show()
                } else {

                }
            }

        }
    }
}