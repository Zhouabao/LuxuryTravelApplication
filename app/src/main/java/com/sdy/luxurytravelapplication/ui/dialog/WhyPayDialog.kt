package com.sdy.luxurytravelapplication.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.DialogWhyPayBinding
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog

/**
 *    author : ZFM
 *    date   : 2021/3/2210:08
 *    desc   :
 *    version: 1.0
 */
class WhyPayDialog(val context1: Context) : BaseBindingDialog<DialogWhyPayBinding>(context1, R.style.MyDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        initView()

    }

    private fun initView() {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        binding.knowBtn.setOnClickListener { dismiss() }

    }

    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.BOTTOM)
        val params = window?.attributes
        params?.width = ScreenUtils.getScreenWidth()- SizeUtils.dp2px(20F)
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        params?.windowAnimations = R.style.MyDialogBottomAnimation
        window?.attributes = params

    }

    override fun show() {
        super.show()
    }


}