package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.IndexRecommendBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/2215:59
 *    desc   :
 *    version: 1.0
 */
interface IndexLuxuryContract {
    interface View : IView {
        fun sweetheart(data: IndexRecommendBean?)
    }

    interface Presenter : IPresenter<View> {
        fun sweetheart(params: HashMap<String, Any>)
    }

    interface Model : IModel {
        fun sweetheart(params: HashMap<String, Any>): Observable<BaseResp<IndexRecommendBean?>>
    }
}