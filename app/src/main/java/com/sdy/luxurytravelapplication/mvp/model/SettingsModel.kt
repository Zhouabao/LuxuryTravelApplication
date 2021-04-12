package com.sdy.luxurytravelapplication.mvp.model

import com.google.gson.Gson
import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.SettingsContract
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
class SettingsModel:BaseModel(),SettingsContract.Model {
    override fun switchSet(type: Int, state: Int): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.switchSet(hashMapOf("type" to type, "state" to state))
    }

    override fun mySettings(): Observable<BaseResp<SettingsBean?>> {
       return RetrofitHelper.service.mySettings(hashMapOf())
    }

    override fun blockedAddressBook(content: MutableList<String?>): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.blockedAddressBook(hashMapOf("content" to Gson().toJson(content)))
    }

    override fun isHideDistance(state: Int): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.isHideDistance(hashMapOf("state" to state))
    }

    override fun getVersion(): Observable<BaseResp<VersionBean?>> {
        return RetrofitHelper.service.getVersion(hashMapOf())
    }
}