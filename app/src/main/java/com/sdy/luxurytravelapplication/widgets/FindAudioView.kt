package com.sdy.luxurytravelapplication.widgets

import `in`.xiandan.countdowntimer.CountDownTimerSupport
import `in`.xiandan.countdowntimer.OnCountDownTimerListener
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.LayoutFindAudioBinding
import com.sdy.luxurytravelapplication.event.OneVoicePlayEvent
import com.sdy.luxurytravelapplication.ui.activity.PublishActivity
import com.sdy.luxurytravelapplication.utils.UriUtils
import com.sdy.luxurytravelapplication.widgets.player.MediaPlayerHelper
import com.sdy.luxurytravelapplication.widgets.player.UpdateVoiceTimeThread
import org.greenrobot.eventbus.EventBus

/**
 *    author : ZFM
 *    date   : 2020/8/3114:22
 *    desc   : 别人的语音
 *    version: 1.0
 */
class FindAudioView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    def: Int = 0
) :
    FrameLayout(context, attrs, def) {


    companion object {
        const val MEDIA_PREPARE = 0
        const val MEDIA_PLAY = 1
        const val MEDIA_PAUSE = 2
        const val MEDIA_STOP = 3
        const val MEDIA_ERROR = 4
    }

    private lateinit var binding: LayoutFindAudioBinding

    init {
        binding = LayoutFindAudioBinding.inflate(
            LayoutInflater.from(context),
            this,
            false
        )
        addView(binding.root)
    }

    private var filePath = ""
    var positionId = 0

    var duration: Int = 0
    private var playState: Int = MEDIA_PREPARE

    //        const val TYPE_RECOMMEND = 1
//        const val TYPE_NEARBY = 2
//        const val TYPE_NEWEST = 3
//        const val TYPE_LIKE = 4
//        const val TYPE_MY_LIKED = 5
//        const val TYPE_MINE = 6
    fun prepareAudio(
        path: String,
        duration: Int,
        id: Int = 0,
        type: Int = 0,
        autoPlay: Boolean = false
    ) {

        filePath = path
        this.duration = duration
        val params = binding.root.layoutParams as FrameLayout.LayoutParams
        params.width =
            SizeUtils.dp2px(115F) + (ScreenUtils.getScreenWidth() - SizeUtils.dp2px(115 + 15 * 2 + 10 * 2F)) /
                    PublishActivity.MAX_RECORD_TIME *
                    if (duration > PublishActivity.MAX_RECORD_TIME) {
                        PublishActivity.MAX_RECORD_TIME
                    } else {
                        duration
                    }
        binding.audioPlayBtn.layoutParams = params

        positionId = id
        setDurationText(duration)
        binding.audioPlayBtn.setOnClickListener {
            when (playState) {
                MEDIA_PREPARE, MEDIA_ERROR, MEDIA_STOP -> {
                    EventBus.getDefault().post(OneVoicePlayEvent(id, type, context))
                    playAudio()
                }
                MEDIA_PAUSE -> {
                    resumeAudio()
                }
                MEDIA_PLAY -> {
                    pauseAudio()
                }
            }
        }

        if (autoPlay) {
            EventBus.getDefault().post(OneVoicePlayEvent(id, type, context))
            playAudio()
        }

    }

    fun initResource(bgResource: Int, textColor: Int, playImg: Int) {
        binding.audioPlayBtn.setBackgroundResource(bgResource)
        binding.audioTime.setTextColor(textColor)
        binding.audioState.setImageResource(playImg)

    }

    private var countTimeThread: CountDownTimerSupport? = null

    /**
     * 开始播放
     */
    private fun playAudio() {
        countTimeThread = CountDownTimerSupport(duration * 1000L, 1000)
        countTimeThread?.setOnCountDownTimerListener(object : OnCountDownTimerListener {
            override fun onFinish() {

            }

            override fun onCancel() {

            }

            override fun onTick(millisUntilFinished: Long) {
                setDurationText((millisUntilFinished / 1000L).toInt())

            }

        })

        if (MediaPlayerHelper.isPlaying()) {
            MediaPlayerHelper.realese()
        }
        MediaPlayerHelper.playSound(filePath,
            { completeAudio() },
            {
                countTimeThread?.start()
            })
        playState = MEDIA_PLAY
        binding.audioState.playAnimation()

    }

    /**
     * 暂停播放
     */
    fun pauseAudio() {
        playState = MEDIA_PAUSE
        MediaPlayerHelper.pause()
        UpdateVoiceTimeThread.getInstance(UriUtils.getShowTime(duration), binding.audioTime).pause()
        if (duration > 0 && filePath.isNotEmpty()) {
            countTimeThread?.pause()
        }
        binding.audioState.pauseAnimation()
    }


    /**
     * 恢复播放
     */
    fun resumeAudio() {
        playState = MEDIA_PLAY
        MediaPlayerHelper.resume()
        UpdateVoiceTimeThread.getInstance(UriUtils.getShowTime(duration), binding.audioTime).start()
        if (duration > 0 && filePath.isNotEmpty()) {
            countTimeThread?.resume()
        }
        binding.audioState.resumeAnimation()
    }

    fun isPlaying() = playState == MEDIA_PLAY

    fun isPause() = playState == MEDIA_PAUSE

    /**
     * 播放结束
     */
    fun completeAudio() {
        playState = MEDIA_STOP
//        UpdateVoiceTimeThread.getInstance(
//            UriUtils.getShowTime(duration),
//            audioTime
//        ).stop()
        countTimeThread?.reset()
        MediaPlayerHelper.realese()
        setDurationText(duration)
        //播放结束 重置全局播放位置
        UserManager.currentPlayPosition = -1
        binding.audioState.cancelAnimation()
        binding.audioState.progress = 0F

    }

    fun release() {
        playState = MEDIA_PREPARE
//        UpdateVoiceTimeThread.getInstance(
//            UriUtils.getShowTime(duration),
//            audioTime
//        ).stop()
        countTimeThread?.reset()
        MediaPlayerHelper.realese()
        setDurationText(duration)
        binding.audioState.cancelAnimation()
        binding.audioState.progress = 0F

    }


    private fun setDurationText(duration: Int) {
        binding.audioTime.isVisible = duration > 0
        binding.audioTime.text = UriUtils.getShowTime(duration)
    }

}