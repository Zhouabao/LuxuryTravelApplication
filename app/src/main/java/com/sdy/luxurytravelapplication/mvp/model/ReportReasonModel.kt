package com.sdy.luxurytravelapplication.mvp.model

import com.sdy.luxurytravelapplication.base.BaseModel
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.contract.ReportReasonContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import io.reactivex.Observable

/**
 *    author : ZFM
 *    date   : 2021/4/714:41
 *    desc   :
 *    version: 1.0
 */
class ReportReasonModel : BaseModel(), ReportReasonContract.Model {
    override fun getReportMsg(): Observable<BaseResp<MutableList<String>>> {
        return RetrofitHelper.service.getReportMsg()

    }
}