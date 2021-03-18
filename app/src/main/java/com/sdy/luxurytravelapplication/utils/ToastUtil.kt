package com.sdy.luxurytravelapplication.utils

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.SnackbarUtils
import com.blankj.utilcode.util.ToastUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.LayoutCustonTipToastBinding

/**
 *    author : ZFM
 *    date   : 2020/10/2920:54
 *    desc   :
 *    version: 1.0
 */
object ToastUtil {

    fun toast(msg: String) {
        ToastUtils.make().setBgColor(Color.parseColor("#CC000000"))
            .setTextColor(Color.WHITE)
            .setGravity(Gravity.CENTER, 0, 0)
            .show(msg)
    }






    fun customTipToast(context: Context, msg: String, icon: Int) {
        val toastView = LayoutInflater.from(context).inflate(R.layout.layout_custon_tip_toast, null)
        val binding = LayoutCustonTipToastBinding.bind(toastView)
        binding.toastIcon.setImageResource(icon)
        binding.toastContent.text = msg
        ToastUtils.make()
            .setGravity(Gravity.CENTER, 0, 0)
            .show(toastView)
    }
}