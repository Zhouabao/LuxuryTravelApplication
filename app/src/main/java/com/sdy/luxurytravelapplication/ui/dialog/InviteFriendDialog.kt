package com.sdy.luxurytravelapplication.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.blankj.utilcode.util.ClickUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.DialogInviteFriendBinding
import com.sdy.luxurytravelapplication.ui.activity.MyInviteActivity
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog
import org.jetbrains.anko.startActivity

/**
 *    author : ZFM
 *    date   : 2020/7/2811:05
 *    desc   : 邀请好友分享
 *    version: 1.0
 */
class InviteFriendDialog : BaseBindingDialog<DialogInviteFriendBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()

        initView()
    }

    private fun initView() {
       binding.apply {
           ClickUtils.applySingleDebouncing(receiveRedPacket) {
               context.startActivity<MyInviteActivity>()
           }
           closeBtn.setOnClickListener {
               dismiss()
           }
       }
    }

    override fun dismiss() {
        super.dismiss()
        UserManager.showCompleteUserCenterDialog = true
    }

    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.CENTER)
        val params = window?.attributes
        params?.width = WindowManager.LayoutParams.WRAP_CONTENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT

        params?.windowAnimations = R.style.MyDialogCenterAnimation
        window?.attributes = params
        //点击外部可取消
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }
}
