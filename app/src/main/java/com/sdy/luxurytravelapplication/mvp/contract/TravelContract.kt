package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.CheckPublishDatingBean
import com.sdy.luxurytravelapplication.mvp.model.bean.TravelPlanBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/3017:11
 *    desc   :
 *    version: 1.0
 */
interface TravelContract {
    interface View : IView {
        fun planList(success:Boolean,datas: MutableList<TravelPlanBean>)
    }

    interface Presenter : IPresenter<View> {

        fun planList(params: HashMap<String, Any>)
    }

    interface Model : IModel {
        fun planList(params: HashMap<String, Any>): Observable<BaseResp<MutableList<TravelPlanBean>?>>
    }
}