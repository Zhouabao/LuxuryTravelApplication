package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityReportReasonBinding
import com.sdy.luxurytravelapplication.mvp.contract.ReportReasonContract
import com.sdy.luxurytravelapplication.mvp.presenter.ReportReasonPresenter
import com.sdy.luxurytravelapplication.ui.adapter.ReportReasonAdapter
import org.jetbrains.anko.startActivity

/**
 * 举报理由
 */
class ReportReasonActivity :
    BaseMvpActivity<ReportReasonContract.View, ReportReasonContract.Presenter, ActivityReportReasonBinding>(),
    ReportReasonContract.View {
    companion object {
        fun startReport(context: Context, target_accid: String) {
            context.startActivity<ReportReasonActivity>("target_accid" to target_accid)
        }
    }

    override fun createPresenter(): ReportReasonContract.Presenter = ReportReasonPresenter()
    private val adapter by lazy { ReportReasonAdapter() }

    override fun initData() {
        binding.apply {
            binding.barCl.btnBack.setOnClickListener {
                finish()
            }
            binding.barCl.actionbarTitle.text = "举报理由"
            reportRv.layoutManager =
                LinearLayoutManager(this@ReportReasonActivity, RecyclerView.VERTICAL, false)
            reportRv.adapter = adapter
            adapter.setOnItemClickListener { _, view, position ->
                startActivity<ReportReasonUploadActivity>(
                    "case_type" to adapter.data[position],
                    "target_accid" to intent.getStringExtra("target_accid")
                )
            }
        }
    }

    override fun start() {
        mPresenter?.getReportMsg()
    }

    override fun onGetReportMsgResult(b: Boolean, datas: MutableList<String>) {
        if (b) {
            adapter.setNewInstance(datas)
        }
    }
}