package com.sdy.luxurytravelapplication.nim.business.session.viewholder

import android.graphics.Color
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import com.blankj.utilcode.util.SpanUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.NimMessageSendWechatBinding
import com.sdy.luxurytravelapplication.nim.attachment.ContactAttachment
import com.sdy.luxurytravelapplication.nim.business.module.list.MsgAdapter
import com.sdy.luxurytravelapplication.nim.common.util.sys.ClipboardUtil
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.viewbinding.bindViewWithGeneric

/**
 *    author : ZFM
 *    date   : 2020/10/2011:15
 *    desc   :
 *    version: 1.0
 */
class MsgViewHolderContact(msgAdapter: MsgAdapter) : MsgViewHolderBase(msgAdapter) {
    private val attachment by lazy { message.attachment as ContactAttachment }
    override val contentResId: Int
        get() = R.layout.nim_message_send_wechat


    private lateinit var binding: NimMessageSendWechatBinding
    override fun inflateContentView() {
        binding = NimMessageSendWechatBinding.inflate(LayoutInflater.from(context), contentContainer, true)
    }



    //	0没有留下联系方式 1 电话 2 微信 3 qq 99隐藏
    override fun bindContentView() {
      binding.apply {
          if (attachment. contactWay== 1) {
              contactImg.setImageResource(R.drawable.icon_unlock_phone)
          } else if (attachment.contactWay == 2) {
              contactImg.setImageResource(R.drawable.icon_unlock_wechat)
          } else if (attachment.contactWay == 3) {
              contactImg.setImageResource(R.drawable.icon_unlock_qq)
          }

          SpanUtils.with(contactContent)
              .append(attachment.contactContent)
              .setClickSpan(object : ClickableSpan() {
                  override fun onClick(widget: View) {
                      ClipboardUtil.clipboardCopyText(context, attachment.contactContent)
                      ToastUtil.toast(context.getString(R.string.contact_has_been_copyed))
                  }
              })
              .setForegroundColor(Color.parseColor("#FF1ED0A7"))
              .create()
      }
    }


}