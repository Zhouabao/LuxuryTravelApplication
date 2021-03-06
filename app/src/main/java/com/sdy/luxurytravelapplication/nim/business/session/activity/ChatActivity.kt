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
import com.blankj.utilcode.util.*
import com.kongzue.dialog.v3.MessageDialog
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.MsgServiceObserve
import com.netease.nimlib.sdk.msg.attachment.AudioAttachment
import com.netease.nimlib.sdk.msg.attachment.FileAttachment
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
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
import com.sdy.luxurytravelapplication.liveface.FaceLivenessExpActivity
import com.sdy.luxurytravelapplication.mvp.contract.ChatContract
import com.sdy.luxurytravelapplication.mvp.model.bean.*
import com.sdy.luxurytravelapplication.mvp.presenter.ChatPresenter
import com.sdy.luxurytravelapplication.nim.api.NimUIKit
import com.sdy.luxurytravelapplication.nim.api.model.contact.ContactChangedObserver
import com.sdy.luxurytravelapplication.nim.api.model.main.OnlineStateChangeObserver
import com.sdy.luxurytravelapplication.nim.api.model.session.SessionCustomization
import com.sdy.luxurytravelapplication.nim.api.model.user.UserInfoObserver
import com.sdy.luxurytravelapplication.nim.attachment.SendCustomTipAttachment
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
import com.sdy.luxurytravelapplication.ui.activity.LocationActivity
import com.sdy.luxurytravelapplication.ui.activity.MessageInfoActivity
import com.sdy.luxurytravelapplication.ui.dialog.ChatUpOpenPtVipDialog
import com.sdy.luxurytravelapplication.ui.dialog.ContactCandyReceiveDialog
import com.sdy.luxurytravelapplication.ui.fragment.SnackBarFragment
import com.sdy.luxurytravelapplication.utils.ToastUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * ????????????
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

