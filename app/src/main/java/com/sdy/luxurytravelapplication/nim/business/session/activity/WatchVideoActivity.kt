package com.sdy.luxurytravelapplication.nim.business.session.activity

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.view.SurfaceHolder
import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ScreenUtils
import com.netease.nimlib.sdk.AbortableFuture
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.MsgServiceObserve
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum
import com.netease.nimlib.sdk.msg.model.AttachmentProgress
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.databinding.ActivityWatchVideoBinding
import com.sdy.luxurytravelapplication.databinding.LayoutActionbarBinding
import com.sdy.luxurytravelapplication.nim.common.ToastHelper
import com.sdy.luxurytravelapplication.nim.common.util.file.FileUtil
import com.sdy.luxurytravelapplication.nim.common.util.sys.TimeUtil
import org.jetbrains.anko.startActivity


/**
 * 查看完整视频
 */
class WatchVideoActivity : BaseActivity<ActivityWatchVideoBinding>(), SurfaceHolder.Callback {

    // player
    private var mediaPlayer: MediaPlayer? = null

    // context
    private val handlerTimes = Handler()
    // download control

    private var downloading = false
    private var downloadFuture: AbortableFuture<Void>? = null
    private var isSurfaceCreated = false

    private var lastPercent = 0f
    protected var videoLength: Long = 0
    private var playState: Int = PLAY_STATE_STOP
    private var videoFilePath: String = ""


    companion object {
        private const val PLAY_STATE_PLAYING = 1

        private const val PLAY_STATE_STOP = 2

        private const val PLAY_STATE_PAUSE = 3

        const val INTENT_EXTRA_DATA = "EXTRA_DATA"
        const val INTENT_EXTRA_MENU = "EXTRA_MENU"

        @JvmOverloads
        fun start(context: Context, message: IMMessage) {
            context.startActivity<WatchVideoActivity>(INTENT_EXTRA_DATA to message)
        }
    }

    private lateinit var message: IMMessage
    private lateinit var surfaceHolder: SurfaceHolder

    private val statusObserver: Observer<IMMessage>

    private val attachmentProgressObserver: Observer<AttachmentProgress>

    init {
        attachmentProgressObserver = Observer<AttachmentProgress> { p ->
            val total = p.total
            var progress = p.transferred
            var percent = progress.toFloat() / total.toFloat()
            if (percent > 1.0) {
                // 消息中标识的文件大小有误，小于实际大小
                percent = 1.0.toFloat()
                progress = total
            }
            if (percent - lastPercent >= 0.10) {
                lastPercent = percent
                setDownloadProgress(getString(R.string.download_video), progress, total)
            } else {
                if (lastPercent.toDouble() == 0.0) {
                    lastPercent = percent
                    setDownloadProgress(getString(R.string.download_video), progress, total)
                }
                if (percent.toDouble() == 1.0 && lastPercent.toDouble() != 1.0) {
                    lastPercent = percent
                    setDownloadProgress(getString(R.string.download_video), progress, total)
                }
            }
        }

        statusObserver = Observer<IMMessage> { msg ->
            if (!msg.isTheSame(message) || isFinishing || isDestroyed) {
                return@Observer
            }
            if (msg.attachStatus == AttachStatusEnum.transferred && isVideoHasDownloaded(msg)) {
                onDownloadSuccess(msg)
            } else if (msg.attachStatus == AttachStatusEnum.fail) {
                onDownloadFailed()
            }
        }
    }



    override fun initView() {
        message = intent.getSerializableExtra(INTENT_EXTRA_DATA) as IMMessage

        surfaceHolder = binding.videoView.holder
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        surfaceHolder.addCallback(this)

        binding.lblVideoTimes.isInvisible = true
        binding.lblVideoFileInfo.isInvisible = false
        binding.controlDownloadBtn.setOnClickListener {
            if (downloading) {
                stopDownload()
            } else {
                download()
            }
            binding.controlDownloadBtn.setImageResource(
                if (downloading) {
                    R.drawable.nim_icon_download_pause
                } else {
                    R.drawable.nim_icon_download_resume
                }
            )
        }


        showVideoInfo()

        registerObservers(true)

        download()

    }

    override fun onResume() {
        super.onResume()
        mediaPlayer = MediaPlayer()
        if (isSurfaceCreated) {
            play()
        }
    }

