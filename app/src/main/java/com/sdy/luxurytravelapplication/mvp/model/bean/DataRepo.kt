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


data class MyTapsBean(
    var child: ArrayList<String> = arrayListOf(),
    var `field`: String = "",
    var title: String = ""

)

data class ExtraData(
    var birth: Int = 0,
    var city_name: String = "",
    var im_token: String = "",
    var isvip: Boolean = false,
    var living_btn: Boolean = false,//  true  需要活体   false  不需要活体
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
    var nickname: String = "",
    val accid: String = "",
    val allvisit: Int = 0,
    val face_audit_state: Int? = 0,
    val isvip: Boolean = false,
    val isplatinum: Boolean = false,
    val isdirectvip: Boolean = false,
    val todayvisit: Int = 0,
    val vip_express: String = "",
    val platinum_vip_express: String = "",
    var isfaced: Int = -1,//   0 未认证 1通过 2机审中 3人审中 4被拒（弹框）
    var contact_way: Int = 0,//  联系方式  0  没有 1 电话 2微信 3 qq
    var mv_faced: Int = 0,//      0 没有视频/拒绝   1视频通过  2视频审核中
    var identification: Int = 0,// int（认证分数）
    var percent_complete: Int = 0,// float（百分比例如 80.99）
    var intention: LabelQualityBean? = null, //意向
    var my_candy_amount: Int = 0,
    var my_candy_amount_str: String = ""
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


data class SquareListBean(
    var list: MutableList<SquareBean> = mutableListOf()
)

data class VideoJson(
    val duration: Int = 0,
    val url: String = "",
    var width: Float = 0f,
    var height: Float = 0f,
    var leftTime: Int = 0//倒计时剩下的时间
) : Serializable


data class ChatGiftStateBean(
    var cate_type: Int = 0,//cate_type  1 礼物  2助力
    var id: Int = 0,
    var state: Int = 0//state  2领取  3过期
)

/**
 * 聊天获取用户信息
 */
data class ChatInfoBean(
    var approve_time: Long = 0L,
    var avatar: String = "",
    var isfriend: Boolean = false,
    var islimit: Boolean = false,
    var normal_chat_times: Int = 0,
    var residue_msg_cnt: Int = 0,//剩余可发送的招呼消息次数
    var matching_content: String = "",
    var matching_icon: String = "",
    var approve_chat_times: Int = 0,//认证后可聊天的人次数
    var mv_state: Int = 0,//视频介绍0 没有 1 通过  2审核中
    var my_gender: Int = 0,
    var my_isfaced: Boolean = false,
    var square_cnt: Int = 0,
    var stared: Boolean = false,
    var target_gender: Int = 0,
    var target_isfaced: Boolean = false,
    var both_gift_list: MutableList<ChatGiftStateBean> = mutableListOf(),
    var is_send_msg: Boolean = false,
    var chatup_amount: Int = 0,//搭讪支付的旅券
    var lockbtn: Boolean = false,//	true 弹出解锁聊天 false不弹出
    var force_isvip: Boolean = false,
    var chat_expend_amount: Int = 0,//1要显示 2不显示
    var chat_expend_time: Long = 0L,
    var plat_cnt: Int = 0,//剩余的搭讪的次数
    var isplatinum: Boolean = false,
    var private_chat_state: Boolean = false,
    var isdirect: Boolean = false,
    var unlock_contact_way: Int = 0, //是否有联系方式
    var unlock_popup_str: String = "", //	我是否被别人解锁弹框 大于0 弹框显示旅券数目
    var is_unlock_contact: Boolean = false,//是否解锁过联系方式
    var target_ishoney: Boolean = false//	true 是甜心圈 fals 不是甜心圈

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
    var list: List<GiftBean> = listOf(),
    var my_coin_amount: Int = 0
)


data class CheckGreetBean(
    var contact_way: String = "",
    var gift_list: ArrayList<GiftBean> = arrayListOf()
)

data class SendGiftOrderBean(
    var order_id: Int = 0,
    var amount: Int = 0,
    var ret_tips_arr: MutableList<SendTipBean> = mutableListOf()
)


//礼物对象
data class GiftBean(
    var amount: Int = 0,
    var count: Int = 0,
    var icon: String = "",
    var id: Int = 0,
    var min_amount: Int = 0,
    var title: String = "",
    var cnt: Int = 0,
    var checked: Boolean = false
)

//本地发送的tip内容
data class SendTipBean(
    var content: String = "",
    var ifSendUserShow: Boolean = false,
    var showType: Int = 0
)

data class VipPurchaseBean(
    val icon_list: MutableList<VipDescr> = mutableListOf(),
    val list: MutableList<ChargeWayBean> = mutableListOf(),//会员按月购买
    val paylist: MutableList<PaywayBean> = mutableListOf(),
    val isvip: Boolean = false,
    val vip_express: String = "",
    val vip_save_str: String = ""
)

data class ChargeWayBeans(
    val icon_list: MutableList<VipDescr> = mutableListOf(),
    val list: MutableList<ChargeWayBean> = mutableListOf(),//会员按月购买
    val pt_icon_list: MutableList<VipDescr> = mutableListOf(),
    val direct_icon_list: MutableList<VipDescr> = mutableListOf(),
    val pt_list: MutableList<ChargeWayBean> = mutableListOf(),//会员按月购买
    val direct_list: MutableList<ChargeWayBean> = mutableListOf(),//会员按月购买
    val paylist: MutableList<PaywayBean> = mutableListOf(),
    val isvip: Boolean = false,
    val mycandy_amount: Int = 0,
    val vip_express: String = "",
    val first_recharge: String = "",
    val descr: String = "",
    val threshold_btn: Boolean = false,
    val isdirect: Boolean = false,
    val direct_cnt: Int = 0,
    val same_sex_cnt: Int = 0,
    val invite_code_all: Int = 0,
    val invite_code_residue: Int = 0,
    val direct_vip_express: String = "",
    val isplatinum: Boolean = false,
    val platinum_save_str: String = "",
    val direct_save_str: String = "",
    val platinum_vip_express: String = "",
    val experience_title: String = "",
    val experience_amount: String = "",
    val experience_time: String = ""
)


data class PayBean(
    val order_id: String? = "",
    val otn: String? = "",
    val wechat: Wechat? = Wechat(),
    val reqstr: String
)

data class Wechat(
    val `package`: String? = "",
    val appid: String? = "",
    val noncestr: String? = "",
    val partnerid: String? = "",
    val prepayid: String? = "",
    val sign: String? = "",
    val timestamp: String? = ""
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
        const val TYPE_GOLD_VIP = 0
        const val TYPE_PT_VIP = 1
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
    val assets_audit_state: Int = 0,//学历认证 1没有 2认证中 3认证通过
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
    var content_type: Int = 0,//1文本 2语音
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
    var rise_province: String = "",

    var detail_address: String = "",
    var duration: Int = 0
) : Serializable

data class BannerGuideBean(
    val image: Any,
    val title: String,
    val descr: String,
    val fileName: String = "",
    val imageName: String = ""
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
    var contact_way: Int = 0, //联系方式  0  没有 1 电话 2微信 3 qq
    var mv_btn: Boolean = false, //是否有视频
    var mv_detail_url: String = "",
    var mv_url: String = "",
    var mv_faced: Boolean = false,
    var photos: ArrayList<String> = arrayListOf(),
    var sign: String = "",
    var constellation: String = "",
    var isfriend: Int = 0,
    var isblock: Int = 1,//1 互相没有拉黑  2 我拉黑了他  3  ta拉黑了我   4 互相拉黑
    var mycandy_amount: Int = 0,
    var personal_info: MutableList<DetailUserInfoBean> = mutableListOf(),
    var birth: Int = 0,
    var assets_audit_way: Int = 0,
    val assets_audit_descr: String = "",
    var face_type: Int = 0,//	0没有认证 1活体 2 真人 3 颜值 4奢旅
    var approve_square_id: Int = 0,//满足是face_type 为4的时侯可以查看动态详情
    var dating: TravelPlanBean? = null,
    var gift_list: MutableList<GiftBean> = mutableListOf()
)

/*用戶相冊*/
data class UserPhotoBean(
    var checked: Boolean,
    var avatar: String,
    var isVideo: Boolean = false,
    var mv_detail_url: String = "" //针对视频
)


data class LabelQuality(
    var icon: String = "",
    var icon2: String = "",
    var title: String = ""
) : Serializable


/*查询礼物领取状态*/
data class GiftStateBean(
    var amount: Int = 0,
    var account_candy: Int = 0,
    var icon: String = "",
    var state: Int = 0,
    var title: String = ""
)


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
    var avatar: String = "",
    var content: String = "",
    var content_type: Int = 0,//1文本 2 语音
    var dating_title: String = "",
    var detail_address: String = "",
    var duration: Int = 0,
    var goal_city: String = "",
    var goal_province: String = "",
    var id: Int = 0,
    var rise_city: String = "",
    var rise_province: String = ""
)


