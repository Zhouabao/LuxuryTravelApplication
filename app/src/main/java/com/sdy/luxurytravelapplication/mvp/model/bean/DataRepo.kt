package com.sdy.luxurytravelapplication.mvp.model.bean

import android.os.Parcel
import android.os.Parcelable
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.contrarywind.interfaces.IPickerViewData
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
    val id: Int = 0,
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
    var isdirectvip: Boolean = false,    //是否铂金会员 true是 false不是
    var isplatinumvip: Boolean = false,    //是否铂金会员 true是 false不是
    var isvip: Int = 0,//是否会员 1是 0 不是
    var accid: String = "",
    var audio_json: MutableList<VideoJson>?,
    var avatar: String = "",
    var city_name: String = "",
    var comment_cnt: Int = 0,
    var create_time: String = "",
    var descr: String? = "",
    var id: Int?,
    var title_id: Int?,
    var isliked: Int = 0,
    var iscollected: Int?,//0没收藏 1收藏
    var like_cnt: Int = 0,
    var member_level: Int?,
    var nickname: String?,
    var out_time: String?,
    var puber_address: String?,
    var photo_json: MutableList<VideoJson>?,
    var province_name: String?,
    var share_cnt: Int?,
    var tag_id: Int?,
    var title: String?,
    var cover_url: String?,
    var tags: String?,
    var video_json: MutableList<VideoJson>?,
    var isfriend: Boolean = true,
    var greet_switch: Boolean = true,//接收招呼开关   true  接收招呼      false   不接受招呼
    var greet_state: Boolean = true,// 认证招呼开关   true  开启认证      flase   不开启认证
    var icon: String = "",
    var isPlayAudio: Int = 0, //0未播放  1 播放中 2暂停  3 停止
    var comment: String = "",
    val distance: String = "",
    var link_url: String?,
    var type: Int = 1,
    var category_type: Int = 1,
    var duration: Long = 0L,
    var clickTime: Int = 0,
    var gender: Int = 2,
    var originalLike: Int = 0,
    var originalLikeCount: Int = 0,
    var isgreeted: Boolean = true,//招呼是否仍然有效
    var member_id: Int? = null,
    var title_list: MutableList<TopicBean>? = mutableListOf(),
    var approve_type: Int = 0,// 0普通 1资产认证 2豪车认证 3 身材认证 4 职业认证  5高额充值
    val assets_audit_descr: String = "",
    var issweet: Boolean = true//是否是甜心圈
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


data class IndexListBean(
    var list: MutableList<IndexTopBean> = mutableListOf(),
    var dating_list: MutableList<String> = mutableListOf(),
    var free_show: Boolean = false,//是否免费查看 true 免费 false 不能查看
    var gender: Int = 0,//我的性别
    var isplatinumvip: Boolean = false,//我是否 钻石会员 true 是 false不是
    var mv_url: Boolean = false,//我是否有视频
    var today_exposure_cnt: Int = 0,//总到访
    var today_visit_cnt: Int = 0,//今日来访
    var total_exposure_cnt: Int = 0,//today_exposure_cnt
    var total_visit_cnt: Int = 0//总曝光
) : Serializable


/**
 * 开屏页推荐
 */
data class IndexTopBean(
    var amount: Int = 0,
    var isplatinum: Boolean = false,
    var type: Int = 1,
    var accid: String = "",
    var age: Int = 0,
    var avatar: String = "",
    var distance: String = "",
    var gender: Int = 0,
    var nickname: String = "",
    var source_type: Int = 0 //1视频 2图片, override val itemType: Int
) : MultiItemEntity, Serializable {
    override val itemType: Int
        get() = type
}

/**
 * 甜心圈进度
 */
data class SweetProgressBean(
    var now_money: String = "0",
    var normal_money: String = "0",
    val gender: Int = 0,
    val assets_audit_state: Int = 0,//学历认证1没有 2认证中 3认证通过
    val img: String = "",
    val female_mv_state: Int = 0//女性视频认证 1没有通过 2审核中 3视频认证通过
) : Serializable


data class SweetUploadBean(
    val defacultIcon: Int = 0,
    val type: Int = 0,
    var url: String = "",
    var width: Int = 0,
    var height: Int = 0
)


data class RecommendSquareBean(
    var accid: String = "",
    var avatar: String = "",
    var comment_cnt: Int = 0,
    var cover_url: String = "",
    var descr: String = "",
    var distance: String = "",
    var gender: Int = 0,
    var id: Int = 0,
    var title_id: Int = 0,
    var isliked: Boolean = false,
    var originalLike: Boolean = false,
    var like_cnt: Int = 0,
    var originalLikeCount: Int = 0,
    var nickname: String = "",
    var title: String = "",
    var type: Int = 0,
    var height: Int = 0,
    var width: Int = 0,
    var is_elite: Boolean = false,
    var approve_type: Int = 0,//新增字段 ：0普通动态 1 资产 2豪车 3身材 4职业
    var assets_audit_descr: String = "",
    var title_list: MutableList<TopicBean>? = mutableListOf()
)

