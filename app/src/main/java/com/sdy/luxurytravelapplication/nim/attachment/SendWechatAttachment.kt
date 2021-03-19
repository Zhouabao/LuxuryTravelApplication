package com.sdy.luxurytravelapplication.nim.attachment

import com.alibaba.fastjson.JSONObject

/**
 *    author : ZFM
 *    date   : 2020/10/2011:29
 *    desc   :
 *    version: 1.0
 */
class SendWechatAttachment : CustomAttachment(CustomAttachmentType.ShareWechat) {
    var url: String = ""//礼物图片

    companion object {
        const val KEY_URL = "url"
    }

    override fun packData(): JSONObject {
        val data = JSONObject()
        data.put(KEY_URL, url)
        return data
    }

    override fun parseData(data: JSONObject) {
        url = data.getString(KEY_URL)
    }
}