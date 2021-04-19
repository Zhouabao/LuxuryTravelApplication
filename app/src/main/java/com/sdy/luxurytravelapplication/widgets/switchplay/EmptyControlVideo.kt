package com.sdy.luxurytravelapplication.widgets.switchplay

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer

/**
 *    author : ZFM
 *    date   : 2020/7/1516:42
 *    desc   :
 *    version: 1.0
 */
class EmptyControlVideo : StandardGSYVideoPlayer {

    constructor(context: Context, fullFlag: Boolean?) : super(context, fullFlag!!) {}

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun getLayoutId(): Int {
        return R.layout.empty_control_video
    }

    override fun touchSurfaceMoveFullLogic(absDeltaX: Float, absDeltaY: Float) {
        mChangePosition = false

        mChangeVolume = false

        mBrightness = false
    }


    override fun touchDoubleUp(e: MotionEvent?) {
//        super.touchDoubleUp(e)
    }

    /**
     * 设置视频封面照片
     */
    fun setThumbImageView(url: Any) {
        //增加封面
        val imageView = ImageView(context)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        GlideUtil.loadImg(context, url, imageView)

        thumbImageView = imageView
    }
}