data class TopicBean(
    var icon: String = "",
    var id: Int = 0,
    var son: MutableList<SquarePicBean> = mutableListOf(),
    var tag_id: Int = 0,
    var tag_title: String = "",
    var title: String = "",
    var used_cnt: Int = 0,
    var visit_cnt: Int = 0
) : Serializable

data class SquarePicBean(
    var cover_url: String = "",
    var square_id: Int = 0,
    var id: Int = 0,
    var descr: String = ""
) : Serializable

data class RecommendSquareListBean(
    var banner: MutableList<SquareBannerBean> = mutableListOf(),
    var list: MutableList<RecommendSquareBean> = mutableListOf()
)

/**
 * 广场运营位广告
 */
data class SquareBannerBean(
    var adv_type: Int = 0,//类型 广告类型默认1   1.只是展示图  2.跳转外连  3.内部跳转   4发布+话题 5发布+兴趣
    var cover_url: String = "",
    var id: Int = 0,
    var link_url: String = "",
    var title: String = "",
    var icon: String = "",
    val used_cnt: Int = 0,
    val visit_cnt: Int = 0
)


/**
 * 广场兴趣列表
 */
data class SquareTagBean(
    var icon: String = "",
    var id: Int = 0,
    var is_hot: Boolean = false,
    var is_join: Boolean = false,
    var cover_list: MutableList<SquarePicBean> = mutableListOf(),
    var place_type: Int = 0,//位置类型 0 没有操作 1置顶 2置底
    var title: String = ""
)

data class TagSquareListBean(
    var banner: SquareBannerBean,
    var list: MutableList<RecommendSquareBean> = mutableListOf()
)

/**
 * 评论数据
 */
data class CommentBean(
    var avatar: String? = null,
    var content: String? = null,
    var create_time: String? = null,
    var id: Int? = 0,
    var isliked: Int? = 0,
    var like_count: Int? = 0,
    var member_accid: String? = null,
    var reply_content: String? = null,
    var reply_count: Int? = 0,
    var reply_id: Int? = 0,
    var replyed_nickname: String? = null,
    var nickname: String? = null,
    var square_id: Int? = 0,
    var type: Int = 1, //1数据  0标题
    override var itemType: Int = type
) : MultiItemEntity {
    companion object {
        const val TITLE = 0
        const val CONTENT = 1
    }

}

data class AllCommentBean(
    var hotlist: MutableList<CommentBean>?,
    var list: MutableList<CommentBean>?
)


/**
 * 媒体文件的参数
 */
data class MediaParamBean(
    var url: String = "",
    var duration: Int = 0,
    var width: Int = 0,
    var height: Int = 0
)


/**
 * 兴趣特质
 */
data class LabelQualityBean(
    var content: String = "",
    var icon: String = "",
    var id: Int = 0,
    var title: String = "",
    var cheked: Boolean = false,
    var isfuse: Boolean = false,
    var outtime: Boolean = false//过期标志
) : Serializable, IPickerViewData {
    override fun getPickerViewText(): String {
        return content
    }
}

data class LabelQualitysBean(
    var roll_list: MutableList<LabelQualityBean> = mutableListOf(),
    var list: MutableList<LabelQualityBean> = mutableListOf(),
    var has_button: Boolean = false
)

data class ChooseTitleBean(
    val limit_cnt: Int?,
    val list: MutableList<LabelQualityBean>?
)

data class TravelCityBean(
    val city_name: String = "",
    val city_cnt: Int = 0,
    val city_image: String = "",
    var checked: Boolean = false
)

data class TravelPlanBean(
    var accid: String = "",
    var apply_cnt: Int = 0,
    var avatar: String = "",
    var content: String = "",
    var content_type: String = "0",
    var cost_money: String = "",
    var cost_type: String = "",
    var create_time: String = "",
    var date: String = "",
    var dating_target: String = "",
    var dating_title: String = "",
    var goal_city: String = "",
    var goal_province: String = "",
    var id: Int = 0,
    var like_cnt: Int = 0,
    var nickname: String = "",
    var purpose: String = "",
    var rise_city: String = "",
    var rise_province: String = ""
) : Serializable

data class BannerGuideBean(
    val image: Any,
    val title: String,
    val descr: String
)

data class PlanOptionsBean(
    var cost_money: List<String> = listOf(),
    var cost_type: List<String> = listOf(),
    var dating_target: List<String> = listOf(),
    var purpose: List<String> = listOf()
)


data class ProviceBean(
    var child: List<ProviceBean> = listOf(),
    var code: String = "",
    var name: String = ""
) : IPickerViewData {
    override fun getPickerViewText(): String {
        return name
    }
}

/**
 * 匹配用户
 */