//409 -> {// ???????????????
//411 -> {//??????????????????
//201 -> {//??????????????????
//else -> { // ??????????????????????????????????????????

    companion object {
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


    override fun initData() {
        initSensor()
        val container = Container(this, sessionId, SessionTypeEnum.P2P, this, true)
        val anchor = intent.getSerializableExtra(EXTRA_ANCHOR) as IMMessage?

        if (!this::messageListPanel.isInitialized) {
            messageListPanel = MessageListPanelEx(container, binding.messageListRv, anchor)
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

        //????????????,??????????????????
        requestBodyInfo()
        displayOnlineState()
        registerObservers(true)
    }

    override fun start() {
        if (!isChatWithRobot()) {
            mPresenter?.getTargetInfo(sessionId)
        }
    }


    private fun initViewAndClick() {
        ClickUtils.applySingleDebouncing(
            arrayOf<View>(
                binding.barCl.btnBack,
                binding.barCl.rightIconBtn,
                binding.unlockChatLl,
                binding.gotoVerifyBtn,
                binding.inputCl.unlockContactBtn,
                binding.inputCl.closeContactBtn
            ), this
        )
        binding.barCl.divider.isVisible = true
        binding.barCl.rightIconBtn.setImageResource(R.drawable.icon_more_black)
        binding.barCl.rightIconBtn.isVisible = !isChatWithRobot()

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
        EventBus.getDefault().post(GetNewMsgEvent())
        volumeControlStream = AudioManager.STREAM_VOICE_CALL//????????????????????????
    }


    override fun onPause() {
        super.onPause()
        if (this::sensorManager.isInitialized && this::proximitySensor.isInitialized) {
            sensorManager.unregisterListener(sensorEventListener)
        }
        NIMClient.getService(MsgService::class.java)
            .setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None)
        //????????????????????????????????????
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
        EventBus.getDefault().postSticky(UpdateHiEvent())
        // NIMClient.getService(MsgService::class.java).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None)
        super.onBackPressed()
    }

    private val sensorEventListener: SensorEventListener by lazy {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val dis = event.values
                if (0.0f == dis[0]) {
                    //??????????????????????????????
                    MessageAudioControl.getInstance(this@ChatActivity).setEarPhoneModeEnable(true)
                } else {
                    //???????????????
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
     * ************************* ???????????? **********************************
     */
    // ????????????????????????
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
                // ????????????
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
        // ??????????????????
        messageListPanel.sendReceipt()
    }


    private fun appendPushConfig(message: IMMessage) {
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
    }


    private fun sendMsgS(message: IMMessage) {
        appendPushConfig(message)
        NIMClient.getService(MsgService::class.java).sendMessage(message, false)
            .setCallback(object : RequestCallback<Void?> {
                override fun onSuccess(param: Void?) {
                    if (sessionId == Constants.ASSISTANT_ACCID) {
                        if (message.msgType == MsgTypeEnum.text)
                            mPresenter?.aideSendMsg(message)
                        messageListPanel.onMsgSend(message)
                    } else {
                        setMessageStatus(message, MsgStatusEnum.success)
                    }
                    //??????????????????????????????????????????????????????????????????????????????
                    if (message.msgType == MsgTypeEnum.image && message.remoteExtension != null && message.remoteExtension[LocationActivity.EXTENSION_LATITUDE] != null) {
                        FileUtils.delete((message.attachment as FileAttachment).path)
                    }
                }

                override fun onFailed(code: Int) {
                    LogUtils.d("${code}")
                }

                override fun onException(exception: Throwable?) {
                    LogUtils.d("${exception}")
                }

            })

    }


    private fun isChatWithRobot() = sessionId == Constants.ASSISTANT_ACCID


    private fun setMessageStatus(message: IMMessage, msgStatus: MsgStatusEnum) {
        message.status = msgStatus
        NIMClient.getService(MsgService::class.java).updateIMMessageStatus(message)
        messageListPanel.refreshMessageItem(message.uuid)
    }


    /**************************???????????????********************************/

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
     * ???????????????????????????
     */
    private val commandObserver =
        Observer<CustomNotification> { message ->
            if (sessionId != message.sessionId || message.sessionType != SessionTypeEnum.P2P) {
                return@Observer
            }
            showCommandMessage(message)
        }


    /**
     * ???????????????????????????
     */
    private val userInfoObserver =
        UserInfoObserver { accounts ->
            if (!accounts.contains(sessionId)) {
                return@UserInfoObserver
            }
            requestBodyInfo()
        }

    /**
     * ?????????????????????eg:?????????
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
     * ???????????????????????????
     */
    private val onlineStateChangeObserver =
        OnlineStateChangeObserver { accounts ->
            if (!accounts.contains(sessionId)) {
                return@OnlineStateChangeObserver
            }
            // ?????????????????????
            displayOnlineState()
        }


    /**
     * ?????????????????????
     */
    private val messageReceiptObserver: Observer<List<MessageReceipt>> by lazy {
        Observer<List<MessageReceipt>> { messageListPanel.receiveReceipt() }
    }


    /**
     * ?????????????????????
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
                if (::nimBean.isInitialized)
                    MessageInfoActivity.start(
                        this,
                        sessionId,
                        nimBean.isfriend,
                        nimBean.stared
                    )
            }

            binding.unlockChatLl -> {  // ????????????????????????
                CommonFunction.checkChat(this, sessionId)
            }
            binding.inputCl.unlockContactBtn -> {  // ??????????????????
                CommonFunction.checkUnlockContact(this, sessionId, nimBean.target_gender)
            }
            binding.inputCl.closeContactBtn -> {
                binding.inputCl.unlockContactLl.isVisible = false
            }
            binding.gotoVerifyBtn -> {// ?????????
                if (!nimBean.my_isfaced) {
                    if (UserManager.isverify == 2)
                        ToastUtil.toast("???????????????????????????....")
                    else
                        CommonFunction.startToFace(
                            this,
                            FaceLivenessExpActivity.TYPE_ACCOUNT_NORMAL,
                            -1
                        )
                } else if (nimBean.mv_state == 0) {
                    CommonFunction.startToVideoIntroduce(this, -1)
                }
            }

        }
    }


    private fun canSendMsg(): Boolean {
        // ????????????????????????0 ????????????
        return if (UserManager.gender == 2) {
            true
            /*if (nimBean.islimit) {
                // ??????????????????????????????????????????,???????????????????????????????????????????????????
                if (!nimBean.my_isfaced) {
                    VerifyAddChatDialog(this, nimBean.approve_chat_times).show()
                } else if (nimBean.mv_state == 0) {
                    VideoAddChatTimeDialog(this).show()
                }
                false
            } else {
                true
            }*/
        } else {
            if (nimBean.private_chat_state && !nimBean.isplatinum) {
                ChatUpOpenPtVipDialog(
                    this, sessionId, ChatUpOpenPtVipDialog.TYPE_CHAT_DETAIL,
                    ChatUpBean(avatar = nimBean.avatar, contact_way = nimBean.unlock_contact_way)
                ).show()
                false
            } else {
                true
            }
        }
    }


    /**
     * ********************** implements ModuleProxy *********************
     */
    override fun sendMessage(message: IMMessage): Boolean {
        if (isChatWithRobot()) {
            sendMsgS(message)
        } else if (this::nimBean.isInitialized && !nimBean.is_send_msg && nimBean.my_gender == 1 && nimBean.target_gender == 2) {
            showConfirmSendDialog(message)
        } else if (canSendMsg()) {
            sendMsgRequest(message)
        }

        return true
    }

    override fun addReport(message: IMMessage) {

        mPresenter?.addReport(message)
    }

    private fun showConfirmSendDialog(message: IMMessage) {

        MessageDialog.show(
            this as AppCompatActivity,
            getString(R.string.send_messgae),
            getString(R.string.cost_one_candy_for_quanlity),
            getString(R.string.confirm_send),
            getString(R.string.cancel)
        )
            .setOnOkButtonClickListener { baseDialog, v ->
                if (canSendMsg()) {
                    sendMsgRequest(message)
                }
                false
            }

    }

    //????????????????????????????????????????????????????????????
