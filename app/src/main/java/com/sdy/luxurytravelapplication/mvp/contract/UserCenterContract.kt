package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.UserInfoBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/810:55
 *    desc   :
 *    version: 1.0
 */
interface UserCenterContract {
    interface View : IView {
        fun onGetMyInfoResult(userinfo: UserInfoBean?)

    }
    interface Presenter : IPresenter<View> {
        //获取个人信息
        fun myInfoCandy()
    }
    interface Model : IModel {
        //获取个人信息
        fun myInfoCandy():Observable<BaseResp<UserInfoBean?>>
    }
}