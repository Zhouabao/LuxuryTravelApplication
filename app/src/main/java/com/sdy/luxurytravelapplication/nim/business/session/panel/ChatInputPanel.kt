package com.sdy.luxurytravelapplication.nim.business.session.panel

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.SystemClock
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.SizeUtils
import com.kongzue.dialog.v3.MessageDialog
import com.kongzue.dialog.v3.TipDialog
import com.netease.nimlib.sdk.media.record.AudioRecorder
import com.netease.nimlib.sdk.media.record.IAudioRecordCallback
import com.netease.nimlib.sdk.media.record.RecordType
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.ActivityChatBinding
import com.sdy.luxurytravelapplication.databinding.LayoutNimInputBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.mvp.model.bean.PublishWayBean
import com.sdy.luxurytravelapplication.nim.api.model.session.SessionCustomization
import com.sdy.luxurytravelapplication.nim.business.emoji.IEmoticonSelectedListener
import com.sdy.luxurytravelapplication.nim.business.module.Container
import com.sdy.luxurytravelapplication.nim.business.session.actions.BaseAction
import com.sdy.luxurytravelapplication.nim.impl.NimUIKitImpl
import com.sdy.luxurytravelapplication.ui.dialog.SendGiftDialog
import java.io.File

/**
 *    author : ZFM
 *    date   : 2020/10/2015:10
 *    desc   :
 *    version: 1.0
 */
