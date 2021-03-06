package com.sdy.luxurytravelapplication.ui.dialog

import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.ScreenUtils
import com.kongzue.dialog.v3.MessageDialog
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.DialogRecordContentBinding
import com.sdy.luxurytravelapplication.event.RecordCompleteEvent
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.utils.UriUtils
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog
import com.sdy.luxurytravelapplication.widgets.player.MediaPlayerHelper
import com.sdy.luxurytravelapplication.widgets.player.MediaRecorderHelper
import com.sdy.luxurytravelapplication.widgets.player.MediaRecorderHelper.*
import com.sdy.luxurytravelapplication.widgets.player.UpdateVoiceTimeThread
import org.greenrobot.eventbus.EventBus

class RecordContentDialog : BaseBindingDialog<DialogRecordContentBinding>(),
    View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        initView()
    }

    companion object {
        const val MIN_RECORD_TIME = 5
        const val MAX_RECORD_TIME = 60 * 5

    }

    private var mIsRecorder = false

    private var countTimeThread: CountDownTimer? = null
    private var mPreviewTimeThread: UpdateVoiceTimeThread? = null
    private lateinit var mMediaRecorderHelper: MediaRecorderHelper
    private var totalSecond = 0
    private var currentActionState = ACTION_NORMAL
    private fun initView() {
//        setCancelable(false)
//        setCanceledOnTouchOutside(false)
        binding.apply {
            ClickUtils.applySingleDebouncing(
                arrayOf(recordBtn, recordRevertBtn, recordConfirmBtn, deleteDialog),
                this@RecordContentDialog
            )

            mMediaRecorderHelper = MediaRecorderHelper(context)
            recordProgressTime.setMaxStepNum(MAX_RECORD_TIME)

            //????????????????????????
            countTimeThread = object : CountDownTimer(MAX_RECORD_TIME * 1000L, 1000) {
                override fun onFinish() {
                    switchActionState()
                }

                override fun onTick(millisUntilFinished: Long) {
                    totalSecond++
                    if (!mIsRecorder) {
                        countTimeThread?.cancel()
                    }

                    if (totalSecond == MAX_RECORD_TIME - 10) {
                        ToastUtil.toast(context.getString(R.string.left_10_seconds_to_record))
                    }
                    recordTimeTv.text = UriUtils.getShowTime(totalSecond)
                    recordProgressTime.update(totalSecond, 0)
                }

            }


        }
    }

    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.BOTTOM)
        val params = window?.attributes
        params?.width = ScreenUtils.getScreenWidth()
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        params?.windowAnimations = R.style.MyDialogBottomAnimation
        window?.attributes = params
    }

    override fun onClick(v: View) {
        when (v) {
            binding.recordBtn -> {//????????????
                if (currentActionState == ACTION_RECORDING && totalSecond < MIN_RECORD_TIME) {
                    ToastUtil.toast(context.getString(R.string.record_longer))
                    return
                }
                switchActionState()
            }

            binding.recordRevertBtn -> {//????????????
                MessageDialog.show(
                    ActivityUtils.getTopActivity() as AppCompatActivity,
                    context.getString(R.string.re_record_title),
                    context.getString(R.string.confirm_to_re_record),
                    context.getString(R.string.ok),
                    context.getString(R.string.cancel)
                )
                    .setOnOkButtonClickListener { baseDialog, v ->
                        changeToNormalState()
                        false
                    }
                    .setOnCancelButtonClickListener { baseDialog, v ->
                        false
                    }

            }

            binding.recordConfirmBtn -> {//??????????????????
                EventBus.getDefault()
                    .post(RecordCompleteEvent(totalSecond, mMediaRecorderHelper.currentFilePath))
                dismiss()

            }
            binding.deleteDialog -> {
                dismiss()
            }
        }
    }

    private fun changeToNormalState() {
        binding.apply {
            mIsRecorder = false
            currentActionState = ACTION_NORMAL
            totalSecond = 0
            recordProgressTime.update(0, 0)
            MediaPlayerHelper.realese()
            recordBtn.setImageResource(R.drawable.icon_record_normal)
            mPreviewTimeThread?.stop()
            mMediaRecorderHelper.cancel()


            recordTitle.visibility = View.VISIBLE
            recordTitle.text = context.getString(R.string.click_to_start_record)


//            recordTimeTv.isVisible = false
            recordRevertBtn.isVisible = false
            recordListenTip.isInvisible = true
            recordConfirmBtn.isVisible = false

        }

    }

    private fun switchActionState() {
        binding.apply {
            mIsRecorder = false
            if (currentActionState == ACTION_NORMAL) { //???????????????
                currentActionState = ACTION_RECORDING
                recordTimeTv.isVisible = true
                recordProgressTime.isVisible = true

                //????????????
                mMediaRecorderHelper.startRecord()
                mIsRecorder = true
                recordTitle.text = context.getString(R.string.click_to_end_record)
                countTimeThread?.start()
            } else if (currentActionState == ACTION_RECORDING) {//?????????
                currentActionState = ACTION_COMMPLETE
                recordBtn.setImageResource(R.drawable.icon_record_start)
                recordRevertBtn.isVisible = true
                recordConfirmBtn.isVisible = true
                recordListenTip.isVisible = true
                recordTitle.text = context.getString(R.string.record_complete)
                recordListenTip.text = context.getString(R.string.click_to_try_listen)

//                recordTitle.isVisible = false
//                recordTimeTv.isVisible = false
                recordProgressTime.isInvisible = true

                mMediaRecorderHelper.stopAndRelease()
            } else if (currentActionState == ACTION_COMMPLETE || currentActionState == ACTION_DONE) {
                currentActionState = ACTION_PLAYING
                //?????????????????????
                if (mPreviewTimeThread != null)
                    mPreviewTimeThread?.stop()
                mPreviewTimeThread = UpdateVoiceTimeThread.getInstance(
                    UriUtils.getShowTime(totalSecond),
                    recordTimeTv
                )

                recordBtn.setImageResource(R.drawable.icon_record_pause)
                MediaPlayerHelper.playSound(mMediaRecorderHelper.currentFilePath, {
                    currentActionState = ACTION_COMMPLETE
                    recordBtn.setImageResource(R.drawable.icon_record_start)
                    mPreviewTimeThread?.stop()
//                MediaPlayerHelper.realese()
                    recordTitle.text = context.getString(R.string.record_complete)
                    recordTimeTv.text = UriUtils.getShowTime(totalSecond)

                }, {
                    mPreviewTimeThread?.start()
                })

            } else if (currentActionState == ACTION_PLAYING) {
                //????????????
                currentActionState = ACTION_PAUSE
                recordBtn.setImageResource(R.drawable.icon_record_start)
                //?????????????????????
                mPreviewTimeThread?.pause()
                MediaPlayerHelper.pause()
            } else if (currentActionState == ACTION_PAUSE) {
                currentActionState = ACTION_PLAYING
                recordBtn.setImageResource(R.drawable.icon_record_pause)
                //????????????????????????
                mPreviewTimeThread?.start()
                MediaPlayerHelper.resume()
            }
        }

    }

    override fun dismiss() {
        countTimeThread?.cancel()
        mPreviewTimeThread?.stop()
        if (currentActionState == ACTION_RECORDING) {
            mMediaRecorderHelper.cancel()
            changeToNormalState()
        }
        super.dismiss()
    }

}
