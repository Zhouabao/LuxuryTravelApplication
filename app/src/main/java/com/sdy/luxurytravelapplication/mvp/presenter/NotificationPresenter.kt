package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.NotificationContract
import com.sdy.luxurytravelapplication.mvp.model.NotificationModel

/**
 *    author : ZFM
 *    date   : 2021/4/1211:27
 *    desc   :
 *    version: 1.0
 */
class NotificationPresenter :
    BasePresenter<NotificationContract.Model, NotificationContract.View>(),
    NotificationContract.Presenter {
    override fun createModel(): NotificationContract.Model? {
        return NotificationModel()

    }

    override fun squareNotifySwitch(type: Int) {
        mModel?.squareNotifySwitch(type)?.ssss {
            mView?.onGreetApproveResult(type, it.code == 200)
        }
    }

    override fun switchSet(type: Int, state: Int) {
        mModel?.switchSet(type, state)?.ssss {
            mView?.switchSetResult(type, it.code == 200)
        }
    }

    override fun mySettings() {
        mModel?.mySettings()?.ssss {
            mView?.onSettingsBeanResult(it.code == 200, it.data)
        }
    }
}