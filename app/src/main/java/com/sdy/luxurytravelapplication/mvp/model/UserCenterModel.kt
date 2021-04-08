package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.UserCenterContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.UserInfoBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/810:55
 *    desc   :
 *    version: 1.0
 */
class UserCenterModel:BaseModel(),UserCenterContract.Model {
    override fun myInfoCandy(): Observable<BaseResp<UserInfoBean?>> {

        return RetrofitHelper.service.myInfoCandy(hashMapOf())
    }
}