package com.sdy.luxurytravelapplication.ui.activity

import androidx.core.view.isVisible
import com.blankj.utilcode.util.ActivityUtils
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.databinding.ActivitySweetHeartVerifyingBinding

/**
 * 认证中
 */
class SweetHeartVerifyingActivity : BaseActivity<ActivitySweetHeartVerifyingBinding>() {
    override fun initData() {

    }

    override fun initView() {
        binding.apply {
            barCl.divider.isVisible = true
            barCl.btnBack.setOnClickListener {
                clearActivity()
            }
            barCl.actionbarTitle.text = "认证审核中"
            completeBtn.setOnClickListener {
                clearActivity()
            }
        }
    }

    private fun clearActivity() {
        if (ActivityUtils.isActivityExistsInStack(ChooseVerifyActivity::class.java)) {
            ActivityUtils.finishActivity(ChooseVerifyActivity::class.java)
        }
        if (ActivityUtils.isActivityExistsInStack(UploadVerifyInfoActivity::class.java)) {
            ActivityUtils.finishActivity(UploadVerifyInfoActivity::class.java)
        }
        if (ActivityUtils.isActivityExistsInStack(UploadVerifyPublicActivity::class.java)) {
            ActivityUtils.finishActivity(UploadVerifyPublicActivity::class.java)
        }
//        EventBus.getDefault().post(RefreshSweetEvent())
//        EventBus.getDefault().post(CloseDialogEvent())
        finish()

    }

    override fun start() {

    }

}