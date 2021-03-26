package com.sdy.luxurytravelapplication.widgets.switchplay

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.SwitchVideoBinding
import com.sdy.luxurytravelapplication.viewbinding.inflateBindingWithGeneric
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView


/**
 *    author : ZFM
 *    date   : 2019/7/1310:03
 *    desc   :
 *    version: 1.0
 */
class SwitchVideo : StandardGSYVideoPlayer {


    constructor(context: Context, fullFlag: Boolean?) : super(context, fullFlag!!) {}

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun init(context: Context) {
        super.init(context)
        if (mIfCurrentIsFullscreen) {
            binding.detailBtn.visibility = View.GONE
        }
    }

     lateinit var binding:SwitchVideoBinding

    override fun getLayoutId(): Int {
        binding = SwitchVideoBinding.inflate(LayoutInflater.from(context), this, false)
        return R.layout.switch_video
    }


    fun setSwitchUrl(url: String) {
        mUrl = url
        mOriginUrl = url
    }

    fun setSwitchCache(cache: Boolean) {
        mCache = cache
    }

    fun setSwitchTitle(title: String) {
        mTitle = title
    }

    fun setSurfaceToPlay() {
        addTextureView()
        gsyVideoManager.setListener(this)
        checkoutState()
    }

    fun saveState(): SwitchVideo {
        val switchVideo = SwitchVideo(context)
        cloneParams(this, switchVideo)
        return switchVideo
    }

    fun cloneState(switchVideo: SwitchVideo) {
        cloneParams(switchVideo, this)
    }


    override fun updateStartImage() {
//        if (mCurrentState == GSYVideoView.CURRENT_STATE_PLAYING) {
//            start.setImageResource(com.sdy.jitangapplication.R.drawable.icon_pause_white)
//        } else if (mCurrentState == GSYVideoView.CURRENT_STATE_ERROR) {
//            start.setImageResource(com.sdy.jitangapplication.R.drawable.icon_play_white)
//        } else {
//            start.setImageResource(com.sdy.jitangapplication.R.drawable.icon_play_white)
//        }


        if (mCurrentState == GSYVideoView.CURRENT_STATE_PLAYING) {
            binding.start.setImageResource(R.drawable.icon_record_pause)
            binding.start.isVisible = false
        } else if (mCurrentState == GSYVideoView.CURRENT_STATE_ERROR || mCurrentState == GSYVideoView.CURRENT_STATE_NORMAL) {
            binding.start.setImageResource(R.drawable.icon_record_play)
            binding.start.isVisible = true

        } else {
            binding.start.isVisible = false

//            start.setImageResource(com.sdy.jitangapplication.R.drawable.icon_play_white)
        }
    }
}
