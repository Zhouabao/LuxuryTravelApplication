package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.IndexLuxuryContract
import com.sdy.luxurytravelapplication.mvp.contract.IndexRecommendContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.IndexRecommendBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/2216:00
 *    desc   :
 *    version: 1.0
 */
class IndexLuxuryModel : BaseModel(), IndexLuxuryContract.Model {
    override fun sweetheart(params: HashMap<String, Any>): Observable<BaseResp<IndexRecommendBean?>> {

        return RetrofitHelper.service.sweetheart(params)
    }
}