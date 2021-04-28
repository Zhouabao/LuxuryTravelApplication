package com.sdy.luxurytravelapplication.ui.activity

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ClickUtils
import com.google.gson.Gson
import com.luck.picture.lib.PictureSelector
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityReportReasonUploadBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.mvp.contract.ReportReasonUploadContract
import com.sdy.luxurytravelapplication.mvp.model.bean.UploadInfoBean
import com.sdy.luxurytravelapplication.mvp.presenter.ReportReasonUploadPresenter
import com.sdy.luxurytravelapplication.ui.adapter.VerifyUploadInfoAdapter
import com.sdy.luxurytravelapplication.utils.ToastUtil

class ReportReasonUploadActivity :
    BaseMvpActivity<ReportReasonUploadContract.View, ReportReasonUploadContract.Presenter, ActivityReportReasonUploadBinding>(),
    ReportReasonUploadContract.View {
    override fun createPresenter(): ReportReasonUploadContract.Presenter =
        ReportReasonUploadPresenter()

    private val case_type by lazy { intent.getStringExtra("case_type")?:""}
    private val target_accid by lazy { intent.getStringExtra("target_accid")?:"" }

    companion object {
        const val MAX_COUNT = 3
        const val REQUEST_REPORT_PIC = 1002

    }

    private val adapter by lazy { VerifyUploadInfoAdapter() }
    override fun start() {
    }

    override fun initData() {
        binding.apply {
            barCl.actionbarTitle.text = "举报理由"
            barCl.btnBack.setOnClickListener { finish() }
            reportPicRv.layoutManager = GridLayoutManager(this@ReportReasonUploadActivity, 3)
            reportPicRv.adapter = adapter
            adapter.addData(UploadInfoBean(R.drawable.icon_info_upload))
            adapter.addChildClickViewIds(R.id.infoDelete, R.id.infoIV)
            adapter.setOnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.infoDelete -> {
                        adapter.removeAt(position)
                        if (adapter.data.size - 1 < MAX_COUNT && !adapter.data.contains(
                                UploadInfoBean(R.drawable.icon_info_upload)
                            )
                        ) {
                            adapter.addData(UploadInfoBean(R.drawable.icon_info_upload))
                        }

                        checkConfirmEnable()
                    }
                    R.id.infoIV -> {
                        if (adapter.data[position].chooseIcon.isNullOrEmpty())
                            CommonFunction.onTakePhoto(
                                this@ReportReasonUploadActivity,
                                MAX_COUNT - (adapter.data.size - 1),
                                REQUEST_REPORT_PIC, compress = true
                            )
                    }
                }
            }

            ClickUtils.applySingleDebouncing(confirmReport) {
                if (adapter.data.indexOfFirst { it.chooseIcon.isNotEmpty() } != -1)
                    mPresenter?.uploadPhoto(adapter.data[index].chooseIcon, index)
                else
                    mPresenter?.addReport("", reportWhyEt.text.toString(), case_type, target_accid)
            }

            reportWhyEt.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    checkConfirmEnable()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

            })
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_REPORT_PIC) {
            if (data != null && !PictureSelector.obtainMultipleResult(data).isNullOrEmpty()) {
                for (data in PictureSelector.obtainMultipleResult(data)) {
                    adapter.addData(
                        adapter.data.size - 1,
                        UploadInfoBean(
                            chooseIcon =
                            if (data.isCompressed) {
                                data.compressPath
                            } else if (!data.androidQToPath.isNullOrEmpty()) {
                                data.androidQToPath
                            } else {
                                data.path
                            }, height = data.height, width = data.width
                        )
                    )
                }
                checkConfirmEnable()
                if (adapter.data.size - 1 == MAX_COUNT) {
                    adapter.removeAt(MAX_COUNT)
                }
            }
        }

    }


    private fun checkConfirmEnable() {
        var allHasPath = false
        for (data in adapter.data) {
            if (data.chooseIcon.isNotEmpty()) {
                allHasPath = true
                break
            }
        }
        binding.confirmReport.isEnabled = allHasPath || binding.reportWhyEt.text.isNotEmpty()

    }


    private var index = 0
    private val keys = arrayListOf<String>()
    override fun uploadImgResult(ok: Boolean, key: String, index1: Int) {
        if (ok) {
            keys.add(key)
            var size = 0
            for (data in adapter.data) {
                if (data.chooseIcon.isNotEmpty()) {
                    size++
                }
            }
            if (index == size - 1) {
                mPresenter?.addReport(
                    Gson().toJson(keys),
                    binding.reportWhyEt.text.toString(),
                    case_type,
                    target_accid
                )
            } else {
                index++
                mPresenter?.uploadPhoto(adapter.data[index].chooseIcon, index)
            }
        } else {
            index = 0
            keys.clear()
            ToastUtil.toast(getString(R.string.upload_pic_fail_please_retry))
        }


    }

    override fun addReportResult(success: Boolean, msg: String) {
        ToastUtil.toast(msg)
        if (success) {
            if (ActivityUtils.isActivityExistsInStack(ReportReasonActivity::class.java)) {
                ActivityUtils.finishActivity(ReportReasonActivity::class.java)
            }
            finish()
        }
    }
}