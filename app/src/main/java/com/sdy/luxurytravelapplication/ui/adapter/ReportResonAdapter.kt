package com.sdy.luxurytravelapplication.ui.adapter

import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemLayoutReportReason1Binding
import com.sdy.luxurytravelapplication.mvp.model.bean.ReportBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class ReportResonAdapter :
    BaseBindingQuickAdapter<ReportBean, ItemLayoutReportReason1Binding>(R.layout.item_layout_report_reason1) {
    override fun convert(binding: ItemLayoutReportReason1Binding, position: Int, item: ReportBean) {
        binding.apply {

            reportReason.text = item.reason
            reportReasonCheck.isChecked = item.checked
        }

    }

}
