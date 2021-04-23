package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.ChangeAccountContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.LoginOffCauseBeans
import com.sdy.luxurytravelapplication.mvp.model.bean.RegisterTooManyBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/1216:16
 *    desc   :
 *    version: 1.0
 */
class ChangeAccountModel : BaseModel(), ChangeAccountContract.Model {
    override fun changeAccount(params: HashMap<String, Any>): Observable<BaseResp<Any>> {

        return RetrofitHelper.service.changeAccount(params)
    }

    override fun sendSms(params: HashMap<String, Any>): Observable<BaseResp<RegisterTooManyBean?>> {
        return RetrofitHelper.service.sendSms(params)
    }

    override fun getCauseList(): Observable<BaseResp<LoginOffCauseBeans>> {
        return RetrofitHelper.service.getCauseList(hashMapOf())
    }
}