package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ActivityUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.databinding.ActivitySweetHeartVerifyingBinding
import com.sdy.luxurytravelapplication.event.FemaleVideoEvent
import com.sdy.luxurytravelapplication.event.RefreshSweetEvent
import com.sdy.luxurytravelapplication.event.TopCardEvent
import com.sdy.luxurytravelapplication.event.UpdateApproveEvent
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity

/**
 * 认证中
 */
class SweetHeartVerifyingActivity : BaseActivity<ActivitySweetHeartVerifyingBinding>() {
    companion object {
        const val TYPE_PERSON_INFO = 1
        const val TYPE_EDUCATION = 2
        const val TYPE_WEALTH = 3
        const val TYPE_WECHAT = 5
        const val TYPE_GIRL_VIDEO = 7
        const val TYPE_GIRL_VOICE = 8
        const val TYPE_REAL_FACE = 9

        const val TYPE_HOUSE = 1
        const val TYPE_CAR = 2
        const val TYPE_FIGURE = 3
        const val TYPE_JOB = 4

        fun start(context: Context, type: Int) {
            context.startActivity<SweetHeartVerifyingActivity>("type" to type)
        }

    }

    private val type by lazy { intent.getIntExtra("type", 0) }

    override fun initData() {
        binding.apply {
            when (type) {
                TYPE_PERSON_INFO -> {
                    barCl.actionbarTitle.text = getString(R.string.verify_title_personal_info)
                }
                TYPE_EDUCATION -> {
                    barCl.actionbarTitle.text = getString(R.string.verify_title_education)
                }
                TYPE_WEALTH -> {
                    barCl.actionbarTitle.text = getString(R.string.verify_title_assets)
                }
                TYPE_CAR -> {
                    barCl.actionbarTitle.text = getString(R.string.verify_title_luxury_car)
                }
                TYPE_WECHAT -> {
                    barCl.actionbarTitle.text = getString(R.string.verify_title_wechat)
                }
                TYPE_JOB -> {
                    barCl.actionbarTitle.text = getString(R.string.verify_title_job)
                }

                TYPE_GIRL_VIDEO -> {
                    barCl.actionbarTitle.text = getString(R.string.verify_title_video)
                    t1.text = getString(R.string.video_under_review)
                }
                TYPE_GIRL_VOICE -> {
                    barCl.actionbarTitle.text = getString(R.string.verify_title_voice)
                    t1.text = getString(R.string.audio_under_review)
                }
                TYPE_REAL_FACE -> {
                    barCl.actionbarTitle.text = getString(R.string.verify_real)
                    t1.text = getString(R.string.verify_real_under_review)
                }
            }


        }
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
        if (ActivityUtils.isActivityExistsInStack(VideoIntroduceActivity::class.java)) {
            ActivityUtils.finishActivity(VideoIntroduceActivity::class.java)
        }
//        EventBus.getDefault().post(RefreshSweetEvent())
//        EventBus.getDefault().post(CloseDialogEvent())
        if (type == TYPE_GIRL_VIDEO) {
            //聊天页面刷新认证数据数据
            EventBus.getDefault().post(UpdateApproveEvent())

            //刷新顶部精选数据
            EventBus.getDefault().post(TopCardEvent(true))

            EventBus.getDefault().post(FemaleVideoEvent(2))

            //更新甜心圈认证状态
            EventBus.getDefault().post(RefreshSweetEvent())
        }
        finish()

    }

    override fun start() {

    }

}