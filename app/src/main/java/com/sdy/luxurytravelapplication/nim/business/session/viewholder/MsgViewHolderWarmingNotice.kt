package com.sdy.luxurytravelapplication.nim.business.session.viewholder

import com.blankj.utilcode.util.ClickUtils
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.msg.MsgService
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.HeadChatWarmingNoticeBinding
import com.sdy.luxurytravelapplication.nim.business.module.list.MsgAdapter

/**
 *    author : ZFM
 *    date   : 2020/10/2011:15
 *    desc   :
 *    version: 1.0
 */
class MsgViewHolderWarmingNotice(adapter: MsgAdapter) : MsgViewHolderBase(adapter) {
    override val contentResId: Int
        get() = R.layout.head_chat_warming_notice

    private val binding by lazy { HeadChatWarmingNoticeBinding.bind(view) }
    override fun inflateContentView() {
    }


    override fun bindContentView() {
        ClickUtils.applySingleDebouncing(binding.knowBtn){
            adapter.remove(layoutPosition)
        }
        ClickUtils.applySingleDebouncing(binding.neverNoticeBtn){
            NIMClient.getService(MsgService::class.java).deleteChattingHistory(message)
            msgAdapter.deleteItem(message, false)
        }

    }

    override val isShowBubble: Boolean
        get() = false
    override val isShowHeadImage: Boolean
        get() = false
    override val isShowTime: Boolean
        get() = false

    override fun shouldDisplayNick(): Boolean {
        return false
    }


}