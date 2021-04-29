package com.sdy.luxurytravelapplication.ui.adapter

import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemMessageListBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.AccostBean
import com.sdy.luxurytravelapplication.nim.api.NimUIKit
import com.sdy.luxurytravelapplication.nim.common.util.sys.TimeUtil
import com.sdy.luxurytravelapplication.nim.impl.NimUIKitImpl
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class AccostListAdapter :
    BaseBindingQuickAdapter<AccostBean, ItemMessageListBinding>(R.layout.item_message_list) {
    override fun convert(binding: ItemMessageListBinding, position: Int, item: AccostBean) {
        binding.apply {
            msgDivider.isInvisible = position == data.size - 1


            swipeLayout.isCanLeftSwipe = true
            menuTop.isVisible = false
            swipeLayout.isCanRightSwipe = false
            newCount.text = "${item.unreadCnt}"
            msgTitle.text = "${item.nickname}"
            GlideUtil.loadAvatorImg(
                context,
                item.avatar,
                msgIcon
            )
            val recent = NIMClient.getService(MsgService::class.java)
                .queryRecentContact(item.accid, SessionTypeEnum.P2P)

            val message = NIMClient.getService(MsgService::class.java)
                .queryLastMessage(item.accid, SessionTypeEnum.P2P)
            text.text = when {
                recent != null -> {
                    CommonFunction.getRecentContent(recent)
                }
                message != null -> {
                    CommonFunction.getRecentContent(
                        message.attachment,
                        message.content,
                        message.localExtension ?: hashMapOf(),
                        message.msgType,
                        message.remoteExtension ?: hashMapOf()
                    )
                }
                item.content.isNotEmpty() -> {
                    item.content
                }
                else -> {
                    ""
                }
            }

            latelyTime.isVisible = item.time != 0L
            latelyTime.text = TimeUtil.getTimeShowString(item.time, true)
            msgOnLineState.isVisible = NimUIKitImpl.enableOnlineState()
                    && !NimUIKitImpl.getOnlineStateContentProvider().getSimpleDisplay(item.accid)
                .isNullOrEmpty()
                    && NimUIKitImpl.getOnlineStateContentProvider().getSimpleDisplay(item.accid)
                .contains(
                    context.getString(R.string.on_line)
                )
            newCount.isVisible = item.unreadCnt > 0

            //0 不是甜心圈 1 资产认证 2豪车认证 3身材 4职业  5高额充值
            val extensionMap: Map<String, Any>? =
                (NimUIKit.getUserInfoProvider()
                    .getUserInfo(item.accid) as NimUserInfo?)?.extensionMap
            if (!extensionMap.isNullOrEmpty() && extensionMap["assets_audit_way"] != null && extensionMap["assets_audit_way"] != 0) {
                sweetLogo.isVisible = true
//                if (extensionMap["assets_audit_way"] == 1 || extensionMap["assets_audit_way"] == 2 || extensionMap["assets_audit_way"] == 5) {
//                    sweetLogo.imageAssetsFolder = "images_sweet_logo_man"
//                    sweetLogo.setAnimation("data_sweet_logo_man.json")
//                } else {
//                    sweetLogo.imageAssetsFolder = "images_sweet_logo_woman"
//                    sweetLogo.setAnimation("data_sweet_logo_woman.json")
//                }

                if (sweetLogo.tag != null) {
                    sweetLogo.removeOnAttachStateChangeListener(sweetLogo.tag as View.OnAttachStateChangeListener)
                }

                val onAttachStateChangeListener = object : View.OnAttachStateChangeListener {
                    override fun onViewDetachedFromWindow(v: View?) {
                        sweetLogo.pauseAnimation()
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
