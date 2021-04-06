package com.sdy.luxurytravelapplication.ui.adapter

import android.view.View
import androidx.core.view.isVisible
import com.blankj.utilcode.util.SizeUtils
import com.netease.nimlib.sdk.msg.model.RecentContact
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemMessageListBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.MessageGiftBean
import com.sdy.luxurytravelapplication.nim.api.NimUIKit
import com.sdy.luxurytravelapplication.nim.business.uinfo.UserInfoHelper
import com.sdy.luxurytravelapplication.nim.common.CommonUtil
import com.sdy.luxurytravelapplication.nim.common.util.sys.TimeUtil
import com.sdy.luxurytravelapplication.nim.impl.NimUIKitImpl
import com.sdy.luxurytravelapplication.ui.fragment.MessageFragment
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class MessageListAdapter :
    BaseBindingQuickAdapter<RecentContact, ItemMessageListBinding>(R.layout.item_message_list) {
    var session_list_arr: MutableList<MessageGiftBean> = mutableListOf()//最近礼物列表
    override fun convert(binding: ItemMessageListBinding, position: Int, item: RecentContact) {

        binding.apply {
//          addOnClickListener(R.id.menuTop)
//            holder.addOnClickListener(R.id.menuDetele)
//            holder.addOnClickListener(R.id.content)
            if (position == data.size) {
                msgDivider.visibility = View.INVISIBLE
            } else {
                msgDivider.visibility = View.VISIBLE
            }

            if (item.contactId != null) {
                msgTitle.text = UserInfoHelper.getUserDisplayName(item.contactId)
            }
            GlideUtil.loadRoundImgCenterCrop(
                context,
                UserInfoHelper.getAvatar(item.contactId),
                msgIcon, SizeUtils.dp2px(15F)
            )

            if (CommonUtil.isTagSet(item, MessageFragment.RECENT_TAG_STICKY)) {
                menuTop.text = "取消置顶"
            } else {
                menuTop.text = "置顶"
            }


            msgNew.isVisible = false
            text.text = CommonFunction.getRecentContent(item)

            latelyTime.text = TimeUtil.getTimeShowString(item.time, true)
            if (item.unreadCount == 0) {
                newCount.visibility = View.GONE
            } else {
                newCount.text = "${item.unreadCount}"
                newCount.visibility = View.VISIBLE
            }
            msgOnLineState.isVisible =
                NimUIKitImpl.enableOnlineState()
                        && !NimUIKitImpl.getOnlineStateContentProvider()
                    .getSimpleDisplay(item.contactId).isNullOrEmpty()
                        && NimUIKitImpl.getOnlineStateContentProvider()
                    .getSimpleDisplay(item.contactId)
                    .contains(
                        context.getString(R.string.on_line)
                    )


            //0 不是甜心圈 1 资产认证 2豪车认证 3身材 4职业  5高额充值
            val extensionMap: Map<String, Any>? = (NimUIKit.getUserInfoProvider()
                .getUserInfo(item.contactId) as NimUserInfo?)?.extensionMap
            if (!extensionMap.isNullOrEmpty() && extensionMap["assets_audit_way"] != null && extensionMap["assets_audit_way"] != 0) {
                sweetLogo.isVisible = true
                if (extensionMap["assets_audit_way"] == 1 || extensionMap["assets_audit_way"] == 2 || extensionMap["assets_audit_way"] == 5) {
                    sweetLogo.imageAssetsFolder = "images_sweet_logo_man"
                    sweetLogo.setAnimation("data_sweet_logo_man.json")
                } else {
                    sweetLogo.imageAssetsFolder = "images_sweet_logo_woman"
                    sweetLogo.setAnimation("data_sweet_logo_woman.json")
                }

                if (sweetLogo.tag != null) {
                    sweetLogo.removeOnAttachStateChangeListener(sweetLogo.tag as View.OnAttachStateChangeListener)
                }

                val onAttachStateChangeListener = object : View.OnAttachStateChangeListener {
                    override fun onViewDetachedFromWindow(v: View?) {
                        sweetLogo.cancelAnimation()
                    }

                    override fun onViewAttachedToWindow(v: View?) {
                        sweetLogo.playAnimation()

                    }
                }
                sweetLogo.addOnAttachStateChangeListener(onAttachStateChangeListener)
                sweetLogo.tag = onAttachStateChangeListener
            } else {
                sweetLogo.isVisible = false
            }

        }

    }

}
