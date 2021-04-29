package com.sdy.luxurytravelapplication.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.DialogContactCandyReceiveBinding
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog

class ContactCandyReceiveDialog(
    var target_accid: String,
    var get_help_amount: String
) : BaseBindingDialog<DialogContactCandyReceiveBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        initView()
    }

    private fun initView() {
        binding.t2.text = get_help_amount
        binding.okBtn.setOnClickListener { dismiss() }
    }

    override fun show() {
        super.show()
        tagUnlockPopup()
    }

    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.BOTTOM)
        val params = window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT

        params?.windowAnimations = R.style.MyDialogBottomAnimation
        window?.attributes = params
        //点击外部可取消
        setCanceledOnTouchOutside(false)
        setCancelable(true)
    }


    /**
     * 标准是否解锁联系方式弹窗
     */
    private fun tagUnlockPopup() {
        RetrofitHelper.service.tagUnlockPopup(hashMapOf("target_accid" to target_accid)).ssss {

        }
    }

}

