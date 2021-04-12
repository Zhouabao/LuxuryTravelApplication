package com.sdy.luxurytravelapplication.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.ActivityProtocolBinding
import com.sdy.luxurytravelapplication.databinding.LayoutActionbarBinding
import org.jetbrains.anko.startActivity

class ProtocolActivity : BaseActivity<ActivityProtocolBinding>() {
    companion object {
        const val TYPE_PRIVACY_PROTOCOL = 1
        const val TYPE_USER_PROTOCOL = 2
        const val TYPE_OTHER = 3
        const val TYPE_CANDY_USAGE = 4

        fun start(context: Context, type: Int) {
            context.startActivity<ProtocolActivity>("type" to type)

        }
    }

    private val type by lazy { intent.getIntExtra("type", TYPE_PRIVACY_PROTOCOL) }

    override fun initData() {
        when (type) {
            TYPE_PRIVACY_PROTOCOL -> {
                binding.  actionbarCl.actionbarTitle.text = resources.getString(R.string.privacy_title)
            }
            TYPE_USER_PROTOCOL -> {
                binding.     actionbarCl.actionbarTitle.text = resources.getString(R.string.user_title)
            }
        }


    }

    override fun initView() {
        binding.actionbarCl.btnBack.setOnClickListener {
            finish()
        }
        initWebview()

    }

    override fun start() {
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebview() {

//         //支持javascript web.getSettings().setJavaScriptEnabled(true);
//
//// 设置可以支持缩放 web.getSettings().setSupportZoom(true);
//
//// 设置出现缩放工具 web.getSettings().setBuiltInZoomControls(true);
//
////扩大比例的缩放 web.getSettings().setUseWideViewPort(true);
//
////自适应屏幕 web.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
//
//web.getSettings().setLoadWithOverviewMode(true);


        //支持javascript
        binding.webView.settings.javaScriptEnabled = true
        // 设置可以支持缩放
        binding.webView.settings.setSupportZoom(true)
        // 设置出现缩放工具
        binding.webView.settings.builtInZoomControls = true
        //扩大比例的缩放
        binding.webView.settings.useWideViewPort = true

        binding.webView.settings.blockNetworkImage = false//解决图片不显示
        binding.webView.settings.mixedContentMode =
            WebSettings.MIXED_CONTENT_ALWAYS_ALLOW//混合模式，允许https中加载http图片
        //自适应屏幕
        binding.webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)

                return true

            }
//            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
//                view.loadUrl(request.url.toString())
//
//                return true
//            }
        }
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                if (!title.isNullOrEmpty())
                    binding.actionbarCl.actionbarTitle.text = title
            }
        }


        when (type) {
            TYPE_PRIVACY_PROTOCOL -> binding.webView.loadUrl(Constants.PRIVACY_URL)
            TYPE_USER_PROTOCOL -> binding.webView.loadUrl(Constants.PROTOCOL_URL)
            TYPE_CANDY_USAGE -> binding.webView.loadUrl(
                "${Constants.BASE_URL}protocol/candyHelp${Constants.END_BASE_URL}"
            )
            TYPE_OTHER -> binding.webView.loadUrl(intent.getStringExtra("url") ?: "")
        }


    }

}