package com.sdy.luxurytravelapplication.http.interceptor

import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.LogUtils
import com.cxz.wanandroid.constant.Constants
import com.qiniu.android.dns.util.MD5
import com.sdy.luxurytravelapplication.app.TravelApp
import com.sdy.luxurytravelapplication.ext.Preference
import com.sdy.luxurytravelapplication.utils.ChannelUtils
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response
import java.net.URLDecoder
import java.util.*

/**
 *    author : ZFM
 *    date   : 2021/3/1715:13
 *    desc   :
 *    version: 1.0
 */
class AddParameterInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder().addHeader("Content-type", "application/json; charset=utf-8")
            .addHeader("os", "android")
            .addHeader("mac", DeviceUtils.getUniqueDeviceId())
            .addHeader("machine", "${DeviceUtils.getManufacturer()},${DeviceUtils.getModel()}")
            .addHeader("app-vrn", AppUtils.getAppVersionName())
            .addHeader("chnl", ChannelUtils.getChannel(TravelApp.context))
            .build()

        if (request.method == "POST") {
            if (request.body is FormBody) {
                val bodyBuilder = FormBody.Builder()
                var formBody = request.body as FormBody
                //把所有的参数添加到新的构造器
                for (i in 0 until formBody.size) {
                    bodyBuilder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i))
                }
                val city_name by Preference(Constants.CITY, "")
                val province by Preference(Constants.PROVINCE, "")
                val accid by Preference(Constants.ACCID, "")
                val token by Preference(Constants.TOKEN, "")
                val lat by Preference(Constants.LAT, "")
                val lng by Preference(Constants.LNG, "")
                formBody = bodyBuilder.addEncoded("_timestamp","${ System.currentTimeMillis()}")
                    .addEncoded("accid", accid)
                    .addEncoded("token", token)
                    .addEncoded("device_id", DeviceUtils.getUniqueDeviceId())
                    .addEncoded("city_name", city_name)
                    .addEncoded("province_name", province)
                    .addEncoded("lat", lat)
                    .addEncoded("lng", lng)
                    .build()

                val bodyMap = hashMapOf<String, String>()
                val nameList = arrayListOf<String>()
                for (i in 0 until formBody.size) {
                    nameList.add(formBody.encodedName(i))
                    bodyMap[formBody.encodedName(i)] =
                        URLDecoder.decode(formBody.encodedValue(i), "UTF-8")
                }

                //按字母升序排序
                nameList.sort()
                val builder = StringBuilder().append("${AppUtils.getAppVersionName()}dcyfyf")
                nameList.forEach {
                    builder.append(it).append("=")
                        .append(URLDecoder.decode(bodyMap[it], "UTF-8"))
                        .append("&")
                }
                LogUtils.d(builder.toString())
                formBody = bodyBuilder.addEncoded(
                    "_signature",
                    MD5.encrypt(builder.substring(0, builder.length - 1))
                )
                    .build()

                request = request.newBuilder().post(formBody).build()
            }
        } else if (request.method == "GET") {
            var httpUrl = request.url
                .newBuilder()
                .addQueryParameter("accid", Preference(Constants.ACCID, "").name)
                .addQueryParameter("token", Preference(Constants.TOKEN, "").name)
                .addQueryParameter("_timestamp", "${System.currentTimeMillis()}")
                .addQueryParameter("device_id", DeviceUtils.getUniqueDeviceId())
                .addQueryParameter("city_name", Preference(Constants.CITY, "").name)
                .addQueryParameter("province_name", Preference(Constants.PROVINCE, "").name)
                .addQueryParameter("lat", Preference(Constants.LAT, "").name)
                .addQueryParameter("lng", Preference(Constants.LNG, "").name)
                .build()

            val nameSet = httpUrl.queryParameterNames
            val nameList = arrayListOf<String>()
            nameList.addAll(nameSet)
            nameList.sort()
            val buffer = StringBuffer().append("${AppUtils.getAppVersionName()}dcyfyf")
            nameList.forEach {
                buffer.append("&").append(it).append("=")
                    .append(
                        if (httpUrl.queryParameterValues(it).isNullOrEmpty()) "" else
                            httpUrl.queryParameterValues(it)[0]
                    )
            }
            httpUrl = httpUrl.newBuilder()
                .addQueryParameter("_signature", MD5.encrypt(buffer.toString()))
                .build()
            request = request.newBuilder().url(httpUrl).build()


        }
        return chain.proceed(request)
    }
}