package com.sdy.luxurytravelapplication.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.DialogVerifyLevelDescrBinding
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog

/**
 *    author : ZFM
 *    date   : 2021/3/1911:22
 *    desc   :
 *    version: 1.0
 */
class VerifyLevelDescrDialog : BaseBindingDialog<DialogVerifyLevelDescrBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        binding.knowBtn.setOnClickListener {
            dismiss()
        }
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


}