package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.TagDetailCategoryContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.TagSquareListBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/2419:45
 *    desc   :
 *    version: 1.0
 */
class TagDetailCategoryModel:BaseModel(),TagDetailCategoryContract.Model {
    override fun squareTagInfo(params: HashMap<String, Any>): Observable<BaseResp<TagSquareListBean?>> {
        return RetrofitHelper.service.squareTagInfo(params)
    }

    override fun checkBlock(): Observable<BaseResp<Any?>> {
        return RetrofitHelper.service.checkBlock(hashMapOf())
    }
}