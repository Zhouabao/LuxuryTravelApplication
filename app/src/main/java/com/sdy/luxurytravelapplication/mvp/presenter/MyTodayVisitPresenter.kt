package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.MyTodayVisitContract
import com.sdy.luxurytravelapplication.mvp.model.MyTodayVisitModel

/**
 *    author : ZFM
 *    date   : 2021/4/1220:54
 *    desc   :
 *    version: 1.0
 */
class MyTodayVisitPresenter :
    BasePresenter<MyTodayVisitContract.Model, MyTodayVisitContract.View>(),
    MyTodayVisitContract.Presenter {
    override fun createModel(): MyTodayVisitContract.Model? {

        return MyTodayVisitModel()
    }

    override fun myVisitedList(params: HashMap<String, Any>) {

        mModel?.myVisitedList(params)?.ssss {
            mView?.onMyVisitResult(it.data)
        }
    }
}