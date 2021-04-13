package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.ChangeUserContactContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.ContactWayBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/1320:46
 *    desc   :
 *    version: 1.0
 */
class ChangeUserContactModel : BaseModel(), ChangeUserContactContract.Model {
    override fun getContact(): Observable<BaseResp<ContactWayBean?>> {

        return RetrofitHelper.service.getContact(hashMapOf())
    }

    override fun setContact(
        contact_way: Int,
        contact_way_content: String,
        contact_way_hide: Int
    ): Observable<BaseResp<Any?>> {
        val params = hashMapOf<String, Any>(
            "contact_way" to contact_way,
            "contact_way_content" to contact_way_content,
            "contact_way_hide" to contact_way_hide
        )

        return RetrofitHelper.service.setContact(params)
    }
}