//??????????????????
    private fun sendMsgRequest(message: IMMessage) {
        setMessageStatus(message, MsgStatusEnum.sending)
        messageListPanel.onMsgSend(message)
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
                if (message.remoteExtension != null && message.remoteExtension[LocationActivity.EXTENSION_LATITUDE] != null) {
                    mPresenter?.sendMsgRequest(message, sessionId, islocation = true)
                } else
                    mPresenter?.uploadImgToQN(
                        message,
                        sessionId,
                        (message.attachment as ImageAttachment).path
                    )
            }
            MsgTypeEnum.text -> {
                mPresenter?.sendMsgRequest(message, sessionId)
            }
            else -> {
                sendMsgS(message)
            }
        }
    }


    override fun isLongClickEnabled(): Boolean {
        return !inputPanel.isRecording()
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


    // ??????????????????
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


    private lateinit var nimBean: ChatInfoBean
    override fun getTargetInfoResult(voiceBean: ChatInfoBean?, code: Int, msg: String) {
        when (code) {
            200 -> {
                this.nimBean = voiceBean!!
                setTargetInfoData()


            }
            409 -> {
                MessageDialog.show(
                    ActivityUtils.getTopActivity() as AppCompatActivity,
                    getString(R.string.notice),
                    msg, getString(R.string.iknow)
                )
                    .setOnOkButtonClickListener { _, v ->
                        CommonFunction.dissolveRelationshipLocal(sessionId)
                        false
                    }
            }
            else -> {
                ToastUtil.toast(msg)
            }
        }

    }


    /**
     * ??????????????????????????????
     * verifyLl
     */
    private var isDirectIn = false // ????????????????????????
    private var showSendGift = false // ?????????????????????

    private fun setTargetInfoData() {
        UserManager.showCandyMessage = nimBean.chat_expend_amount > 0
        UserManager.showCandyTime = nimBean.chat_expend_time
        // ???????????????????????????
        /*if (nimBean.my_gender == 2 && (!nimBean.my_isfaced || nimBean.mv_state == 0)) {
            binding.apply {
                verifyLl.isVisible = true
                if (!nimBean.my_isfaced) {
                    gotoVerifyBtn.text = "????????????"
                    if (nimBean.residue_msg_cnt == nimBean.normal_chat_times) {
                        leftChatTimes.text = getString(
                            R.string.unverify_only_some_can_chat,
                            nimBean.normal_chat_times
                        )
                    } else {
                        leftChatTimes.text = getString(
                            R.string.unverify_residue_count,
                            nimBean.residue_msg_cnt
                        )
                    }
                } else {
                    gotoVerifyBtn.text = "????????????"
                    leftChatTimes.text =
                        getString(
                            R.string.unverify_today_residue_count,
                            nimBean.residue_msg_cnt
                        )
                }
            }
        } else {
            binding.verifyLl.isInvisible = true
        }*/

        showLockChat()

        //????????????????????????
        // 0???????????????????????? 1 ?????? 2 ?????? 3 qq 99??????
        binding.inputCl.apply {
            if (nimBean.unlock_contact_way != 0 && !nimBean.is_unlock_contact) {
                unlockContactLl.isVisible = true
                when (nimBean.unlock_contact_way) {
                    1 -> {
                        contactIv.setImageResource(R.drawable.icon_unlock_phone)
                    }
                    2 -> {
                        contactIv.setImageResource(R.drawable.icon_unlock_wechat)
                    }
                    3 -> {
                        contactIv.setImageResource(R.drawable.icon_unlock_qq)
                    }
                }
            } else {
                unlockContactLl.isVisible = false
            }
        }

        messageListPanel.refreshMessageList()
        if (nimBean.unlock_popup_str.isNotEmpty()) {
            ContactCandyReceiveDialog(sessionId, nimBean.unlock_popup_str).show()
            nimBean.unlock_popup_str = ""
        }
        if (!isChatWithRobot() && UserManager.gender == 2
            && !nimBean.private_chat_state
            && !UserManager.isSendChargePtVip && messageListPanel.messageSize() > 0
            && messageListPanel.items.last().direct == MsgDirectionEnum.In
        ) {
            val tips = arrayListOf<SendTipBean>()
            tips.add(
                SendTipBean(
                    getString(R.string.hide_message_if_gold_vip), true,
                    SendCustomTipAttachment.CUSTOME_TIP_PRIVICY_SETTINGS
                )
            )
            CommonFunction.sendTips(sessionId, tips)
            UserManager.isSendChargePtVip = true
        } else {
            UserManager.isSendChargePtVip = true
        }

        if ((nimBean.my_gender == 1 && nimBean.target_gender == 2 && nimBean.target_ishoney) || nimBean.my_gender == 2) {
            EventBus.getDefault().post(EnableGiftEvent(6))
        } else {
            EventBus.getDefault().post(EnableGiftEvent(5))
        }

    }

    private fun showLockChat() {
        //??????????????????
        binding.unlockChatLl.isVisible = nimBean.lockbtn

    }


    /**
     * ????????????????????????
     */
    override fun uploadImgToQNResult(
        isOk: Boolean,
        key: String,
        content: IMMessage,
        targetAccid: String
    ) {
        if (isOk) {
            mPresenter?.sendMsgRequest(content, targetAccid, key)
        }

    }


    /**
     * ??????????????????????????????
     */
    override fun sendMsgResult(
        code: Int,
        nimBeanBaseResp: SendMsgBean?,
        msg: String,
        content: IMMessage
    ) {
        if (code == 200 || code == 211) {
            // ??????????????????????????????????????????????????????????????????????????????
//            if (nimBeanBaseResp?.rid_data != null && nimBeanBaseResp.rid_data!!.icon.isNotEmpty()) {
////                ReceiveAccostGiftDialog(this, nimBeanBaseResp.rid_data).show()
//            }
            sendMsgS(content)
            if (nimBeanBaseResp!!.ret_tips_arr.isNotEmpty())
                CommonFunction.sendTips(sessionId, nimBeanBaseResp.ret_tips_arr)
            nimBean.is_send_msg = true
            if (UserManager.gender == 1 && !UserManager.isSendChargePtVip
                && sessionId != Constants.ASSISTANT_ACCID && !nimBean.isplatinum
            ) {
                val tips = ArrayList<SendTipBean>()
                tips.add(
                    SendTipBean(
                        getString(R.string.charge_to_free), true,
                        SendCustomTipAttachment.CUSTOME_TIP_CHARGE_PT_VIP
                    )
                )
                CommonFunction.sendTips(sessionId, tips)
                UserManager.isSendChargePtVip = true
            }
        } else if (code == 409) { // ???????????????
            setMessageStatus(content, MsgStatusEnum.fail)
            MessageDialog.show(this as AppCompatActivity, "??????", msg, "?????????")
                .setOnOkButtonClickListener { baseDialog, v ->
                    NIMClient.getService(MsgService::class.java)
                        .deleteRecentContact2(sessionId, SessionTypeEnum.P2P)
                    finish()
                    false
                }

        } else if (code == 411) { // ??????????????????
            setMessageStatus(content, MsgStatusEnum.fail)
            MessageDialog.show(this as AppCompatActivity, "??????", msg, "????????????", "??????")
                .setOnOkButtonClickListener { baseDialog, v ->
                    CommonFunction.gotoCandyRecharge(this)
                    finish()
                    false
                }

        } else if (code == 201) { // ??????????????????
            setMessageStatus(content, MsgStatusEnum.fail)
            CommonFunction.startToFootPrice(this)
        } else {
            setMessageStatus(content, MsgStatusEnum.fail)
            SnackBarFragment.showAlert(
                CustomerMsgBean(
                    SnackBarFragment.SEND_FAILED,
                    getString(R.string.send_failed),
                    msg,
                    R.drawable.icon_wrong
                )
            )
        }
    }

    override fun addReport(msg: String) {


    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun updateApproveEvent(event: UpdateApproveEvent?) {
        if (sessionId != Constants.ASSISTANT_ACCID) mPresenter?.getTargetInfo(sessionId)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateVerifyEvent(event: UpdateVerifyEvent) {
        if (!isChatWithRobot())
            mPresenter?.getTargetInfo(sessionId)

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateSendGiftEvent(event: UpdateSendGiftEvent) {
        messageListPanel.onMsgSend(event.message)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun hideChatLlEvent(event: HideChatLlEvent) {
        nimBean.lockbtn = false
        showLockChat()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun hideContactLlEvent(event: HideContactLlEvent?) {
        binding.inputCl.unlockContactLl.visibility = View.GONE
        nimBean.is_unlock_contact = true
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateStarEvent(event: UpdateStarEvent) {
        nimBean.stared = event.isStar
    }

}