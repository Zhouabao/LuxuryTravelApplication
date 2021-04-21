package com.sdy.luxurytravelapplication.mvp.contract

import com.qiniu.android.storage.UpCompletionHandler
import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/2317:10
 *    desc   :
 *    version: 1.0
 */
interface UploadVerifyPublicContract {




    interface View : IView {

        fun uploadImgResult(success: Boolean, key: String, index: Int)

        fun uploadDataResult(success: Boolean)

        //	1 资产认证 2豪车认证 3身材 4职业
        fun getPicTplResult(datas: ArrayList<String>)

    }

    interface Presenter : IPresenter<View> {
        fun uploadData(public_type: Int, type: Int, photo: String,content:String)
        fun uploadPhoto(filePath: String, index: Int = 0)

        //	1 资产认证 2豪车认证 3身材 4职业
        fun getPicTpl(type: Int)
    }

    interface Model : IModel {
        fun uploadData(public_type: Int, type: Int, photo: String,content:String): Observable<BaseResp<Any>>
        fun uploadPhoto(filePath: String,  options: UpCompletionHandler)

        //	1 资产认证 2豪车认证 3身材 4职业
        fun getPicTpl(type: Int):Observable<BaseResp<ArrayList<String>>>
    }
}