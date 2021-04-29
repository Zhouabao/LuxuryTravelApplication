package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.AccountBean
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.WechatNameBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/1215:29
 *    desc   :
 *    version: 1.0
 */
interface AccountAboutContract {
    interface View : IView {

        fun getAccountResult(accountBean: AccountBean)


        fun unbundWeChatResult(result: Boolean)

        fun bundWeChatResult(result: WechatNameBean)
    }

    interface Presenter : IPresenter<View> {
        /**
         * 获取账户信息
         */
        fun getAccountInfo()

        /**
         * 微信解绑
         */
        fun unbundWeChat()

        /**
         *微信绑定
         */
        fun bundWeChat(wxcode: String, nickname: String)
    }

    interface Model : IModel {
        /**
         * 获取账户信息
         */
        fun getAccountInfo(): Observable<BaseResp<AccountBean>>

        /**
         * 微信解绑
         */
        fun unbundWeChat(): Observable<BaseResp<Any>>


        /**
         *微信绑定
         */
        fun bundWeChat(wxcode: String, nickname: String): Observable<BaseResp<WechatNameBean>>
    }
}