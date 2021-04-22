package com.sdy.luxurytravelapplication.nim.business.session.viewholder

import android.view.LayoutInflater
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemContactCandyBinding
import com.sdy.luxurytravelapplication.nim.attachment.ContactCandyAttachment
import com.sdy.luxurytravelapplication.nim.business.module.list.MsgAdapter

/**
 *    author : ZFM
 *    date   : 2020/10/2011:15
 *    desc   :
 *    version: 1.0
 */
class MsgViewHolderContactCandy(msgAdapter1: MsgAdapter) : MsgViewHolderBase(msgAdapter1) {
    private val attachment by lazy { message.attachment as ContactCandyAttachment }
    override val contentResId: Int
        get() = R.layout.item_contact_candy

    private lateinit var binding: ItemContactCandyBinding
    override fun inflateContentView() {
        binding =
            ItemContactCandyBinding.inflate(LayoutInflater.from(context), contentContainer, true)
    }

    override fun bindContentView() {
        binding.giftAmount.text = "${attachment.contactCandy}旅券"
    }

    override fun leftBackground(): Int {
        return R.color.white
    }

    override fun rightBackground(): Int {
        return R.color.white
    }

    override fun onItemClick() {

    }

}


