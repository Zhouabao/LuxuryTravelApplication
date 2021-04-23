package com.sdy.luxurytravelapplication.api

import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.mvp.model.bean.*
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by chenxz on 2018/4/21.
 */
interface ApiService {


    /*************注册登录付费****************/
    /**
     * 获取登录配置开关信息
     */
    @POST("OpenApi/getVersion${Constants.END_BASE_URL}")
    fun getRegisterProcessType(): Observable<BaseResp<RegisterFileBean>>


    /**
     * 注册发送短信
     * scene :场景 register 注册/登录 change 变更 cancel注销
     * phone :电话号码
     */
    @FormUrlEncoded
    @POST("OpenApi/SendSms${Constants.END_BASE_URL}")
    fun sendSms(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 注册
     */
    @FormUrlEncoded
    @POST("OpenApi/LoginOrAlloc${Constants.END_BASE_URL_v2}")
    fun loginOrAlloc(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<LoginBean>>



    /**
     * 男性进入首页后获取想要的列表
     */
    @FormUrlEncoded
    @POST("Home/getManTaps${Constants.END_BASE_URL_v2}")
    fun getManTaps(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<MutableList<MyTapsBean>?>>



    /**
     * 注销账号
     */
    @FormUrlEncoded
    @POST("Account/cancelAccount${Constants.END_BASE_URL}")
    fun cancelAccount(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 退出登录
     * http://www.wanandroid.com/user/logout/json
     */
    @GET("user/logout/json")
    fun logout(): Observable<BaseResp<Any>>


    /**
     * 注册填写个人信息
     */
    @FormUrlEncoded
    @POST("MemberInfo/setProfileCandy${Constants.END_BASE_URL_v2}")
    fun setPersonal(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<SetPersonalBean>>


    /**
     * 验证邀请码
     */
    @FormUrlEncoded
    @POST("Home/checkCode${Constants.END_BASE_URL_v2}")
    fun checkCode(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 推荐10个
     */
    @FormUrlEncoded
    @POST("Home/topList${Constants.END_BASE_URL}")
    fun indexTop(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<IndexListBean>>


    /**
     * 每天首次开屏推荐（男性）
     */
    @FormUrlEncoded
    @POST("Home/todayRecommend${Constants.END_BASE_URL}")
    fun todayRecommend(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<TodayFateBean?>>


    /**
     * 批量送礼物成为好友
     */
    @FormUrlEncoded
    @POST("Home/batchChatup${Constants.END_BASE_URL}")
    fun batchGreetWoman(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<MutableList<BatchGreetBean>?>>


    /**
     * 首页推荐
     */
    @FormUrlEncoded
    @POST("Home/recommendIndex${Constants.END_BASE_URL_v2}")
    fun recommendIndex(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<IndexRecommendBean?>>


    /**
     * 首页同城页面
     */
    @FormUrlEncoded
    @POST("Home/theSameCity${Constants.END_BASE_URL_v2}")
    fun theSameCity(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<IndexRecommendBean?>>

    /**
     * 首页奢旅圈
     */
    @FormUrlEncoded
    @POST("Sweetheart/indexListV5${Constants.END_BASE_URL_v2}")
    fun sweetheart(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<IndexRecommendBean?>>


    /**
     * 对方个人页数据
     */
    @FormUrlEncoded
    @POST("MemberInfo/userInfoCandy${Constants.END_BASE_URL_v2}")
    fun getMatchUserInfo(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<MatchBean?>>


    /**
     * 获取用户广场列表
     */
    @FormUrlEncoded
    @POST("square/someoneSquareCandy${Constants.END_BASE_URL}")
    fun someoneSquareCandy(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<RecommendSquareListBean?>>


    /**
     * 拉黑用户
     */
    @FormUrlEncoded
    @POST("StrageBlock/blockMember${Constants.END_BASE_URL}")
    fun shieldingFriend(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 解除拉黑
     */
    @FormUrlEncoded
    @POST("StrageBlock/removeBlock${Constants.END_BASE_URL}")
    fun removeBlock(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 解除匹配
     */
    @FormUrlEncoded
    @POST("relationship/dissolutionFriend${Constants.END_BASE_URL}")
    fun dissolutionFriend(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 获取消息总的个数汇总数据
     */
    @FormUrlEncoded
    @POST("Index/msgListend${Constants.END_BASE_URL}")
    fun msgList(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<AllMsgCount>>

    /**
     * 启动统计
     */
    @FormUrlEncoded
    @POST("MemberInfo/startupRecord${Constants.END_BASE_URL}")
    fun startupRecord(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>



    /*****************************广场**********************************/

    /**
     * 推荐广场
     *
     */
    @FormUrlEncoded
    @POST("Square/squareEliteList${Constants.END_BASE_URL_v2}")
    fun squareEliteList(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<RecommendSquareListBean?>>


    /**
     * 附近动态
     *
     */
    @FormUrlEncoded
    @POST("Square/squareNearly${Constants.END_BASE_URL_v2}")
    fun squareNearly(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<RecommendSquareListBean?>>


    /**
     * 最新动态
     *
     */
    @FormUrlEncoded
    @POST("Square/squareNewestLists${Constants.END_BASE_URL_v2}")
    fun squareNewestLists(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<RecommendSquareListBean?>>


    /**
     * 广场点赞/取消点赞
     */
    @FormUrlEncoded
    @POST("square/squareLikes${Constants.END_BASE_URL}")
    fun getSquareLike(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 广场获取兴趣
     *
     */
    @FormUrlEncoded
    @POST("Square/squareTopicList${Constants.END_BASE_URL_v2}")
    fun squareTopicList(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<MutableList<SquareTagBean>?>>


    /**
     * 兴趣广场详情列表
     */
    @FormUrlEncoded
    @POST("Square/squareTagInfo${Constants.END_BASE_URL}")
    fun squareTagInfo(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<TagSquareListBean?>>


    /**
     * 发布动态验证是否被禁封
     */
    @FormUrlEncoded
    @POST("Square/checkBlock${Constants.END_BASE_URL}")
    fun checkBlock(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<Any?>>


    /**
     * 获取某一广场详情
     */
    @FormUrlEncoded
    @POST("square/squareInfoV13${Constants.END_BASE_URL}")
    fun getSquareInfo(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<SquareBean?>>


    /**
     * 分享成功调用
     */
    @FormUrlEncoded
    @POST("square/addShare${Constants.END_BASE_URL}")
    fun addShare(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 广场评论
     */
    @FormUrlEncoded
    @POST("square/addComment${Constants.END_BASE_URL}")
    fun addComment(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 广场收藏
     */
    @FormUrlEncoded
    @POST("square/squareCollect${Constants.END_BASE_URL}")
    fun getSquareCollect(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 广场举报
     */
    @FormUrlEncoded
    @POST("square/squareReport${Constants.END_BASE_URL}")
    fun getSquareReport(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 获取广场的评论列表
     */
    @FormUrlEncoded
    @POST("square/commentLists${Constants.END_BASE_URL}")
    fun getCommentLists(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<AllCommentBean?>>

    /**
     * 删除评论
     */
    @FormUrlEncoded
    @POST("square/destoryComment${Constants.END_BASE_URL}")
    fun destoryComment(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 删除我的动态
     */
    @FormUrlEncoded
    @POST("square/removeMySquare${Constants.END_BASE_URL}")
    fun removeMySquare(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>

    /**
     * 评论点赞
     */
    @FormUrlEncoded
    @POST("square/commentLikes${Constants.END_BASE_URL}")
    fun commentLikes(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 评论举报
     */
    @FormUrlEncoded
    @POST("square/replyReport${Constants.END_BASE_URL}")
    fun commentReport(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>

    /**
     * 广场发布
     */
    @FormUrlEncoded
    @POST("square/announceV13${Constants.END_BASE_URL}")
    fun squareAnnounce(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 获取所有的标题
     */
    @FormUrlEncoded
    @POST("Tags/getTagTitleListV13${Constants.END_BASE_URL}")
    fun getTagTitleList(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<ChooseTitleBean>>


    /****************************伴游****************************/

    /**
     * 验证是否可以发布旅行计划
     */
    @FormUrlEncoded
    @POST("Travel/checkPlan${Constants.END_BASE_URL_v2}")
    fun checkPlan(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<CheckPublishDatingBean?>>


    /**
     * 计划列表
     */
    @FormUrlEncoded
    @POST("Travel/planList${Constants.END_BASE_URL_v2}")
    fun planList(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<MutableList<TravelPlanBean>?>>


    /**
     * 旅行计划详情
     */
    @FormUrlEncoded
    @POST("Travel/planInfo${Constants.END_BASE_URL_v2}")
    fun planInfo(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<TravelPlanBean?>>


    /**

    旅行计划 发布选项
     */
    @FormUrlEncoded
    @POST("Travel/planOptions${Constants.END_BASE_URL_v2}")
    fun planOptions(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<PlanOptionsBean?>>


    /**
     * 发布旅行计划
     */
    @FormUrlEncoded
    @POST("Travel/issuePlan${Constants.END_BASE_URL_v2}")
    fun issuePlan(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>



    /**
     * 验证旅行报名
     */
    @FormUrlEncoded
    @POST("Dating/checkDatingapply${Constants.END_BASE_URL}")
    fun checkDatingapply(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<CheckPublishDatingBean?>>

    /**
     * 报名旅行
     */
    @FormUrlEncoded
    @POST("Dating/datingApply${Constants.END_BASE_URL}")
    fun datingApply(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<ApplyDatingBean?>>


    /**
     * 旅行计划的 选城市列表
     */
    @FormUrlEncoded
    @POST("Travel/getMenuList${Constants.END_BASE_URL_v2}")
    fun getMenuList(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<MutableList<TravelCityBean>?>>


    /******************************消息中心***********************************/

    /**
     * 所有的消息列表
     */
    @FormUrlEncoded
    @POST("Tidings/messageCensuscandyend${Constants.END_BASE_URL}")
    fun messageCensus(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<MessageListBean1?>>


    @FormUrlEncoded
    @POST("Tidings/chatupList${Constants.END_BASE_URL}")
    fun chatupList(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<AccostListBean?>>

    /**
     * 删除搭讪
     */
    @FormUrlEncoded
    @POST("Ticket/delChat${Constants.END_BASE_URL}")
    fun delChat(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 广场消息列表
     */
    @FormUrlEncoded
    @POST("tidings/squareListsEnd${Constants.END_BASE_URL}")
    fun squareLists(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<MutableList<SquareMsgBean>>>

    /**
     * 标记广场消息已读
     * （type  1点赞   2评论）
     */
    @FormUrlEncoded
    @POST("Tidings/markSquareRead${Constants.END_BASE_URL}")
    fun markSquareRead(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 删除广场消息
     */
    @FormUrlEncoded
    @POST("Tidings/delSquareMsg${Constants.END_BASE_URL}")
    fun delSquareMsg(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**********************消息中心****************************/

    /**
     * 解除匹配
     */
    @FormUrlEncoded
    @POST("Relationship/removeFriend${Constants.END_BASE_URL}")
    fun removeFriend(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>

    /**
     * 聊天界面添加好友
     */
    @FormUrlEncoded
    @POST("Relationship/addFriend${Constants.END_BASE_URL}")
    fun addFriend(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<Any>>

    /**
     * 添加星标好友
     */
    @FormUrlEncoded
    @POST("Relationship/addStarTarget${Constants.END_BASE_URL}")
    fun addStarTarget(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 移除星标好友
     */
    @FormUrlEncoded
    @POST("Relationship/removeStarTarget${Constants.END_BASE_URL}")
    fun removeStarTarget(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * type	1通话举报 2主页举报 3聊天内容举报 4广场动态举报 5广场评论举报
     * content  当type为3和5 为举报内容 为4 广场动态的id
     * photo 举报图片json串
     * case_type 返回举报类型【色情涉黄/广告或垃圾信息。。。】
     */
    @FormUrlEncoded
    @POST("Report/add${Constants.END_BASE_URL_v2}")
    fun addReport(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 获取举报理由
     */
    @POST("OpenApi/getReportMsg${Constants.END_BASE_URL_v2}")
    fun getReportMsg(): Observable<BaseResp<MutableList<String>>>



    /**
     * /ppsns/MemberInfo/saveChatupMsg/v1.json 保存搭讪消息
     */
    @FormUrlEncoded
    @POST("MemberInfo/saveChatupMsg${Constants.END_BASE_URL}")
    fun saveChatupMsg(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     *
     * 男性解锁搭讪聊天消息
     *
     */
    @FormUrlEncoded
    @POST("Ticket/lockChatup${Constants.END_BASE_URL}")
    fun lockChatup(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any?>>




    /*--------------------------------会员充值---------------------------------*/

    /**
     * 获取聊天🎁列表
     */
    @FormUrlEncoded
    @POST("Gift/getGiftList${Constants.END_BASE_URL}")
    fun getGiftList(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<GetGiftBean>>

    /*
      * 领取礼物
      */
    @FormUrlEncoded
    @POST("Gift/getGift${Constants.END_BASE_URL}")
    fun getGift(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<SendGiftOrderBean>>


    /**
     * 查询礼物领取状态
     * Gift/checkGiftState
     */
    @FormUrlEncoded
    @POST("Gift/checkGiftState${Constants.END_BASE_URL}")
    fun checkGiftState(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<GiftStateBean?>>


    /**
     * 赠送礼物
     */
    @FormUrlEncoded
    @POST("Gift/giveGift${Constants.END_BASE_URL}")
    fun giveGift(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<SendGiftOrderBean>>

    /**
     * 礼物发送成功 客户端回调上传绑定消息id
     */
    @FormUrlEncoded
    @POST("Gift/upGiftMsgId${Constants.END_BASE_URL}")
    fun upGiftMsgId(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>




    /**
     * 拒绝领取礼物
     */
    @FormUrlEncoded
    @POST("Gift/refundGift${Constants.END_BASE_URL}")
    fun refundGift(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 获取会员支付方式
     */
    @FormUrlEncoded
    @POST("pay_order/productLists${Constants.END_BASE_URL}")
    fun productLists(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<ChargeWayBeans?>>


    /**
     * 门槛支付列表
     */
    @FormUrlEncoded
    @POST("PayOrder/getThresholdEnd${Constants.END_BASE_URL_v2}")
    fun getThreshold(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<ChargeWayBeans>>


    /**
     * 获取订单信息
     */
    @FormUrlEncoded
    @POST("pay_order/createOrder${Constants.END_BASE_URL}")
    fun createOrder(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<PayBean>>

    /**
     * 充值价格列表
     */
    @FormUrlEncoded
    @POST("PayOrder/candyRechargeList${Constants.END_BASE_URL_v2}")
    fun candyRechargeList(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<ChargeWayBeans?>>


    /*******************************个人中心*****************************************/
    /**
     * 获取联系方式
     */
    @FormUrlEncoded
    @POST("MemberInfo/getContact${Constants.END_BASE_URL}")
    fun getContact(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<ContactWayBean?>>

    /**
     * 设置联系方式
     */
    @FormUrlEncoded
    @POST("MemberInfo/setContact${Constants.END_BASE_URL}")
    fun setContact(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any?>>

    /**
     * 个人中心
     */
    @FormUrlEncoded
    @POST("MemberInfo/myInfoCandyV231${Constants.END_BASE_URL}")
    fun myInfoCandy(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<UserInfoBean?>>


    /**
     * 我的点赞 我
     */
    @FormUrlEncoded
    @POST("square/aboutMeSquareV13${Constants.END_BASE_URL}")
    fun myCollectionAndLike(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<RecommendSquareListBean?>>


    /**
     * 我的动态
     */
    @FormUrlEncoded
    @POST("square/aboutMeSquareCandy${Constants.END_BASE_URL}")
    fun aboutMeSquareCandy(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<RecommendSquareListBean?>>


    /**
     * 我的计划列表
     */
    @FormUrlEncoded
    @POST("Travel/myPlan${Constants.END_BASE_URL_v2}")
    fun myPlan(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<MutableList<TravelPlanBean>?>>


    /**
     * 我的邀请记录
     */
    @FormUrlEncoded
    @POST("Invite/myInvite${Constants.END_BASE_URL_v2}")
    fun myInvite(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<MyInviteBean?>>

    /**
     * 流水记录
     */
    @FormUrlEncoded
    @POST("Candy/myBillList${Constants.END_BASE_URL}")
    fun myBillList(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<MutableList<BillBean>?>>

    /**
     * 拉起提现
     * Candy/pullWithdraw
     */
    @FormUrlEncoded
    @POST("Candy/myCandy${Constants.END_BASE_URL}")
    fun myCadny(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<PullWithdrawBean?>>


    /**
     * 提现
     */
    @FormUrlEncoded
    @POST("Candy/withdraw${Constants.END_BASE_URL}")
    fun withdraw(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<WithDrawSuccessBean?>>


    /**
     * 绑定支付宝账号
     * Candy/saveWithdrawAccount
     */
    @FormUrlEncoded
    @POST("Candy/saveWithdrawAccount${Constants.END_BASE_URL}")
    fun saveWithdrawAccount(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Alipay?>>


    /*******************************设置****************************/
    /**
     * 设置开关
     */
    @FormUrlEncoded
    @POST("MemberInfo/mySettings${Constants.END_BASE_URL}")
    fun mySettings(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<SettingsBean?>>


    /**
     * 设置用户的短信/隐身/私聊接收状态
     */
    @FormUrlEncoded
    @POST("UserSet/switchSet${Constants.END_BASE_URL}")
    fun switchSet(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 获取通讯录
     */
    @FormUrlEncoded
    @POST("relationship/getLists${Constants.END_BASE_URL}")
    fun getContactLists(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<ContactDataBean?>>


    /**
     * 获取黑名单
     */
    @FormUrlEncoded
    @POST("StrageBlock/blackList${Constants.END_BASE_URL}")
    fun myShieldingList(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<MutableList<BlackBean>?>>


    /**
     * 屏蔽通讯录
     */
    @FormUrlEncoded
    @POST("StrageBlock/blockedAddressBook${Constants.END_BASE_URL}")
    fun blockedAddressBook(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 屏蔽距离
     */
    @FormUrlEncoded
    @POST("StrageBlock/isHideDistance${Constants.END_BASE_URL}")
    fun isHideDistance(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<Any>>

    /**
     * 版本更新
     */
    @FormUrlEncoded
    @POST("OpenApi/getVersion${Constants.END_BASE_URL}")
    fun getVersion(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<VersionBean?>>


    /**
     * 获取用户的二维码照片
     */
    @FormUrlEncoded
    @POST("UserSet/getQrCode${Constants.END_BASE_URL}")
    fun getQrCode(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<QRCodeBean>>


    /**
     * 广场点赞评论提醒开关
     */
    @FormUrlEncoded
    @POST("Relationship/squareNotifySwitch${Constants.END_BASE_URL}")
    fun squareNotifySwitch(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 招呼的开关
     */
    @FormUrlEncoded
    @POST("Relationship/greetSwitch${Constants.END_BASE_URL}")
    fun greetSwitch(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<Any>>


    /*--------------------------------账号相关---------------------------------*/

    /**
     * 获取账号相关信息
     */
    @FormUrlEncoded
    @POST("Account/getAccountInfo${Constants.END_BASE_URL}")
    fun getAccountInfo(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<AccountBean>>


    /**
     * 更改手机号
     */
    @FormUrlEncoded
    @POST("Account/changeAccount${Constants.END_BASE_URL}")
    fun changeAccount(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 微信解绑
     */
    @FormUrlEncoded
    @POST("Account/unbundWeChat${Constants.END_BASE_URL}")
    fun unbundWeChat(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 微信绑定
     */
    @FormUrlEncoded
    @POST("Account/bundWeChat${Constants.END_BASE_URL}")
    fun bundWeChat(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<WechatNameBean>>


    /**
     * 发送短信验证码(新)
     */
    @FormUrlEncoded
    @POST("OpenApi/SendSms${Constants.END_BASE_URL}")
    fun sendSms(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<RegisterTooManyBean?>>

    /**
     * 注销原因(新)
     */
    @FormUrlEncoded
    @POST("Account/getCauseList${Constants.END_BASE_URL}")
    fun getCauseList(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<LoginOffCauseBeans>>


    /**
     * 我的评论
     */
    @FormUrlEncoded
    @POST("square/myCommentList${Constants.END_BASE_URL_v2}")
    fun myCommentList(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<MyCommentList?>>


    /**
     * 来访记录
     */
    @FormUrlEncoded
    @POST("MemberInfo/myVisitedList${Constants.END_BASE_URL_v2}")
    fun myVisitingList(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<MutableList<VisitorBean>?>>


    /**
     * 个人信息
     */
    @FormUrlEncoded
    @POST("MemberInfo/personalInfoCandy${Constants.END_BASE_URL_v2}")
    fun personalInfoCandy(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<UserInfoSettingBean?>>



    /**
     *   修改个人信息
     */
    @FormUrlEncoded
    @POST("MemberInfo/savePersonalCandy${Constants.END_BASE_URL_v2}")
    fun savePersonalCandy(@FieldMap params: MutableMap<String, Any?>?): Observable<BaseResp<Any>>


    /**
     * 单张相册上传
     */
    @FormUrlEncoded
    @POST("MemberInfo/addPhotoWall${Constants.END_BASE_URL}")
    fun addPhotoWall(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<MyPhotoBean?>>


    /**
     * 获取职业列表
     */
    @POST("OpenApi/getOccupationList${Constants.END_BASE_URL}")
    fun getOccupationList(): Observable<BaseResp<MutableList<String>?>>

    /**
     * 获取模板签名
     */
    @FormUrlEncoded
    @POST("OpenApi/getSignTemplate${Constants.END_BASE_URL}")
    fun getSignTemplate(
        @Field("page") page: Int,
        @Field("gender") gender: Int
    ): Observable<BaseResp<MutableList<LabelQualityBean>?>>


    /**
     * 上传视频介绍
     */
    @FormUrlEncoded
    @POST("Home/uploadMv${Constants.END_BASE_URL}")
    fun uploadMv(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>

    /**
     * 获取上传视频的标准视频
     */
    @FormUrlEncoded
    @POST("Home/normalMv${Constants.END_BASE_URL}")
    fun normalMv(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<CopyMvBean?>>


    /**
     * 动态喜欢（关注）
     * 	type：1关注（喜欢） 2取消关注（取消喜欢
     * 	target_accid：
     */
    @FormUrlEncoded
    @POST("MemberInfo/memberFocus${Constants.END_BASE_URL}")
    fun memberFocus(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<FocusBean>>


    /**
     * 聊天页个人信息和限制返回
     */
    @FormUrlEncoded
    @POST("MemberInfo/getTargetInfoCandyEnd${Constants.END_BASE_URL}")
    fun getTargetInfo(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<ChatInfoBean>>

    /**
     * 发送消息
     */
    @FormUrlEncoded
    @POST("Tidings/sendMsgV21${Constants.END_BASE_URL}")
    fun sendMsg(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<SendMsgBean>>

    /**
     * 发送消息给小助手
     */
    @FormUrlEncoded
    @POST("Tidings/aideSendMsg${Constants.END_BASE_URL}")
    fun aideSendMsg(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<Any>>



    /**
     * 验证解锁联系方式
     *
     */
    @FormUrlEncoded
    @POST("Home/checkContactV231${Constants.END_BASE_URL}")
    fun checkUnlockContact(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<ChatUpBean?>>


    /**
     * 验证解锁视频介绍
     */
    @FormUrlEncoded
    @POST("Candy/checkUnlockMvV231${Constants.END_BASE_URL}")
    fun checkUnlockMv(@FieldMap hashMapOf: HashMap<String, String>): Observable<BaseResp<UnlockCheckBean?>>


    /**
     * 解锁联系方式
     */
    @FormUrlEncoded
    @POST("Home/unlockContact${Constants.END_BASE_URL}")
    fun unlockContact(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<UnlockBean?>>


    /**
     * 解锁聊天
     */
    @FormUrlEncoded
    @POST("Home/unlockChat${Constants.END_BASE_URL}")
    fun unlockChat(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<UnlockBean?>>



    /**
     *
     * 验证解锁聊天
     *
     */
    @FormUrlEncoded
    @POST("Home/checkChatV231${Constants.END_BASE_URL}")
    fun checkChat(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<ChatUpBean?>>


    /**
     *
     *  标记获取过我的联系方式 弹出过了框
     * Home/tagUnlockPopup
     *
     */
    @FormUrlEncoded
    @POST("MemberInfo/tagUnlockPopup${Constants.END_BASE_URL}")
    fun tagUnlockPopup(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any?>>





    /**
     * 男性加入甜心圈
     *
     */
    @FormUrlEncoded
    @POST("Sweetheart/joinSweetApply${Constants.END_BASE_URL}")
    fun joinSweetApply(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 甜心圈上传认证
     *
     */
    @FormUrlEncoded
    @POST("Sweetheart/uploadData${Constants.END_BASE_URL_v2}")
    fun uploadData(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 上传图片模板
     *
     */
    @FormUrlEncoded
    @POST("Sweetheart/getPicTpl${Constants.END_BASE_URL}")
    fun getPicTpl(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<ArrayList<String>>>


    /**
     * 人工审核
     * 1 人工认证 2重传头像或则取消
     */
    @FormUrlEncoded
    @POST("member_info/humanAduit${Constants.END_BASE_URL}")
    fun humanAduit(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any?>>

}