package com.sdy.luxurytravelapplication.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.DialogContactPassBinding
import com.sdy.luxurytravelapplication.ui.activity.ChangeUserContactActivity
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog
import org.jetbrains.anko.startActivity

class ContactNotPassDialog : BaseBindingDialog<DialogContactPassBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        changeVerifyStatus()
    }

    fun changeVerifyStatus() {
       binding.apply {
           //关闭弹窗
           closeBtn.setOnClickListener {
               dismiss()
           }


           //立即替换
           ClickUtils.applySingleDebouncing(changeBtn){
               context.startActivity<ChangeUserContactActivity>()
           }
       }
    }

    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.CENTER)
        val params = window?.attributes
        params?.width =WindowManager.LayoutParams.WRAP_CONTENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        params?.windowAnimations = R.style.MyDialogCenterAnimation
        window?.attributes = params
        setCancelable(true)
        setCanceledOnTouchOutside(true)
    }
}
