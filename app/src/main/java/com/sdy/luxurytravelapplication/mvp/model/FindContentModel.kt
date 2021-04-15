package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.FindContentContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.RecommendSquareListBean
import com.sdy.luxurytravelapplication.ui.fragment.FindContentFragment
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/2411:37
 *    desc   :
 *    version: 1.0
 */
class FindContentModel : BaseModel(), FindContentContract.Model {
    override fun squareEliteList(
        params: HashMap<String, Any>,
        type: Int
    ): Observable<BaseResp<RecommendSquareListBean?>> {
        if (type == FindContentFragment.TYPE_RECOMMEND) { //推荐
            return RetrofitHelper.service.squareEliteList(params)
        } else if (type == FindContentFragment.TYPE_NEARBY) {//附近
            return RetrofitHelper.service.squareNearly(params)
        } else if (type == FindContentFragment.TYPE_NEWEST) {//最新
            return RetrofitHelper.service.squareNewestLists(params)
        } else if (type == FindContentFragment.TYPE_MINE) {//我的动态
            return RetrofitHelper.service.aboutMeSquareCandy(params)
        } else /*if (type == FindContentFragment.TYPE_LIKE)*/ {//我的点赞
            params["type"] = 2
            return RetrofitHelper.service.myCollectionAndLike(params)
        }

    }
}