package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.BindAlipayAccountContract
import com.sdy.luxurytravelapplication.mvp.model.bean.Alipay
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/915:48
 *    desc   :
 *    version: 1.0
 */
class BindAlipayAccountModel : BaseModel(), BindAlipayAccountContract.Model {
    override fun saveWithdrawAccount(params: HashMap<String, Any>): Observable<BaseResp<Alipay?>> {
        return  RetrofitHelper.service.saveWithdrawAccount(params)
    }
}