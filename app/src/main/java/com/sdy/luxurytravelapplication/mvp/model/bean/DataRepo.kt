package com.sdy.luxurytravelapplication.mvp.model.bean

import com.squareup.moshi.Json


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


