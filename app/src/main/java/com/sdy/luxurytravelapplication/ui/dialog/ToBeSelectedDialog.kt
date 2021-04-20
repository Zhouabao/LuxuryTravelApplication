package com.sdy.luxurytravelapplication.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.DialogToBeSelectedBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.ui.activity.VipChargeActivity
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog

/**
 *    author : ZFM
 *    date   : 2021/3/1911:22
 *    desc   :
 *    version: 1.0
 */
class ToBeSelectedDialog(val isSelected: Boolean) : BaseBindingDialog<DialogToBeSelectedBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        initView()
    }

    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.BOTTOM)
        val params = window?.attributes
//        params?.width = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(15F) * 2
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT

        params?.windowAnimations = R.style.MyDialogBottomAnimation
//        params?.y = SizeUtils.dp2px(15F)
        window?.attributes = params
        //点击外部可取消
        setCanceledOnTouchOutside(true)
    }


    private fun initView() {

        if (isSelected) {
            binding.selectedTitle.text = "您已成为精选用户"
            binding.selectedContent.text = "已有20人通过精选用户看到了你"
            binding.tobeSelectedBtn.text = "谁看过我"
            binding.selectedLogo.isVisible = true
            GlideUtil.loadRoundImgCenterCrop(
                context,
                UserManager.avatar,
                binding.userAvatar,
                SizeUtils.dp2px(25F)
            )
        } else {
            if (UserManager.gender == 1) {
                binding.selectedTitle.text = "成为精选用户"
                binding.selectedContent.text = "成为高级会员，让女生第一眼看到你"
                binding.tobeSelectedBtn.text = "开通高级会员"
                //成为精选用户
                ClickUtils.applySingleDebouncing(binding.tobeSelectedBtn) {
                    CommonFunction.startToVip(context)
                    dismiss()
                }
            } else {
                binding.selectedTitle.text = "成为精选用户"
                binding.selectedContent.text = "上传视频介绍，让优质男性用户第一眼就看到你"
                binding.tobeSelectedBtn.text = "上传视频介绍"
                //成为精选用户
                ClickUtils.applySingleDebouncing(binding.tobeSelectedBtn) {
                    CommonFunction.startToVideoIntroduce(context)
                    dismiss()
                }
            }
        }


    }
}