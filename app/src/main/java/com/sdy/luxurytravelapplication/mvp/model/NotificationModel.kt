package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.NotificationContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.SettingsBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/1211:26
 *    desc   :
 *    version: 1.0
 */
class NotificationModel : BaseModel(), NotificationContract.Model {
    override fun squareNotifySwitch(type: Int): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.squareNotifySwitch(hashMapOf("type" to type))

    }

    override fun switchSet(type: Int, state: Int): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.squareNotifySwitch(hashMapOf("type" to type, "state" to state))

    }

    override fun mySettings(): Observable<BaseResp<SettingsBean?>> {
        return RetrofitHelper.service.mySettings(hashMapOf())
    }
}