/**
 * 首页数据
 */
data class IndexBean(
    var assets_audit_way: Int = 0,//0 不是甜心圈 1 资产认证 2豪车认证 3身材 4职业  5高额充值
    var contact_way: Int = 0,//	0没有留下联系方式 1 电话 2 微信 3 qq 99隐藏
    var face_type: Int = 0,  //	0没有认证 1活体 2 真人 3 颜值 4奢旅
    var accid: String = "",
    var age: Int = 0,
    var assets_audit_descr: String = "",
    var avatar: String = "",
    var constellation: String = "",
    var dating_content: String = "",
    var dating_title: String = "",
    var detail_address: String = "",
    var direct_vip_expire: Int = 0,
    var distance: String = "",
    var gender: Int = 0,
    var invitation_id: Int = 0,
    var isfaced: Int = 0,
    var isfriend: Boolean = false,
    var ismv: Int = 0,
    var isplatinumvip: Boolean = false,
    var isdirectvip: Boolean = false,
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
    var want: MutableList<String> = mutableListOf<String>()
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
    var want_step_man_pull: Boolean = false,


    val is_honey: Boolean = false,
    val progress: SweetProgressBean = SweetProgressBean()
)


data class MessageGiftBean(
    var mid: String = "",
    var id: Int = 0,
    var state: Int = 0////state  2领取  3过期
)

