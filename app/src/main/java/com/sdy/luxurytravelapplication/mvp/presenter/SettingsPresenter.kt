package com.sdy.luxurytravelapplication.mvp.presenter

import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.SettingsContract
import com.sdy.luxurytravelapplication.mvp.model.SettingsModel

/**
 *    author : ZFM
 *    date   : 2021/4/129:31
 *    desc   :
 *    version: 1.0
 */
class SettingsPresenter : BasePresenter<SettingsContract.Model, SettingsContract.View>(),
    SettingsContract.Presenter {
    override fun createModel(): SettingsContract.Model? {
        return SettingsModel()
    }

    override fun switchSet(type: Int, state: Int) {
        mModel?.switchSet(type, state)?.ss(mModel, mView, false) {
            mView?.showMsg(it.msg)
        }
    }

    override fun mySettings() {
        mModel?.mySettings()?.ssss(mView, true) {
            mView?.onSettingsBeanResult(it.code == 200, it.data)
        }
    }

    override fun blockedAddressBook(content: MutableList<String?>) {
        mModel?.blockedAddressBook(content)?.ssss {
            mView?.onBlockedAddressBookResult(it.code == 200)
        }
    }

    override fun isHideDistance(state: Int) {
        mModel?.isHideDistance(state)?.ssss {
            mView?.onHideDistanceResult(it.code == 200)
        }
    }

    override fun getVersion() {
        mModel?.getVersion()?.ssss { mView?.onGetVersionResult(it.data) }
    }

}