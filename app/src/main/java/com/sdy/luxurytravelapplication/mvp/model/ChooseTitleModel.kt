package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.ChooseTitleContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.ChooseTitleBean
import com.sdy.luxurytravelapplication.mvp.model.bean.LabelQualityBean
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/3/3010:52
 *    desc   :
 *    version: 1.0
 */
class ChooseTitleModel : BaseModel(), ChooseTitleContract.Model {
    override fun getTagTitleList(page: Int): Observable<BaseResp<ChooseTitleBean>> {
        return RetrofitHelper.service.getTagTitleList(hashMapOf("page" to page))
    }
}