data class MatchBean(
    var intention_icon: String = "",
    var isvip: Int = 0,    //是否会员 true是 false不是
    var isdirectvip: Boolean = false,    //是否铂金会员 true是 false不是
    var isplatinumvip: Boolean = false,    //是否铂金会员 true是 false不是
    var myisplatinumvip: Boolean = false,    //是否铂金会员 true是 false不是
    var isfaced: Int = 0,  //0未认证/认证不成功     1认证通过     2认证中
    var accid: String = "",
    var age: Int = 0,
    var avatar: String = "",
    var distance: String = "",
    var face_str: String = "",
    var online_time: String = "",
    var gender: Int = 0,
    var nickname: String = "",
    var contact_way: Int = 0,
    var mv_btn: Boolean = false, //是否有视频
    var photos: ArrayList<String> = arrayListOf(),
    var sign: String = "",
    var constellation: String = "",
    var isfriend: Int = 0,
    var isblock: Int = 1,//1 互相没有拉黑  2 我拉黑了他  3  ta拉黑了我   4 互相拉黑
    var mycandy_amount: Int = 0,
    var need_notice: Boolean = true,
    var intention_title: String = "",
    var personal_info: MutableList<DetailUserInfoBean> = mutableListOf(),
    var birth: Int = 0,
    var assets_audit_way: Int = 0,
    val assets_audit_descr: String = "",
    var approve_square_id: Int = 0,
    var mv_url: String = "",
    var mv_faced: Boolean = false,
    var dating: DatingBean? = null,

    var gift_list: MutableList<GiftBean> = mutableListOf(),
    var label_quality: MutableList<LabelQuality> = mutableListOf()

)

data class LabelQuality(
    var icon: String = "",
    var icon2: String = "",
    var title: String = ""
) : Serializable


/**
 * 礼物
 */
data class GiftBean(
    var amount: Int = 0,
    var count: Int = 0,
    var icon: String = "",
    var id: Int = 0,
    var min_amount: Int = 0,
    var title: String = "",
    var cnt: Int = 0,
    var checked: Boolean = false
) : Serializable


data class DetailUserInfoBean(
    var icon: String = "",
    var title: String = "",
    var content: String = ""
)

/**
 * 广场兴趣列表
 */
data class DatingBean(
    var accid: String = "",
    var apply_cnt: Int = 0,
    var city_name: String = "",
    var content: String = "",
    var icon: String = "",
    var duration: Int = 0,
    var content_type: Int = 0,
    var cost_money: String = "",
    var cost_type: String = "",
    var dating_distance: String = "",
    var private_chat_state: String = "",
    var dating_target: String = "",
    var dating_title: String = "",
    var distance: String = "",
    var follow_up: String = "",
    var gender: Int = 0,
    var isplatinumvip: Boolean = false,
    var like_cnt: Int = 0,
    var temp_like_cnt: Int = like_cnt,
    var nickname: String = "",
    var place: String = "",
    var province_name: String = "",
    var title: String = "",
    var avatar: String = "",
    var id: Int = 0,
    var is_hot: Boolean = false,
    var type: Int = TYPE_WOMAN,

    var dating_type: Int = 0,
    var isliked: Boolean = false,
    var online_time: String = "",
    var tempLike: Boolean = isliked,
    var ranking_level: Int = 0,
    var show_type: Boolean = false,
    override var itemType: Int = if (show_type) {
        TYPE_WOMAN
    } else {
        TYPE_MAN
    }
) : MultiItemEntity {
    companion object {
        const val TYPE_WOMAN = 1
        const val TYPE_MAN = 2
    }
}


/**
 * 首页数据
 */
data class IndexBean(
    var accid: String = "",
    var age: Int = 0,
    var assets_audit_descr: String = "",
    var assets_audit_way: Int = 0,//0 不是甜心圈 1 资产认证 2豪车认证 3身材 4职业  5高额充值
    var avatar: String = "",
    var constellation: String = "",
    var contact_way: Int = 0,//	0没有留下联系方式 1 电话 2 微信 3 qq 99隐藏
    var dating_title: String = "",
    var direct_vip_expire: Int = 0,
    var distance: String = "",
    var face_str: String = "",
    var gender: Int = 0,
    var hb_time: Int = 0,
    var intention_icon: String = "",
    var intention_title: String = "",
    var invitation_id: Int = 0,
    var is_platinum: Int = 0,
    var isdirectvip: Boolean = false,
    var isfaced: Int = 0,
    var isfriend: Boolean = false,
    var ismv: Int = 0,
    var isplatinumvip: Boolean = false,
    var isvip: Boolean = false,
    var member_level: Int = 0,
    var mv_btn: Boolean = false,
    var mv_faced: Boolean = false,
    var mv_url: String = "",
    var mv_url_state: Int = 0,
    var nickname: String = "",
    var online_time: String = "",
    var platinum_vip_expire: Int = 0,
    var private_chat_state: Boolean = false,
    var ranking_level: Int = 0,
    var sign: String = "",
    var title: String = "",
    var want: List<String> = listOf()
)

data class IndexRecommendBean(
    var complete_percent: Int = 0,
    var complete_percent_normal: Int = 0,
    var has_face_url: Boolean = false,
    var is_human: Boolean = false,
    var isfaced: Int = 0,
    var isplatinum: Boolean = false,
    var isvip: Boolean = false,
    var list: MutableList<IndexBean> = mutableListOf(),
    var motion: Int = 0,
    var my_candy_amount: Int = 0,
    var my_mv_url: Boolean = false,
    var ranking_level: Int = 0,
    var today_pull_dating: Boolean = false,
    var want_step_man_pull: Boolean = false
)

