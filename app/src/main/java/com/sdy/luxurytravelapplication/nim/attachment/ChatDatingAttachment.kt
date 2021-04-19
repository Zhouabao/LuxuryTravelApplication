package com.sdy.luxurytravelapplication.nim.attachment

import com.alibaba.fastjson.JSONObject

/**
 *    author : ZFM
 *    date   : 2020/10/2011:29
 *    desc   :约会
 *    version: 1.0
 */
class ChatDatingAttachment(
    var content: String="",
    var img: String="",
    var datingId: Int = 0
) : CustomAttachment(CustomAttachmentType.ChatDating) {


    companion object {
        const val KEY_DATING_CONTENT = "content"
        const val KEY_DATING_ICON = "icon"
        const val KEY_DATING_ID = "datingId"

    }

    override fun packData(): JSONObject {
        val data = JSONObject()
        data[KEY_DATING_CONTENT] = content
        data[KEY_DATING_ICON] = img
        data[KEY_DATING_ID] = datingId
        return data
    }

    override fun parseData(data: JSONObject) {
        content = data.getString(KEY_DATING_CONTENT)
        img = data.getString(KEY_DATING_ICON)
        datingId = data.getInteger(KEY_DATING_ID)
    }
}