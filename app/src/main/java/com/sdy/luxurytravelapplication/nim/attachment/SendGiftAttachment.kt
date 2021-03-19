package com.sdy.luxurytravelapplication.nim.attachment

import com.alibaba.fastjson.JSONObject

/**
 *    author : ZFM
 *    date   : 2020/10/2011:29
 *    desc   :
 *    version: 1.0
 */
class SendGiftAttachment : CustomAttachment(CustomAttachmentType.Gift) {
    var giftId: Int = 0//礼物id
    var orderId: Int = 0//礼物订单id
    var giftIcon: String = ""//礼物图片
    var giftAmount: Int = 0//礼物金币价值
    var giftName: String = ""//礼物金币价值
    var giftType: Int = GIFT_TYPE_NORMAL//0未领取 1已领取 2已退回

    companion object {
        const val KEY_ID = "giftId"
        const val KEY_ORDERID = "orderId"
        const val KEY_NAME = "giftName"
        const val KEY_ICON = "giftIcon"
        const val KEY_AMOUNT = "giftAmount"
        const val KEY_STATUS = "giftReceiveStatus" //0未领取 1已领取 2已退回
        const val KEY_GIFT_TYPE = "giftType"
        const val STATUS_WAIT = 0 //礼物待领取
        const val STATUS_RECEIVED = 1 //已领取
        const val STATUS_TIMEOUT = 2 //超时退回
        const val STATUS_RETURNED = 3//拒绝

        const val GIFT_TYPE_NORMAL = 0
        const val GIFT_TYPE_HOT = 1
        const val GIFT_TYPE_CONTACT = 2
    }

    override fun packData(): JSONObject {
        val data = JSONObject()
        data.put(KEY_ID, giftId)
        data.put(KEY_ORDERID, orderId)
        data.put(KEY_ICON, giftIcon)
        data.put(KEY_AMOUNT, giftAmount)
        data.put(KEY_NAME, giftName)
        data.put(KEY_GIFT_TYPE, giftType)
        return data
    }

    override fun parseData(data: JSONObject) {
        orderId = data.getIntValue(KEY_ORDERID)
        giftId = data.getIntValue(KEY_ID)
        giftAmount = data.getIntValue(KEY_AMOUNT)
        giftIcon = data.getString(KEY_ICON)
        giftName = data.getString(KEY_NAME)
        giftType = data.getIntValue(KEY_GIFT_TYPE)
    }
}