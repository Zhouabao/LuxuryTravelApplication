package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.MatchBean
import com.sdy.luxurytravelapplication.mvp.model.bean.RecommendSquareListBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/117:28
 *    desc   :
 *    version: 1.0
 */
interface TargetUserContract {
    interface View : IView {
        /**
         * 获取对方信息详情
         */
        fun getMatchUserInfo(code: Int, msg: String, target_accid: MatchBean?)

        /**
         * 获取广场列表
         */
        fun onGetSquareListResult(
            data: RecommendSquareListBean?,
            result: Boolean
        )

    }

    interface Presenter : IPresenter<View> {
        fun getMatchUserInfo(target_accid: String)
        fun someoneSquareCandy(params: HashMap<String, Any>)
    }

    interface Model : IModel {

        fun getMatchUserInfo(target_accid: String): Observable<BaseResp<MatchBean?>>

        fun someoneSquareCandy(params: HashMap<String, Any>): Observable<BaseResp<RecommendSquareListBean?>>
    }
}