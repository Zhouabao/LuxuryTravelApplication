package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.SetPersonalBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/1810:48
 *    desc   :
 *    version: 1.0
 */
interface LoginInfoContract {

    interface Presenter : IPresenter<View> {
        fun setPersonal(params: HashMap<String, Any>)
        fun uploadAvatar(filePath: String, imageName: String)
    }

    interface Model : IModel {
        fun setPersonal(params: HashMap<String, Any>): Observable<BaseResp<SetPersonalBean>>

        fun uploadAvatar(filePath: String, imageName: String): String

    }

    interface View : IView {
        fun setPersonalResult(setPersonalBean: SetPersonalBean?, success: Boolean)
        fun uploadAvatarResult(filePath: String?, success: Boolean)
    }


}