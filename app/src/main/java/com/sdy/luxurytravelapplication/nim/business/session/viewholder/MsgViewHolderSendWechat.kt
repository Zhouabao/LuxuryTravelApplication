package com.sdy.luxurytravelapplication.nim.business.session.viewholder

import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.NimMessageSendWechatBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.nim.attachment.SendWechatAttachment
import com.sdy.luxurytravelapplication.nim.business.module.list.MsgAdapter
import com.sdy.luxurytravelapplication.viewbinding.bindViewWithGeneric

/**
 *    author : ZFM
 *    date   : 2020/10/2011:15
 *    desc   :
 *    version: 1.0
 */
class MsgViewHolderSendWechat(msgAdapter: MsgAdapter) : MsgViewHolderBase(msgAdapter) {
    private val attachment by lazy { message.attachment as SendWechatAttachment }
    override val contentResId: Int
        get() = R.layout.nim_message_send_wechat


    private lateinit var binding: NimMessageSendWechatBinding
    override fun inflateContentView() {
        binding = bindViewWithGeneric(view)
    }


    override fun bindContentView() {
        GlideUtil.loadRoundImgCenterCrop(
            context,
            attachment.url,
            binding.sendWechatIv,
            SizeUtils.dp2px(15F)
        )
    }


}