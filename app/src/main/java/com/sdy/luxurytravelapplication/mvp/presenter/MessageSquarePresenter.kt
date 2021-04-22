package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.MessageSquareContract
import com.sdy.luxurytravelapplication.mvp.model.MessageSquareModel

/**
 *    author : ZFM
 *    date   : 2021/4/617:26
 *    desc   :
 *    version: 1.0
 */
class MessageSquarePresenter :
    BasePresenter<MessageSquareContract.Model, MessageSquareContract.View>(),
    MessageSquareContract.Presenter {
    override fun createModel(): MessageSquareContract.Model? = MessageSquareModel()
    override fun squareLists(params: HashMap<String, Any>) {
        mModel?.squareLists(params)?.ssss {
            mView?.onSquareListsResult(it.data)
        }

    }

    override fun markSquareRead(params: HashMap<String, Any>) {
        mModel?.markSquareRead(params)?.ss {
            mView?.markSquareReadResult(true)
        }
    }

    override fun delSquareMsg(params: HashMap<String, Any>) {
        mModel?.delSquareMsg(params)?.ssss {
            mView?.onDelSquareMsgResult(it.code == 200)
        }
    }
}