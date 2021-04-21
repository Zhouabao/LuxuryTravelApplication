package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.FindAllTagContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareTagBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/2216:00
 *    desc   :
 *    version: 1.0
 */
class FindAllTagModel : BaseModel(), FindAllTagContract.Model {
    override fun squareTopicList(): Observable<BaseResp<MutableList<SquareTagBean>?>> {
        return RetrofitHelper.service.squareTopicList(hashMapOf())
    }
}