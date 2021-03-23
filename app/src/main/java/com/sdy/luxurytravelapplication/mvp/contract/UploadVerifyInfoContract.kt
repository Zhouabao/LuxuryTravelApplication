package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView

/**
 *    author : ZFM
 *    date   : 2021/3/2313:43
 *    desc   :
 *    version: 1.0
 */
interface UploadVerifyInfoContract {
    interface Model : IModel {}
    interface View : IView {}
    interface Presenter : IPresenter<View> {}
}