    override fun onPause() {
        super.onPause()
        stopMediaPlayer()
    }

    override fun onBackPressed() {
        stopDownload()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        registerObservers(false)
    }


    private fun registerObservers(register: Boolean) {
        NIMClient.getService(MsgServiceObserve::class.java)
            .observeMsgStatus(statusObserver, register)
        NIMClient.getService(MsgServiceObserve::class.java)
            .observeAttachmentProgress(attachmentProgressObserver, register)

    }


    private fun initVideoSize() {
        if (mediaPlayer == null) {
            return
        }
        // 视频宽高
        val width = mediaPlayer!!.videoWidth
        val height = mediaPlayer!!.videoHeight
        if (width <= 0 || height <= 0) {
            return
        }

        // 屏幕宽高
        val screenWidth = ScreenUtils.getScreenWidth()
        val screenHeight = ScreenUtils.getScreenHeight()
        val videoRatio = width / height
        val screenRatio = screenWidth / screenHeight
        if (screenRatio > videoRatio) {
            val newWidth = screenHeight * width / height
            val layoutParams = RelativeLayout.LayoutParams(newWidth, screenHeight)
            val margin = (screenWidth - newWidth) / 2
            layoutParams.setMargins(margin, 0, margin, 0)
            binding.videoView.layoutParams = layoutParams
        } else {
            val newHeight = screenWidth * height / width
            val layoutParams = RelativeLayout.LayoutParams(screenWidth, newHeight)
            val margin = (screenHeight - newHeight) / 2
            layoutParams.setMargins(0, margin, 0, margin)
            binding.videoView.layoutParams = layoutParams
        }
    }


