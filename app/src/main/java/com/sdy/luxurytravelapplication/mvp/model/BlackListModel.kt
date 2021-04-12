package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.BlackListContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.BlackBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/1212:19
 *    desc   :
 *    version: 1.0
 */
class BlackListModel : BaseModel(), BlackListContract.Model {
    override fun myShieldingList(params: HashMap<String, Any>): Observable<BaseResp<MutableList<BlackBean>?>> {
        return RetrofitHelper.service.myShieldingList(params)

    }

    override fun removeBlock(params: HashMap<String, Any>): Observable<BaseResp<Any>> {

        return RetrofitHelper.service.removeBlock(params)
    }
}