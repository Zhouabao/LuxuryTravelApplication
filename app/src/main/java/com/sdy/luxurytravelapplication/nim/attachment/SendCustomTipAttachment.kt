package com.sdy.luxurytravelapplication.nim.attachment

import com.alibaba.fastjson.JSONObject

/**
 *    author : ZFM
 *    date   : 2020/10/2011:29
 *    desc   : 发送自定义的tip消息
 *    version: 1.0
 */
class SendCustomTipAttachment(
    var content: String = "",
    var showType: Int = 0,
    var ifSendUserShow: Boolean = false
) : CustomAttachment(CustomAttachmentType.SendTip) {


    companion object {
        const val KEY_CONTENT = "content" //发送tip的内容
        const val KEY_TYPE = "showType" //发送tip的类型
        const val KEY_IF_SEND_USER_SHOW = "ifSendUserShow" //是否是发送方显示


        const val CUSTOM_TIP_NO_MONEY_MAN = 1 //男方发送消息余额不足
        const val CUSTOME_TIP_RECEIVE_VERIFY_WOMAN = 2 //认证后的女方收到第一条消息（仅显示一次，切换用户不重复发送）
        const val CUSTOME_TIP_RECEIVE_NOT_VERIFY_WOMAN = 3 //未认证的女方收到第一条消息（切换用户重复发送）
        const val CUSTOME_TIP_WOMAN_CHAT_COUNT = 4 //女方聊天条数到达设定条数时,没有心愿礼物
        const val CUSTOME_TIP_MAN_BIGGER_CHAT_COUNT_HAS_GIFT = 5 //男方 双方聊天数>设定聊天条数  女方有心愿礼物
        const val CUSTOME_TIP_MAN_BIGGER_CHAT_COUNT_NOT_HAS_GIFT = 6 //男方 双方聊天数>设定聊天条数 女方没有心愿礼物
        const val CUSTOME_TIP_RECEIVED_GIFT = 7 //已领取对方赠送的旅券礼物，可直接用于兑换商品和提现
        const val CUSTOME_TIP_EXCHANGE_PRODUCT = 8 //已满足兑换所需旅券，立即兑换
        const val CUSTOME_TIP_EXCHANGE_FOR_ASSISTANT = 9 //小助手发聊天旅券退回警告（针对女方）
        const val CUSTOME_TIP_NORMAL = 10 //常规的tip
        const val CUSTOME_TIP_PRIVICY_SETTINGS = 11 //私聊权限
        const val CUSTOME_TIP_CHARGE_PT_VIP = 12 //充值高级会员


    }

    override fun packData(): JSONObject {
        val data = JSONObject()
        data[KEY_TYPE] = showType
        data[KEY_CONTENT] = content
        data[KEY_IF_SEND_USER_SHOW] = ifSendUserShow
        return data
    }

    override fun parseData(data: JSONObject) {
        showType = data.getIntValue(KEY_TYPE)
        content = data.getString(KEY_CONTENT)
        ifSendUserShow = data.getBoolean(KEY_IF_SEND_USER_SHOW)
    }
}