data class AccostListBean(var list: MutableList<AccostBean> = mutableListOf())
data class AccostBean(
    var accid: String = "",
    var avatar: String = "",
    var icon: String = "",
    var gender: Int = 0,
    var unreadCnt: Int = 0,
    var time: Long = 0L,
    var nickname: String = "",
    var content: String = ""
)

data class MessageListBean(
    var title: String = "",
    var msg: String = "",
    var count: Int = 0,
    var time: String = "",
    var icon: Int = 0,
    var id: String = ""
)

/*************消息列表**************/
data class MessageListBean1(
    var square_count: Int = 0,//评论未读数
    var square_type: Int = 0,// 1广场点赞 2评论我的 3为我评论点赞的 4@我的列表
    var square_time: String = "",
    var square_nickname: String = "",
    var chatup_list_lasttime: String = "",
    var session_list_arr: MutableList<MessageGiftBean> = mutableListOf(),
    var chatup_rid_list: MutableList<String> = mutableListOf(), //要剔除的id
    var chatup_list: MutableList<AccostBean> = mutableListOf()
)

/**
 * 广场消息列表
 */
data class SquareMsgBean(
    var accid: String = "",
    var avatar: String = "",
    var category: Int = 0,
    var click_cnt: Int = 0,
    var content: String = "",
    var cover_url: String = "",
    var create_time: String = "",
    var descr: String = "",
    var id: Int = 0,
    var is_read: Boolean = false,
    var msg_id: Int = 0,
    var status: Int = 0,
    var amount: Int = 0,
    var nickname: String = "",
    var type: Int = 0
)


/**
 * 举报对象
 */
data class ReportBean(var reason: String, var checked: Boolean)

/**
 * 认证资料
 */
data class UploadInfoBean(
    var defaultIcon: Int = -1,
    var chooseIcon: String = "",
    var duration: Int = 0,
    var height: Int = 0,
    var width: Int = 0,
    var requestCode: Int = -1
)

/**
 * 提现成功bean
 */
data class WithDrawSuccessBean(
    var candy_amount: Int = 0,
    var create_tme: String = "",
    var money_amount: Float = 0F,
    var trade_no: String = ""
)

/**
 * 拉起提现bean
 */
data class PullWithdrawBean(
    var alipay: Alipay? = null,
    var has_unread: Boolean = false,
    var is_withdraw: Boolean = false,//是否可以提现 true 可以 false 不可以
    var candy_amount: Int = 0,
    var red_balance_money: Float = 0.0F,
    var money_amount: Float = 0.0F
)

data class Alipay(
    var ali_account: String = "",
    var nickname: String = "",
    var wth_account: String = "",
    var account_descr: String = "",
    var phone: String = ""
) : Serializable


data class ContactWayBean(
    var contact_way: Int = 0,
    var contact_way_str: String = "",
    var contact_way_content: String = "",
    var contact_way_hide: Int = 0
)


