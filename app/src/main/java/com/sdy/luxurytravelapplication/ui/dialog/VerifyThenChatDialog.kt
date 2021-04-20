package com.sdy.luxurytravelapplication.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.SpanUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.DialogVerifyThenChatBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog

class VerifyThenChatDialog(
    val context1: Context,
    var type: Int = FROM_CHAT_VERIFY
) : BaseBindingDialog<DialogVerifyThenChatBinding>() {
    companion object {
        const val FROM_VERIFY_MUST_KNOW = 1
        const val FROM_CHAT_VERIFY = 2
        const val FROM_CONTACT_VERIFY = 3
        const val FROM_APPLY_DATING = 4
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        initView()
    }


    fun initView() {
        binding.apply {
            when (type) {
                FROM_VERIFY_MUST_KNOW -> {//认证须知
                    val parmas = accountDangerLogo.layoutParams as ConstraintLayout.LayoutParams
                    parmas.width = SizeUtils.dp2px(86F)
                    parmas.height = SizeUtils.dp2px(86F)
                    accountDangerLogo.layoutParams = parmas
                    GlideUtil.loadCircleImg(context1, true, UserManager.avatar, accountDangerLogo)
                    moreInfoTitle.text = context1.getString(R.string.verify_know)
                    t2.text = context1.getString(R.string.verify_notice1)
                    verifyBtn.text = context1.getString(R.string.ok_1)
                    verifyBtn.setBackgroundResource(R.drawable.shape_1ed0a7_29dp)
                    closeBtn.isVisible = true
                }
                FROM_CHAT_VERIFY -> {//认证才能聊天
                    closeBtn.isVisible = false
                    accountDangerLogo.setImageResource(R.drawable.icon_verify_to_logo)
                    moreInfoTitle.text = context1.getString(R.string.verify_then_chat)
                    SpanUtils.with(t2)
                        .append(context1.getString(R.string.verify_then_chat_content))
                        .create()
                    verifyBtn.text = context1.getString(R.string.verify_now)
                    verifyBtn.setBackgroundResource(R.drawable.shape_1ed0a7_29dp)
                }
                FROM_CONTACT_VERIFY -> {//认证才能解锁联系方式
                    closeBtn.isVisible = false
                    accountDangerLogo.setImageResource(R.drawable.icon_verify_then_contact)
                    moreInfoTitle.text = context1.getString(R.string.verify_then_unlock)
                    SpanUtils.with(t2)
                        .append(context1.getString(R.string.face_then_chat_to_real))
                        .create()
                    verifyBtn.text = context1.getString(R.string.verify_now)
                    verifyBtn.setBackgroundResource(R.drawable.shape_1ed0a7_29dp)
                }
                FROM_APPLY_DATING -> {//认证才能解锁联系方式
                    closeBtn.isVisible = false
                    accountDangerLogo.setImageResource(R.drawable.icon_verify_to_logo)
                    moreInfoTitle.text = context1.getString(R.string.verify_then_apply_dating)
                    SpanUtils.with(t2)
                        .append(context1.getString(R.string.face_then_chat_to_real))
                        .create()
                    verifyBtn.text = context1.getString(R.string.verify_now)
                    verifyBtn.setBackgroundResource(R.drawable.shape_1ed0a7_29dp)
                }
            }
            ClickUtils.applySingleDebouncing(verifyBtn){
                when (type) {
                    FROM_APPLY_DATING,
                    FROM_CHAT_VERIFY,
                    FROM_CONTACT_VERIFY -> {
                        CommonFunction.startToFace(context1)
                        dismiss()
                    }
                    FROM_VERIFY_MUST_KNOW -> {
                        dismiss()
                    }
                }

            }

           ClickUtils.applySingleDebouncing(closeBtn) {
                dismiss()
            }

        }
    }

    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.BOTTOM)
        val params = window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        params?.windowAnimations = R.style.MyDialogBottomAnimation
        window?.attributes = params
        setCancelable(true)
        setCanceledOnTouchOutside(true)
    }
}
