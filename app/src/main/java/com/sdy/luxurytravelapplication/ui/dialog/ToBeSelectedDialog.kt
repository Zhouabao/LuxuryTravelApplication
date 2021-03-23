package com.sdy.luxurytravelapplication.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.blankj.utilcode.util.ClickUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.DialogToBeSelectedBinding
import com.sdy.luxurytravelapplication.ui.activity.ChooseVerifyActivity
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog
import org.jetbrains.anko.startActivity

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
        } else {
            if (UserManager.gender == 1) {
                binding.selectedTitle.text = "成为精选用户"
                binding.selectedContent.text = "成为高级会员，让女生第一眼看到你"
                binding.tobeSelectedBtn.text = "开通黄金会员"
            } else {
                binding.selectedTitle.text = "成为精选用户"
                binding.selectedContent.text = "上传视频介绍，让优质男性用户第一眼就看到你"
                binding.tobeSelectedBtn.text = "上传视频介绍"
            }
        }
        //成为精选用户
        ClickUtils.applySingleDebouncing(binding.tobeSelectedBtn) {
            context.startActivity<ChooseVerifyActivity>()
            dismiss()
        }


    }
}