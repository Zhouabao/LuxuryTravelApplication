package com.sdy.luxurytravelapplication.ui.adapter

import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ItemLayoutReportReasonBinding
import com.sdy.luxurytravelapplication.mvp.model.bean.ReportBean
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingQuickAdapter

class ReportReasonAdapter :
    BaseBindingQuickAdapter<String, ItemLayoutReportReasonBinding>(R.layout.item_layout_report_reason) {
    override fun convert(binding: ItemLayoutReportReasonBinding, position: Int, item: String) {
        binding.apply {
            reportType.text = item
        }

    }

}