    /**
     * ****************************** MediaPlayer Start ********************************
     * ****************************** MediaPlayer Start ********************************
     */
    private fun stopMediaPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying()) {
                mediaPlayer!!.stop()
            }
            mediaPlayer!!.reset()
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    /**
     * 处理视频播放时间
     */
    private val timeRunnable: Runnable = object : Runnable {
        override fun run() {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying()) {
                playState = PLAY_STATE_PLAYING
                if (videoLength <= 0) {
                    binding.lblVideoTimes.setVisibility(View.INVISIBLE)
                } else {
                    // 由于mediaPlayer取到的时间不统一,采用消息体中的时间
                    var leftTimes =
                        (videoLength * 1000 - mediaPlayer!!.getCurrentPosition()).toInt()
                    if (leftTimes < 0) {
                        leftTimes = 0
                    }
                    binding.lblVideoTimes.setVisibility(View.VISIBLE)
                    val seconds: Long = TimeUtil.getSecondsByMilliseconds(leftTimes.toLong())
                    binding.lblVideoTimes.setText(TimeUtil.secToTime(seconds.toInt()))
                    handlerTimes.postDelayed(this, 1000)
                }
            }
        }
    }

    protected fun pauseVideo() {
        binding.videoIcon.visibility = View.VISIBLE
        if (mediaPlayer != null && mediaPlayer!!.isPlaying()) {
            mediaPlayer!!.pause()
            handlerTimes.removeCallbacks(timeRunnable)
            playState =
                PLAY_STATE_PAUSE
        }
    }

    protected fun resumeVideo() {
        binding.videoIcon.visibility = View.GONE
        if (mediaPlayer != null) {
            if (!mediaPlayer!!.isPlaying()) {
                mediaPlayer!!.start()
                playState = PLAY_STATE_PLAYING
                handlerTimes.postDelayed(timeRunnable, 100)
            }
        }
    }

    protected fun playVideo() {
        binding.videoIcon.visibility = View.GONE
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying()) {
                mediaPlayer!!.stop()
            } else {
                if (isSurfaceCreated) {
                    mediaPlayer!!.setDisplay(surfaceHolder)
                } else {
                    ToastHelper.showToast(
                        this@WatchVideoActivity,
                        R.string.surface_has_not_been_created
                    )
                    return
                }
            }
            mediaPlayer!!.reset()
            try {
                mediaPlayer!!.setDataSource(videoFilePath)
            } catch (e: Exception) {
                ToastHelper.showToast(this@WatchVideoActivity, R.string.look_video_fail_try_again)
                e.printStackTrace()
                return
            }
            setMediaPlayerListener()
            mediaPlayer!!.prepareAsync()
        }
    }

    private fun setMediaPlayerListener() {
        mediaPlayer!!.setOnCompletionListener {
            binding.videoIcon.visibility = View.VISIBLE
            playState = PLAY_STATE_STOP
            binding.lblVideoTimes.setText("00:00")
            handlerTimes.removeCallbacks(timeRunnable)
        }
        mediaPlayer!!.setOnErrorListener { mp, what, extra ->
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                val type = "video/3gp"
                val name = Uri.parse("file://$videoFilePath")
                intent.setDataAndType(name, type)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                ToastHelper.showToastLong(this@WatchVideoActivity, R.string.look_video_fail)
            }
            true
        }
        mediaPlayer!!.setOnPreparedListener {
            mediaPlayer!!.start() // 播放视频
            initVideoSize() //根据视频宽高，调整视频显示
            // if (position > 0) {
            // mediaPlayer.seekTo(position);
            // mediaPlayer.start();
            // }
            handlerTimes.postDelayed(timeRunnable, 100)
        }
    }


    /**
     * **************************** 下载视频 *********************************
     */


    private fun showVideoInfo() {
        val duration = (message.attachment as VideoAttachment).duration
        val fileSize = (message.attachment as VideoAttachment).size

        if (duration <= 0) {
            binding.lblVideoFileInfo.text =
                getString(R.string.file_size, FileUtil.formatFileSize(fileSize))
        } else {
            val seconds = TimeUtil.getSecondsByMilliseconds(duration)
            binding.lblVideoFileInfo.text =
                getString(R.string.size_and_duration, FileUtil.formatFileSize(fileSize), seconds)
            videoLength = seconds
        }
    }

    private fun onDownloadStart(message: IMMessage) {
        setDownloadProgress(
            getString(R.string.download_video),
            0,
            (message.attachment as VideoAttachment).size
        )
        binding.layoutDownload.visibility = View.VISIBLE
    }


    private fun setDownloadProgress(label: String, progress: Long, total: Long) {
        val percent = (progress * 1F / total)
        runOnUiThread {
            val fgLayoutParams = binding.downloadProgressForeground.layoutParams
            fgLayoutParams.width = (binding.downloadProgressBackground.width * percent).toInt()
            binding.downloadProgressForeground.layoutParams = fgLayoutParams
            binding.downloadProgressText.text = String.format(
                getString(R.string.download_progress_description),
                label,
                FileUtil.formatFileSize(progress),
                FileUtil.formatFileSize(total)
            )
        }
    }


    private fun isVideoHasDownloaded(message: IMMessage): Boolean {
        if (message.attachStatus == AttachStatusEnum.transferred
            && !(message.attachment as VideoAttachment).path.isNullOrEmpty()
        ) {
            return true
        }
        return false
    }

    private fun download() {
        if (!isVideoHasDownloaded(message)) {
            onDownloadStart(message)
            downloadFuture =
                NIMClient.getService(MsgService::class.java).downloadAttachment(message, false)
            downloading = true
        }

    }


    private fun play() {
        if (isVideoHasDownloaded(message) || message.direct == MsgDirectionEnum.Out) {
            onDownloadSuccess(message)
        }
    }

    private fun onDownloadSuccess(message: IMMessage) {
        downloadFuture = null
        binding.layoutDownload.visibility = View.GONE
        videoFilePath = (message.attachment as VideoAttachment).path
        binding.videoView.setOnClickListener {
            when (playState) {
                PLAY_STATE_PAUSE -> {
                    resumeVideo()
                }
                PLAY_STATE_PLAYING -> {
                    pauseVideo()
                }
                PLAY_STATE_STOP -> {
                    playVideo()
                }
            }
        }
        playVideo()
    }

    private fun onDownloadFailed() {
        downloadFuture = null
        binding.layoutDownload.visibility = View.GONE
        ToastHelper.showToast(
            this@WatchVideoActivity,
            R.string.download_video_fail
        )
    }


    private fun stopDownload() {
        if (downloadFuture != null) {
            downloadFuture!!.abort()
            downloadFuture = null
            downloading = false
        }

    }

    /**
     * ***************************** SurfaceHolder Callback **************************************
     */


    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isSurfaceCreated = false
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (!isSurfaceCreated) {
            isSurfaceCreated = true
            play()
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        initVideoSize() // 屏幕旋转后，改变视频显示布局
    }

    override fun initData() {
    }

    override fun start() {
    }

}