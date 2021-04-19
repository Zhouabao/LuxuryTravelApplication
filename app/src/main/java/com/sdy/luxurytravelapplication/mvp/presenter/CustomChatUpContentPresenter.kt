package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.CustomChatUpContentContract
import com.sdy.luxurytravelapplication.mvp.model.CustomChatUpContentModel

/**
 *    author : ZFM
 *    date   : 2021/4/1919:16
 *    desc   :
 *    version: 1.0
 */
class CustomChatUpContentPresenter :
    BasePresenter<CustomChatUpContentContract.Model, CustomChatUpContentContract.View>(),
    CustomChatUpContentContract.Presenter {
    override fun createModel(): CustomChatUpContentContract.Model? {

        return CustomChatUpContentModel()
    }

    override fun saveChatupMsg(aboutme: String) {
        mModel?.saveChatupMsg(aboutme)?.ssss(mView, true) {
            mView?.onSaveChatupMsg(it.code == 200, it.msg)
        }
    }
}