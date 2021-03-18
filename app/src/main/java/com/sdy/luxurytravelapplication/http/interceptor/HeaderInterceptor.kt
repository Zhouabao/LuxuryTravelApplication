package com.sdy.luxurytravelapplication.http.interceptor

import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.cxz.wanandroid.constant.Constants
import com.sdy.luxurytravelapplication.app.TravelApp
import com.sdy.luxurytravelapplication.ext.Preference
import com.sdy.luxurytravelapplication.utils.ChannelUtils
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author chenxz
 * @date 2018/9/26
 * @desc HeaderInterceptor: 设置请求头
 */
class HeaderInterceptor : Interceptor {

    /**
     * token
     */
    private var token: String by Preference(Constants.TOKEN, "")

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        val builder = request.newBuilder()

        builder.addHeader("Content-type", "application/json; charset=utf-8")
            .addHeader("os","android")
            .addHeader("mac",DeviceUtils.getUniqueDeviceId())
            .addHeader("machine","${DeviceUtils.getManufacturer()},${DeviceUtils.getModel()}")
            .addHeader("app-vrn",AppUtils.getAppVersionName())
            .addHeader("chnl",ChannelUtils.getChannel(TravelApp.context))

        return chain.proceed(builder.build())
    }

}