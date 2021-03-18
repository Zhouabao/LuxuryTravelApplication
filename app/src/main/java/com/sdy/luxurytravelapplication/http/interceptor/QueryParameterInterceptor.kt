package com.sdy.luxurytravelapplication.http.interceptor

import com.blankj.utilcode.util.DeviceUtils
import com.cxz.wanandroid.constant.Constants
import com.sdy.luxurytravelapplication.ext.Preference
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * @author chenxz
 * @date 2018/9/26
 * @desc QueryParameterInterceptor 设置公共参数
 */
class QueryParameterInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val request: Request
        val modifiedUrl = originalRequest.url.newBuilder()
                // Provide your custom parameter here
                .addQueryParameter("accid", Preference(Constants.ACCID,"").name)
                .addQueryParameter("token", Preference(Constants.TOKEN,"").name)
                .addQueryParameter("_timestamp", "${System.currentTimeMillis()}")
                .addQueryParameter("device_id", DeviceUtils.getUniqueDeviceId())
                .addQueryParameter("city_name", Preference(Constants.CITY,"").name)
                .addQueryParameter("province_name", Preference(Constants.PROVINCE,"").name)
                .addQueryParameter("lat", Preference(Constants.LAT,"").name)
                .addQueryParameter("lng", Preference(Constants.LNG,"").name)
                .build()
        request = originalRequest.newBuilder().url(modifiedUrl).build()
        return chain.proceed(request)
    }
}