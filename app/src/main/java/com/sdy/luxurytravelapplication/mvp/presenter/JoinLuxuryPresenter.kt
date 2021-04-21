package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.JoinLuxuryContract
import com.sdy.luxurytravelapplication.mvp.model.JoinLuxuryModel

/**
 *    author : ZFM
 *    date   : 2021/4/219:41
 *    desc   :
 *    version: 1.0
 */
class JoinLuxuryPresenter : BasePresenter<JoinLuxuryContract.Model, JoinLuxuryContract.View>(),
    JoinLuxuryContract.Presenter {
    override fun createModel(): JoinLuxuryContract.Model? {

        return JoinLuxuryModel()
    }

    override fun joinSweetApply() {

        mModel?.joinSweetApply()?.ssss {
            mView?.joinSweetApplyResult(it.code == 200)
        }
    }
}