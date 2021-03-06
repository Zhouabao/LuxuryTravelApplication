package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.RegisterInfoTwoContract
import com.sdy.luxurytravelapplication.mvp.model.bean.*
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/1710:55
 *    desc   :
 *    version: 1.0
 */
class RegisterInfoTwoModel : BaseModel(), RegisterInfoTwoContract.Model {
    override fun setPersonal(params: HashMap<String, Any>): Observable<BaseResp<SetPersonalBean>> {

        return RetrofitHelper.service.setPersonal(params)
    }

    override fun getRegisterProcessType(): Observable<BaseResp<MutableList<AnswerBean>?>> {
        return  RetrofitHelper.service.getManTaps(hashMapOf())
    }
}