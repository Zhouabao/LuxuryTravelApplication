package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.IndexListBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/2214:19
 *    desc   :
 *    version: 1.0
 */
interface IndexContract {
    interface View : IView {
        fun indexTop(data: IndexListBean)
    }

    interface Model : IModel {
        fun indexTop(params: HashMap<String, Any>): Observable<BaseResp<IndexListBean>>
    }

    interface Presenter : IPresenter<View> {

        fun indexTop(params: HashMap<String, Any>)
    }
}