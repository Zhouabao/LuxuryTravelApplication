package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.AccountAboutContract
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
class AccountAboutModel:BaseModel(),AccountAboutContract.Model {
    override fun getAccountInfo(): Observable<BaseResp<AccountBean>> {

        return RetrofitHelper.service.getAccountInfo(hashMapOf())
    }

    override fun unbundWeChat(): Observable<BaseResp<Any>> {

        return RetrofitHelper.service.unbundWeChat(hashMapOf())
    }

    override fun bundWeChat(wxcode: String): Observable<BaseResp<WechatNameBean>> {

        return RetrofitHelper.service.bundWeChat(hashMapOf("wxcode" to wxcode))
    }
}