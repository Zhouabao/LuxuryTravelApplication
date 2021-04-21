package com.sdy.luxurytravelapplication.ui.dialog

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ActivityUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ChangeAvatorRealManDialogLayoutBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.ui.activity.MyInfoActivity
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog
import org.jetbrains.anko.startActivity

class ChangeAvatarRealManDialog(
    var status: Int = VERIFY_NEED_REAL_MAN
):BaseBindingDialog<ChangeAvatorRealManDialogLayoutBinding>() {

    companion object {
        const val VERIFY_NEED_VALID_REAL_MAN = 1 //替换为合规的真人照片
        const val VERIFY_NEED_REAL_MAN = 2 //替换真人（非真人）
 
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        initView()
    }


    private fun initView() {
        binding.apply {
            GlideUtil.loadCircleImg(context, UserManager.avatar, accountImg)
            close.setOnClickListener {
                dismiss()
            }
            accountDangerBtn.setOnClickListener {
                if (ActivityUtils.getTopActivity() !is MyInfoActivity)
                    context.startActivity<MyInfoActivity>()
                dismiss()
            }



            if (UserManager.gender == 1) {
                standardImg.setImageResource(R.drawable.icon_standard_man_avator)
            } else {
                standardImg.setImageResource(R.drawable.icon_standard_woman_avator)
            }

            when (status) {
                VERIFY_NEED_REAL_MAN -> {
                    setCancelable(true)
                    setCanceledOnTouchOutside(true)
                    setOnKeyListener { dialogInterface, keyCode, event ->
                        false
                    }
                    close.isVisible = false
                    accountDangerTitle.text = context.getString(R.string.avatar_not_pass)
                    accountDangerContent.text = context.getString(R.string.please_change_real_to_match_more)
                    accountDangerBtn.text = context.getString(R.string.replace_now)
                }
                VERIFY_NEED_VALID_REAL_MAN -> {
                    setCancelable(true)
                    setCanceledOnTouchOutside(true)
                    setOnKeyListener { dialogInterface, keyCode, event ->
                        false
                    }
                    close.isVisible = true
                    accountDangerTitle.text = context.getString(R.string.please_change_avatar)
                    accountDangerContent.text = context.getString(R.string.please_change_standard_avatar)
                    accountDangerBtn.text = context.getString(R.string.replace_now)

                }
            }
        }


    }

    override fun dismiss() {
        super.dismiss()
    }


    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.CENTER)
        val params = window?.attributes
        params?.width = WindowManager.LayoutParams.WRAP_CONTENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        params?.windowAnimations = R.style.MyDialogCenterAnimation
        window?.attributes = params

    }

}
