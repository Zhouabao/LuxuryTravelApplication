package com.sdy.luxurytravelapplication.mvp.model.bean

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json
import java.io.Serializable


/**
 * Created by chenxz on 2018/4/21.
 */
//data class HttpResult<T>(@Json(name = "data") val data: T,
//                         @Json(name = "errorCode") val errorCode: Int,
//                         @Json(name = "errorMsg") val errorMsg: String)

/**
 * 注册信息表
 */
data class RegisterFileBean(
    var people_amount: Int = 0,
    var supplement: Int = 0,//补充资料 1 前置 2后置 3 关闭
    var threshold: Boolean = false,//门槛开关 开启true 关闭false
    var living_btn: Boolean = false,//活体认证的性别判断  true去认证，false不去认证
    var tourists: Boolean = false,//	游客模式 开启true 关闭false
    var experience_state: Boolean = true,//	体验券状态 true开 flase 关闭
    var region: Int = 0  //2为海外模式

)


data class LoginBean(
    var accid: String = "",
    var extra_data: ExtraData = ExtraData(),
    var info_check: Boolean = false,
    var phone_check: Boolean = false,
    var qn_prefix: List<String> = listOf(),
    var qntk: String = "",
    var register: Boolean = false,
    var token: String = "",
    var userinfo: Userinfo = Userinfo()
)


data class LoginOffCauseBean(
    var descr: String = "",
    var list: MutableList<String> = mutableListOf()
)

data class ExtraData(
    var birth: Int = 0,
    var city_name: String = "",
    var im_token: String = "",
    var isvip: Boolean = false,
    var living_btn: Boolean = false,
    var supplement: Int = 0,
    var threshold: Boolean = false,
    var invite_code: Boolean = false,
    var inviter_data_code: String = "",
    var tourists: Boolean = false
)

data class Userinfo(
    var avatar: String = "",
    var birth: Int = 0,
    var gender: Int = 0,
    var nickname: String = ""
)

/**
 * 设置个人信息后的数据
 */
data class SetPersonalBean(
    var city_name: String = "",
    var gender_str: String = "",
    var people_amount: Int = 0,
    var avatar: String = "",
    var nickname: String = "",
    var gender: Int = 0,
    var birth: Int = 0,
    var force_vip: Boolean = false,
    var threshold: Boolean = false,
    var living_btn: Boolean = false,//  true  需要活体   false  不需要活体
    var isvip: Boolean = false,
    var share_btn: String = ""//分享开关是否显示
)



data class MediaBean(
    val id :Int = 0,
    val fileType: TYPE = TYPE.IMAGE,
    var filePath: Any = "",
    var fileName: String = "",
    var thumbnail: String = "",
    var duration: Int = 0,
    var size: Long = 0L,
    var ischecked: Boolean = false,
    var width: Int = 0,
    var height: Int = 0
) {
    enum class TYPE { IMAGE, VIDEO }

}

data class ContactInfo(val id: String, val name: String, val phone: String)

/************发布相关*************/
data class PublishWayBean(var checked: Boolean, var normalIcon: Int, var checkedIcon: Int)




data class SquareBean(
    var accid: String = "",
    var audio_json: ArrayList<VideoJson> = arrayListOf(),
    var avatar: String = "",
    var city_name: String = "",
    var comment_cnt: Int = 0,
    var cover_url: String = "",
    var create_time: String = "",
    var descr: String = "",
    var id: Int = 0,
    var iscollected: Boolean = false,
    var isfocused: Boolean = false,
    var originalFocus: Boolean = false,
    var isvip: Boolean = false,
    var isliked: Boolean = false,
    var istalk_btn: Boolean = false, //为True才显示  为false不显示
    var like_cnt: Int = 0,
    var gender: Int = 0,
    var clickCnt: Int = 0,
    var nickname: String = "",
    var out_time: String = "",
    var photo_json: ArrayList<VideoJson> = arrayListOf(),
    var province_name: String = "",
    var puber_address: String = "",
    var share_cnt: Int = 0,
    var type: Int = TEXT,
    var lastZanTime: Long = 0L,
    var lastFocusTime: Long = 0L,
    var video_json: ArrayList<VideoJson> = arrayListOf()
) : Serializable {
    companion object {
        const val TEXT = 0
        const val PIC = 1
        const val VIDEO = 2
        const val AUDIO = 3
        const val OFFICIAL_NOTICE = 4
    }
}

data class VideoJson(
    val duration: Int = 0,
    val url: String = "",
    var width: Float = 0f,
    var height: Float = 0f,
    var leftTime: Int = 0//倒计时剩下的时间
) : Serializable


/**
 * 聊天获取用户信息
 */
