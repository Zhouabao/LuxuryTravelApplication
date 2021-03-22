package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.IndexContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.IndexListBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/2214:20
 *    desc   :
 *    version: 1.0
 */
class IndexModel : BaseModel(), IndexContract.Model {
    override fun indexTop(params: HashMap<String, Any>): Observable<BaseResp<IndexListBean>> {

        return RetrofitHelper.service.indexTop(params)
    }
}