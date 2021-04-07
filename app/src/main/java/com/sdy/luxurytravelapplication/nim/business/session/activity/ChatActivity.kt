package com.sdy.luxurytravelapplication.nim.business.session.activity

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.alibaba.fastjson.JSON
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.ToastUtils
import com.kongzue.dialog.v3.MessageDialog
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.MsgServiceObserve
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig
import com.netease.nimlib.sdk.msg.model.CustomNotification
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.netease.nimlib.sdk.msg.model.MessageReceipt
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityChatBinding
import com.sdy.luxurytravelapplication.event.*
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.mvp.contract.ChatContract
import com.sdy.luxurytravelapplication.mvp.model.bean.ChatInfoBean
import com.sdy.luxurytravelapplication.mvp.model.bean.SendMsgBean
import com.sdy.luxurytravelapplication.mvp.presenter.ChatPresenter
import com.sdy.luxurytravelapplication.nim.api.NimUIKit
import com.sdy.luxurytravelapplication.nim.api.model.contact.ContactChangedObserver
import com.sdy.luxurytravelapplication.nim.api.model.main.OnlineStateChangeObserver
import com.sdy.luxurytravelapplication.nim.api.model.session.SessionCustomization
import com.sdy.luxurytravelapplication.nim.api.model.user.UserInfoObserver
import com.sdy.luxurytravelapplication.nim.attachment.SendGiftAttachment
import com.sdy.luxurytravelapplication.nim.business.audio.MessageAudioControl
import com.sdy.luxurytravelapplication.nim.business.module.Container
import com.sdy.luxurytravelapplication.nim.business.module.ModuleProxy
import com.sdy.luxurytravelapplication.nim.business.preference.UserPreferences
import com.sdy.luxurytravelapplication.nim.business.session.actions.BaseAction
import com.sdy.luxurytravelapplication.nim.business.session.actions.ImageAction
import com.sdy.luxurytravelapplication.nim.business.session.actions.LocationAction
import com.sdy.luxurytravelapplication.nim.business.session.panel.ChatInputPanel
import com.sdy.luxurytravelapplication.nim.business.session.panel.MessageListPanelEx
import com.sdy.luxurytravelapplication.nim.business.uinfo.UserInfoHelper
import com.sdy.luxurytravelapplication.nim.impl.NimUIKitImpl
import com.sdy.luxurytravelapplication.ui.activity.MessageInfoActivity
import com.sdy.luxurytravelapplication.utils.ToastUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * 聊天界面
 */
