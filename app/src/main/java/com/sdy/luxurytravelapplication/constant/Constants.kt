package com.sdy.luxurytravelapplication.constant

/**
 * Created by chenxz on 2018/4/21.
 */

object Constants {
    //测试
    const val BASE_URL = "http://testxapp.talkdating.cn/api/"
    //  生产
//    const val BASE_URL = "https://xapp.talkdating.cn/api/"

    const val END_BASE_URL = "/v1.json"




    const val BUGLY_ID = "76e2b2867d"


    //云信key
    const val NIM_APP_KEY = "789f6cf551ec789fbe39b0a4ea68c951"//测试1
//    const val NIM_APP_KEY = "8556d54f7f6f3453efd7713dba23f852"//正式

    //是否处于测试环境
    const val TEST = true



    //微信支付appid
    const val WECHAT_APP_ID = "wx60458b95420980eb"
    const val WECHAT_APP_KEY = "5f170ce153408bd24ce5e51562dcca11"


    //新浪
    const val SINA_APP_KEY = "3040898942"
    const val SINA_APP_SECRET = "a2e38436e5c4f3bd60ec44c5236a525e"

    //QQ
    const val QQ_APP_KEY = "1110939011"
    const val QQ_APP_SECRET = "F9vVAJooQZ96Zdqw"
    //bugly
    const val BUGLY_APP_ID = "25933c5127"
    //声网
    const val AGORA_APP_ID = "7d9f7f306ee44de4a168d2979e12330e"
    //闪验
    const val SY_APP_ID = "ENbQ6DUs"

    const val SPNAME = "SweetDateSp"

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

    const val CACHE_DIR = "/datingapplication"//缓存文件夹

    const val PRIVACY_URL = "https://xapp.talkdating.cn/index/Protocol/privacyProtocol.html"//
    const val PROTOCOL_URL = "https://xapp.talkdating.cn/index/Protocol/userProtocol.html"//




    /*spkey*/
    const val LOGIN_KEY = "login"
    const val USERNAME_KEY = "username"
    const val PASSWORD_KEY = "password"
    const val HAS_NETWORK_KEY = "has_network"


    //pagesize
    const val PAGESIZE = 15
    const val MAX_VOICE_DURATION = 60
}
