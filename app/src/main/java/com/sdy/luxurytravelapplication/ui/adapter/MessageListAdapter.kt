package com.sdy.luxurytravelapplication.ui.adapter

import android.view.View
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.RecentContact
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.ItemMessageListBinding
import com.sdy.luxurytravelapplication.event.GetNewMsgEvent
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.MessageGiftBean
import com.sdy.luxurytravelapplication.nim.api.NimUIKit
import com.sdy.luxurytravelapplication.nim.business.session.activity.ChatActivity
import com.sdy.luxurytravelapplication.nim.business.uinfo.UserInfoHelper
import com.sdy.luxurytravelapplication.nim.common.CommonUtil
import com.sdy.luxurytravelapplication.nim.common.util.sys.TimeUtil
import com.sdy.luxurytravelapplication.nim.impl.NimUIKitImpl
import com.sdy.luxurytravelapplication.ui.fragment.MessageFragment
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter
import org.greenrobot.eventbus.EventBus

class MessageListAdapter :
    BaseBindingQuickAdapter<RecentContact, ItemMessageListBinding>(R.layout.item_message_list) {
    var session_list_arr: MutableList<MessageGiftBean> = mutableListOf()//最近礼物列表
    override fun convert(binding: ItemMessageListBinding, position: Int, item: RecentContact) {

        binding.apply {

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
            latelyTime.isVisible = item.time != 0L
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

            ClickUtils.applySingleDebouncing(menuTop){
                if (CommonUtil.isTagSet(item, MessageFragment.RECENT_TAG_STICKY)) {
                    CommonUtil.removeTag(item, MessageFragment.RECENT_TAG_STICKY)
                } else {
                    CommonUtil.addTag(item, MessageFragment.RECENT_TAG_STICKY)
                }
                NIMClient.getService(MsgService::class.java).updateRecentAndNotify(item)
            }
            ClickUtils.applySingleDebouncing(menuDetele){
                CommonFunction.dissolveRelationshipLocal(item.contactId)
            }

            ClickUtils.applySingleDebouncing(content){
                ChatActivity.start(context,item.contactId)
            }
//             NIMClient.getService(MsgService::class.java).clearUnreadCount(adapter.data[position].contactId, SessionTypeEnum.P2P)
//                ChatActivity.start(activity!!, adapter.data[position].contactId)
//                EventBus.getDefault().post(GetNewMsgEvent())

        }

    }

}