data class UserInfoBean(
    val mytags_count: Int = 0,//兴趣个数 *    desc   : 个人中心请求model
    val label_quality: MutableList<LabelQuality> = mutableListOf(),//展示的兴趣
    val userinfo: Userinfo? = null,
    val sign: String? = "",
    val power_url: String = "",
    val platinum_vip_str: String = "",
    val red_packet_btn: Boolean = false,
    val hide_distance: Boolean = false,//（true开启隐藏  false  关闭隐藏）
    val hide_book: Boolean = false,//（ true 屏蔽通讯录     false  关闭隐藏通讯录）
    val greet_status: Boolean = false,//true 开启招呼认证 false关闭招呼认证
    val threshold_btn: Boolean = false,//门槛是否开启
    val free_show: Boolean = false,//  true（显示）  false(模糊)
    val vip_descr: MutableList<VipDescr>? = mutableListOf(),//会员权益描述
    val platinum_vip_descr: MutableList<VipDescr>? = mutableListOf(),//高级会员权益描述
    var visitlist: MutableList<String> = mutableListOf()//看过我的头像列表
)


/**
 * 交易流水对象
 */
data class BillBean(
    var affect_candy: Int = 0,
    var create_time: String = "",
    var intro: String = "",
    var type_title: String = "",
    var id: Int = 0,
    var info: String = "",
    var icon: String = "",
    var type: Int = 0
)

data class ContactDataBean(
    var list: MutableList<ContactBean>? = mutableListOf(),
    var asterisk: MutableList<ContactBean>? = mutableListOf()
)

data class ContactBean(
    var nickname: String = "",
    var stared: Boolean = false,
    var accid: String = "",
    var avatar: String = "",
    var member_level: Int = 0,
    var index: String = ""
//    var index: String? = Cn2Spell.getPinYinFirstLetter(nickname).toUpperCase()
)

data class BlackBean(
    val accid: String = "",
    val age: Int = 0,
    val avatar: String = "",
    val constellation: String = "",
    val gender: Int = 0,
    val isvip: Int = 0,
    val nickname: String = ""
)


//设置中心开关
data class SettingsBean(
    var hide_distance: Boolean = false,//（true开启隐藏  false  关闭隐藏）
    var hide_book: Boolean = false,//（ true 屏蔽通讯录     false  关闭隐藏通讯录）
    val greet_switch: Boolean = false,//true 开启招呼 false关闭招呼
    val sms_state: Boolean = false,//true 开启短信通知 false关闭短信通知
    val greet_status: Boolean = false,//true 开启招呼认证 false关闭招呼认证
    val notify_square_like_state: Boolean = true,//true 开启招呼认证 false关闭招呼认证
    val notify_square_comment_state: Boolean = true,//true 开启招呼认证 false关闭招呼认证
    val wechat_tem_state: Boolean = false,//true 是否开启推送模板消息 true false
    val we_openid: Boolean = false,//true 	是否绑定公众号 true绑定 false 没有绑定
    val invisible_state: StateBean = StateBean(),// 选中的隐身状态
    val invisible_list: MutableList<StateBean> = mutableListOf(),// 1 不隐身 2 离线时间隐身 3 一直隐身
    val private_chat_state: StateBean = StateBean(),//选中的私聊权限
    val private_chat_list: MutableList<StateBean> = mutableListOf()// 1 不隐身 2 离线时间隐身 3 一直隐身
)

data class QRCodeBean(val url: String)

data class StateBean(
    var id: Int = 0,
    var title: String = ""
) : IPickerViewData {
    override fun getPickerViewText(): String {
        return title
    }
}

data class VersionBean(val version: String)

/**
 * 账户信息
 */
data class AccountBean(
    var phone: String = "",
    var wechat: String = ""
)


data class WechatNameBean(
    var nickname: String = ""
)


/**
 * 注销原因
 */
data class LoginOffCauseBeans(
    var descr: String = "",
    var list: MutableList<String> = mutableListOf()
)


data class RegisterTooManyBean(val countdown_time: Int = 0)


/**
 * 手机区号bean
 */
data class CountryCodeBean(
    var code: Int = 0,
    var en: String = "",
    var locale: String = "",
    var pinyin: String = "",
    var sc: String = "",
    var tc: String = "",
    var index: String = ""
)


data class MyCommentList(
    val list: MutableList<MyCommentBean> = mutableListOf()
)

/**
 * 访客
 */
data class VisitorBean(
    var accid: String = "",
    var age: Int = 0,
    var avatar: String = "",
    var constellation: String = "",
    var distance: String = "",
    var gender: Int = 0,
    var height: String = "",
    var isvip: Int = 0,
    var member_id: Int = 0,
    var nickname: String = "",
    var visitcount: Int = 0,
    var weight: String = ""
)

data class MyCommentBean(
    var avatar: String = "",
    var content: String = "",
    var cover_url: String = "",
    var create_time: String = "",
    var id: Int = 0,
    var nickname: String = "",
    var reply_content: String = "",
    var replyed_nickname: String = "",
    var square_descr: String = "",
    var square_id: Int = 0,
    var type: Int = 0,
    var accid: String = ""
)


