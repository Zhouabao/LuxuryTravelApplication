package com.sdy.luxurytravelapplication.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.blankj.utilcode.util.ActivityUtils
import com.sdy.luxurytravelapplication.R

/**
 *    author : ZFM
 *    date   : 2020/8/2117:51
 *    desc   :
 *    version: 1.0
 */
class LoadingDialog : Dialog(ActivityUtils.getTopActivity(), R.style.MyFullTransparentDialog) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        setContentView(R.layout.loading_layout)
    }

    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.CENTER)
        val params = window?.attributes
        // 设置窗口背景透明度
//        params?.alpha = 0.5f
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = params
        //点击外部可取消
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }

    override fun dismiss() {
        super.dismiss()
//        if (context is Activity) {
//            if (!((context as Activity).isFinishing || (context as Activity).isDestroyed)) {
//            }
//        }

    }

}