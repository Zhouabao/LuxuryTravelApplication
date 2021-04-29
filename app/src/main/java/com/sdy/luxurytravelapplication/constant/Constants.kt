package com.sdy.luxurytravelapplication.constant

/**
 * Created by chenxz on 2018/4/21.
 */

object Constants {

    //测试
//    const val BASE_URL = "http://testppsns.talkdating.cn/ppsns/"
    const val BASE_URL = "http://testslapp.talkdating.cn/ppsns/"
    const val NIM_APP_KEY = "789f6cf551ec789fbe39b0a4ea68c951"//测试1

    //  生产
//    const val BASE_URL = "https://slapp.talkdating.cn/"
//    const val NIM_APP_KEY = "8556d54f7f6f3453efd7713dba23f852"//正式

    //是否处于测试环境
    const val TEST = true


    const val END_BASE_URL = "/v1.json"
    const val END_BASE_URL_v2 = "/v2.json"
    const val PRIVACY_URL = "${BASE_URL}protocol/privacyProtocol${END_BASE_URL}"//
    const val PROTOCOL_URL = "${BASE_URL}protocol/userProtocol${END_BASE_URL}"//

    //    ppns/文件类型名/用户ID/当前时间戳/16位随机字符串
    const val FILE_NAME_INDEX = "ppsns/"

    //上传文件的类型
    const val AVATOR = "avator/" //头像
    const val PUBLISH = "publish/" //发布
    const val DATING = "dating/" //约会
    const val IDVERIFY = "idverify/" //身份认证
    const val CHATREPORT = "chatreport/" //聊天举报
    const val USERCENTER = "usecenter/"//个人中心
    const val REPORTUSER = "report/"//舉報用户
    const val CANDYPRODUCT = "candyproduct/"//舉報用户
    const val CHATCHECK = "chatcheck/"//聊天上传内容
    const val FEEDBACK = "feedback/"//问题反馈
    const val VIDEOFACE = "verifyVideo/"//认证视频
    const val DEFAULT_AVATAR = "/meta/default_avatar.jpg"//默认图
    const val DEFAULT_EMPTY_AVATAR = "/ppsns_default_avatar.png"//默认头像
    const val SWEETHEART: String = "sweetheart/"
    const val CACHE_DIR = "/luxurytravelapplication"//缓存文件夹

    /*spkey*/
    const val LOGIN_KEY = "login"
    const val USERNAME_KEY = "username"
    const val PASSWORD_KEY = "password"
    const val HAS_NETWORK_KEY = "has_network"

    //pagesize
    const val PAGESIZE = 15
    const val MAX_VOICE_DURATION = 60
    const val EXTRA_RESULT_ITEMS = "extra_result_items"


    //bugly
    const val BUGLY_ID = "d0f305ba40"


    //微信支付appid
    const val WECHAT_APP_ID = "wx4b574e4037526791"
    const val WECHAT_APP_KEY = "44ffcfa1e1044f2267c873eb3329fbe2"


    //QQ
    const val QQ_APP_KEY = "1111700988"
    const val QQ_APP_SECRET = "ftpBAnnSsXMAnZtf"


    //闪验
    const val SY_APP_ID = "ucYrKKKC"

    const val SPNAME = "TravelSp"


    //官方助手id
    const val ASSISTANT_ACCID = "yapp_01"


    //百度人脸认证
    var licenseID = "youwan-application-face-android"
    var licenseFileName = "idl-license.face-android"
}