/**
 * 个人中心信息
 */
data class UserInfoSettingBean(
    var answer_list: MutableList<AnswerBean> = mutableListOf(),
    var avatar: String = "",
    var birth: String = "",
    var constellation: String = "",
    var face_state: Boolean = false,
    var gender: Int = 0,
    var mv_faced: Int = 0, //新增字段 认证状态 0 未认证且无视频 1 认证通过的 2 认证中 3认证不通过-需要更换头像认证
    var job: String = "",
    var nickname: String = "",
    var photos: MutableList<String> = mutableListOf(),
    var photos_wall: MutableList<MyPhotoBean> = mutableListOf(),
    var qiniu_domain: String = "",
    var score_rule: ScoreRule = ScoreRule(),
    var sign: String = ""
)


data class AnswerBean(
    var child: MutableList<FindTagBean> = mutableListOf(),
    var find_tag: FindTagBean? = null,
    var id: Int = 0,
    var title: String = "",
    var point: Int = 0,
    var descr: String = ""
)

data class FindTagBean(
    var id: Int = -1,
    var title: String = ""
) : IPickerViewData {
    override fun getPickerViewText(): String {
        return title
    }
}

data class ScoreRule(
    var about: Int = 0,
    var base: Int = 0,
    var base_total: Int = 0,
    var me: Int = 0,
    var photo: Int = 0,
    var total: Int = 0
)


/**
 * 照片墙
 */
data class MyPhotoBean(
    var has_face: Int = 0,//1 没有 2 有
    var id: Int = 0,
    var url: String = "",
    var photoScore: Int = 0,
    override var itemType: Int = PHOTO
) : MultiItemEntity {
    companion object {
        const val COVER = 1
        const val PHOTO = 2
    }
}


data class MoreMatchBean(
    var nickname: String = "",
    var gender: Int = 0,
    var birth: Int = 0,
    var avatar: String = "",
    var threshold: Boolean = false,
    var living_btn: Boolean = false,//  true  需要活体   false  不需要活体
    var isvip: Boolean = false,
    var people_amount: Int = 0,
    var share_btn: String = ""//分享开关是否显示
) : Serializable


data class VideoVerifyBannerBean(
    var content: String = "",
    var title: String = "",
    var icon: Int = 0,
    var id: Int = 0
) : Serializable

data class CopyMvBean(
    val mv_url: String = "",
    val mv_url_cover: String = "",
    var list: MutableList<VideoVerifyBannerBean> = mutableListOf()
) : Serializable

/*我的邀请记录*/
data class MyInviteBean(
    var all_cnt: Int = 0,
    var coin_all_amount: Int = 0,
    var invite_list: MutableList<Invite> = mutableListOf(),
    var now_amount: Int = 0,
    var invite_rule: String = "",
    var invite_url: String = "",
    var invite_title: String = "",
    var invite_descr: String = "",
    var invite_pic: String = "",
    var residue_cnt: Int = 0
)

data class Invite(
    var accid: String = "",
    var avatar: String = ""
)


/**
 * 旅券解锁验证
 */
data class UnlockCheckBean(
    var isnew_friend: Boolean = false,
    var isplatinumvip: Boolean = false,
    var mv_url: String = "",
    var amount: Int = 0
)


/*验证报名旅行*/
data class CheckPublishDatingBean(
    var is_publish: Boolean = false,
    var dating_amount: Int = 0,//报名邀约旅券数
    var isplatinum: Boolean = false,//是否高级会员 true 是 false 不是
    var private_chat: Boolean = false,//该邀约是否设置高级会员访问 true设置 false没有设置
    var residue_cnt: Int = 0,//	是否剩余免费次数
    val datingId: Int,
    val content: String,
    val icon: String
)

/*报名旅行计划*/
data class ApplyDatingBean(
    val datingId: Int,
    val title: String,
    val content: String,
    val icon: String
)

data class UnlockBean(
    var isnew_friend: Boolean = false,
    var contact_way: Int,
    var contact_content: String
)

data class ChatUpBean(
    var chat_amount: Int = 0,
    var plat_cnt: Int = 0,
    var online: Boolean = false,
    var contact_amount: Int = 0,
    var direct_residue_cnt: Int = 0,
    var contact: String = "",
    var contact_way: Int = 0,//联系方式 int 0 没有 1 手机 2微信 3 qq
    var avatar: String = "",
    var isplatinum: Boolean = false,
    var ishoney: Boolean = false,
    var isdirect: Boolean = false, //是否是直连卡
    var vip_normal_cnt: Int = 0, //开通vip获得的聊天次数
    var private_chat_btn: Boolean = false  //


)

