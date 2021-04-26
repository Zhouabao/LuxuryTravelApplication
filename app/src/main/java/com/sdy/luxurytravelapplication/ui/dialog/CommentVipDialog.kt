package com.sdy.luxurytravelapplication.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.blankj.utilcode.util.ClickUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.DialogPtVipCommentBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog
import org.greenrobot.eventbus.EventBus

class CommentVipDialog : BaseBindingDialog<DialogPtVipCommentBinding>() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        ClickUtils.applySingleDebouncing(binding.openPtVipBtn){
            CommonFunction.startToVip(context)
        }

    }




    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.BOTTOM)
//        window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val params = window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        params?.windowAnimations = R.style.MyDialogBottomAnimation
        window?.attributes = params
        setCancelable(true)
        setCanceledOnTouchOutside(true)
    }


    override fun show() {
        super.show()
    }


}