class ChatActivity :
    BaseMvpActivity<ChatContract.View, ChatContract.Presenter, ActivityChatBinding>(),
    ChatContract.View, ModuleProxy, View.OnClickListener {
    private val sessionId by lazy { intent.getStringExtra(EXTRA_ACCOUNT) ?: "" }

    private val customization: SessionCustomization by lazy {
        if (intent.getSerializableExtra(
                EXTRA_CUSTOMIZATION
            ) as SessionCustomization? != null
        ) {
            intent.getSerializableExtra(
                EXTRA_CUSTOMIZATION
            ) as SessionCustomization
        } else {
            NimUIKit.getCommonP2PSessionCustomization()
        }

    }
    private var isResume = false

    private lateinit var sensorManager: SensorManager

    private lateinit var proximitySensor: Sensor

    //modules
    lateinit var messageListPanel: MessageListPanelEx
    lateinit var inputPanel: ChatInputPanel

//409 -> {// 用户被封禁
//411 -> {//糖果余额不足
//201 -> {//门槛会员提示
//else -> { // 其他的发送失败，送出失败原因

    companion object {
        const val NOTICE_CHARGE = 1//金币余额不足，请充值后再试   「充值」
        const val NOTICE_REAL_VERIFY = 2 //真人认证后获取聊天金币      「真人认证」
        const val NOTICE_NORMAL = 3 //消息发送失败请重试  //对方未真人认证，请核实对方身份 等等
        const val NOTICE_FOOT_PRICE = 4 //门槛充值
        const val SEND_NOTICE_MSG: String = "noticeText"
        const val SEND_NOTICE_CODE: String = "noticeCode"


        const val IS_SHOW_REAL_TIP: String = "isShowRealTip"
        const val COIN_RECEIVE_STATE: String = "CoinReceiveState"
        const val COIN_CNT: String = "CoinCnt"
        const val COIN_TIMEOUT_TIME: String = "CoinTimeoutTime"


        const val EXTRA_ACCOUNT: String = "target_accid"
        const val EXTRA_ANCHOR: String = "anchor"
        const val EXTRA_CUSTOMIZATION: String = "customization"
        fun start(
            context: Context,
            target_accid: String,
            anchor: IMMessage? = null,
            customization: SessionCustomization? = null
        ) {
            val intent = Intent(context, ChatActivity::class.java)
            if (anchor != null)
                intent.putExtra(EXTRA_ANCHOR, anchor)

            if (customization != null)
                intent.putExtra(EXTRA_CUSTOMIZATION, customization)
            intent.putExtra(EXTRA_ACCOUNT, target_accid)
            context.startActivity(intent)

        }
    }


    override fun createPresenter(): ChatContract.Presenter = ChatPresenter()


    override fun initView() {
        initSensor()
        val container = Container(this, sessionId, SessionTypeEnum.P2P, this, true)
        val anchor = intent.getSerializableExtra(EXTRA_ANCHOR) as IMMessage?

        if (!this::messageListPanel.isInitialized) {
            messageListPanel = MessageListPanelEx(container, binding, anchor)
        } else {
            messageListPanel.reload(container, anchor)
        }

        if (!this::inputPanel.isInitialized) {
            inputPanel = ChatInputPanel(container, binding.inputCl, getActionList(), customization)
        } else {
            inputPanel.reload(container, customization)
        }

        inputPanel.switchRobotMode(isChatWithRobot())

        initViewAndClick()
    }


    override fun initData() {
        //单聊数据,包括个人信息
        requestBodyInfo()
        displayOnlineState()
        registerObservers(true)
    }

    override fun start() {
        if (!isChatWithRobot()) {
            mPresenter?.getTargetInfo(sessionId)
        } else {
            binding.barCl.rightIconBtn.isVisible = false
        }

    }


    private fun initViewAndClick() {
        ClickUtils.applySingleDebouncing(
            arrayOf<View>(
                binding.barCl.btnBack,
                binding.barCl.rightIconBtn
            ), this
        )
        binding.barCl.divider.isVisible = true
    }

    private fun displayOnlineState() {
        if (!NimUIKitImpl.enableOnlineState()) {
            return
        }
        val detailContent = NimUIKitImpl.getOnlineStateContentProvider().getDetailDisplay(sessionId)
    }

    private fun requestBodyInfo() {
        val name = UserInfoHelper.getUserTitleName(
            sessionId,
            SessionTypeEnum.P2P
        )
        binding.barCl.actionbarTitle.text = name
    }

    override fun onResume() {
        super.onResume()
        isResume = true
        if (this::sensorManager.isInitialized && this::proximitySensor.isInitialized) {
            sensorManager.registerListener(
                sensorEventListener, proximitySensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        messageListPanel.onResume()
        NIMClient.getService(MsgService::class.java)
            .setChattingAccount(sessionId, SessionTypeEnum.P2P)
        EventBus.getDefault().post(UpdateUnreadCntEvent())
        volumeControlStream = AudioManager.STREAM_VOICE_CALL//默认使用听筒播放
    }


    override fun onPause() {
        super.onPause()
        if (this::sensorManager.isInitialized && this::proximitySensor.isInitialized) {
            sensorManager.unregisterListener(sensorEventListener)
        }
        NIMClient.getService(MsgService::class.java)
            .setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None)
        //退出聊天界面刷新消息中心
        EventBus.getDefault().post(RefreshMessageCenterEvent())
        inputPanel.onPause()
        messageListPanel.onPause()
    }

    override fun onStop() {
        super.onStop()
        isResume = false
    }


    override fun onDestroy() {
        super.onDestroy()
        registerObservers(false)
        messageListPanel.onDestory()
        inputPanel.onDestroy()
    }


    override fun onBackPressed() {
        inputPanel.collapse(true)
        messageListPanel.onBackPressed()
        // NIMClient.getService(MsgService::class.java).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None)
        super.onBackPressed()
    }

    private val sensorEventListener: SensorEventListener by lazy {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val dis = event.values
                if (0.0f == dis[0]) {
                    //靠近，设置为听筒模式
                    MessageAudioControl.getInstance(this@ChatActivity).setEarPhoneModeEnable(true)
                } else {
                    //离开，复原
                    MessageAudioControl.getInstance(this@ChatActivity)
                        .setEarPhoneModeEnable(UserPreferences.isEarPhoneModeEnable())
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }
    }

    private fun initSensor() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (sensorManager != null) {
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        }
    }


    /**
     * ************************* 消息收发 **********************************
     */
    // 是否允许发送消息
    protected fun isAllowSendMessage(message: IMMessage): Boolean {
        return customization?.isAllowSendMessage(message) ?: false
    }

    protected fun showCommandMessage(message: CustomNotification) {
        if (!isResume) {
            return
        }
        val content = message.content
        try {
            val json =
                JSON.parseObject(content)
            val id = json.getIntValue("id")
            if (id == 1) {
                // 正在输入
                ToastUtils.showShort(getString(R.string.target_is_inputting))
            } else {
                ToastUtils.showShort("command: $content")
            }
        } catch (ignored: Exception) {
        }
    }

    private fun onMessageIncoming(messages: MutableList<IMMessage>) {
        if (messages.isNullOrEmpty()) {
            return
        }
        messageListPanel.onIncomingMessage(messages)
        // 发送已读回执
        messageListPanel.sendReceipt()
    }


    private fun appendPushConfig(message: IMMessage, deadline: Int, expend_coin: Int) {
        val customConfig = NimUIKitImpl.getCustomPushContentProvider() ?: return
        val content = customConfig.getPushContent(message)
        val payload =
            customConfig.getPushPayload(message)
        if (!TextUtils.isEmpty(content)) {
            message.pushContent = content
        }
        if (payload != null) {
            message.pushPayload = payload
        }

        if (message.msgType != MsgTypeEnum.tip && message.msgType != MsgTypeEnum.notification && message.msgType != MsgTypeEnum.custom && expend_coin > 0) {
            val extension =
                if (message.localExtension.isNullOrEmpty()) hashMapOf<String, Any>() else message.localExtension
            extension[COIN_RECEIVE_STATE] = SendGiftAttachment.STATUS_WAIT
            message.localExtension = extension
            // 添加金币超时时间 添加金币数目
            val remoteExtension =
                if (message.remoteExtension.isNullOrEmpty()) hashMapOf<String, Any>() else message.remoteExtension
            remoteExtension[COIN_CNT] = expend_coin
            remoteExtension[COIN_TIMEOUT_TIME] = deadline
            message.remoteExtension = remoteExtension
        }
    }

    fun appendPushConfigAndSend(message: IMMessage, deadline: Int, expend_coin: Int) {
        appendPushConfig(message, deadline, expend_coin)
        sendMsgS(message)
    }

    private fun sendMsgS(message: IMMessage) {
        NIMClient.getService(MsgService::class.java).sendMessage(message, false)
            .setCallback(object : RequestCallback<Void?> {
                override fun onSuccess(param: Void?) {
                    if (sessionId == Constants.ASSISTANT_ACCID) {
                        if (message.msgType == MsgTypeEnum.text)
                            mPresenter?.aideSendMsg(message)
                    } else {
                        //刷新搭讪列表
                        EventBus.getDefault().post(RemoveChatUpEvent(message))
                    }
                    //如果是图片消息并且有扩展字段，则为地图消息，删除图片
//                    if (message.msgType == MsgTypeEnum.image && message.remoteExtension != null && message.remoteExtension[LocationActivity.EXTENSION_LATITUDE] != null) {
//                        FileUtils.delete((message.attachment as FileAttachment).path)
//                    }
                }

                override fun onFailed(code: Int) {
                }

                override fun onException(exception: Throwable?) {
                }

            })

        //如果是和真人聊天就更新聊天金币，和小助手聊天就新增聊天
        if (isChatWithRobot()) {
            messageListPanel.onPretendMsgSend(message)
        } else {
            messageListPanel.onMsgSend(message)
        }
    }


    private fun isChatWithRobot() = sessionId == Constants.ASSISTANT_ACCID


    private fun setMessageStatus(message: IMMessage, msgStatus: MsgStatusEnum) {
        message.status = msgStatus
        NIMClient.getService(MsgService::class.java).updateIMMessageStatus(message)
    }


    /**************************观察者模式********************************/

    private fun registerObservers(register: Boolean) {
        if (NimUIKitImpl.getOptions().shouldHandleReceipt) {
            NIMClient.getService(MsgServiceObserve::class.java)
                .observeMessageReceipt(messageReceiptObserver, register)
        }
        NIMClient.getService(MsgServiceObserve::class.java)
            .observeReceiveMessage(incomingMessageObserver, register)
//        NIMClient.getService(MsgServiceObserve::class.java).observeCustomNotification(commandObserver, register)
        NimUIKit.getUserInfoObservable().registerObserver(userInfoObserver, register)
//        NimUIKit.getContactChangedObservable().registerObserver(friendDataChangedObserver, register)
        if (NimUIKit.enableOnlineState()) {
            NimUIKit.getOnlineStateChangeObservable()
                .registerOnlineStateChangeListeners(onlineStateChangeObserver, register)
        }
    }


    /**
     * 命令消息接收观察者
     */
    private val commandObserver =
        Observer<CustomNotification> { message ->
            if (sessionId != message.sessionId || message.sessionType != SessionTypeEnum.P2P) {
                return@Observer
            }
            showCommandMessage(message)
        }


    /**
     * 用户信息变更观察者
     */
    private val userInfoObserver =
        UserInfoObserver { accounts ->
            if (!accounts.contains(sessionId)) {
                return@UserInfoObserver
            }
            requestBodyInfo()
        }

    /**
     * 好友资料变更（eg:关系）
     */
    private val friendDataChangedObserver: ContactChangedObserver =
        object :
            ContactChangedObserver {
            override fun onAddedOrUpdatedFriends(accounts: List<String>) {
                title = UserInfoHelper.getUserTitleName(
                    sessionId,
                    SessionTypeEnum.P2P
                )
            }

            override fun onDeletedFriends(accounts: List<String>) {
                title = UserInfoHelper.getUserTitleName(
                    sessionId,
                    SessionTypeEnum.P2P
                )
            }

            override fun onAddUserToBlackList(account: List<String>) {
                title = UserInfoHelper.getUserTitleName(
                    sessionId,
                    SessionTypeEnum.P2P
                )
            }

            override fun onRemoveUserFromBlackList(account: List<String>) {
                title = UserInfoHelper.getUserTitleName(
                    sessionId,
                    SessionTypeEnum.P2P
                )
            }
        }

    /**
     * 好友在线状态观察者
     */
    private val onlineStateChangeObserver =
        OnlineStateChangeObserver { accounts ->
            if (!accounts.contains(sessionId)) {
                return@OnlineStateChangeObserver
            }
            // 按照交互来展示
            displayOnlineState()
        }


    /**
     * 已读回执观察者
     */
    private val messageReceiptObserver: Observer<List<MessageReceipt>> by lazy {
        Observer<List<MessageReceipt>> { messageListPanel.receiveReceipt() }
    }


    /**
     * 消息接收观察者
     */
    private val incomingMessageObserver: Observer<MutableList<IMMessage>> by lazy {
        Observer<MutableList<IMMessage>> { messages ->
            onMessageIncoming(messages)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        inputPanel.onActivityResult(requestCode, resultCode, data)
        messageListPanel.onActivityResult(requestCode, resultCode, data)

        customization.onActivityResult(this, requestCode, resultCode, data)
    }

    override fun onClick(view: View) {
        when (view) {
            binding.barCl.btnBack -> {
                onBackPressed()
            }
            binding.barCl.rightIconBtn -> {
                if (::chatInfoBean.isInitialized)
                    MessageInfoActivity.start(
                        this,
                        sessionId,
                        chatInfoBean.isfriend,
                        chatInfoBean.stared
                    )
            }

        }
    }


    private fun canSendMsg(): Boolean {
        if (::chatInfoBean.isInitialized && chatInfoBean.islimit) {
            if (!chatInfoBean.my_isfaced || chatInfoBean.mv_url_state == 0) {
                MessageDialog.show(
                    this, getString(R.string.chat_cnt_use_up), if (!chatInfoBean.my_isfaced) {
                        getString(R.string.to_get_more_by_verify)
                    } else if (!chatInfoBean.mv_url) {
                        getString(R.string.to_get_more_by_video)
                    } else {
                        getString(R.string.today_has_used_up)
                    },
                    getString(R.string.ok), getString(R.string.cancel)
                ).setOnOkButtonClickListener { baseDialog, v ->
//                    if (chatInfoBean.my_face_state == 0) {
//                        if (chatInfoBean.has_face_url)
//                            MyInfoActivity.start(this, true)
//                        else
//                            FaceLivenessExpActivity.startActivity(
//                                this,
//                                FaceLivenessExpActivity.TYPE_ACCOUNT_NORMAL
//                            )
//                    } else if (chatInfoBean.mv_url_state == 0) {
//                        MyPowerActivity.start(this)
//                    } else {
//                        ToastUtil.toast("认证正在审核中，请等待")
//                    }
                    false
                }
                    .setOnCancelButtonClickListener { baseDialog, v ->
                        false
                    }
                return false
            } else {
                return true
            }
        } else {
            return true
        }
    }


    /**
     * ********************** implements ModuleProxy *********************
     */
    override fun sendMessage(message: IMMessage): Boolean {
        if (isChatWithRobot()) {
            sendMsgS(message)
        } else if (canSendMsg()) {
            sendMsgRequest(message)
        }

        return true
    }

    //语音、文字、视频、图片、定位消息调用接口
//其他直接发送
    private fun sendMsgRequest(message: IMMessage) {
        setMessageStatus(message, MsgStatusEnum.sending)
        messageListPanel.onPretendMsgSend(message)
        when (message.msgType) {
            MsgTypeEnum.audio -> {
                mPresenter?.uploadImgToQN(
                    message,
                    sessionId,
                    (message.attachment as AudioAttachment).path
                )
            }
            MsgTypeEnum.video -> {
                mPresenter?.uploadImgToQN(
                    message,
                    sessionId,
                    (message.attachment as VideoAttachment).path
                )
            }
            MsgTypeEnum.image -> {
//                if (message.remoteExtension != null && message.remoteExtension[LocationActivity.EXTENSION_LATITUDE] != null) {
//                    mPresenter.sendMsg(message, sessionId, islocation = true)
//                } else {
                mPresenter?.uploadImgToQN(
                    message,
                    sessionId,
                    (message.attachment as ImageAttachment).path
                )
//                }
            }
            MsgTypeEnum.text -> {
                mPresenter?.sendMsg(message, sessionId)
            }
            else -> {
                sendMsgS(message)
            }
        }
    }


    override fun isLongClickEnabled(): Boolean {
        return !inputPanel.isRecording()
    }


    /**
     * 提示消息点击
     */
    override fun onNoticeMessageClick(message: IMMessage) {
        val extension = message.localExtension
        val noticeType = extension?.get(SEND_NOTICE_CODE) ?: -1
        when (noticeType) {
            NOTICE_CHARGE -> { //金币充值
//                GoldChargeDialog(this).show()
            }
            NOTICE_REAL_VERIFY -> {//真人认证
//                FaceLivenessExpActivity.startActivity(
//                    this,
//                    FaceLivenessExpActivity.TYPE_ACCOUNT_NORMAL
//                )
            }
            NOTICE_FOOT_PRICE -> { //门槛充值

            }
        }

    }


    override fun shouldCollapseInputPanel() {
        inputPanel.collapse(false)
    }

    override fun onInputPanelExpand() {
        messageListPanel.scrollToBottom()
    }

    override fun onReplyMessage(replyMsg: IMMessage) {
    }

    override fun onItemFooterClick(message: IMMessage) {

    }


    //发起语音聊天
    override fun createVoiceCall() {
//        CommonFunction.checkTalk(this, sessionId)
    }

    // 操作面板集合
    protected fun getActionList(): MutableList<BaseAction> {
        val actions: MutableList<BaseAction> = ArrayList()
        actions.add(ImageAction())
//        actions.add(VideoAction())
        actions.add(LocationAction())
        if (customization.actions != null) {
            actions.addAll(customization.actions)
        }
        return actions
    }


    override fun focusResult(success: Boolean, isfocus: Boolean) {
        if (success) {
            sendLikeTipMessage(sessionId ?: "", false)
        }
    }


    fun sendLikeTipMessage(send_accid: String, isReceive: Boolean) {
        if (!chatInfoBean.isliked) {
            val tipMessage = MessageBuilder.createTipMessage(send_accid, SessionTypeEnum.P2P)
            tipMessage.content = if (isReceive) {
                getString(R.string.receive_like_tip)
            } else {
                getString(R.string.send_like_tip)
            }
            val confis = CustomMessageConfig()
            confis.enableUnreadCount = false
            tipMessage.config = confis
            tipMessage.status = MsgStatusEnum.success
            NIMClient.getService(MsgService::class.java).saveMessageToLocal(tipMessage, true)
        }
    }

    private lateinit var chatInfoBean: ChatInfoBean
    override fun getTargetInfoResult(voiceBean: ChatInfoBean?, code: Int, msg: String) {
        when (code) {
            200 -> {
                this.chatInfoBean = chatInfoBean
                UserManager.isFaced = chatInfoBean.my_isfaced
                UserManager.mvFaced = chatInfoBean.mv_url
                UserManager.hasFaceUrl = chatInfoBean.has_face_url

                binding.barCl.actionbarTitle.text = chatInfoBean.nickname
                messageListPanel.sendWarmingNotice(chatInfoBean.chat_expend_aomount)

            }
            409 -> {
                MessageDialog.show(
                    ActivityUtils.getTopActivity() as AppCompatActivity,
                    getString(R.string.notice),
                    msg, getString(R.string.iknow)
                )
                    .setOnOkButtonClickListener { _, v ->
                        finish()
                        false
                    }
            }
            else -> {
                ToastUtil.toast(msg)
            }
        }

    }


    /**
     * 七牛上传媒体结果
     */
    override fun uploadImgToQNResult(
        isOk: Boolean,
        key: String,
        content: IMMessage,
        targetAccid: String
    ) {
        if (isOk) {
            mPresenter?.sendMsg(content, targetAccid, key)
        } else {
            setFailedStatus(content, CommonFunction.getErrorMsg(this), NOTICE_NORMAL)
        }

    }


    /**
     * 发送消息回传给服务器
     */
    override fun sendMsgResult(
        code: Int,
        sendMsgBean: SendMsgBean?,
        msg: String,
        message: IMMessage
    ) {
        when (code) {
            200 -> {
                appendPushConfigAndSend(
                    message,
                    sendMsgBean?.deadline ?: 0,
                    sendMsgBean?.expend_coin ?: 0
                )
                CommonFunction.updateMessageExtension(
                    message, SEND_NOTICE_MSG,
                    if (sendMsgBean?.noticeType == NOTICE_REAL_VERIFY)
                        getString(R.string.notice_real_verify)
                    else
                        sendMsgBean?.noticeText ?: ""
                )
                CommonFunction.updateMessageExtension(
                    message, SEND_NOTICE_CODE,
                    sendMsgBean?.noticeType ?: NOTICE_NORMAL
                )
                EventBus.getDefault().post(RefreshGoldEvent())
            }
            409 -> {// 用户被封禁
                setFailedStatus(message, msg, NOTICE_NORMAL)
                MessageDialog.show(
                    ActivityUtils.getTopActivity() as AppCompatActivity,
                    getString(R.string.notice),
                    msg,
                    getString(R.string.iknow)
                )
                    .setOnOkButtonClickListener { baseDialog, v ->
                        CommonFunction.dissolveRelationshipLocal(sessionId)
                        false
                    }
            }
            411 -> {//糖果余额不足
                // 糖果余额不足
                setFailedStatus(message, getString(R.string.notice_gold_charge), NOTICE_CHARGE)
            }
            201 -> {//门槛会员提示
                setFailedStatus(message, getString(R.string.notice_foot_price), NOTICE_CHARGE)
            }
            else -> { // 其他的发送失败，送出失败原因
                setFailedStatus(message, msg, NOTICE_NORMAL)
            }
        }
    }

    private fun setFailedStatus(message: IMMessage, msg: String, code: Int) {
        setMessageStatus(message, MsgStatusEnum.fail)
        CommonFunction.updateMessageExtension(message, SEND_NOTICE_MSG, msg)
        CommonFunction.updateMessageExtension(message, SEND_NOTICE_CODE, code)
        messageListPanel.refreshMessageItem(message.uuid)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSendLikeTipMessageEvent(event: SendLikeTipMessageEvent) {
        sendLikeTipMessage(event.accid, event.isReceive)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateVerifyEvent(event: UpdateVerifyEvent) {
        if (!isChatWithRobot())
            mPresenter?.getTargetInfo(sessionId)

    }

}