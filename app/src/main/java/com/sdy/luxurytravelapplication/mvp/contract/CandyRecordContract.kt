package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.BillBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/911:51
 *    desc   :
 *    version: 1.0
 */
interface CandyRecordContract {
    interface View : IView {
        fun onMyBillList(success: Boolean, billList: MutableList<BillBean>)
    }
    interface Presenter : IPresenter<View> {
        /**
         * 获取交易流水
         */
        fun myBillList(params: HashMap<String, Any>)
    }
    interface Model : IModel {
        /**
         * 获取交易流水
         */
        fun myBillList(params: HashMap<String, Any>):Observable<BaseResp<MutableList<BillBean>?>>
    }
}