package com.sdy.luxurytravelapplication.mvp.contract

import com.qiniu.android.storage.UpCompletionHandler
import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.constant.QNUploadManager
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.MyPhotoBean
import com.sdy.luxurytravelapplication.mvp.model.bean.UserInfoSettingBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/1315:14
 *    desc   :
 *    version: 1.0
 */
interface MyInfoContract {
    interface View : IView {
        fun onPersonalInfoResult(data: UserInfoSettingBean)


        /**
         * type  1 个人信息 2 头像
         */
        fun onSavePersonalResult(result: Boolean, type: Int, from: Int = 0)

        /**
         * 单张上传照片结果
         */
        fun onAddPhotoWallResult(replaceAvator: Boolean, result: MyPhotoBean)

        fun uploadImgResult(b: Boolean, key: String, replaceAvator: Boolean = false)

    }

    interface Presenter : IPresenter<View> {
        /**
         * 获取个人信息
         */
        fun personalInfo()
        /**
         * 保存相册
         */
        fun addPhotoV2(
            params: HashMap<String, Any?> = hashMapOf(),
            photos: MutableList<Int?>,
            type: Int
        )

        /**
         * 保存头像
         */
        fun addPhotoWall(replaceAvator: Boolean,key: String)
        /**
         * 上传照片
         * imagePath 文件名格式： ppns/文件类型名/用户ID/当前时间戳/16位随机字符串
         */
        fun uploadProfile(filePath: String, imagePath: String, replaceAvator: Boolean = false)
    }

    interface Model:IModel{
        /**
         * 获取个人信息
         */
        fun personalInfo():Observable<BaseResp<UserInfoSettingBean?>>
        /**
         * 保存相册
         */
        fun addPhotoV2(
            params: HashMap<String, Any?> = hashMapOf(),
            photos: MutableList<Int?>,
            type: Int
        ):Observable<BaseResp<Any>>
        /**
         * 保存头像
         */
        fun addPhotoWall(replaceAvator: Boolean,key: String):Observable<BaseResp<MyPhotoBean?>>

        /**
         * 上传照片
         * imagePath 文件名格式： ppns/文件类型名/用户ID/当前时间戳/16位随机字符串
         */
        fun uploadProfile(filePath: String, imagePath: String, replaceAvator: Boolean = false,upCompletionHandler:UpCompletionHandler)
    }
}