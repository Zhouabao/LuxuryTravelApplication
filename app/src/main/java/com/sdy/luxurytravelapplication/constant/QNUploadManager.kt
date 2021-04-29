package com.sdy.luxurytravelapplication.constant

import com.qiniu.android.storage.Configuration
import com.qiniu.android.storage.UploadManager

/**
 *    author : ZFM
 *    date   : 2020/8/2117:50
 *    desc   :
 *    version: 1.0
 */
object QNUploadManager {
    private fun getConfiguration(): Configuration {
        return Configuration.Builder()
            .chunkSize(512 * 1024)        // 分片上传时，每片的大小。 默认256K
            .chunkSize(1024 * 1024)   // 启用分片上传阀值。默认512K
            .connectTimeout(10)           // 链接超时。默认10秒
            .useHttps(true)               // 是否使用https上传域名
            .responseTimeout(60)          // 服务器响应超时。默认60秒
            .build()
    }

    fun getInstance(): UploadManager {
        return UploadManager(getConfiguration())
    }


    /**
     * ?vframe/jpg/offset/1
     * 末位的数字1代表截取第一秒，可自定义
     */
    fun getCoverUrl(mvUrl: String) = "$mvUrl?vframe/jpg/offset/1"


    /**
     * ?vframe/jpg/offset/1/w/200/h/150
     * 末位的数字1代表截取第一秒，可自定义 其中w/200代表宽200PX，h/150代表高150px.
     */
    fun getCoverUrlWithWH(mvUrl: String, width: Int, height: Int) =
        "$mvUrl?vframe/jpg/offset/1/w/$width/h/$height "


}