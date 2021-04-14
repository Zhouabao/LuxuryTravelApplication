package com.sdy.luxurytravelapplication.mvp.contract

import com.qiniu.android.storage.UpCompletionHandler
import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.CopyMvBean
import io.reactivex.Observable
import java.util.*

/**
 *    author : ZFM
 *    date   : 2021/4/1411:12
 *    desc   :
 *    version: 1.0
 */
interface VideoIntroduceContract {
    interface View : IView {
        fun uploadProfileResult(success: Boolean, fileName: String)

        fun uploadMvResult(success: Boolean)

        fun getVideoNormalResult(data: CopyMvBean?)
    }

    interface Presenter : IPresenter<View> {
        //    mv_url复制 [string]    是    视频地址
//    type [string]    是    1 自拍 2 上传
        //normal_id [string]	是	模板id

        /**
         * 上传视频认证
         */
        fun uploadMv(params: HashMap<String, Any>)

        /**
         * 获取标准化
         */
        fun getVideoNormal()

        /**
         * 上传视频到七牛
         * imagePath 文件名格式： ppns/文件类型名/用户ID/当前时间戳/16位随机字符串
         */
        fun uploadProfile(filePath: String,type:Int,normal_id:Int)
    }

    interface Model : IModel {
        /**
         * 上传视频认证
         */
        fun uploadMv(params: HashMap<String, Any>): Observable<BaseResp<Any>>

        /**
         * 获取标准化
         */
        fun getVideoNormal(): Observable<BaseResp<CopyMvBean?>>

        /**
         * 上传视频到七牛
         * imagePath 文件名格式： ppns/文件类型名/用户ID/当前时间戳/16位随机字符串
         */
        fun uploadProfile(filePath: String, options: UpCompletionHandler)
    }
}