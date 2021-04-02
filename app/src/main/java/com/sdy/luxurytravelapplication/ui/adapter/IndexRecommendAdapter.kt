package com.sdy.luxurytravelapplication.ui.adapter

import android.graphics.Color
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.SpanUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemIndexRecommendBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.IndexBean
import com.sdy.luxurytravelapplication.ui.activity.TargetUserActivity
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

/**
 *    author : ZFM
 *    date   : 2021/3/2216:08
 *    desc   :
 *    version: 1.0
 */
class IndexRecommendAdapter :
    BaseBindingQuickAdapter<IndexBean, ItemIndexRecommendBinding>(R.layout.item_index_recommend) {
    override fun convert(binding: ItemIndexRecommendBinding, position: Int, item: IndexBean) {

        binding.apply {
            if (item.assets_audit_way == 0) {
                binding.root.setBackgroundResource(R.drawable.icon_index_recommend_normal_bg)
            } else {
                binding.root.setBackgroundResource(R.drawable.icon_index_recommend_luxury_bg)
            }

            userNickname.text = item.nickname
            userSign.text = item.sign
            GlideUtil.loadRoundImgCenterCrop(context, item.avatar, userAvatar, SizeUtils.dp2px(10f))
            userVerifyLevel.isVisible = item.face_str.isNotEmpty()
            userVerifyLevel.text = item.face_str
            if (item.isfaced == 1) {
                userVerifyLevel.setBackgroundResource(R.drawable.shape_rectangle_ffedb2_5dp)
                userVerifyLevel.setTextColor(Color.parseColor("#FFFC9010"))
                userVerifyLevel.setCompoundDrawablesWithIntrinsicBounds(
                    context.resources.getDrawable(R.drawable.icon_luxury),
                    null,
                    null,
                    null
                )
            } else {
                userVerifyLevel.setBackgroundResource(R.drawable.shape_rectangle_fff0f2_5dp)
                userVerifyLevel.setTextColor(Color.parseColor("#FFFF6B82"))
                userVerifyLevel.setCompoundDrawablesWithIntrinsicBounds(
                    context.resources.getDrawable(R.drawable.icon_index_beauty),
                    null,
                    null,
                    null
                )
            }


            if (item.gender == 1) {
                userContact.isVisible = item.isplatinumvip
                userContact.setImageResource(R.drawable.icon_index_vip)
            } else {
                userContact.isVisible = item.contact_way != 0
                userContact.setImageResource(R.drawable.icon_index_wechat)
            }

            SpanUtils.with(userBasicInfo).append(item.distance)
                .setForegroundColor(context.resources.getColor(R.color.colorAccent))
                .setBold()
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
                .append(
                    if (item.constellation.isNotEmpty()) {
                        "·"
                    } else {
                        ""
                    }
                )
                .append(item.online_time)
                .create()

            ClickUtils.applySingleDebouncing(userAvatar) {
                TargetUserActivity.start(context, item.accid)
            }

        }

    }
}