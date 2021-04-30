package com.sdy.luxurytravelapplication.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ActivityUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.DialogVideoIntroduceBeforeBinding
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.model.bean.CopyMvBean
import com.sdy.luxurytravelapplication.ui.activity.VideoIntroduceActivity
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog
import com.shuyu.gsyvideoplayer.GSYVideoManager

class VideoIntroduceBeforeDialog(
    val context1: Context,
    var requestCode: Int = -1
) : BaseBindingDialog<DialogVideoIntroduceBeforeBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        initView()
        getNormalMv()
    }

    private fun initView() {
        binding.apply {
            GlideUtil.loadImg(context1, UserManager.avatar, avator)

            videoPlay.setOnClickListener {
                videoStandard.isVisible = true
                playVideo()

            }


            verifyBtn.setOnClickListener {
                VideoIntroduceActivity.start(ActivityUtils.getTopActivity(), requestCode)
                dismiss()
            }


//            videoStandard.backButton.setOnClickListener {
//                videoStandard.isVisible = false
//                GSYVideoManager.releaseAllVideos()
//            }


        }

    }

    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.BOTTOM)
        val params = window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        params?.windowAnimations = R.style.MyDialogBottomAnimation
        window?.attributes = params
        setCancelable(true)
        setCanceledOnTouchOutside(true)
    }


    private var copyMvBean: CopyMvBean? = null

    private fun getNormalMv() {
        RetrofitHelper.service
            .normalMv(hashMapOf())
            .ss { t ->
                copyMvBean = t.data
                setVideoView()
            }
    }


    private fun setVideoView() {
        binding.apply {
            if (copyMvBean != null && !copyMvBean?.mv_url.isNullOrEmpty()) {
                avator.isVisible = false
                videoPlay.isVisible = true
                playVideo()
            } else {
                GlideUtil.loadImg(
                    context1,
                    UserManager.avatar,
                    avator
                )
                videoStandard.isInvisible = true
                avator.isVisible = true
                videoPlay.isVisible = true
            }
        }
    }

    private fun playVideo() {
        binding.videoStandard.setUp(copyMvBean?.mv_url ?: "", true, "")
        binding.videoStandard.startPlayLogic()
    }

    override fun dismiss() {
        super.dismiss()
        GSYVideoManager.releaseAllVideos()
    }
}
