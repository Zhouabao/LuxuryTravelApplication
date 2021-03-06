package com.sdy.luxurytravelapplication.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.MediaController
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ScreenUtils
import com.kongzue.dialog.v3.MessageDialog
import com.kongzue.dialog.v3.TipDialog
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.camera_filter.callback.LoadAssetsImageCallback
import com.sdy.luxurytravelapplication.camera_filter.listener.EndRecordingFilterCallback
import com.sdy.luxurytravelapplication.camera_filter.listener.StartRecordingFilterCallback
import com.sdy.luxurytravelapplication.databinding.ActivityVideoIntroduceBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.contract.VideoIntroduceContract
import com.sdy.luxurytravelapplication.mvp.model.bean.CopyMvBean
import com.sdy.luxurytravelapplication.mvp.model.bean.VideoVerifyBannerBean
import com.sdy.luxurytravelapplication.mvp.presenter.VideoIntroducePresenter
import com.sdy.luxurytravelapplication.nim.common.ToastHelper
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.utils.UriUtils
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.wysaid.nativePort.CGENativeLibrary
import java.io.File

/**
 * ?????????????????????
 */
class VideoIntroduceActivity :
    BaseMvpActivity<VideoIntroduceContract.View, VideoIntroduceContract.Presenter, ActivityVideoIntroduceBinding>(),
    VideoIntroduceContract.View, View.OnClickListener {
    companion object {
        val RATIO = 500 / 375F //????????????
        const val RESULT_CODE_CHOOSE_VIDEO = 1007
        const val RESULT_CODE_CONFIRM_VIDEO = 1008

        const val RECORD_MAX_TIME = 30 //?????????????????????????????????????????????30???
        const val RECORD_MIN_TIME = 5 //???????????????????????????????????????1???


        fun start(context1: Context, requestCode: Int = -1) {
            if (Build.VERSION.SDK_INT < 21) {
                ToastHelper.showToast(context1, context1.getString(R.string.system_unsupport_video))
                return
            }
            if (requestCode != -1) {
                context1.startActivity<VideoIntroduceActivity>()
            } else {
                (context1 as Activity).startActivityForResult<VideoIntroduceActivity>(requestCode)
            }

        }
    }

    override fun createPresenter(): VideoIntroduceContract.Presenter = VideoIntroducePresenter()

    private var type = 1 //1.?????? 2???????????????
    private val mStartRecordingFilterCallback by lazy {
        object : StartRecordingFilterCallback(this) {
            override fun startRecordingOver(success: Boolean) {
//            super.startRecordingOver(success)
                if (success) {
                    ToastUtil.toast(getString(R.string.start_record_video))
                    runOnUiThread {
                        binding.chooseVideoBtn.isEnabled = false
                    }
                } else {
                    ToastUtil.toast(getString(R.string.record_fail))
                }
            }
        }
    }
    private val mEndRecordingFilterCallback by lazy {
        object : EndRecordingFilterCallback(this) {
            override fun endRecordingOK() {
                super.endRecordingOK()
                runOnUiThread {
                    switchToFinishState(videoSavePath)
                }
//                startActivityForResult<VideoVerifyConfirmActivity>(
//                    VideoVerifyConfirmActivity.RESULT_CODE_CONFIRM_VIDEO,
//                    "ratio" to RATIO,
//                    "path" to videoSavePath,
//                    "duration" to currentTime * 1000L
//                )
            }
        }
    }
    private lateinit var mainHandler: Handler
    private var longPressRunnable: LongPressRunnable? = null
    private var isAction = false
    private var isRecording = false
    private var isComplete = false
    private var currentTime = 0
    private var videoSavePath = ""

    override fun initData() {
        binding.apply {

            mainHandler = Handler()
            barCl.root.setBackgroundColor(Color.WHITE)
            barCl.actionbarTitle.text = getString(R.string.video_verify)
            barCl.btnBack.setImageResource(R.drawable.icon_return_arrow)

            CGENativeLibrary.setLoadImageCallback(
                LoadAssetsImageCallback(this@VideoIntroduceActivity),
                null
            )

            //?????????????????????
            cameraPreview.presetCameraForward(false)
            //??????????????????
            cameraPreview.presetRecordingSize(480, 640)
            val params = cameraPreview.layoutParams as ConstraintLayout.LayoutParams
            params.width = ScreenUtils.getScreenWidth()
            params.height = (RATIO * ScreenUtils.getScreenWidth()).toInt()
            cameraPreview.layoutParams = params
            //????????????
            cameraPreview.setPictureSize(2048, 2048, true)
            //??????view
            cameraPreview.setFitFullView(true)
//        setupTouchListener()

            longPressRunnable = LongPressRunnable()

            ClickUtils.applySingleDebouncing(
                arrayOf(
                    barCl.btnBack,
                    startRecordBtn,
                    commitBtn,
                    revertBtn,
                    chooseVideoBtn,
                    turnCameraBtn
                ), this@VideoIntroduceActivity
            )
        }
    }

    override fun start() {
        mPresenter?.getVideoNormal()
    }


    private fun handleActionUpByState() {
        longPressRunnable?.let { mainHandler.removeCallbacks(it) } //?????????????????????Runnable
        //????????????????????????
        if (isRecording) {
            stopMediaRecorder()
        }
    }


    private fun startMediaRecorder() {
        isRecording = true
        startButtonAnimation()
        currentTime = 0
        mainHandler.postDelayed(progressRunnable, 0)

        videoSavePath = UriUtils.getCacheDir(this) + "/" + System.currentTimeMillis() + ".mp4"
        mEndRecordingFilterCallback.setVideoFilePath(videoSavePath)
        binding.cameraPreview.startRecording(videoSavePath, mStartRecordingFilterCallback)

    }

    private fun stopMediaRecorder() {
        if (currentTime <= RECORD_MIN_TIME) {
            TipDialog.show(this, getString(R.string.min_duration, 5), TipDialog.TYPE.WARNING)
            isAction = true
            return
        }
        mainHandler.removeCallbacks(progressRunnable)
        stopButtonAnimation()
        binding.mProgressView.reset()
        isRecording = false
//        mEndRecordingFilterCallback.endRecordingOK()
        binding.cameraPreview.endRecording(mEndRecordingFilterCallback)

    }

    private fun switchToFinishState(filePath: String) {
        binding.tvBalanceTime.text = getString(R.string.click_play)

        binding.revertBtn.isVisible = true
        binding.commitBtn.isVisible = true
        binding.turnCameraBtn.isInvisible = true
        binding.chooseVideoBtn.isInvisible = true
        binding.recordNormalCl.isVisible = false
        binding.videoPreview.isVisible = true
        binding.videoCover.isVisible = true
        binding.cameraPreview.isInvisible = true
        isComplete = true

        showVideoPreview()
    }

    inner class LongPressRunnable : Runnable {
        override fun run() {
            startMediaRecorder()
        }
    }

    private val progressRunnable: Runnable by lazy {
        Runnable {
            currentTime++
            Log.i(TAG, "recordRunnable currentTime:" + currentTime)
            //?????????????????????????????????????????????
            if (currentTime > RECORD_MAX_TIME) {
                isAction = false
                stopMediaRecorder()
            } else {
                mainHandler.postDelayed(progressRunnable, 1000)
            }
        }
    }

    private val animatorSet by lazy {
        //????????????
        AnimatorSet().apply {
            val scaleX = ObjectAnimator.ofFloat(binding.view1, "scaleX", 1F, 2f, 1F)
            scaleX.repeatCount = -1
            val scaleY = ObjectAnimator.ofFloat(binding.view1, "scaleY", 1F, 2f, 1F)
            scaleY.repeatCount = -1
            duration = 1000
            interpolator = LinearInterpolator()
            play(scaleX).with(scaleY) //????????????????????????
        }

    }

    //????????????????????????
    private fun startButtonAnimation() {
        binding.startRecordBtn.setImageResource(R.drawable.icon_voice_stop)
        binding.tvBalanceTime.base = SystemClock.elapsedRealtime()
        binding.tvBalanceTime.start()
        binding.view1.isVisible = true
        animatorSet.start()

        binding.turnCameraBtn.isInvisible = true
        binding.chooseVideoBtn.isInvisible = true
    }

    //????????????????????????
    private fun stopButtonAnimation() {
        if (isRecording) {
            binding.startRecordBtn.setImageResource(R.drawable.icon_record_play)
            binding.view1.isVisible = false
            animatorSet.cancel()
            binding.view1.clearAnimation()
            binding.tvBalanceTime.stop()
            binding.tvBalanceTime.base = SystemClock.elapsedRealtime()
        }
    }


    //??????????????????
    private fun switchNormalState() {
        currentTime = 0
        isRecording = false
        isComplete = false
        binding.tvBalanceTime.stop()
        binding.tvBalanceTime.base = SystemClock.elapsedRealtime()
        binding.tvBalanceTime.text = getString(R.string.video_record_begin)
        binding.chooseVideoBtn.isEnabled = true


        binding.revertBtn.isVisible = false
        binding.commitBtn.isVisible = false
        binding.turnCameraBtn.isVisible = true
        binding.chooseVideoBtn.isVisible = true
        binding.recordNormalCl.isVisible = true
        binding.startRecordBtn.setImageResource(R.drawable.icon_video_verify_normal)

        binding.videoPreview.stopPlayback()
        File(videoSavePath).delete()

        binding.videoPreview.isVisible = false
        binding.videoCover.isVisible = false
        binding.cameraPreview.isVisible = true
    }


    private fun requestVideoPermissions() {
        PermissionUtils.permissionGroup(
            PermissionConstants.STORAGE,
            PermissionConstants.CAMERA,
            PermissionConstants.MICROPHONE
        )
            .callback(object : PermissionUtils.SimpleCallback {
                override fun onGranted() {
                    binding.cameraPreview.resumePreview()
                }

                override fun onDenied() {
                    MessageDialog.show(
                        this@VideoIntroduceActivity,
                        getString(R.string.permission_title),
                        getString(R.string.permission_audio_and_camera),
                        getString(R.string.ok)
                    )
                        .setOnOkButtonClickListener { baseDialog, v ->
                            false
                        }
                        .setOnCancelButtonClickListener { baseDialog, v ->
                            this@VideoIntroduceActivity.finish()
                            false
                        }
                }
            })
            .request()
    }


    override fun onClick(v: View) {
        when (v) {
            binding.barCl.btnBack -> {
                onBackPressed()
            }
            binding.turnCameraBtn -> {
                binding.cameraPreview.switchCamera()
//                switchCamera()
            }
            binding.chooseVideoBtn -> {
                type = 2
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    CommonFunction.onTakePhoto(
                        this,
                        1,
                        RESULT_CODE_CHOOSE_VIDEO,
                        PictureMimeType.ofVideo(),
                        minSeconds = RECORD_MIN_TIME,
                        maxSeconds = RECORD_MAX_TIME
                    )
                } else
                    CommonFunction.onTakePhoto(
                        this,
                        1,
                        RESULT_CODE_CHOOSE_VIDEO,
                        PictureMimeType.ofVideo()
                    )
            }
            binding.startRecordBtn -> {//????????????
                if (!isComplete) {
                    type = 1
                    if (!isRecording) {
                        isAction = true
                        isRecording = false
                        longPressRunnable?.let { mainHandler.post(it) } //????????????500??????????????????????????????Runnable
                    } else {
                        if (isAction) {
                            isAction = false
                            handleActionUpByState()
                        }
                    }
                } else {
                    if (binding.videoPreview.isPlaying) {
                        binding.videoPreview.pause()
                        binding.tvBalanceTime.text = getString(R.string.click_play)
                        binding.startRecordBtn.setImageResource(R.drawable.icon_record_play)
                    } else {
                        binding.startRecordBtn.setImageResource(R.drawable.icon_record_pause)
                        binding.tvBalanceTime.text = getString(R.string.click_pause)
                        binding.videoPreview.start()
                        binding.videoCover.isVisible = false
                    }
//                    showVideoPreview()
                }
            }

            binding.commitBtn -> {//??????
                mPresenter?.uploadProfile(
                    videoSavePath, type, if (switchIndex > -1 && mvCopy.size > switchIndex) {
                        mvCopy[switchIndex].id
                    } else {
                        0
                    }
                )
            }
            binding.revertBtn -> {//????????????
                MessageDialog.show(
                    this,
                    getString(R.string.re_record_title),
                    getString(R.string.confirm_to_re_record),
                    getString(R.string.ok),
                    getString(R.string.cancel)
                )
                    .setOnOkButtonClickListener { baseDialog, v ->
                        // revert
                        switchNormalState()
                        false
                    }
                    .setOnCancelButtonClickListener { baseDialog, v ->
                        false
                    }

            }
        }
    }


    private fun showVideoPreview() {


        GlideUtil.loadImg(this, videoSavePath, binding.videoCover)
        binding.videoPreview.setMediaController(MediaController(this))
        binding.videoPreview.setVideoURI(Uri.fromFile(File(videoSavePath)))
        binding.videoPreview.setOnCompletionListener {
//            videoPreview.stopPlayback()
            binding.tvBalanceTime.text = getString(R.string.click_play)
            binding.videoCover.isVisible = true
            binding.startRecordBtn.setImageResource(R.drawable.icon_record_play)

//            tvBalanceTime.stop()
//            tvBalanceTime.base = SystemClock.elapsedRealtime()
//            tvBalanceTime.start()
        }
        binding.videoPreview.setOnPreparedListener {
//            videoPreview.start()
//            tvBalanceTime.base = SystemClock.elapsedRealtime()
//            tvBalanceTime.start()
        }

        binding.videoPreview.setOnErrorListener { mp, what, extra ->
            LogUtils.e("${what},$extra")
            true
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_CODE_CHOOSE_VIDEO) {//??????????????????
            if (resultCode == Activity.RESULT_OK) {
                videoSavePath =
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P && !PictureSelector.obtainMultipleResult(
                            data
                        )[0].androidQToPath.isNullOrEmpty()
                    ) {
                        PictureSelector.obtainMultipleResult(data)[0].androidQToPath
                    } else {
                        PictureSelector.obtainMultipleResult(data)[0].path
                    }

                switchToFinishState(videoSavePath)
            }
        }
    }


    override fun onResume() {
        super.onResume()
        requestVideoPermissions()

    }


    override fun onPause() {
        super.onPause()
        isAction = false
        binding.cameraPreview.stopPreview()
        stopButtonAnimation()
        binding.mProgressView.reset()
        isRecording = false

    }

    override fun onDestroy() {
        super.onDestroy()
        binding.cameraPreview.release(null)
//        EventBus.getDefault().unregister(this)
    }

    override fun uploadProfileResult(success: Boolean, fileName: String) {
        if (success) {
            //??????????????????
            mPresenter?.uploadMv(
                hashMapOf(
                    "mv_url" to fileName,
                    "type" to type,
                    "normal_id" to if (switchIndex > -1 && mvCopy.size > switchIndex) {
                        mvCopy[switchIndex].id
                    } else {
                        0
                    }
                )
            )
        } else {
            ToastUtil.toast(getString(R.string.video_submit_fail_please_retry))
        }

    }

    override fun uploadMvResult(success: Boolean) {
        SweetHeartVerifyingActivity.start(this, SweetHeartVerifyingActivity.TYPE_GIRL_VIDEO)

    }

    override fun getVideoNormalResult(data: CopyMvBean?) {
        if (data != null) {
            mvCopy.addAll(data.list)
            switchMvCopy()
        }

    }


    private var switchIndex = -1
    private var mvCopy: ArrayList<VideoVerifyBannerBean> = arrayListOf<VideoVerifyBannerBean>()
    private fun switchMvCopy() {
        if (mvCopy.isNotEmpty()) {
            if (switchIndex < mvCopy.size - 1 && switchIndex >= 0) {
                switchIndex += 1
            } else {
                switchIndex = 0
            }
            binding.recordTitle.text = mvCopy[switchIndex].title
            binding.recordContent.text = mvCopy[switchIndex].content
        }
    }
}