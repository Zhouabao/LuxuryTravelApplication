package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.LoginOffCauseBeans
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/1216:14
 *    desc   :
 *    version: 1.0
 */
interface ChangeAccountContract {
    interface View : IView {

        fun onChangeAccountResult(result: Boolean)

        fun onSendSmsResult(result: Boolean)

        fun onCauseListResult(result: LoginOffCauseBeans)

    }

    interface Presenter:IPresenter<View>{
        /**
         * 更改手机号
         */
        fun changeAccount(params: HashMap<String, Any>)
        /**
         * 发送验证码
         */
        fun sendSms(params: HashMap<String, Any>)
        /**
         * 获取注销原因
         */
        fun getCauseList()
    }

    interface Model:IModel{
        /**
         * 更改手机号
         */
        fun changeAccount(params: HashMap<String, Any>):Observable<BaseResp<Any>>
        /**
         * 发送验证码
         */
        fun sendSms(params: HashMap<String, Any>):Observable<BaseResp<Any>>
        /**
         * 获取注销原因
         */
        fun getCauseList():Observable<BaseResp<LoginOffCauseBeans>>
    }
}