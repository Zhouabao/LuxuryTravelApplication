package com.sdy.luxurytravelapplication.nim.attachment

import com.alibaba.fastjson.JSONObject

/**
 *    author : ZFM
 *    date   : 2020/10/2011:29
 *    desc   :发送联系方式
 *    version: 1.0
 */
class ContactCandyAttachment(
    var contactCandy: Int = 0// 获取联系方式赠送的旅券
) : CustomAttachment(CustomAttachmentType.ChatContactCandy) {


    companion object {
        const val KEY_CONTACT_CANDY = "amount"

    }

    override fun packData(): JSONObject {
        val data = JSONObject()
        data[KEY_CONTACT_CANDY] = contactCandy
        return data
    }

    override fun parseData(data: JSONObject) {
        contactCandy = data.getIntValue(KEY_CONTACT_CANDY)
    }
}