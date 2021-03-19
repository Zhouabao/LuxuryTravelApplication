package com.sdy.luxurytravelapplication.nim.attachment

import com.alibaba.fastjson.JSONObject

/**
 *    author : ZFM
 *    date   : 2020/10/2011:29
 *    desc   :
 *    version: 1.0
 */
class ShareSquareAttachment(
    var desc: String = "",//分享的描述
    var content: String = "",//分享的文本内容
    var shareType: Int = 0,//分享的类型
    var img: String = "",//分享的图片
    var squareId: Int = 0//分享的图片

) : CustomAttachment(CustomAttachmentType.ShareSquare) {


    companion object {

        private const val KEY_DESC = "desc" //分享的描述

        private const val KEY_CONTENT = "content" //分享的文本内容

        private const val KEY_TYPE = "shareType" //分享的类型

        private const val KEY_IMG = "img" //分享的图片

        private const val KEY_ID = "squareId" //分享的图片


    }

    override fun packData(): JSONObject {
        val data = JSONObject()
        data[KEY_DESC] = desc
        data[KEY_CONTENT] = content
        data[KEY_ID] = squareId
        data[KEY_IMG] = img
        data[KEY_TYPE] = shareType
        return data
    }

    override fun parseData(data: JSONObject) {
        desc = data.getString(KEY_DESC)
        content = data.getString(KEY_CONTENT)
        squareId = data.getInteger(KEY_ID)
        shareType = data.getInteger(KEY_TYPE)
        img = data.getString(KEY_IMG)
    }
}