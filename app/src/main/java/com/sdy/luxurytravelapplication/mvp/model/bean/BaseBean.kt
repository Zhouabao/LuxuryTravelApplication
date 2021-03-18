package com.sdy.luxurytravelapplication.mvp.model.bean

import com.squareup.moshi.Json

/**
 * @author admin
 * @date 2018/11/21
 * @desc
 */
open class BaseBean {
    var code: Int = 0
    var msg: String = ""
}
data class BaseResp<T>(@Json(name = "data") val data: T) : BaseBean()