data class ChatInfoBean(
    var accid: String = "",
    var age: Int = 0,
    var approve_chat_times: Int = 0,
    var avatar: String = "",
    var gender: Int = 0,
    var height: String = "",
    var isfriend: Boolean = false,
    var isliked: Boolean = false,
    var islimit: Boolean = false, //为true才限制,为false就让他发
    var mv_url: Boolean = false,
    var my_isfaced: Boolean = false,
    var my_face_state: Int = 0,//  0 未认证  1认证   2审核中
    var mv_url_state: Int = 0,//  0 未认证  1认证   2审核中
    var nickname: String = "",
    var has_face_url: Boolean = false,
    var normal_chat_times: Int = 0,
    var online_time: String = "",
    var photo: List<String> = listOf(),
    var profession: String = "",
    var chat_expend_aomount: Int = 0,  //聊天消耗金币数目
    var residue_msg_cnt: Int = 0,
    var stared: Boolean = false,
    var istalk_btn: Boolean = false,//是否显示闪聊按钮
    var weight: String = ""
)
/**
 * 发送消息tip
 */
data class SendMsgBean(
    val deadline: Int, //金币过期时间
    val expend_coin: Int,//金币数目
    var noticeText: String = "",//提示语
    var noticeType: Int = 0//提示类型
)


data class FocusBean(
    val avatar: String,
    val accid: String,
    val love_each: Boolean, //是否相互喜欢
    val content: String,
    val title: String
)


data class GetGiftBean(
    var list: List<SendGiftBean> = listOf(),
    var my_coin_amount: Int = 0
)


data class CheckGreetBean(
    var contact_way: String = "",
    var gift_list: ArrayList<SendGiftBean> = arrayListOf()
)


//礼物对象
data class SendGiftBean(
    var icon: String = "",
    var title: String = "",
    var amount: Int = 0,
    var order_id: Int = 0,
    var checked: Boolean = false,
    var id: Int = 0,
    var status: Int = 0
)




data class ChargeWayBeans(
    val icon_list: MutableList<VipDescr>? = mutableListOf(),
    val list: MutableList<ChargeWayBean>? = mutableListOf(),//会员按月购买
    val pt_icon_list: MutableList<VipDescr>? = mutableListOf(),
    val direct_icon_list: MutableList<VipDescr>? = mutableListOf(),
    val pt_list: MutableList<ChargeWayBean>? = mutableListOf(),//会员按月购买
    val direct_list: MutableList<ChargeWayBean>? = mutableListOf(),//会员按月购买
    val paylist: MutableList<PaywayBean>? = mutableListOf(),
    val isvip: Boolean = false,
    val mycandy_amount: Int = 0,
    val vip_express: String = "",
    val first_recharge: String = "",
    val threshold_btn: Boolean = false,
    val isdirect: Boolean = false,
    val direct_cnt: Int = 0,
    val same_sex_cnt: Int = 0,
    val direct_vip_express: String = "",
    val isplatinum: Boolean = false,
    val platinum_save_str: String = "",
    val direct_save_str: String = "",
    val platinum_vip_express: String = "",
    val experience_title: String = "",
    val experience_amount: String = "",
    val experience_time: String = ""
)

data class VipPowerBean(
    val icon_list: MutableList<VipDescr>? = mutableListOf(),
    val isplatinum: Boolean = false,
    val platinum_vip_express: String = "",
    val platinum_save_str: String = "",
    var type: Int = TYPE_PT_VIP,
    val list: MutableList<ChargeWayBean> = mutableListOf(),
    val payway: MutableList<PaywayBean> = mutableListOf()

) {
    companion object {
        const val TYPE_PT_VIP = 1
        const val TYPE_GOLD_VIP = 0
    }
}


data class ChargeWayBean(
    var is_promote: Boolean = false,
    val ename: String? = "",
    val id: Int = 0,
    var discount_price: String = "0",
    var original_price: String = "0",
    val title: String? = "",
    val giving_amount: Int = 0,
    val giving_gold_day: String = "",
    val descr: String? = "",//限时折扣文案
    var type: Int?,//	1 原价售卖 2折扣价售卖 3限时折扣
    var unit_price: String = "",//单价(显示)
    var amount: Int = 0,
    var isfirst: Boolean = false,//是否首充  true  首充   false  常规
    var product_id: String = "",
    var checked: Boolean = false
)


data class PaywayBean(
    val comments: String = "",
    val id: Int = 0,
    val payment_type: Int = 0//支付类型 1支付宝 2微信支付 3余额支付
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(comments)
        parcel.writeInt(id)
        parcel.writeInt(payment_type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PaywayBean> {
        override fun createFromParcel(parcel: Parcel): PaywayBean {
            return PaywayBean(parcel)
        }

        override fun newArray(size: Int): Array<PaywayBean?> {
            return arrayOfNulls(size)
        }
    }
}

//vip权益描述广告
data class VipDescr(
    val rule: String = "",
    val title: String = "",
    val url: String = "",
    val icon_vip: String = "",
    var countdown: Int = 0,
    var id: Int = 0,
    val title_pay: String = ""
)
