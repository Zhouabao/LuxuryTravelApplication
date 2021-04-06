package com.sdy.luxurytravelapplication.mvp.presenter

import com.netease.nimlib.sdk.RequestCallbackWrapper
import com.netease.nimlib.sdk.ResponseCode
import com.netease.nimlib.sdk.msg.model.RecentContact
import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.AccostListContract
import com.sdy.luxurytravelapplication.mvp.model.AccostListModel
import com.sdy.luxurytravelapplication.mvp.model.bean.AccostBean

/**
 *    author : ZFM
 *    date   : 2021/4/616:41
 *    desc   :
 *    version: 1.0
 */
class AccostListPresenter : BasePresenter<AccostListContract.Model, AccostListContract.View>(),
    AccostListContract.Presenter {
    override fun createModel(): AccostListContract.Model? = AccostListModel()
    override fun chatupList(params: HashMap<String, Any>) {
        mModel?.chatupList(params)?.ssss {
            mView?.chatupList(it.data?.list ?: mutableListOf())
        }
    }

    override fun delChat(params: HashMap<String, Any>, position: Int) {

        mModel?.delChat(params, position)?.ssss {
            mView?.delChat(it.code == 200, position)
        }
    }

    override fun getRecentContacts(data: MutableList<AccostBean>) {
        mModel?.getRecentContacts(object : RequestCallbackWrapper<MutableList<RecentContact>>() {
            override fun onResult(
                code: Int,
                result: MutableList<RecentContact>?,
                exception: Throwable?
            ) {
                if (code != ResponseCode.RES_SUCCESS.toInt() || result == null) {
                    return
                }
                mView?.getRecentContacts(data, result)
            }

        })

    }
}