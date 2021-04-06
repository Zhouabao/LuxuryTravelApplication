package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareMsgBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/617:24
 *    desc   :
 *    version: 1.0
 */
interface MessageSquareContract {
    interface View : IView {
        fun onSquareListsResult(data: MutableList<SquareMsgBean>?)

        fun onDelSquareMsgResult(success: Boolean)
    }

    interface Presenter : IPresenter<View> {
        fun squareLists(params: HashMap<String, Any>)
        fun markSquareRead(params: HashMap<String, Any>)
        fun delSquareMsg(params: HashMap<String, Any>)
    }

    interface Model : IModel {
        fun squareLists(params: HashMap<String, Any>): Observable<BaseResp<MutableList<SquareMsgBean>>>
        fun markSquareRead(params: HashMap<String, Any>):Observable<BaseResp<Any>>
        fun delSquareMsg(params: HashMap<String, Any>): Observable<BaseResp<Any>>
    }
}