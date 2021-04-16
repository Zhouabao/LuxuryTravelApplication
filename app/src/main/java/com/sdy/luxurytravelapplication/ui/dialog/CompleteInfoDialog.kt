package com.sdy.luxurytravelapplication.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.blankj.utilcode.util.ActivityUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.DialogCompleteInfoBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.ui.activity.MyInfoActivity
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog
import org.jetbrains.anko.startActivity

/**
 *    author : ZFM
 *    date   : 2021/3/1911:22
 *    desc   :完善个人信息
 *    version: 1.0
 */
class CompleteInfoDialog(val normalPercent: Int = 0) : BaseBindingDialog<DialogCompleteInfoBinding>() {

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
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }


    private fun initView() {
        binding.completePercent.text = context.resources.getString(R.string.complete_precent, normalPercent)
        GlideUtil.loadImg(context, UserManager.avatar, binding.userAvator)
        binding.completeInfoBtn.setOnClickListener {
            context.startActivity<MyInfoActivity>()
            dismiss()
        }
    }
}