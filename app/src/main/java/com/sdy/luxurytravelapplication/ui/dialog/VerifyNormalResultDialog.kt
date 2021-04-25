package com.sdy.luxurytravelapplication.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ClickUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.DialogVerifyNormalResultBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.ui.activity.VideoIntroduceActivity
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog

class VerifyNormalResultDialog(val status: Int = 0) :
    BaseBindingDialog<DialogVerifyNormalResultBinding>() {
    companion object {
        const val VERIFY_NORMAL_NOTPASS_CHANGE_VIDEO = 93 //视频认证失败
        const val VERIFY_NORMAL_PASS = 91 //视频认证通过
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        changeVerifyStatus(status)
    }

    fun changeVerifyStatus(status: Int) {
        binding.apply {
            GlideUtil.loadImg(context, UserManager.avatar, userAvatar)
            when (status) {
                VERIFY_NORMAL_NOTPASS_CHANGE_VIDEO -> {
                    verifyStateLogo.setImageResource(R.drawable.icon_video_cha)
                    verifyState.text = context.getString(R.string.verify_fail)
                    verifyTip.text = context.getString(R.string.mv_fail_content)
                    continueBtn.text = context.getString(R.string.re_record)
                    ClickUtils.applySingleDebouncing(continueBtn) {
                        if (ActivityUtils.getTopActivity() !is VideoIntroduceActivity)
                            CommonFunction.startToVideoIntroduce(context)
                        dismiss()
                    }
                }
                VERIFY_NORMAL_PASS -> {
                    verifyStateLogo.setImageResource(R.drawable.icon_video_gou)
                    verifyState.text = context.getString(R.string.mv_pass)
                    verifyTip.text = context.getString(R.string.mv_pass_content)
                    continueBtn.text = context.getString(R.string.continue_use)
                    continueBtn.setOnClickListener {
                        dismiss()
                    }
                }

            }
        }
    }

    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.CENTER)
        val params = window?.attributes
        params?.width =WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        params?.windowAnimations = R.style.MyDialogCenterAnimation
        window?.attributes = params
        setCancelable(true)
        setCanceledOnTouchOutside(true)
    }
}
