package com.sdy.luxurytravelapplication.ui.adapter

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.SpanUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemIndexLuxuryBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.IndexBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

/**
 *    author : ZFM
 *    date   : 2021/3/2216:08
 *    desc   :
 *    version: 1.0
 */
class IndexLuxuryAdapter :
    BaseBindingQuickAdapter<IndexBean, ItemIndexLuxuryBinding>(R.layout.item_index_luxury) {
    override fun convert(binding: ItemIndexLuxuryBinding, position: Int, item: IndexBean) {
        binding.apply {
            onlineTime.text = item.online_time
            userNickname.text = item.nickname
            userTravelPlace.text = item.dating_title
            userContact.isVisible = item.contact_way != 0
            SpanUtils.with(userBasicInfo).append(item.distance)
                .append(
                    if (item.distance.isNotEmpty()) {
                        "·"
                    } else {
                        ""
                    }
                )
                .append(
                    if (item.gender == 1) {
                        "男"
                    } else {
                        "女"
                    }
                )
                .append(
                    if (item.gender != 0) {
                        "·"
                    } else {
                        ""
                    }
                )
                .append(item.constellation)
                .create()

            userSign.text = item.sign
            GlideUtil.loadRoundImgCenterCrop(context, item.avatar, userAvatar, SizeUtils.dp2px(15F))
            userLabelRv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            val adapter = LuxuryBaseInfoAdapter()
            adapter.setNewInstance(item.want)
            userLabelRv.adapter =adapter
        }
    }
}