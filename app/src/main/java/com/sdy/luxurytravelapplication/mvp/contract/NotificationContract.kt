package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.SettingsBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/1211:23
 *    desc   :
 *    version: 1.0
 */
interface NotificationContract {
    interface View : IView {

        fun onGreetApproveResult(type: Int, success: Boolean)

        fun switchSetResult(type: Int, success: Boolean)

        fun onSettingsBeanResult(success: Boolean, settingsBean: SettingsBean?)
    }

    interface Presenter : IPresenter<View> {
        /**
         * 用户广场点赞/评论接收推送开关 参数 type（int）型    1点赞    2评论
         */
        fun squareNotifySwitch(type: Int)

        /**
         * type  1短信 2隐身 3私聊  4微信公众号
         * state  	短信(1开启 2关闭)
         *          隐身（1 不隐身 2离线隐身 3一直隐身 ）
         */
        fun switchSet(type: Int, state: Int)

        /**
         * 获取我的设置
         */
        fun mySettings()
    }

    interface Model : IModel {
        /**
         * 用户广场点赞/评论接收推送开关 参数 type（int）型    1点赞    2评论
         */
        fun squareNotifySwitch(type: Int): Observable<BaseResp<Any>>

        /**
         * type  1短信 2隐身 3私聊  4微信公众号
         * state  	短信(1开启 2关闭)
         *          隐身（1 不隐身 2离线隐身 3一直隐身 ）
         */
        fun switchSet(type: Int, state: Int): Observable<BaseResp<Any>>

        /**
         * 获取我的设置
         */
        fun mySettings(): Observable<BaseResp<SettingsBean?>>
    }
}