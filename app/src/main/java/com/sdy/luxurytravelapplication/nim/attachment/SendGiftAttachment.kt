package com.sdy.luxurytravelapplication.nim.attachment

import com.alibaba.fastjson.JSONObject

/**
 *    author : ZFM
 *    date   : 2020/10/2011:29
 *    desc   :
 *    version: 1.0
 */
class SendGiftAttachment(
    //礼物订单id
    var orderId: Int = 0,
    //礼物状态
    var giftStatus: Int = GIFT_RECEIVE_STATUS_NORMAL
) : CustomAttachment(CustomAttachmentType.Gift) {


    companion object {
        const val KEY_ORDER_ID = "orderId"
        const val KEY_GIFT_STATUS = "giftStatus"
        const val GIFT_RECEIVE_STATUS_NORMAL = 1 // 开启 待开启  （待领取状态）
        const val GIFT_RECEIVE_STATUS_HAS_OPEN = 2 // 已开启（领取成功 or 发送成功）
        const val GIFT_RECEIVE_STATUS_HAS_RETURNED = 3// 已退回  超时已退回（超时退回）

    }

    override fun packData(): JSONObject {
        val data = JSONObject()
        data[KEY_ORDER_ID] = orderId
        data[KEY_GIFT_STATUS] = giftStatus
        return data
    }

    override fun parseData(data: JSONObject) {
        orderId = data.getIntValue(KEY_ORDER_ID)
        giftStatus = data.getIntValue(KEY_GIFT_STATUS)
    }
}