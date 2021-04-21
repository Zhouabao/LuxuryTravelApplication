package com.sdy.luxurytravelapplication.ui.dialog

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

class AccountDangerDialog(var status: Int = VERIFY_NEED_ACCOUNT_DANGER) :
    BaseBindingDialog<DialogAccountDangerBinding>() {

    companion object {
        const val VERIFY_NEED_AVATOR_INVALID = -1 //头像审核不通过去认证
        const val VERIFY_NEED_ACCOUNT_DANGER = 0 //账号异常去认证
        const val VERIFY_ING = 1//认证中
        const val VERIFY_NOT_PASS = 2//未通过
        const val VERIFY_NOT_PASS_FORCE = 3//强制认证未通过
        const val VERIFY_PASS = 4//通过
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        changeVerifyStatus(status)
    }


    fun changeVerifyStatus(status: Int) {
        binding.apply {
            when (status) {
                VERIFY_NEED_AVATOR_INVALID -> {
                    accountDangerResultCl.isVisible = false
                    accountDangerCl.isVisible = true

                    accountDangerImgAlert.isVisible = true
                    humanVerify.isVisible = false
                    accountDangerVerifyStatuLogo.isVisible = true
                    GlideUtil.loadImg(context, UserManager.avatar, accountDangerVerifyStatuLogo)
                    accountDangerTitle.text = context.getString(R.string.face_verify)
                    accountDangerContent.text = context.getString(R.string.please_verify_face)
                    accountDangerBtn.text = context.getString(R.string.goto_verify)
                    accountDangerLoading.isVisible = false
                    accountDangerBtn.isEnabled = true
                    accountDangerBtn.setOnClickListener {
                        CommonFunction.startToFace(context)
                    }
                }
                VERIFY_NEED_ACCOUNT_DANGER -> {
                    accountDangerVerifyStatuLogo.setImageResource(R.drawable.icon_account_danger)
                    accountDangerResultCl.isVisible = false
                    accountDangerCl.isVisible = true

                    accountDangerImgAlert.isVisible = false
                    humanVerify.isVisible = false
                    accountDangerVerifyStatuLogo.isVisible = true
                    accountDangerTitle.text = context.getString(R.string.account_danger)
                    accountDangerContent.text =
                        context.getString(R.string.please_verify_to_clear_danger)
                    accountDangerBtn.text = context.getString(R.string.goto_verify)
                    accountDangerLoading.isVisible = false
                    accountDangerBtn.isEnabled = true
                    accountDangerBtn.setOnClickListener {
                        CommonFunction.startToFace(
                            context,
                            FaceLivenessExpActivity.TYPE_ACCOUNT_DANGER
                        )
                    }
                }
                VERIFY_ING -> {
                    if (ActivityUtils.getTopActivity() is FaceLivenessExpActivity) {
                        accountDangerResultCl.isVisible = true
                        accountDangerCl.isVisible = false

                        GlideUtil.loadImg(
                            context,
                            UserManager.avatar,
                            accountDangerVerifyStatuLogo1
                        )
                        accountDangerImgAlert1.isVisible = false
                        humanVerify1.isVisible = false
                        accountDangerTitle1.text = context.getString(R.string.verify_account_ing)
                        accountDangerContent1.text =
                            context.getString(R.string.please_wait_to_clear_danger)
                        accountDangerBtn1.text = context.getString(R.string.waiting)
                        accountDangerBtn1.isEnabled = false
                    } else {
                        accountDangerResultCl.isVisible = false
                        accountDangerCl.isVisible = true
                        accountDangerImgAlert.isVisible = false
                        humanVerify.isVisible = false
                        accountDangerVerifyStatuLogo.isVisible = true
                        accountDangerVerifyStatuLogo.setImageResource(R.drawable.icon_verify_account_ing)
                        accountDangerTitle.text = context.getString(R.string.verify_account_ing)
                        accountDangerContent.text =
                            context.getString(R.string.please_wait_to_clear_danger)
                        accountDangerBtn.text = ""
                        accountDangerLoading.isVisible = true
                        accountDangerBtn.isEnabled = false
                    }

                }
                VERIFY_NOT_PASS -> {
                    if (ActivityUtils.getTopActivity() is FaceLivenessExpActivity) {
                        accountDangerResultCl.isVisible = true
                        accountDangerCl.isVisible = false

                        GlideUtil.loadImg(
                            context,
                            UserManager.avatar,
                            accountDangerVerifyStatuLogo1
                        )
                        accountDangerImgAlert1.isVisible = true
                        humanVerify1.isVisible = true
//                accountDangerVerifyStatuLogo1.setImageResource(R.drawable.icon_verify_account_not_pass)
                        accountDangerTitle1.text = context.getString(R.string.avata_verify_fail)
                        accountDangerContent1.text =
                            context.getString(R.string.avatar_cannot_pass_verify)
                        accountDangerBtn1.text = context.getString(R.string.change_avatar)
                        humanVerify1.setTextColor(Color.parseColor("#FFFF6318"))
                        accountDangerBtn1.isEnabled = true
                        accountDangerBtn1.setOnClickListener {
                            if (ActivityUtils.getTopActivity() !is MyInfoActivity) {
                                context.startActivity<MyInfoActivity>()
                            } else {
                                dismiss()
                            }
                        }
                        humanVerify1.setOnClickListener {
                            humanVerify(1)
                        }
                    } else {

                        accountDangerResultCl.isVisible = false
                        accountDangerCl.isVisible = true
                        closeBtn.isVisible = true
                        accountDangerImgAlert.isVisible = true
                        humanVerify.isVisible = true
                        accountDangerVerifyStatuLogo.isVisible = true
                        GlideUtil.loadImg(
                            context,
                            UserManager.avatar,
                            accountDangerVerifyStatuLogo
                        )
//                accountDangerVerifyStatuLogo.setImageResource(R.drawable.icon_verify_account_not_pass)
                        accountDangerTitle.text = context.getString(R.string.avata_verify_fail)
                        accountDangerContent.text =
                            context.getString(R.string.avatar_cannot_pass_verify)
                        accountDangerBtn.text = context.getString(R.string.change_avatar)
                        humanVerify.setTextColor(Color.parseColor("#FFFF6318"))
                        accountDangerLoading.isVisible = false
                        accountDangerBtn.isEnabled = true
                        accountDangerBtn.setOnClickListener {
                            if (ActivityUtils.getTopActivity() !is MyInfoActivity)
                                context.startActivity<MyInfoActivity>()
                            else
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
                VERIFY_NOT_PASS_FORCE -> {
                    if (ActivityUtils.getTopActivity() is FaceLivenessExpActivity) {
                        accountDangerResultCl.isVisible = true
                        accountDangerCl.isVisible = false

                        GlideUtil.loadImg(
                            context,
                            UserManager.avatar,
                            accountDangerVerifyStatuLogo1
                        )
                        accountDangerImgAlert1.isVisible = false
                        humanVerify1.isVisible = true
                        accountDangerTitle1.text = context.getString(R.string.avata_verify_fail)
                        accountDangerContent1.text =
                            context.getString(R.string.avatar_cannot_pass_verify)
                        accountDangerBtn1.text = context.getString(R.string.change_avatar)
                        accountDangerBtn1.isEnabled = true
                        accountDangerBtn1.setOnClickListener {
                            if (ActivityUtils.getTopActivity() !is MyInfoActivity) {
                                context.startActivity<MyInfoActivity>()
                            } else {
                                dismiss()
                            }
                        }
                        humanVerify1.setOnClickListener {
                            humanVerify(1)
                        }
                    } else {
                        accountDangerResultCl.isVisible = false
                        accountDangerCl.isVisible = true
                        accountDangerImgAlert.isVisible = false
                        humanVerify.isVisible = true
                        accountDangerVerifyStatuLogo.isVisible = true
                        accountDangerVerifyStatuLogo.setImageResource(R.drawable.icon_verify_account_not_pass)
                        accountDangerTitle.text = context.getString(R.string.avata_verify_fail)
                        accountDangerContent.text =
                            context.getString(R.string.avatar_cannot_pass_verify)
                        accountDangerBtn.text = context.getString(R.string.change_avatar)
                        accountDangerLoading.isVisible = false
                        accountDangerBtn.isEnabled = true
                        accountDangerBtn.setOnClickListener {
                            if (ActivityUtils.getTopActivity() !is MyInfoActivity)
                                context.startActivity<MyInfoActivity>()
                            else
                                dismiss()
                        }
                        humanVerify.setOnClickListener {
                            humanVerify(1)
                        }
                    }
                }
                VERIFY_PASS -> {
                    accountDangerResultCl.isVisible = false
                    accountDangerCl.isVisible = true

                    accountDangerImgAlert.isVisible = true
                    accountDangerImgAlert.setImageResource(R.drawable.icon_video_gou)
                    humanVerify.isVisible = false
                    accountDangerVerifyStatuLogo.isVisible = true
                    GlideUtil.loadImg(
                        context,
                        UserManager.avatar,
                        accountDangerVerifyStatuLogo
                    )
                    accountDangerTitle.text = context.getString(R.string.verify_success)
                    accountDangerContent.text = context.getString(R.string.account_has_clear_danger)
                    accountDangerBtn.text = context.getString(R.string.iknow)
                    accountDangerLoading.isVisible = false
                    accountDangerBtn.isEnabled = true
                    accountDangerBtn.setOnClickListener {
                        dismiss()
                    }
                }
            }
        }
    }


    fun changeVerifyResultStatus(status: Int) {
        binding.apply {
            when (status) {
                VERIFY_ING -> {
                    accountDangerImgAlert.isVisible = false
                    humanVerify.isVisible = false
                    accountDangerVerifyStatuLogo.isVisible = true
                    accountDangerVerifyStatuLogo.setImageResource(R.drawable.icon_verify_account_ing)
                    accountDangerTitle.text = context.getString(R.string.verify_account_ing)
                    accountDangerContent.text =
                        context.getString(R.string.please_wait_to_clear_danger)
                    accountDangerBtn.text = ""
                    accountDangerLoading.isVisible = true
                    accountDangerBtn.isEnabled = false
                }
                VERIFY_NOT_PASS -> {
                    closeBtn.isVisible = true
                    accountDangerImgAlert.isVisible = true
                    humanVerify.isVisible = true
                    accountDangerVerifyStatuLogo.isVisible = true
                    GlideUtil.loadImg(
                        context,
                        UserManager.avatar,
                        accountDangerVerifyStatuLogo
                    )
//                accountDangerVerifyStatuLogo.setImageResource(R.drawable.icon_verify_account_not_pass)
                    accountDangerTitle.text = context.getString(R.string.avata_verify_fail)
                    accountDangerContent.text =
                        context.getString(R.string.avatar_cannot_pass_verify)
                    accountDangerBtn.text = context.getString(R.string.change_avatar)
                    humanVerify.setTextColor(Color.parseColor("#FFFF6318"))
                    accountDangerLoading.isVisible = false
                    accountDangerBtn.isEnabled = true
                    accountDangerBtn.setOnClickListener {
                        if (ActivityUtils.getTopActivity() !is MyInfoActivity)
                            context.startActivity<MyInfoActivity>()
                        else
                            dismiss()
                    }
                    humanVerify.setOnClickListener {
                        humanVerify(1)
                    }

                    closeBtn.setOnClickListener {
                        dismiss()
                    }
                }
                VERIFY_NOT_PASS_FORCE -> {
                    accountDangerImgAlert.isVisible = false
                    humanVerify.isVisible = true
                    accountDangerVerifyStatuLogo.isVisible = true
                    accountDangerVerifyStatuLogo.setImageResource(R.drawable.icon_verify_account_not_pass)
                    accountDangerTitle.text = context.getString(R.string.avata_verify_fail)
                    accountDangerContent.text =
                        context.getString(R.string.avatar_cannot_pass_verify)
                    accountDangerBtn.text = context.getString(R.string.change_avatar)
                    accountDangerLoading.isVisible = false
                    accountDangerBtn.isEnabled = true
                    accountDangerBtn.setOnClickListener {
                        if (ActivityUtils.getTopActivity() !is MyInfoActivity)
                            context.startActivity<MyInfoActivity>()
                        else
                            dismiss()
                    }
                    humanVerify.setOnClickListener {
                        humanVerify(1)
                    }
                }
            }
        }
    }

    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.CENTER)
        val params = window?.attributes
        if (ActivityUtils.getTopActivity() is FaceLivenessExpActivity && (status == VERIFY_ING || status == VERIFY_NOT_PASS_FORCE || status == VERIFY_NOT_PASS)) {
            params?.width = ScreenUtils.getScreenWidth()
            WindowManager.LayoutParams.MATCH_PARENT
            params?.height = ScreenUtils.getScreenHeight()
            WindowManager.LayoutParams.MATCH_PARENT
            params?.windowAnimations = R.style.MyDialogBottomAnimation
            BarUtils.setStatusBarColor(window!!, Color.WHITE)

        } else {
            params?.width = WindowManager.LayoutParams.WRAP_CONTENT
            params?.height = WindowManager.LayoutParams.WRAP_CONTENT
            params?.windowAnimations = R.style.MyDialogCenterAnimation

        }

        window?.attributes = params
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setOnKeyListener { dialogInterface, keyCode, event ->
            keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0
        }
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
                    changeVerifyStatus(VERIFY_ING)
                    ToastUtil.toast(context.getString(R.string.has_commit_human_verify))
                }
            }
    }


}
