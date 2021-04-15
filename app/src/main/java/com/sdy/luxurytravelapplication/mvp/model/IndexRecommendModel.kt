package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.IndexRecommendContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.IndexRecommendBean
import com.sdy.luxurytravelapplication.ui.fragment.IndexRecommendFragment
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/2216:00
 *    desc   :
 *    version: 1.0
 */
class IndexRecommendModel : BaseModel(), IndexRecommendContract.Model {
    override fun recommendIndex(
        params: HashMap<String, Any>,
        type: Int
    ): Observable<BaseResp<IndexRecommendBean?>> {
        if (type == IndexRecommendFragment.TYPE_RECOMMEND)
            return RetrofitHelper.service.recommendIndex(params)
        else
            return RetrofitHelper.service.theSameCity(params)
    }
}