package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.mvp.contract.MessageInfoContract
import com.sdy.luxurytravelapplication.mvp.model.MessageInfoModel

/**
 *    author : ZFM
 *    date   : 2021/4/712:25
 *    desc   :
 *    version: 1.0
 */
class MessageInfoPresenter : BasePresenter<MessageInfoContract.Model, MessageInfoContract.View>(),
    MessageInfoContract.Presenter {
    override fun createModel(): MessageInfoContract.Model?  = MessageInfoModel()
    override fun addStarTarget(target_accid: String) {
        mModel?.addStarTarget(target_accid)?.ss(mModel,mView){
            mView?.addStarTargetResult(true)
        }
    }

    override fun dissolutionFriend(target_accid: String) {
        mModel?.dissolutionFriend(target_accid)?.ss(mModel,mView){
            mView?.delFriendResult(true)
        }
    }

    override fun removeStarTarget(target_accid: String) {
        mModel?.removeStarTarget(target_accid)?.ss(mModel,mView){
            mView?.removeStarResult(true)
        }
    }

}