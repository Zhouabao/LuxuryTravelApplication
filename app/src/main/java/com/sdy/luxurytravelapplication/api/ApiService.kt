package com.sdy.luxurytravelapplication.api

import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.mvp.model.bean.*
import io.reactivex.Observable
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Created by chenxz on 2018/4/21.
 */
interface ApiService {

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
    @POST("OpenApi/LoginOrAlloc${Constants.END_BASE_URL}")
    fun loginOrAlloc(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<LoginBean>>



    /**
     * 注册填写个人信息
     */
    @FormUrlEncoded
    @POST("MemberInfo/setProfileCandy${Constants.END_BASE_URL}")
    fun setPersonal(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<SetPersonalBean>>




    /**
     * 门槛支付列表
     */
    @FormUrlEncoded
    @POST("PayOrder/getThresholdEnd${Constants.END_BASE_URL}")
    fun getThreshold(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<ChargeWayBeans>>



    /**
     * 推荐10个
     */
    @FormUrlEncoded
    @POST("Home/topList${Constants.END_BASE_URL}")
    fun indexTop(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<IndexListBean>>




    /**
     * 广场点赞/取消点赞
     */
    @FormUrlEncoded
    @POST("square/squareLikes${Constants.END_BASE_URL}")
    fun getSquareLike(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 推荐广场
     *
     */
    @FormUrlEncoded
    @POST("Square/squareEliteList${Constants.END_BASE_URL}")
    fun squareEliteList(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<RecommendSquareListBean?>>


    /**
     * 广场获取兴趣
     *
     */
    @FormUrlEncoded
    @POST("Square/squareTagList${Constants.END_BASE_URL}")
    fun squareTagList(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<MutableList<SquareTagBean>?>>




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
     * 获取个人积分，需要登录后访问
     * https://www.wanandroid.com/lg/coin/userinfo/json
     */
    @GET("/lg/coin/userinfo/json")
    fun getUserInfo(): Observable<BaseResp<Any>>


    /**
     * 微信绑定
     */
    @POST("Account/bundSocial${Constants.END_BASE_URL}")
    @FormUrlEncoded
    fun bundSocial(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 微信绑定
     */
    @POST("Account/bundSocial${Constants.END_BASE_URL}")
    @FormUrlEncoded
    fun register(@FieldMap params: HashMap<String, String>): Observable<BaseResp<Any>>

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
    @POST("Tidings/getTargetInfo${Constants.END_BASE_URL}")
    fun getTargetInfo(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<ChatInfoBean>>

    /**
     * 发送消息
     */
    @FormUrlEncoded
    @POST("Tidings/sendMsg${Constants.END_BASE_URL}")
    fun sendMsg(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<SendMsgBean>>

    /**
     * 发送消息给小助手
     */
    @FormUrlEncoded
    @POST("Tidings/aideSendMsg${Constants.END_BASE_URL}")
    fun aideSendMsg(@FieldMap params: HashMap<String, Any>): Observable<BaseResp<Any>>


    /**
     * 获取聊天🎁列表
     */
    @FormUrlEncoded
    @POST("Gifts/getGiftList${Constants.END_BASE_URL}")
    fun getGiftList(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<GetGiftBean>>
  /*
    * 领取礼物
    */
    @FormUrlEncoded
    @POST("Gifts/getGift${Constants.END_BASE_URL}")
    fun getGift(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<CheckGreetBean>>


    /**
     * 拒绝领取礼物
     */
    @FormUrlEncoded
    @POST("Gifts/refundGift${Constants.END_BASE_URL}")
    fun refundGift(@FieldMap params: MutableMap<String, Any>): Observable<BaseResp<Any>>




}