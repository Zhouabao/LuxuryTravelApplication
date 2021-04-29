package com.sdy.luxurytravelapplication.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.sdy.luxurytravelapplication.databinding.DialogPlayVideoBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog
import com.shuyu.gsyvideoplayer.GSYVideoManager

class PlayVideoDialog(val mv_url: String) : BaseBindingDialog<DialogPlayVideoBinding>() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        showVideoPreview()
    }

    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.BOTTOM)
//        window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val params = window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.MATCH_PARENT

        window?.attributes = params

        setCancelable(true)
        setCanceledOnTouchOutside(true)
    }

    private fun showVideoPreview() {
        CommonFunction.initVideo(binding.videoPreview, mv_url)

        binding.videoPreview.backButton.setOnClickListener {
            dismiss()
        }
    }

    override fun show() {
        super.show()
    }

    override fun dismiss() {
        super.dismiss()
        GSYVideoManager.releaseAllVideos()

    }
}
