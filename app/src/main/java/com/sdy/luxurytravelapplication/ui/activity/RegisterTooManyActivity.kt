package com.sdy.luxurytravelapplication.ui.activity

import android.app.Activity
import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.databinding.ActivityRegisterTooManyBinding
import org.jetbrains.anko.startActivity

/**
 * 排队等待注册中...
 */
class RegisterTooManyActivity : BaseActivity<ActivityRegisterTooManyBinding>() {
    private val duration by lazy { intent.getIntExtra("duration", 0) }

    companion object {
        fun start(duration: Int, context: Context) {
            context.startActivity<RegisterTooManyActivity>("duration" to duration)
            (context as Activity).finish()
        }
    }

    override fun initData() {

    }

    override fun initView() {
        binding.apply {
            (btnBack.layoutParams as ConstraintLayout.LayoutParams).topMargin =
                BarUtils.getStatusBarHeight() + SizeUtils.dp2px(10F)
            BarUtils.setStatusBarLightMode(this@RegisterTooManyActivity, false)
            btnBack.setOnClickListener { finish() }
            timeRunTextView.startTime(duration.toLong(), "2")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.timeRunTextView.stopTime()
    }
    override fun start() {
    }

}