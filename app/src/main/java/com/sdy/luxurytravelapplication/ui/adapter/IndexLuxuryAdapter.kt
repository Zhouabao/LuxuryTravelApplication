package com.sdy.luxurytravelapplication.ui.adapter

import android.os.Handler
import android.text.TextUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.SpanUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ItemIndexLuxuryBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
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
class IndexLuxuryAdapter :
    BaseBindingQuickAdapter<IndexBean, ItemIndexLuxuryBinding>(R.layout.item_index_luxury) {
    override fun convert(binding: ItemIndexLuxuryBinding, position: Int, item: IndexBean) {
        binding.apply {
            onlineTime.text = item.online_time
            userNickname.text = item.nickname
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
            userVip.isVisible = UserManager.gender == 1 && (item.isplatinumvip || item.isdirectvip)
            if (item.isplatinumvip) {
                userVip.setImageResource(R.drawable.icon_index_vip)
            } else if (item.isdirectvip) {
                userVip.setImageResource(R.drawable.icon_vip_connnect)
            }
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
            SpanUtils.with(userBasicInfo).append(item.distance)
                .setForegroundColor(context.resources.getColor(R.color.colorAccent))
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
            userSign.isVisible = item.sign.isNotEmpty()
            GlideUtil.loadRoundImgCenterCrop(context, item.avatar, userAvatar, SizeUtils.dp2px(15F))
            userLabelRv.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            val adapter = LuxuryBaseInfoAdapter()
            adapter.setNewInstance(item.want)
            userLabelRv.adapter = adapter
            userLabelRv.isVisible = item.want.isNotEmpty()


            if (item.contact_way != 0) {
                contactBtn.text = "获取联系方式"
                contactBtn.isVisible = true
                ClickUtils.applySingleDebouncing(contactBtn) {
                    CommonFunction.checkUnlockContact(context, item.accid, item.gender)
                }

            } else if (item.mv_btn) {
                contactBtn.text = "视频介绍"
                contactBtn.isVisible = true
                ClickUtils.applySingleDebouncing(contactBtn) {
                    CommonFunction.checkUnlockIntroduceVideo(context, item.accid, item.mv_url)
                }
            } else {
                contactBtn.isVisible = false
            }

            ClickUtils.applySingleDebouncing(hiBtn) {
                CommonFunction.checkChat(context, item.accid)
            }
            ClickUtils.applySingleDebouncing(root) {
                TargetUserActivity.start(context, item.accid)
            }
        }
    }
}