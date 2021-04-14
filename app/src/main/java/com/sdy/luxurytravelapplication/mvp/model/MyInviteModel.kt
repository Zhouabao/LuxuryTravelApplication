package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.MyInviteContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.MyInviteBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/1416:48
 *    desc   :
 *    version: 1.0
 */
class MyInviteModel : BaseModel(), MyInviteContract.Model {
    override fun myInvite(): Observable<BaseResp<MyInviteBean?>> {
        return RetrofitHelper.service.myInvite(hashMapOf())
    }
}