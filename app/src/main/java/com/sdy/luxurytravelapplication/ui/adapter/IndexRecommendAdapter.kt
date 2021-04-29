package com.sdy.luxurytravelapplication.ui.adapter

import android.graphics.Color
import android.os.Handler
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.SpanUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.DialogTodayHasDatingBinding
import com.sdy.luxurytravelapplication.databinding.ItemIndexRecommendBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.IndexBean
import com.sdy.luxurytravelapplication.nim.business.session.activity.ChatActivity
import com.sdy.luxurytravelapplication.ui.activity.TargetUserActivity
import com.sdy.luxurytravelapplication.ui.dialog.AccountDangerDialog
import com.sdy.luxurytravelapplication.ui.dialog.PublishDatingDialog
import com.sdy.luxurytravelapplication.ui.dialog.TodayHasDatingDialog
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
            userVideoIcon.isVisible = item.mv_btn
            userTravelPlace.isVisible = item.dating_content.isNotEmpty()
            userTravelPlace.text = item.dating_content
            if(userTravelPlace.isVisible){
                //延迟启动跑马灯 避免一进去因为赋值问题导致设置的属性不起作用
                Handler().postDelayed({
                    userTravelPlace.ellipsize = TextUtils.TruncateAt.MARQUEE
                    userTravelPlace.maxLines = 1
                    userTravelPlace.isSelected = true
                    userTravelPlace.isFocusable = true
                    userTravelPlace.isFocusableInTouchMode = true
                    userTravelPlace.marqueeRepeatLimit = -1
                }, 500L)
            }
            GlideUtil.loadRoundImgCenterCrop(context, item.avatar, userAvatar, SizeUtils.dp2px(10f))
            userVerifyLevel.isVisible = item.face_type == 2 || item.face_type == 3
            //	0没有认证 1活体 2 真人 3 颜值 4奢旅
            when (item.face_type) {
                3 -> {
                    userVerifyLevel.isVisible = true
                    userVerifyLevel.setBackgroundResource(R.drawable.shape_rectangle_ffedb2_5dp)
                    userVerifyLevel.setTextColor(Color.parseColor("#FFFC9010"))
                    userVerifyLevel.text = "颜值认证"
                    userVerifyLevel.setCompoundDrawablesWithIntrinsicBounds(
                        context.getDrawable(R.drawable.icon_beauty),
                        null,
                        null,
                        null
                    )
                }
                4 -> {
                    userVerifyLevel.isVisible = true
                    userVerifyLevel.setBackgroundResource(R.drawable.shape_rectangle_fff0f2_5dp)
                    userVerifyLevel.setTextColor(Color.parseColor("#FFFF6B82"))
                    userVerifyLevel.text = "奢旅圈"
                    userVerifyLevel.setTextColor(Color.parseColor("#FFFC9010"))
                    userVerifyLevel.setCompoundDrawablesWithIntrinsicBounds(
                        context.getDrawable(R.drawable.icon_luxury),
                        null,
                        null,
                        null
                    )
                }
                else -> {
                    userVerifyLevel.isVisible = false
                }

            }

            if (item.gender == 1) {
                userContact.isVisible = item.isplatinumvip || item.isdirectvip
                if (item.isplatinumvip) {
                    userContact.setImageResource(R.drawable.icon_index_vip)
                } else {
                    userContact.setImageResource(R.drawable.icon_vip_connnect)
                }
            } else {
                //联系方式  0  没有 1 电话 2微信 3 qq
                userContact.isVisible = item.contact_way != 0
                when (item.contact_way) {
                    1 -> {
                        userContact.setImageResource(R.drawable.icon_index_phone)
                    }
                    2 -> {
                        userContact.setImageResource(R.drawable.icon_index_wechat)
                    }
                    3 -> {
                        userContact.setImageResource(R.drawable.icon_index_qq)
                    }
                }
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

            ClickUtils.applySingleDebouncing(root) {
                TargetUserActivity.start(context, item.accid)
            }

        }

    }
}