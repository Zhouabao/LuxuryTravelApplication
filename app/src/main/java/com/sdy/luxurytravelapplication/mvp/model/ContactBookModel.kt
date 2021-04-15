package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.ContactBookContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.ContactDataBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/1511:49
 *    desc   :
 *    version: 1.0
 */
class ContactBookModel:BaseModel(), ContactBookContract.Model{
    override fun getContactLists(): Observable<BaseResp<ContactDataBean?>> {

        return RetrofitHelper.service.getContactLists(hashMapOf())
    }
}