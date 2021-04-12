package com.sdy.luxurytravelapplication.ui.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import com.blankj.utilcode.util.SpanUtils
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.DialogPrivacyBinding
import com.sdy.luxurytravelapplication.ui.activity.ProtocolActivity
import com.sdy.luxurytravelapplication.utils.ToastUtil
import org.jetbrains.anko.startActivity

/**
 *    author : ZFM
 *    date   : 2021/3/1911:05
 *    desc   :
 *    version: 1.0
 */
class PrivacyDialog(context: Context) :
    BaseBindingDialog<DialogPrivacyBinding>(context, R.style.MyDialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        initView()
    }

    private fun initView() {
        val clickSpanPrivacy = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val intent = Intent(context, ProtocolActivity::class.java)
                intent.putExtra("type", ProtocolActivity.TYPE_PRIVACY_PROTOCOL)
                context.startActivity(intent)
            }

        }

        val clickSpanProtocol = object : ClickableSpan() {
            override fun onClick(p0: View) {

                val intent = Intent(context, ProtocolActivity::class.java)
                intent.putExtra("type", ProtocolActivity.TYPE_USER_PROTOCOL)
                context.startActivity(intent)
            }

        }
        val clickSpanPrivacy1 = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val intent = Intent(context, ProtocolActivity::class.java)
                intent.putExtra("type", ProtocolActivity.TYPE_PRIVACY_PROTOCOL)
                context.startActivity(intent)
            }

        }

        val clickSpanProtocol1 = object : ClickableSpan() {
            override fun onClick(p0: View) {
                val intent = Intent(context, ProtocolActivity::class.java)
                intent.putExtra("type", ProtocolActivity.TYPE_USER_PROTOCOL)
                context.startActivity(intent)
            }

        }
        binding.privacyContent.highlightColor = context.resources.getColor(R.color.transparent)
        SpanUtils.with(binding.privacyContent)
            .append(context.getString(R.string.privacy_t1))
            .append(context.resources.getString(R.string.user_protocol))
            .setClickSpan(clickSpanProtocol1)
            .setForegroundColor(context.resources.getColor(R.color.colorAccent))
            .append(context.getString(R.string.login_tip_and))
            .setForegroundColor(context.resources.getColor(R.color.color_333333))
            .append(context.resources.getString(R.string.privacy_protocol))
            .setClickSpan(clickSpanPrivacy1)
            .setForegroundColor(context.resources.getColor(R.color.colorAccent))
            .append(context.getString(R.string.privacy_t2))
            .setForegroundColor(context.resources.getColor(R.color.color_333333))
            .append(context.getString(R.string.privacy_t3))
            .setForegroundColor(context.resources.getColor(R.color.color_333333))
            .append(context.getString(R.string.privacy_t7))
            .setForegroundColor(context.resources.getColor(R.color.colorAccent))
            .append(context.getString(R.string.privacy_t8))
            .setForegroundColor(context.resources.getColor(R.color.color_333333))
            .append(context.resources.getString(R.string.user_protocol))
            .setClickSpan(clickSpanProtocol)
            .setForegroundColor(context.resources.getColor(R.color.colorAccent))
            .append(context.getString(R.string.login_tip_and))
            .setForegroundColor(context.resources.getColor(R.color.color_333333))
            .append(context.resources.getString(R.string.privacy_protocol))
            .setClickSpan(clickSpanPrivacy)
            .setForegroundColor(context.resources.getColor(R.color.colorAccent))
            .append(context.getString(R.string.privacy_t4))
            .setForegroundColor(context.resources.getColor(R.color.color_333333))
            .create()


        binding.disAgree.setOnClickListener {
            ToastUtil.toast(
                context.getString(R.string.privacy_t5)
                        + "${context.resources.getString(R.string.user_protocol)}"
                        + context.getString(R.string.login_tip_and)
                        + "${context.resources.getString(R.string.privacy_protocol)}"
                        + context.getString(R.string.privacy_t6)
            )
        }

    }

    private fun initWindow() {
//        val window = this.window
//        window?.setGravity(Gravity.CENTER)
//        val params = window?.attributes
//        params?.width = WindowManager.LayoutParams.MATCH_PARENT
//        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
//        window?.attributes = params
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setOnKeyListener { dialogInterface, keyCode, event ->
            keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0
        }
    }

    override fun dismiss() {
        super.dismiss()
    }

}