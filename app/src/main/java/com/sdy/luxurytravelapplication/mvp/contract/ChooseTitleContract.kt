package com.sdy.luxurytravelapplication.mvp.contract

import com.sdy.luxurytravelapplication.base.IModel
import com.sdy.luxurytravelapplication.base.IPresenter
import com.sdy.luxurytravelapplication.base.IView
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.ChooseTitleBean
import com.sdy.luxurytravelapplication.mvp.model.bean.LabelQualityBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/3010:50
 *    desc   :
 *    version: 1.0
 */
interface ChooseTitleContract {
    interface View : IView {
        fun getTagTraitInfoResult(
            b: Boolean,
            mutableList: MutableList<LabelQualityBean>?,
            maxCount: Int
        )

    }

    interface Presenter : IPresenter<View> {
        /**
         *  获取兴趣的  特质/模板/意向/标题  type  1 2 3 4
         */
        fun getTagTitleList(page: Int)
    }

    interface Model : IModel {
        fun getTagTitleList(page: Int): Observable<BaseResp<ChooseTitleBean>>

    }
}