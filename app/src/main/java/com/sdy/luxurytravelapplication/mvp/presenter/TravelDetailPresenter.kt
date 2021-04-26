package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.TravelDetailContract
import com.sdy.luxurytravelapplication.mvp.model.TravelDetailModel

/**
 *    author : ZFM
 *    date   : 2021/3/3017:13
 *    desc   :
 *    version: 1.0
 */
class TravelDetailPresenter :
    BasePresenter<TravelDetailContract.Model, TravelDetailContract.View>(),
    TravelDetailContract.Presenter {
    override fun createModel(): TravelDetailContract.Model? = TravelDetailModel()
    override fun planInfo(dating_id: Int) {
        mModel?.planInfo(dating_id)?.ssss(mView) {
            mView?.planInfo(it.data)
        }
    }

    override fun getcommentPlan(params: HashMap<String, Any>) {
        mModel?.getcommentPlan(params)?.ssss(mView, false) {
            mView?.getcommentPlan(it.data)
        }

    }

    override fun addCommentPlan(params: HashMap<String, Any>) {

        mModel?.addCommentPlan(params)?.ssss { mView?.addCommentPlan(it.code,it.msg) }
    }

    override fun commentPlanLike(params: HashMap<String, Any>, position: Int) {
        mModel?.commentPlanLike(params)?.ssss {
            mView?.commentPlanLike(it.code,it.msg,position)
        }
    }

    override fun delCommentPlan(id: Int,position:Int) {
        mModel?.delCommentPlan(id)?.ssss {
            mView?.delCommentPlan(it.code,it.msg,position)
        }
    }

    override fun commentReport(params: HashMap<String, Any>, position: Int) {
       mModel?.commentReport(params, position)?.ssss {
           mView?.commentReport(it,position)
       }
    }

}