package com.sdy.luxurytravelapplication.ui.activity

import android.os.Build
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.*
import com.kongzue.dialog.v3.MessageDialog
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityPublishTravelEndBinding
import com.sdy.luxurytravelapplication.event.RecordCompleteEvent
import com.sdy.luxurytravelapplication.mvp.contract.PublishTravelEndContract
import com.sdy.luxurytravelapplication.mvp.presenter.PublishTravelEndPresenter
import com.sdy.luxurytravelapplication.ui.dialog.RecordContentDialog
import com.sdy.luxurytravelapplication.utils.RandomUtils
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.utils.UriUtils
import com.sdy.luxurytravelapplication.widgets.player.MediaPlayerHelper
import com.sdy.luxurytravelapplication.widgets.player.MediaRecorderHelper
import com.sdy.luxurytravelapplication.widgets.player.UpdateVoiceTimeThread
import org.greenrobot.eventbus.EventBus

/**
 * 发布旅行
 */
class PublishTravelEndActivity :
    BaseMvpActivity<PublishTravelEndContract.View, PublishTravelEndContract.Presenter, ActivityPublishTravelEndBinding>(),
    PublishTravelEndContract.View, View.OnClickListener {
    private val params by lazy { intent.getSerializableExtra("params") as HashMap<String, Any> }
    override fun createPresenter(): PublishTravelEndContract.Presenter = PublishTravelEndPresenter()

    companion object {
        const val MIN_RECORD_TIME = 5
        const val MAX_RECORD_TIME = 60 * 5
    }

    private var mIsRecorder = false

    private var countTimeThread: CountDownTimer? = null
    private var mPreviewTimeThread: UpdateVoiceTimeThread? = null
    private lateinit var mMediaRecorderHelper: MediaRecorderHelper
    private var totalSecond = 0
    private var currentActionState = MediaRecorderHelper.ACTION_NORMAL


    override fun initData() {
        LogUtils.d(params)
        binding.apply {
            barCl.actionbarTitle.text = "发布旅行"
            barCl.divider.isVisible = true

            ClickUtils.applySingleDebouncing(switchBtn, 0, this@PublishTravelEndActivity)
            ClickUtils.applySingleDebouncing(
                arrayOf(
                    barCl.btnBack,
                    recordBtn,
                    recordRevertBtn,
                    recordConfirmBtn,
                    audioDeleteBtn, nextBtn
                ),
                this@PublishTravelEndActivity
            )

            autoCb.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    travelDescrEt.setText("这是自动生成的文本哦·····")
                } else {
                    travelDescrEt.setText("")
                }
            }
            travelDescrEt.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    checkEnable()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

            })



            mMediaRecorderHelper = MediaRecorderHelper(this@PublishTravelEndActivity)

            //开启录音计时线程
            countTimeThread =
                object : CountDownTimer(RecordContentDialog.MAX_RECORD_TIME * 1000L, 1000) {
                    override fun onFinish() {

                    }

                    override fun onTick(millisUntilFinished: Long) {
                        totalSecond++
                        if (!mIsRecorder) {
                            countTimeThread?.cancel()
                        }

                        if (totalSecond == 170) {
                            ToastUtil.toast(getString(R.string.left_10_seconds_to_record))
                        }
                        recordTimeTv.text = UriUtils.getShowTime(totalSecond)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            recordProgressTime.update(
                                ((totalSecond * 1f / RecordContentDialog.MAX_RECORD_TIME) * 100).toInt(),
                                0
                            )
                        } else {
                            recordProgressTime.update(
                                ((totalSecond * 1f / RecordContentDialog.MAX_RECORD_TIME) * 100).toInt(),
                                0
                            )
                        }
                    }

                }
        }
    }

    private fun checkEnable() {
        binding.nextBtn.isEnabled =
            binding.travelTitle.text.isNotEmpty() && (binding.travelDescrEt.text.isNotEmpty() || !TextUtils.isEmpty(mMediaRecorderHelper.currentFilePath))
    }

    override fun start() {
    }


    var switchType = false
    override fun onClick(v: View) {
        when (v) {
            binding.barCl.btnBack -> {
                finish()
            }

            binding.nextBtn -> {
                if (binding.travelDescrEt.text.isNotEmpty()) {
                    publishTravel(binding.travelDescrEt.text.toString())
                } else {
                    //上传音频
                    val imagePath =
                        "${Constants.FILE_NAME_INDEX}${Constants.DATING}${UserManager.accid}/${System.currentTimeMillis()}/${RandomUtils.getRandomString(
                            16
                        )}"
                    params["duration"] = totalSecond
                    mPresenter?.uploadFile(mMediaRecorderHelper.currentFilePath, imagePath)
                }


            }
            binding.switchBtn -> {//切换按钮
                if (switchType) {
                    switchType = false
                    changeToNormalState()
                    binding.recordCl.isVisible = false
                    binding.contentCl.isVisible = true
                    binding.switchImg.setImageResource(R.drawable.icon_switch_audio)
                    binding.switchBtn.text = "切换语音描述"

                    binding.previewAudioLl.visibility = View.GONE
                    // previewAudio停止播放
                    if(binding.previewAudio.isPlaying()){
                        binding.previewAudio.release()
                    }
                } else {
                    PermissionUtils.permissionGroup(PermissionConstants.MICROPHONE)
                        .callback { isAllGranted, granted, deniedForever, denied ->
                            if (isAllGranted) {
                                switchType = true
                                binding.recordTimeTv.text = "00.00"
                                binding.travelDescrEt.setText("")
                                binding.autoCb.isChecked = false
                                binding.recordCl.isVisible = true
                                binding.contentCl.isVisible = false
                                binding.switchImg.setImageResource(R.drawable.icon_switch_text)
                                binding.switchBtn.text = "切换文字描述"
                            } else {
                                ToastUtil.toast(getString(R.string.permission_record_rejected))
                            }
                        }
                        .request()
                }
            }
            binding.recordBtn -> {//录制按钮
                if (currentActionState == MediaRecorderHelper.ACTION_RECORDING && totalSecond < RecordContentDialog.MIN_RECORD_TIME) {
                    ToastUtil.toast(getString(R.string.record_longer))
                    return
                }
                switchActionState()
            }

            binding.recordRevertBtn -> {//语音撤回
                MessageDialog.show(
                    ActivityUtils.getTopActivity() as AppCompatActivity,
                    getString(R.string.re_record_title),
                    getString(R.string.confirm_to_re_record),
                    getString(R.string.ok),
                    getString(R.string.cancel)
                )
                    .setOnOkButtonClickListener { baseDialog, v ->
                        changeToNormalState()
                        false
                    }
                    .setOnCancelButtonClickListener { baseDialog, v ->
                        false
                    }

            }

            binding.recordConfirmBtn -> {//确认使用语音
                binding.recordCl.isVisible = false
                binding.previewAudioLl.isVisible = true
                binding.previewAudio.prepareAudio(mMediaRecorderHelper.currentFilePath, totalSecond)
                EventBus.getDefault().post(RecordCompleteEvent(totalSecond,mMediaRecorderHelper.currentFilePath))
                checkEnable()
            }
            binding.audioDeleteBtn -> {
                binding.previewAudioLl.visibility = View.GONE
                // previewAudio停止播放
                binding.previewAudio.release()
                changeToNormalState()
                binding.recordCl.isVisible = true
                checkEnable()
            }

        }
    }

    private fun publishTravel(content: String) {
        params["content"] = content
        params["content_type"] = if (binding.travelDescrEt.text.isNotEmpty()) {
            1
        } else {
            2
        }
        params["title"] = binding.travelTitle.text
        mPresenter?.issuePlan(params)
    }

    private fun changeToNormalState() {
        binding.apply {
            mIsRecorder = false
            currentActionState = MediaRecorderHelper.ACTION_NORMAL
            totalSecond = 0
            recordProgressTime.update(0, 0)
            MediaPlayerHelper.realese()
            recordBtn.setImageResource(R.drawable.icon_record_normal)
            mPreviewTimeThread?.stop()
            mMediaRecorderHelper.cancel()


            recordTitle.visibility = View.VISIBLE
            recordTitle.text = getString(R.string.click_to_start_record)


//            recordTimeTv.isVisible = false
            recordRevertBtn.isVisible = false
            recordListenTip.isInvisible = true
            recordConfirmBtn.isVisible = false

        }

    }

    private fun switchActionState() {
        binding.apply {
            mIsRecorder = false
            if (currentActionState == MediaRecorderHelper.ACTION_NORMAL) { //未录制状态
                currentActionState = MediaRecorderHelper.ACTION_RECORDING
                recordTimeTv.isVisible = true
                recordProgressTime.isVisible = true

                //开始录音
                mMediaRecorderHelper.startRecord()
                mIsRecorder = true
                recordTitle.text = getString(R.string.click_to_end_record)
                countTimeThread?.start()
            } else if (currentActionState == MediaRecorderHelper.ACTION_RECORDING) {//录制中
                currentActionState = MediaRecorderHelper.ACTION_COMMPLETE
                recordBtn.setImageResource(R.drawable.icon_record_start)
                recordRevertBtn.isVisible = true
                recordConfirmBtn.isVisible = true
                recordListenTip.isVisible = true
                recordTitle.text = getString(R.string.record_complete)
                recordListenTip.text = getString(R.string.click_to_try_listen)

//                recordTitle.isVisible = false
//                recordTimeTv.isVisible = false
                recordProgressTime.isInvisible = true

                mMediaRecorderHelper.stopAndRelease()
            } else if (currentActionState == MediaRecorderHelper.ACTION_COMMPLETE || currentActionState == MediaRecorderHelper.ACTION_DONE) {
                currentActionState = MediaRecorderHelper.ACTION_PLAYING
                //开启预览倒计时
                if (mPreviewTimeThread != null)
                    mPreviewTimeThread?.stop()
                mPreviewTimeThread = UpdateVoiceTimeThread.getInstance(
                    UriUtils.getShowTime(totalSecond),
                    recordTimeTv
                )

                recordBtn.setImageResource(R.drawable.icon_record_pause)
                MediaPlayerHelper.playSound(mMediaRecorderHelper.currentFilePath, {
                    currentActionState = MediaRecorderHelper.ACTION_COMMPLETE
                    recordBtn.setImageResource(R.drawable.icon_record_start)
                    mPreviewTimeThread?.stop()
//                MediaPlayerHelper.realese()
                    recordTitle.text = getString(R.string.record_complete)
                    recordTimeTv.text = UriUtils.getShowTime(totalSecond)

                }, {
                    mPreviewTimeThread?.start()
                })

            } else if (currentActionState == MediaRecorderHelper.ACTION_PLAYING) {
                //暂停播放
                currentActionState = MediaRecorderHelper.ACTION_PAUSE
                recordBtn.setImageResource(R.drawable.icon_record_start)
                //暂停预览倒计时
                mPreviewTimeThread?.pause()
                MediaPlayerHelper.pause()
            } else if (currentActionState == MediaRecorderHelper.ACTION_PAUSE) {
                currentActionState = MediaRecorderHelper.ACTION_PLAYING
                recordBtn.setImageResource(R.drawable.icon_record_pause)
                //开启预览计时线程
                mPreviewTimeThread?.start()
                MediaPlayerHelper.resume()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        countTimeThread?.cancel()
        mPreviewTimeThread?.stop()
        if (currentActionState == MediaRecorderHelper.ACTION_RECORDING) {
            mMediaRecorderHelper.cancel()
            changeToNormalState()
        }
    }

    override fun finish() {
        super.finish()
        countTimeThread?.cancel()
        mPreviewTimeThread?.stop()
        if (currentActionState == MediaRecorderHelper.ACTION_RECORDING) {
            mMediaRecorderHelper.cancel()
            changeToNormalState()
        }
        if (KeyboardUtils.isSoftInputVisible(this)) {
            KeyboardUtils.hideSoftInput(this)
        }
    }


    override fun uploadFile(success: Boolean, key: String) {
        if (success) {
            publishTravel(key)
        }
    }

    override fun issuePlan(success: Boolean) {
        if (success) {
            ToastUtil.toast("旅行计划发布成功")
            //todo  更新自己的旅行计划
            if (ActivityUtils.isActivityExistsInStack(PublishTravelBeforeActivity::class.java)) {
                ActivityUtils.finishActivity(PublishTravelBeforeActivity::class.java)
            }
            if (ActivityUtils.isActivityExistsInStack(PublishTravelActivity::class.java)) {
                ActivityUtils.finishActivity(PublishTravelActivity::class.java)
            }
            finish()

        }

    }
}