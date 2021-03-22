package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView

/**
 *    author : ZFM
 *    date   : 2021/3/2215:59
 *    desc   :
 *    version: 1.0
 */
interface IndexRecommendContract {
    interface View : IView {

    }
    interface Presenter : IPresenter<View> {

    }

    interface Model : IModel {

    }
}