package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.TargetUserContract
import com.sdy.luxurytravelapplication.mvp.model.TargetUserModel

/**
 *    author : ZFM
 *    date   : 2021/4/117:30
 *    desc   :
 *    version: 1.0
 */
class TargetUserPresenter : BasePresenter<TargetUserContract.Model, TargetUserContract.View>(),
    TargetUserContract.Presenter {
    override fun createModel(): TargetUserContract.Model? = TargetUserModel()
    override fun getMatchUserInfo(target_accid: String) {
        mModel?.getMatchUserInfo(target_accid)?.ssss{
            mView?.getMatchUserInfo(it.code, it.msg, it.data)
        }
    }

    override fun someoneSquareCandy(params: HashMap<String, Any>) {

        mModel?.someoneSquareCandy(params)?.ssss {
            mView?.onGetSquareListResult(it.data, it.code == 200)
        }
    }

    override fun shieldingFriend(params: HashMap<String, Any>) {
        mModel?.shieldingFriend(params)?.ss {
            mView?.onGetUserActionResult(true,false)
        }
    }

    override fun removeBlock(params: HashMap<String, Any>) {
        mModel?.removeBlock(params)?.ss {
            mView?.onRemoveBlockResult(true)
        }
    }

    override fun dissolutionFriend(params: HashMap<String, Any>) {
        mModel?.dissolutionFriend(params)?.ss {
            mView?.onGetUserActionResult(true,true)
        }

    }

    override fun playMv(target_accid: String) {
        mModel?.playMv(target_accid)
    }
}