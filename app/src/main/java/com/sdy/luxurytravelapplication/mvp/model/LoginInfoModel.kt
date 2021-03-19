package com.sdy.luxurytravelapplication.mvp.model

import com.qiniu.android.storage.UpCompletionHandler
import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.constant.QNUploadManager
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.LoginInfoContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.SetPersonalBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/1810:51
 *    desc   :
 *    version: 1.0
 */
class LoginInfoModel : BaseModel(), LoginInfoContract.Model {
    override fun setPersonal(params: HashMap<String, Any>): Observable<BaseResp<SetPersonalBean>> {
        return RetrofitHelper.service.setPersonal(params)

    }

    override fun uploadAvatar(
        filePath: String,
        imageName: String,
        upCompletionHandler: UpCompletionHandler
    ){

        QNUploadManager.getInstance()
            .put(filePath, imageName, UserManager.qnToken, upCompletionHandler, null)

    }
}