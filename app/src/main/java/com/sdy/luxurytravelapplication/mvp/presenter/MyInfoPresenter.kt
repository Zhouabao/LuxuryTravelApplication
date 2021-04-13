package com.sdy.luxurytravelapplication.mvp.presenter

import com.qiniu.android.storage.UpCompletionHandler
import com.sdy.luxurytravelapplication.base.BasePresenter
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.mvp.contract.MyInfoContract
import com.sdy.luxurytravelapplication.mvp.model.MyInfoModel

/**
 *    author : ZFM
 *    date   : 2021/4/1315:16
 *    desc   :
 *    version: 1.0
 */
class MyInfoPresenter : BasePresenter<MyInfoContract.Model, MyInfoContract.View>(),
    MyInfoContract.Presenter {
    override fun createModel(): MyInfoContract.Model? {
        return MyInfoModel()
    }

    override fun personalInfo() {
        mModel?.personalInfo()?.ss(mModel, mView, true) {
            mView?.onPersonalInfoResult(it.data!!)
        }
    }

    override fun addPhotoV2(params: HashMap<String, Any?>, photos: MutableList<Int?>, type: Int) {
        mModel?.addPhotoV2(params, photos, type)?.ssss(mView,true) {
            mView?.onSavePersonalResult(it.code==200,2,type)
        }
    }

    override fun addPhotoWall(replaceAvator: Boolean, key: String) {
      mModel?.addPhotoWall(replaceAvator, key)?.ss(mModel,mView,true) {
          mView?.onAddPhotoWallResult(replaceAvator,it.data!!)
      }
    }

    override fun uploadProfile(filePath: String, imagePath: String, replaceAvator: Boolean) {
        mModel?.uploadProfile(filePath,imagePath,replaceAvator,
            UpCompletionHandler { key, info, response ->
                if (info != null) {
                    mView?.uploadImgResult(info.isOK, key, replaceAvator)
                }
            })
    }
}