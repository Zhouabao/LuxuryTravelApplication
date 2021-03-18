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



