package com.sdy.luxurytravelapplication.nim.attachment

import com.alibaba.fastjson.JSONObject

/**
 *    author : ZFM
 *    date   : 2020/10/2011:29
 *    desc   :搭讪语消息
 *    version: 1.0
 */
class ChatUpAttachment(var chatUpContent: String = "") :
    CustomAttachment(CustomAttachmentType.ChatUp) {


    companion object {
        const val KEY_CHAT_UP_CONTENT = "chatUpContent"

    }

    override fun packData(): JSONObject {
        val data = JSONObject()
        data[KEY_CHAT_UP_CONTENT] = chatUpContent
        return data
    }

    override fun parseData(data: JSONObject) {
        chatUpContent = data.getString(KEY_CHAT_UP_CONTENT)
    }
}