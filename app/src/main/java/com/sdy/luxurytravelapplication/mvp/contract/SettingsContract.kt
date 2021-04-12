package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.SettingsBean
import com.sdy.luxurytravelapplication.mvp.model.bean.VersionBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/129:31
 *    desc   :
 *    version: 1.0
 */
interface SettingsContract {
    interface View : IView {
        fun onBlockedAddressBookResult(success: Boolean)

        fun onHideDistanceResult(success: Boolean)

        fun onGetVersionResult(versionBean: VersionBean?)


        fun onSettingsBeanResult(success: Boolean, settingsBean: SettingsBean?)
    }

    interface Presenter : IPresenter<View> {

        /**
         * type  1短信 2隐身 3私聊
         * state  	短信(1开启 2关闭)
         *          隐身（1 不隐身 2离线隐身 3一直隐身 ）
         */
        fun switchSet(type: Int, state: Int)

        /**
         * 获取我的设置
         */
        fun mySettings()

        /**
         * 屏蔽通讯录
         */
        fun blockedAddressBook(content: MutableList<String?> = mutableListOf())

        /**
         * 屏蔽距离
         */
        fun isHideDistance(state: Int)

        /**获取当前最新版本**/
        fun getVersion()
    }

    interface Model : IModel {

        /**
         * type  1短信 2隐身 3私聊
         * state  	短信(1开启 2关闭)
         *          隐身（1 不隐身 2离线隐身 3一直隐身 ）
         */
        fun switchSet(type: Int, state: Int):Observable<BaseResp<Any>>

        /**
         * 获取我的设置
         */
        fun mySettings():Observable<BaseResp<SettingsBean?>>

        /**
         * 屏蔽通讯录
         */
        fun blockedAddressBook(content: MutableList<String?> = mutableListOf()):Observable<BaseResp<Any>>

        /**
         * 屏蔽距离
         */
        fun isHideDistance(state: Int):Observable<BaseResp<Any>>

        /**获取当前最新版本**/
        fun getVersion():Observable<BaseResp<VersionBean?>>
    }
}