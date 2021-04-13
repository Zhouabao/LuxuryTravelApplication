package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.MyCommentContract
import com.sdy.luxurytravelapplication.mvp.model.MyCommentModel

/**
 *    author : ZFM
 *    date   : 2021/4/1219:43
 *    desc   :
 *    version: 1.0
 */
class MyCommentPresenter : BasePresenter<MyCommentContract.Model, MyCommentContract.View>(),
    MyCommentContract.Presenter {
    override fun createModel(): MyCommentContract.Model? {
        return MyCommentModel()
    }

    override fun myCommentList(params: HashMap<String, Any>) {

        mModel?.myCommentList(params)?.ssss {
            mView?.onGetCommentListResult(it.data)
        }
    }

    override fun deleteComment(params: HashMap<String, Any>, position: Int) {
        mModel?.deleteComment(params, position)?.ss(mModel, mView, false) {
            mView?.onDeleteCommentResult(true, position
            )
        }
    }
}