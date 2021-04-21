package com.sdy.luxurytravelapplication.ui.dialog

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.WindowManager
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ScreenUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.DialogAccountDangerBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.liveface.FaceLivenessExpActivity
import com.sdy.luxurytravelapplication.ui.activity.MyInfoActivity
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog
import org.jetbrains.anko.startActivity

class HumanVerifyDialog(val type: Int, val showToast: Boolean):
    BaseBindingDialog<DialogAccountDangerBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        changeVerifyStatus()
    }


    fun changeVerifyStatus() {
        binding.apply {
            closeBtn.isVisible = true
            accountDangerImgAlert.isVisible = true
            humanVerify.isVisible = true
            accountDangerVerifyStatuLogo.isVisible = true
            GlideUtil.loadCircleImg(
                context,
                UserManager.avatar,
                accountDangerVerifyStatuLogo
            )
            accountDangerTitle.text = context.getString(R.string.avata_verify_fail)

            accountDangerContent.text =  context.getString(R.string.avatar_compare_fail)
            accountDangerBtn.text = context.getString(R.string.change_avatar)
            humanVerify.setTextColor(Color.parseColor("#FFFF6318"))
            accountDangerLoading.isVisible = false
            accountDangerBtn.isEnabled = true
            accountDangerBtn.setOnClickListener {
                context.startActivity<MyInfoActivity>(
                    "showToast" to true,
                    "type" to type
                )
                dismiss()
            }
            humanVerify.setOnClickListener {
                humanVerify(1)
            }

            closeBtn.setOnClickListener {
                dismiss()
            }
        }
    }


    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.CENTER)
        val params = window?.attributes
        params?.width = WindowManager.LayoutParams.WRAP_CONTENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        params?.windowAnimations = R.style.MyDialogCenterAnimation
        window?.attributes = params
        setCancelable(true)
        setCanceledOnTouchOutside(false)
    }

    /**
     * 人工审核
     * 1 人工认证 2重传头像或则取消
     */
    fun humanVerify(type: Int) {
        val params = hashMapOf<String, Any>()
        params["type"] = type
        RetrofitHelper.service
            .humanAduit(params)
            .ss { t ->
                if (t.code == 200) {
                    ToastUtil.toast(context.getString(R.string.has_commit_human_verify))
                    dismiss()
                }
            }
    }


}
