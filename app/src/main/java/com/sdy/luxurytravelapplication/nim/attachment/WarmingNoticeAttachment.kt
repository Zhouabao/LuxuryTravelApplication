package com.sdy.luxurytravelapplication.nim.attachment

import com.alibaba.fastjson.JSONObject

/**
 *    author : ZFM
 *    date   : 2020/10/2011:29
 *    desc   :
 *    version: 1.0
 */
class WarmingNoticeAttachment : CustomAttachment(CustomAttachmentType.WarmingNotice) {
    var name: String = ""//appname

    companion object {
        const val KEY_APPNAME = "name"
    }

    override fun packData(): JSONObject {
        val data = JSONObject()
        data.put("appname", name)
        return data
    }

    override fun parseData(data: JSONObject) {
        name = data.getString(KEY_APPNAME)
    }
}