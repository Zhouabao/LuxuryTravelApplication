package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.Alipay
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/915:47
 *    desc   :
 *    version: 1.0
 */
interface BindAlipayAccountContract {
    interface View : IView {
        fun saveWithdrawAccountResult(success: Boolean, data: Alipay?)
    }

    interface Presenter : IPresenter<View> {
        fun saveWithdrawAccount(params: HashMap<String, Any>)
    }

    interface Model : IModel {
        fun saveWithdrawAccount(params: HashMap<String, Any>): Observable<BaseResp<Alipay?>>

    }
}