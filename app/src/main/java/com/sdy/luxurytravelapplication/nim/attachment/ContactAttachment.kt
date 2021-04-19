package com.sdy.luxurytravelapplication.nim.attachment

import com.alibaba.fastjson.JSONObject

/**
 *    author : ZFM
 *    date   : 2020/10/2011:29
 *    desc   :
 *    version: 1.0
 */
class ContactAttachment : CustomAttachment(CustomAttachmentType.ChatContact) {
    private val KEY_CONTACT_WAY = "contactWay" //联系方式
    private val KEY_CONTACT_CONTENT = "contactContent" //联系号码

     var contactWay = 0////联系方式
     var contactContent: String = "" //联系号码


    override fun parseData(data: JSONObject) {
        contactWay = data.getInteger(KEY_CONTACT_WAY)
        contactContent = data.getString(KEY_CONTACT_CONTENT)
    }

    override fun packData(): JSONObject? {
        val data = JSONObject()
        data[KEY_CONTACT_WAY] = contactWay
        data[KEY_CONTACT_CONTENT] = contactContent
        return data
    }

}