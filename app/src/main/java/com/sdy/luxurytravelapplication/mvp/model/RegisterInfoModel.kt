package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.RegisterInfoContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.RegisterFileBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/1710:55
 *    desc   :
 *    version: 1.0
 */
class RegisterInfoModel : BaseModel(), RegisterInfoContract.Model {
    override fun register(params: HashMap<String, String>): Observable<BaseResp<Any>> {

        return RetrofitHelper.service.register(params)
    }

    override fun getRegisterProcessType(): Observable<BaseResp<RegisterFileBean>> {

        return  RetrofitHelper.service.getRegisterProcessType()
    }
}