class ChatInputPanel(
    var container: Container,
    val binding: LayoutNimInputBinding,
    val mediaActions: MutableList<BaseAction>,
    var customization: SessionCustomization
) : IEmoticonSelectedListener, IAudioRecordCallback {
    private val handler by lazy { Handler() }

    companion object {
        const val TAG = "MsgSendLayout"
        const val SHOW_LAYOUT_DELAY = 200L
    }

    init {
        init()
    }

    fun reload(container: Container, customization: SessionCustomization) {
        this.container = container
        this.customization = customization
    }

    private fun init() {
        initTextEdit()
        initAudioRecordButton()
        restoreText(false)
        for (i in mediaActions.indices) {
            mediaActions[i].setIndex(i)
            mediaActions[i].container = container
        }
    }


    private val actions: MutableList<PublishWayBean> by lazy {
        mutableListOf(
            PublishWayBean(false, R.drawable.icon_msg_voice, R.drawable.icon_msg_voice_selected),
            PublishWayBean(false, R.drawable.icon_msg_emoj, R.drawable.icon_msg_emoj_selected),
            PublishWayBean(false, R.drawable.icon_msg_image, R.drawable.icon_msg_image),
            PublishWayBean(
                false,
                R.drawable.icon_msg_location,
                R.drawable.icon_msg_location
            ),
            PublishWayBean(false, R.drawable.icon_msg_call, R.drawable.icon_msg_call),
            PublishWayBean(false, R.drawable.icon_msg_gift, R.drawable.icon_msg_gift)
        )

    }

    private val adapter by lazy { ChatActionAdapter() }
    private fun initActionPanel(isRobot: Boolean = false) {
        adapter.setNewInstance(
            if (isRobot) {
                actions
//                actions.subList(0, actions.size - 2)
            } else {
                actions
            }
        )
        binding.inputActionsRv.layoutManager =
            GridLayoutManager(container.activity, adapter.data.size)
        binding.inputActionsRv.adapter = adapter
        adapter.setOnItemClickListener { _, view, position ->
            val checkedData = adapter.data[position]
            for (data in adapter.data) {
                if (data == checkedData)
                    data.checked = !data.checked
                else
                    data.checked = false
            }
            adapter.notifyDataSetChanged()
            when (position) {
                0 -> {
                    if (checkedData.checked) {
                        PermissionUtils.permission(PermissionConstants.MICROPHONE)
                            .callback { isAllGranted, granted, deniedForever, denied ->
                                if (isAllGranted) {
                                    showAudioLayout()
                                }
                            }
                            .request()
                    } else {
                        hideAudioLayout()
                    }
                }
                1 -> {
                    if (checkedData.checked)
                        showEmojLayout()
                    else
                        hideEmojLayout()
                }
                2, 3 -> {
                    hideAllInputLayout()
                    mediaActions[position - 2].onClick()
                }

                4 -> {//发起语音聊天
                    CommonFunction.checkUnlockContact(container.activity,container.account,1)
                }
                5 -> {//点击赠送🎁
                    SendGiftDialog(container).show()
                }
            }
        }
    }

    private var keyboardHeight = 0F
    private fun initTextEdit() {
        binding.editTextMessage.setHorizontallyScrolling(false)
        binding.editTextMessage.maxLines = 2
//          binding.editTextMessage.inputType = InputType.TYPE_CLASS_TEXT
        binding.editTextMessage.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                switchToTextLayout(true)
            }
            false
        }

        binding.editTextMessage.setOnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_SEND) {
                // 检测是否能发送消息
                if (!TextUtils.isEmpty(binding.editTextMessage.text.toString().trim())) {
                    onTextMessageSendButtonPressed()
                }

                true
            } else
                false
        }

    }


    /**
     * 发送文本消息
     */
    var previousContent: String = ""
    private fun onTextMessageSendButtonPressed() {
        val text = binding.editTextMessage.text.toString()
        if (!text.isEmpty() && previousContent != text) {
            previousContent = text
            val textMessage =
                MessageBuilder.createTextMessage(container.account, container.sessionType, text)
            if (container.proxy.sendMessage(textMessage)) {
                restoreText(true)
            }
        }
    }

    public fun restoreText(clearText: Boolean) {
        if (clearText) {
            binding.editTextMessage.setText("")
            handler.postDelayed({
//                showInputMethod(  binding.editTextMessage)
                binding.editTextMessage.requestFocus()
                binding.editTextMessage.setSelection(binding.editTextMessage.text.length)
            }, SHOW_LAYOUT_DELAY)
        }
        previousContent = ""

    }


    private fun switchToTextLayout(needShowInput: Boolean) {
        hideEmojLayout()
        hideAudioLayout()

        if (needShowInput) {
            handler.postDelayed({
                showInputMethod(binding.editTextMessage)
            }, SHOW_LAYOUT_DELAY)
        } else {
            hideInputMethod()
        }
    }

    private var isKeyboardShowed = false


    // 显示键盘布局
    fun showInputMethod(editText: EditText) {
        editText.requestFocus()
        //如果已经显示,则继续操作时不需要把光标定位到最后
        if (!isKeyboardShowed) {
            editText.setSelection(editText.text.length)
            isKeyboardShowed = true
        }
        KeyboardUtils.showSoftInput(editText, 0)

        container.proxy.onInputPanelExpand()
    }

    //隐藏键盘布局
    fun hideInputMethod() {
        isKeyboardShowed = false
        handler.removeCallbacksAndMessages(null)
        KeyboardUtils.hideSoftInput(binding.editTextMessage)
        binding.editTextMessage.clearFocus()
    }

    // 隐藏表情布局
    fun hideEmojLayout() {
        handler.removeCallbacksAndMessages(null)
        binding.emotionPickerView.isVisible = false
    }

    //隐藏录音布局
    fun hideAudioLayout() {
        binding.audioCl.isVisible = false
    }


    //显示录音布局
    fun showAudioLayout() {
        hideInputMethod()
        hideEmojLayout()

        binding.audioCl.isVisible = true
    }


    private fun hideAllInputLayout() {
        hideAudioLayout()
        hideEmojLayout()
        hideInputMethod()

        for (data in adapter.data) {
            data.checked = false
        }
        adapter.notifyDataSetChanged()
    }


    /*****************表情模块*************************/
    // 显示表情布局
    fun showEmojLayout() {
        hideInputMethod()
        hideAudioLayout()

        binding.editTextMessage.requestFocus()
        handler.postDelayed({
            binding.emotionPickerView.isVisible = true
        }, SHOW_LAYOUT_DELAY)
        binding.emotionPickerView.show(this)

        container.proxy.onInputPanelExpand()
    }


    override fun onEmojiSelected(key: String) {
        val mEditable = binding.editTextMessage.text
        if (key == "/DEL") {
            binding.editTextMessage.dispatchKeyEvent(
                KeyEvent(
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_DEL
                )
            )
        } else {
            var start =
                if (binding.editTextMessage.selectionStart < 0) 0 else binding.editTextMessage.selectionStart
            var end =
                if (binding.editTextMessage.selectionEnd < 0) 0 else binding.editTextMessage.selectionEnd
            mEditable.replace(start, end, key)
        }
    }

    override fun onStickerSelected(categoryName: String?, stickerName: String?) {
        val attachment = customization.createStickerAttachment(categoryName, stickerName)
        val stickerMsg =
            MessageBuilder.createCustomMessage(container.account, container.sessionType, attachment)
        container.proxy.sendMessage(stickerMsg)
    }

    /****************************语音模块*************************************/
    /**
     * 初始化AudioRecord
     */
    private var started = false
    private var cancelled = false
    private var touched = false
    private lateinit var audioMessageHelper: AudioRecorder
    private fun initAudioRecordButton() {
        binding.recordBtn.setOnTouchListener { view1, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touched = true
                    initAudioRecord()
                    onStartAudioRecord()
                }
                MotionEvent.ACTION_CANCEL,
                MotionEvent.ACTION_UP -> {
                    touched = false
                    onEndAudioRecord(isCancelled(view1, event))
                }
                MotionEvent.ACTION_MOVE -> {
                    touched = true
                    cancelAudioRecord(isCancelled(view1, event))
                }

            }

            false
        }
    }


    //上滑取消录音判断
    fun isCancelled(view: View, event: MotionEvent): Boolean {
        val location = intArrayOf(0, 0)
        binding.root.getLocationOnScreen(location)
        if (event.rawX < location[0] || event.rawX > location[0] + binding.root.width || event.rawY < location[1] - 40) {
            return true
        }
        return false
    }

    private fun initAudioRecord() {
        if (!this::audioMessageHelper.isInitialized) {
            val options = NimUIKitImpl.getOptions()
            audioMessageHelper = AudioRecorder(
                container.activity,
                options.audioRecordType,
                options.audioRecordMaxTime,
                this
            )
        }
    }

    /**
     * 开始录音
     */
    fun onStartAudioRecord() {
        container.activity.window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        audioMessageHelper.startRecord()
        cancelled = false

    }


    /**
     * 结束录音
     */
    fun onEndAudioRecord(cancel: Boolean) {
        started = false
        container.activity.window.setFlags(0, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        audioMessageHelper.completeRecord(cancel)
        binding.recordTitle.setText(R.string.record_audio)
        stopAudioRecordAnim()
    }

    /**
     * 结束语音录制动画
     */
    private fun stopAudioRecordAnim() {
        binding.recordAnimation.isVisible = false
        binding.recordTimeTv.stop()
        binding.recordTimeTv.base = SystemClock.elapsedRealtime()
    }

    /**
     * 开始语音录制动画
     */
    private fun playAudioRecordAnim() {
        binding.recordAnimation.isVisible = true
        binding.recordTimeTv.base = SystemClock.elapsedRealtime()
        binding.recordTimeTv.start()
        binding.recordTimeTv.setOnChronometerTickListener {
            val time = (SystemClock.elapsedRealtime() - it.base) / 1000L
            binding.recordProgressTime.progress =
                (time * 1f / NimUIKitImpl.getOptions().audioRecordMaxTime * 100).toInt()
        }
    }


    /**
     * 取消录音
     */

    fun cancelAudioRecord(cancel: Boolean) {
        if (!started) {
            return
        }

        if (cancelled == cancel) {
            return
        }
        cancelled = cancel
        updateTimerTip(cancel)
    }

    /**
     * 正在进行语音录制和取消语音录制，界面展示
     *
     * @param cancel
     */
    private fun updateTimerTip(cancel: Boolean) {
        if (cancel) {
            binding.recordTitle.setText(R.string.recording_cancel_tip)
        } else {
            binding.recordTitle.setText(R.string.recording_cancel)
        }
    }


    /**
     * 是否正在录音
     */
    fun isRecording(): Boolean {
        return this::audioMessageHelper.isInitialized && audioMessageHelper.isRecording
    }

    override fun onRecordSuccess(audioFile: File, audioLength: Long, recordType: RecordType) {
        val audioMessage = MessageBuilder.createAudioMessage(
            container.account,
            container.sessionType,
            audioFile,
            audioLength
        )
        container.proxy.sendMessage(audioMessage)
    }

    override fun onRecordReachedMaxTime(maxTime: Int) {
        stopAudioRecordAnim()
        MessageDialog.show(
            container.activity as AppCompatActivity,
            container.activity.getString(R.string.record_notice),
            container.activity.getString(R.string.recording_max_time),
            container.activity.getString(R.string.send),
            container.activity.getString(R.string.cancel)
        ).setOnOkButtonClickListener { baseDialog, v ->
            audioMessageHelper.handleEndRecord(true, maxTime)
            false
        }.setOnCancelButtonClickListener { baseDialog, v ->
            false
        }

    }

    override fun onRecordReady() {
    }

    override fun onRecordCancel() {
    }

    override fun onRecordStart(audioFile: File?, recordType: RecordType?) {
        started = true
        if (!touched)
            return
        binding.recordTitle.setText(R.string.record_audio_end)
        updateTimerTip(false)//初始化语音动画状态
        playAudioRecordAnim()
    }

    override fun onRecordFail() {
        if (started) {
            TipDialog.show(
                container.activity as AppCompatActivity,
                container.activity.resources.getString(R.string.recording_error),
                TipDialog.TYPE.ERROR
            )
        }
    }


    fun onPause() {
        //停止录音
        if (this::audioMessageHelper.isInitialized) {
            onEndAudioRecord(true)
        }
    }


    fun onDestroy() {
        // release
        if (this::audioMessageHelper.isInitialized) {
            audioMessageHelper.destroyAudioRecorder()
        }
    }

    fun collapse(immediately: Boolean) {
        hideAllInputLayout()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        var index = (requestCode shl 16) shr 24
        if (index != 0) {
            index--
            if (index < 0 || index >= mediaActions.size) {
                Log.d(TAG, "request code out of actions' range")
                return
            }
            val action = mediaActions[index]
            action?.onActivityResult(requestCode and 0xff, resultCode, data)
        }
    }


    private var isRobotSession = false
    fun switchRobotMode(isRobot: Boolean) {
        isRobotSession = isRobot
        initActionPanel(isRobot)
    }
}