package com.sdy.luxurytravelapplication.http.interceptor

import com.blankj.utilcode.util.NetworkUtils
import com.sdy.luxurytravelapplication.app.TravelApp
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author chenxz
 * @date 2018/11/15
 * @desc OfflineCacheInterceptor: 没有网时缓存
 */
class OfflineCacheInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (!NetworkUtils.isAvailable()) {
            // 无网络时，设置超时为4周  只对get有用,post没有缓冲
            val maxStale = 60 * 60 * 24 * 28
            response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                    .removeHeader("nyn")
                    .build()
        }

        return response
    }
}