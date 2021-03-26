package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.SquareCommentDetailContract
import com.sdy.luxurytravelapplication.mvp.model.SquareCommentDetailModel

/**
 *    author : ZFM
 *    date   : 2021/3/2514:42
 *    desc   :
 *    version: 1.0
 */
class SquareCommentDetailPresenter :
    BasePresenter<SquareCommentDetailContract.Model, SquareCommentDetailContract.View>(),
    SquareCommentDetailContract.Presenter {
    override fun createModel(): SquareCommentDetailContract.Model? = SquareCommentDetailModel()
    override fun getCommentLike(params: HashMap<String, Any>, position: Int) {
        mModel?.getCommentLike(params, position)?.ssss {
            mView?.onLikeCommentResult(it, position)
        }
    }

    override fun getSquareInfo(params: HashMap<String, Any>) {
        mModel?.getSquareInfo(params)?.ssss {
            mView?.onGetSquareInfoResults(it.data)
        }

    }

    override fun getCommentList(params: HashMap<String, Any>, refresh: Boolean) {
        mModel?.getCommentList(params)?.ssss {
            mView?.onGetCommentListResult(it.data)
        }
    }

    override fun getSquareLike(params: HashMap<String, Any>, auto: Boolean) {
        mModel?.getSquareLike(params, auto)?.ssss {
            mView?.onGetSquareLikeResult(it.code == 200)
        }
    }

    override fun getSquareCollect(params: HashMap<String, Any>) {
        mModel?.getSquareCollect(params)?.ssss {
            mView?.onGetSquareCollectResult(it)
        }
    }

    override fun getSquareReport(params: HashMap<String, Any>) {
        mModel?.getSquareReport(params)?.ssss {
            mView?.onGetSquareReport(it)
        }
    }

    override fun addComment(params: HashMap<String, Any>) {
        mModel?.addComment(params)?.ssss {
            mView?.onAddCommentResult(it, it.code == 200)
        }
    }

    override fun deleteComment(params: HashMap<String, Any>, position: Int) {
        mModel?.deleteComment(params, position)?.ssss {
            mView?.onDeleteCommentResult(it, position)
        }
    }

    override fun commentReport(params: HashMap<String, Any>, position: Int) {
        mModel?.commentReport(params, position)?.ssss {
            mView?.onReportCommentResult(it, position)
        }
    }

    override fun removeMySquare(params: HashMap<String, Any>) {
        mModel?.removeMySquare(params)?.ssss {
            mView?.onRemoveMySquareResult(it.code == 200)
        }
    }


}