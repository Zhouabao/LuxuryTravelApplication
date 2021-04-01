package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.MainContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/1710:14
 *    desc   :
 *    version: 1.0
 */
class MainModel : BaseModel(), MainContract.Model {
    override fun logout(): Observable<BaseResp<Any>> {
        return RetrofitHelper.service.logout()
    }

}