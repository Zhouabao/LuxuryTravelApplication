package com.sdy.luxurytravelapplication.nim.business.session.panel

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.NetworkUtils
import com.kongzue.dialog.v3.BottomMenu
import com.kongzue.dialog.v3.MessageDialog
import com.netease.nimlib.sdk.*
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.MsgServiceObserve
import com.netease.nimlib.sdk.msg.attachment.FileAttachment
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.AttachmentProgress
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum
import com.netease.nimlib.sdk.msg.model.RevokeMsgNotification
import com.netease.nimlib.sdk.robot.model.RobotAttachment
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.ActivityChatBinding
import com.sdy.luxurytravelapplication.event.RefreshCandyMessageEvent
import com.sdy.luxurytravelapplication.mvp.model.bean.ChatInfoBean
import com.sdy.luxurytravelapplication.nim.api.NimUIKit
import com.sdy.luxurytravelapplication.nim.api.model.user.UserInfoObserver
import com.sdy.luxurytravelapplication.nim.attachment.ContactAttachment
import com.sdy.luxurytravelapplication.nim.attachment.SendCustomTipAttachment
import com.sdy.luxurytravelapplication.nim.attachment.SendGiftAttachment
import com.sdy.luxurytravelapplication.nim.business.audio.MessageAudioControl
import com.sdy.luxurytravelapplication.nim.business.helper.MessageHelper
import com.sdy.luxurytravelapplication.nim.business.helper.MessageListPanelHelper
import com.sdy.luxurytravelapplication.nim.business.module.Container
import com.sdy.luxurytravelapplication.nim.business.module.list.MsgAdapter
import com.sdy.luxurytravelapplication.nim.business.preference.UserPreferences
import com.sdy.luxurytravelapplication.nim.common.ui.recyclerview.adapter.BaseFetchLoadAdapter
import com.sdy.luxurytravelapplication.nim.common.ui.recyclerview.loadmore.MsgListFetchLoadMoreView
import com.sdy.luxurytravelapplication.nim.common.util.sys.ClipboardUtil
import com.sdy.luxurytravelapplication.nim.impl.NimUIKitImpl
import com.sdy.luxurytravelapplication.utils.ToastUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList

/**
 *    author : ZFM
 *    date   : 2020/10/199:57
 *    desc   :
 *    version: 1.0
 */
class MessageListPanelEx @JvmOverloads constructor(
    private var container: Container,
    private val binding: RecyclerView,
    private val anchor: IMMessage? = null,
    val recordOnly: Boolean = false,//????????????????????????????????????????????????
    private val remote: Boolean = false,//??????????????????????????????
    val persistClear: Boolean = true//??????????????????????????????????????????????????????????????????
) {
    companion object {
        private const val TAG = "MessageListPanelEx"

        const val REQUEST_CODE_FORWARD_PERSON = 0x01
        const val REQUEST_CODE_FORWARD_TEAM = 0x02

        /** MsgSelectActivity????????????  */
        const val REQUEST_CODE_FORWARD_MULTI_RETWEET = 0x03
    }

    /** message list view */
    public var items: ArrayList<IMMessage> = arrayListOf()
    private val adapter by lazy { MsgAdapter(messageListView, items, container) }

    private lateinit var uiHandler: Handler

    /** ???????????????  */
    private var forwardMessage: IMMessage? = null
    private val incomingLocalMessageObserver: MessageListPanelHelper.LocalMessageObserver
    private val userInfoObserver: UserInfoObserver
    private val messageStatusObserver: Observer<IMMessage>
    private val attachmentProgressObserver: Observer<AttachmentProgress>
    private val revokeMessageObserver: Observer<RevokeMsgNotification>
    private val deleteMsgSelfObserver: Observer<IMMessage>

    /** ???????????????????????? ???????????????????????????????????????????????????????????????????????????MessageLoader ??????????????????????????? loadFromLocal
     * ????????????????????????list ????????????????????????????????????RequestFetchMoreListener ???????????? loadFromLocal
     * ?????????????????????
     */
    private var mIsInitFetchingLocal = false

    init {
        /**
         * ???????????????????????????
         */
        incomingLocalMessageObserver = object :
            MessageListPanelHelper.LocalMessageObserver {
            override fun onAddMessage(message: IMMessage) {
                if (container.account != message.sessionId) {
                    return
                }
                onPretendMsgSend(message)
                onMsgSend(message)
            }

            override fun onClearMessages(account: String) {
                items.clear()
                //            refreshMessageList();
                adapter.notifyDataSetChanged()
                adapter.fetchMoreEnd(null, true)
            }
        }


        /**
         * ?????????????????????
         */
        userInfoObserver = UserInfoObserver {
            if (container.sessionType == SessionTypeEnum.P2P) {
                if (it.contains(container.account) || it.contains(NimUIKit.getAccount())) {
                    adapter.notifyDataSetChanged()
                }
            } else { // ??????????????????????????????
                adapter.notifyDataSetChanged()
            }
        }

        /**
         * ???????????????????????????
         */
        messageStatusObserver = Observer<IMMessage> {
            if (isMyMessage(it)) {
                onMessageStatusChange(it)
            }
        }

        /**
         * ??????????????????/?????????????????????
         */
        attachmentProgressObserver = Observer<AttachmentProgress> {
            onAttachmentProgressChange(it)
        }

        /**
         * ?????????????????????
         */
        revokeMessageObserver = Observer<RevokeMsgNotification> { notification ->
            if (notification == null || notification.message == null) {
                return@Observer
            }
            val message = notification.message
            // ????????????????????? 1??????????????????2??????????????? ????????? 0
            Log.i(
                TAG,
                "notification type = " + notification.notificationType
            )
            if (container.account != message.sessionId) {
                return@Observer
            }
            deleteItem(message, false)
        }

        /**
         * ?????????????????????
         */
        deleteMsgSelfObserver = Observer<IMMessage> {
            deleteItem(it, true)
        }

        init(anchor)

    }

    fun onResume() {
        setEarPhoneMode(UserPreferences.isEarPhoneModeEnable(), false)
    }


    private fun setEarPhoneMode(earPhoneMode: Boolean, update: Boolean) {
        if (update) {
            UserPreferences.setEarPhoneModeEnable(earPhoneMode)
        }
        MessageAudioControl.getInstance(container.activity).setEarPhoneModeEnable(earPhoneMode)
    }

    fun onPause() {
        MessageAudioControl.getInstance(container.activity).stopAudio()
    }

    fun onDestory() {
        registerObservers(false)
    }

    fun onBackPressed() {
        uiHandler.removeCallbacksAndMessages(null)
        MessageAudioControl.getInstance(container.activity).stopAudio()

    }

    fun reload(container: Container, anchor: IMMessage?) {
        this.container = container
        if (adapter != null) {
            adapter.clearData()
        }
        initFetchLoadListener(anchor)
    }


    private fun init(anchor: IMMessage?) {
        initListView(anchor)

        uiHandler = Handler()
        registerObservers(true)
    }


    private fun registerObservers(register: Boolean) {
        val service = NIMClient.getService(MsgServiceObserve::class.java)
        service.observeMsgStatus(messageStatusObserver, register)
        service.observeAttachmentProgress(attachmentProgressObserver, register)
        service.observeRevokeMessage(revokeMessageObserver, register)
        service.observeDeleteMsgSelf(deleteMsgSelfObserver, register)
        NimUIKit.getUserInfoObservable().registerObserver(userInfoObserver, register)
        MessageListPanelHelper.getInstance()
            .registerObserver(incomingLocalMessageObserver, register)
        if (register) {
            EventBus.getDefault().register(this)
        } else {
            EventBus.getDefault().unregister(this)
        }
    }

    lateinit var messageListView: RecyclerView
    private fun initListView(anchor: IMMessage?) {
        // RecyclerView
        messageListView = binding
        messageListView.layoutManager =
            LinearLayoutManager(container.activity, RecyclerView.VERTICAL, false)
        messageListView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    container.proxy.shouldCollapseInputPanel()
                }
            }
        })
        messageListView.overScrollMode = View.OVER_SCROLL_NEVER
        //items
        adapter.setFetchMoreView(MsgListFetchLoadMoreView())
        adapter.setLoadMoreView(MsgListFetchLoadMoreView())
        adapter.eventListener = MsgItemEventListener()
        initFetchLoadListener(anchor)
        messageListView.adapter = adapter
        adapter.eventListener
    }

    private fun initFetchLoadListener(anchor: IMMessage?) {
        val loader = MessageLoader(anchor, remote)
        if (recordOnly && !remote) {
            //??????load
            adapter.setOnFetchMoreListener(loader)
            adapter.setOnLoadMoreListener(loader)
        } else {
            //???????????????Old??????
            adapter.setOnFetchMoreListener(loader)
        }
    }

    //??????????????????
    fun refreshMessageList() {
        uiHandler.post { adapter.notifyDataSetChanged() }
    }

    fun scrollToBottom() {
        uiHandler.postDelayed({
            doScrollToBottom()
        }, 200)
    }

    fun refreshMessageItem(uuid: String) {
        if (uuid.isEmpty()) {
            return
        }
        var msg: IMMessage
        for (i in items.indices) {
            msg = items[i]
            if (msg == null) {
                continue
            }
            if (uuid == msg.uuid) {
                refreshViewHolderByIndex(i)
                break
            }
        }

    }

    fun onIncomingMessage(messages: MutableList<IMMessage>) {
        // ????????????????????????tip?????? ???????????????????????????

        // ????????????????????????tip?????? ???????????????????????????
        val iterator: MutableIterator<*> = messages.iterator()
        while (iterator.hasNext()) {
            val message = iterator.next() as IMMessage
            val isSend = message.direct == MsgDirectionEnum.Out
            if ((message.attachment is SendCustomTipAttachment && (message.attachment as SendCustomTipAttachment).ifSendUserShow != isSend)
                || (message.attachment is ContactAttachment && message.direct == MsgDirectionEnum.Out)
            ) {
                NIMClient.getService(MsgService::class.java).deleteChattingHistory(message)
                iterator.remove()
            }
        }


        val needScrollToBottom = isLastMessageVisible1()
        var needRefresh = false
        val addedListItems = arrayListOf<IMMessage>()
        for (message in messages) {
            if (isMyMessage(message)) {
                items.add(message)
                addedListItems.add(message)
                needRefresh = true
            }
        }


        if (needRefresh) {
            sortMessages(items)
            adapter.notifyDataSetChanged()
        }
        adapter.updateShowTimeItem(addedListItems, false, true)


        val lastMsg = messages.lastOrNull()
        if (lastMsg != null && isMyMessage(lastMsg)) {
            if (needScrollToBottom) {
                doScrollToBottom()
            }
        }
    }


    private fun isLastMessageVisible1(): Boolean {
        val manager = messageListView.layoutManager as LinearLayoutManager
        val lastVisiblePosition = manager.findLastVisibleItemPosition()
        return lastVisiblePosition >= adapter.bottomDataPosition
    }


    fun isSessionMode(): Boolean {
        return !recordOnly && !remote
    }

    fun messageSize(): Int {
        return adapter.data.size
    }

    /**
     * ???????????? ??????????????????
     */
    fun onPretendMsgSend(message: IMMessage) {
        if (container.account != message.sessionId) {
            return
        }
        val addedListItems = arrayListOf<IMMessage>(message)
        adapter.updateShowTimeItem(addedListItems, false, true)
        adapter.appendData(message)
        doScrollToBottom()
    }

    fun onMsgSend(message: IMMessage) {
        if (container.account != message.sessionId) {
            return
        }

        if ((message.attachment is SendCustomTipAttachment && !(message.attachment as SendCustomTipAttachment).ifSendUserShow)
            || (message.attachment is ContactAttachment && message.direct == MsgDirectionEnum.Out)
        ) {
            return
        }

        val addedListItems: MutableList<IMMessage> = mutableListOf()
        addedListItems.add(message)
        adapter.updateShowTimeItem(addedListItems, false, true)
        adapter.appendData(message)
        doScrollToBottom()
    }


    private fun doScrollToBottom() {
        messageListView.scrollToPosition(adapter.bottomDataPosition)
    }


    //????????????
    private fun deleteItem(message: IMMessage, isRelocateTime: Boolean) {
        deleteItem(message, isRelocateTime, true)
    }

    private fun deleteItem(messageItem: IMMessage, isRelocateTime: Boolean, recordOpe: Boolean) {
        NIMClient.getService(MsgService::class.java).deleteChattingHistory(messageItem, !recordOpe)
        val messages: ArrayList<IMMessage> = arrayListOf()
        for (message in items) {
            if (message.uuid == messageItem.uuid) {
                continue
            }
            messages.add(message)
        }
        updateReceipt(messages)
        adapter.deleteItem(messageItem, isRelocateTime)
    }


    //????????????????????????
    private fun onAttachmentProgressChange(it: AttachmentProgress) {
        val index = getItemIndex(it.uuid)
        if (index >= 0 && index < items.size) {
            val item = items[index]
//            if (item.remoteExtension == null || item.remoteExtension[LocationActivity.EXTENSION_LATITUDE] == null) {
//                val value = it.transferred * 1f / it.total
//                adapter.putProgress(item, value)
//                refreshViewHolderByIndex(index)
//            }
        }
    }


    //??????????????????
    private fun onMessageStatusChange(message: IMMessage) {
        val index = getItemIndex(message.uuid)
        if (index >= 0 && index < items.size) {
            items[index] = message

            // resend???????????????????????????????????????????????????????????????????????????????????????
            val msgList = arrayListOf(message)
            adapter.updateShowTimeItem(msgList, false, true)
            refreshViewHolderByIndex(index)
        }

    }


    private fun isMyMessage(message: IMMessage): Boolean {
        return message.sessionType == container.sessionType && !message.sessionId.isNullOrEmpty() && message.sessionId == container.account
    }

    private fun isOutMessage(message: IMMessage): Boolean {
        return message.direct == MsgDirectionEnum.Out
    }

    private fun refreshViewHolderByIndex(index: Int) {
        uiHandler?.post {
            if (index < 0) {
                return@post
            }
            adapter.notifyDataItemChanged(index)
        }

    }

    private fun getItemIndex(uuid: String): Int {
        for (i in items.indices) {
            val message = items[i]
            if (TextUtils.equals(message.uuid, uuid)) {
                return i
            }
        }
        return -1
    }


    /**
     * **************************** ?????? ***********************************
     */
    private fun sortMessages(list: ArrayList<IMMessage>) {
        if (list.size == 0) {
            return
        }
        Collections.sort(list, comp)

    }

    private val comp: Comparator<IMMessage> = Comparator<IMMessage> { o1, o2 ->
        val time = o1.time - o2.time
        if (time == 0L) 0 else if (time < 0) -1 else 1
    }

    /**
     * ***************************************** ???????????? *********************************************
     */
    private var firstLoad = true

    private inner class MessageLoader(
        private val anchor: IMMessage?,
        private val remote: Boolean
    ) :
        BaseFetchLoadAdapter.RequestLoadMoreListener,
        BaseFetchLoadAdapter.RequestFetchMoreListener {
        private val loadMsgCount = NimUIKitImpl.getOptions().messageCountLoadOnce
        private var direction: QueryDirectionEnum? = null
        private val callback: RequestCallback<MutableList<IMMessage>> =
            object : RequestCallbackWrapper<MutableList<IMMessage>>() {
                override fun onResult(
                    code: Int,
                    result: MutableList<IMMessage>?,
                    exception: Throwable?
                ) {
                    mIsInitFetchingLocal = false
                    if (code != ResponseCode.RES_SUCCESS.toInt() || exception != null) {
                        if (direction == QueryDirectionEnum.QUERY_OLD) {
                            adapter.fetchMoreFailed()
                        } else if (direction == QueryDirectionEnum.QUERY_NEW) {
                            adapter.loadMoreFail()
                        }
                        return
                    }
                    if (result != null) {
                        val iterator = result.iterator()
                        while (iterator.hasNext()) {
                            val message = iterator.next()
                            val isSend = message.getDirect() == MsgDirectionEnum.Out
                            // ??????????????????????????? ????????????????????????????????? ????????????
                            if ((message.attachment is SendCustomTipAttachment && (message.attachment as SendCustomTipAttachment).ifSendUserShow != isSend)
                                || message.attachment is ContactAttachment && isSend
                            ) {
                                NIMClient.getService(MsgService::class.java)
                                    .deleteChattingHistory(message)
                                iterator.remove()
                            }
                        }

                        onMessageLoaded(result)
                    }
                }

            }


        private fun loadAnchorContext() {
            // query new, auto load old
            direction = QueryDirectionEnum.QUERY_NEW
            NIMClient.getService(MsgService::class.java)
                .queryMessageListEx(anchor(), direction, loadMsgCount, true)
                .setCallback(object : RequestCallbackWrapper<MutableList<IMMessage>?>() {
                    override fun onResult(
                        code: Int,
                        messages: MutableList<IMMessage>?,
                        exception: Throwable?
                    ) {
                        if (code != ResponseCode.RES_SUCCESS.toInt() || exception != null) {
                            return
                        }
                        onAnchorContextMessageLoaded(messages ?: mutableListOf())
                    }

                    override fun onException(exception: Throwable?) {
                        super.onException(exception)
                    }

                    override fun onFailed(code: Int) {
                        super.onFailed(code)
                    }

                    override fun onSuccess(result: MutableList<IMMessage>?) {
                        super.onSuccess(result)
                    }
                })
        }

        private fun loadFromLocal(direction: QueryDirectionEnum) {
            if (mIsInitFetchingLocal) {
                return
            }
            this.direction = direction
            NIMClient.getService(MsgService::class.java)
                .queryMessageListEx(anchor(), direction, loadMsgCount, true)
                .setCallback(callback)
        }

        private fun loadFromRemote() {
            direction = QueryDirectionEnum.QUERY_OLD
            NIMClient.getService(MsgService::class.java)
                .pullMessageHistory(
                    anchor(),
                    loadMsgCount,
                    true, persistClear
                )
                .setCallback(callback)
        }

        private fun anchor(): IMMessage {
            return if (items.size == 0) {
                anchor ?: MessageBuilder.createEmptyMessage(
                    container.account,
                    container.sessionType,
                    0
                )
            } else {
                val index = if (direction == QueryDirectionEnum.QUERY_NEW) items.size - 1 else 0
                items[index]
            }
        }

        private fun onMessageLoaded(messages: MutableList<IMMessage>?) {
            if (messages == null) {
                return
            }
            val noMoreMessage = messages.size < loadMsgCount
            if (remote) {
                messages.reverse()
            }

            // ?????????????????????????????????????????????????????????????????????
            if (firstLoad && items.size > 0) {
                for (message in messages) {
                    var removeIndex = 0
                    for (item in items) {
                        if (item.isTheSame(message)) {
                            adapter.remove(removeIndex)
                            break
                        }
                        removeIndex++
                    }
                }
            }


            // ??????anchor
            if (firstLoad && anchor != null) {
                messages.add(anchor)
            }

            // ????????????????????????????????????
            val total: MutableList<IMMessage> = ArrayList<IMMessage>(items)
            val isBottomLoad = direction == QueryDirectionEnum.QUERY_NEW
            if (isBottomLoad) {
                total.addAll(messages)
            } else {
                total.addAll(0, messages)
            }
            // ??????????????????????????????
            adapter.updateShowTimeItem(total, true, firstLoad)
            // ????????????????????????
            updateReceipt(total)


            // ??????????????????,????????????
            if (isBottomLoad) {
                // ????????????
                if (noMoreMessage) {
                    adapter.loadMoreEnd(messages, true)
                } else {
                    adapter.loadMoreComplete(messages)
                }
            } else {
                // ????????????
                if (noMoreMessage) {
                    adapter.fetchMoreEnd(messages, true)
                } else {
                    adapter.fetchMoreComplete(messages)
                }
            }

            // ???????????????????????????updateShowTimeItem???????????????lastShowTimeItem
            if (firstLoad) {
                doScrollToBottom()
                sendReceipt() // ??????????????????
            }

            // ????????????????????????????????????????????????????????????????????????????????????
            if (container.sessionType == SessionTypeEnum.Team) {
                NIMSDK.getTeamService().refreshTeamMessageReceipt(messages)
            }
            firstLoad = false
        }

        private fun onAnchorContextMessageLoaded(messages: MutableList<IMMessage>) {
            if (messages == null) {
                return
            }
            if (remote) {
                messages.reverse()
            }
            val loadCount = messages.size
            if (firstLoad && anchor != null) {
                messages.add(0, anchor)
            }

            // ????????????????????????????????????
            adapter.updateShowTimeItem(messages, true, firstLoad) // ??????????????????????????????
            updateReceipt(messages) // ????????????????????????

            // new data
            if (loadCount < loadMsgCount) {
                adapter.loadMoreEnd(messages, true)
            } else {
                adapter.appendData(messages)
            }
            firstLoad = false
        }

        override fun onFetchMoreRequested() {
            // ????????????????????????
            if (remote) {
                loadFromRemote()
            } else {
                loadFromLocal(QueryDirectionEnum.QUERY_OLD)
            }
        }

        override fun onLoadMoreRequested() {
            // ?????????????????????
            if (!remote) {
                loadFromLocal(QueryDirectionEnum.QUERY_NEW)
            }
        }

        init {
            if (remote) {
                loadFromRemote()
            } else {
                if (anchor == null) {
                    loadFromLocal(QueryDirectionEnum.QUERY_OLD)
                    mIsInitFetchingLocal = true
                } else {
                    loadAnchorContext() // ????????????anchor????????????
                }
            }
        }
    }


    /**
     * ???????????????????????????VH?????????label???
     */
    fun receiveReceipt() {
        updateReceipt(items)
        refreshMessageList()
    }

    private fun updateReceipt(messages: MutableList<IMMessage>) {
        if (messages.size > 0)
            for (i in messages.size - 1 downTo 0) {
                if (receiveReceiptCheck(messages[i])) {
                    adapter.uuid = messages[i].uuid
                    break
                }
            }

    }

    private fun receiveReceiptCheck(msg: IMMessage?): Boolean {
        return (msg != null && msg.sessionType == SessionTypeEnum.P2P
                && msg.direct == MsgDirectionEnum.Out
                && msg.msgType != MsgTypeEnum.tip
                && msg.msgType != MsgTypeEnum.notification
                && msg.attachment !is SendCustomTipAttachment
                && !(msg.attachment is ContactAttachment
                && msg.direct == MsgDirectionEnum.Out)
                && msg.isRemoteRead)
    }

    /**
     * ????????????????????????????????????
     */
    fun sendReceipt() {
        // ??????????????????????????????????????????

        // ??????????????????????????????????????????
        if (!NimUIKitImpl.getOptions().shouldHandleReceipt) {
            return
        }

        if (container.account == null || container.sessionType != SessionTypeEnum.P2P) {
            return
        }

        val message: IMMessage? = getLastReceivedMessage()
        if (!sendReceiptCheck(message)) {
            return
        }

        NIMClient.getService(MsgService::class.java)
            .sendMessageReceipt(container.account, message)

    }

    private fun getLastReceivedMessage(): IMMessage? {
        var lastMessage: IMMessage? = null
        for (i in items.indices.reversed()) {
            if (sendReceiptCheck(items[i])) {
                lastMessage = items[i]
                break
            }
        }
        return lastMessage
    }

    private fun sendReceiptCheck(msg: IMMessage?): Boolean {
        return msg != null && !isOutMessage(msg) && msg.msgType != MsgTypeEnum.tip && msg.msgType != MsgTypeEnum.notification
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }
//        if (requestCode == ReportChatContentDialog.REQUEST_REPORT_PIC) {
//            EventBus.getDefault().post(ReportDataEvent(requestCode, resultCode, data))
//        }
    }

    /**
     * adapter??????
     */
    private inner class MsgItemEventListener : MsgAdapter.ViewHolderEventListener {
        override fun onViewHolderLongClick(
            clickView: View?,
            viewHolderView: View?,
            item: IMMessage
        ): Boolean {
            if (container.proxy.isLongClickEnabled) {
                showLongClickAction(item)
            }

            return true
        }


        override fun onFailedBtnClick(message: IMMessage) {
            if (isOutMessage(message)) {
                //??????????????????????????????????????????????????????????????????????????????????????????????????????
                if (message.status == MsgStatusEnum.fail) {
                    resendMessage(message)//??????
                } else {
                    if (message.attachment is FileAttachment) {
                        val attachment = message.attachment as FileAttachment
                        if (attachment.path.isNullOrEmpty() && attachment.thumbPath.isNullOrEmpty()) {
                            showReDownloadConfirmDialog(message)
                        }
                    } else {
                        resendMessage(message)
                    }
                }
            } else {
                showReDownloadConfirmDialog(message)
            }
        }


        override fun onFooterClick(message: IMMessage) {
            container.proxy.onItemFooterClick(message)
        }

        override fun onTipBtnClick(message: IMMessage) {

        }

        override fun onCheckStateChanged(index: Int, newState: Boolean) {
        }


        //????????????
        private fun showReDownloadConfirmDialog(message: IMMessage) {
            MessageDialog.show(
                container.activity as AppCompatActivity,
                container.activity.getString(R.string.re_download),
                container.activity.getString(R.string.confirm_re_download),
                container.activity.getString(R.string.ok),
                container.activity.getString(R.string.cancel)
            ).setOnOkButtonClickListener { baseDialog, v ->
                //??????????????????????????????????????????????????????????????????????????????????????????????????????
                if (message.attachment != null && message.attachment is FileAttachment) {
                    NIMClient.getService(MsgService::class.java).downloadAttachment(message, true)
                }
                false
            }.setOnCancelButtonClickListener { baseDialog, v ->
                false
            }
        }


        //????????????????????????
        private fun resendMessage(message: IMMessage) {
            //?????????unsend
            val index = getItemIndex(message.uuid)
            if (index >= 0 && index < items.size) {
                val item = items[index]
//                item.status = MsgStatusEnum.sending
                deleteItem(item, true)
//                onPretendMsgSend(message)
            }
            container.proxy.sendMessage(message)
        }

        /**
         * ****************************** ???????????? ********************************
         */
        // ????????????Item?????????????????????
        private fun showLongClickAction(selectedItem: IMMessage) {
            onNormaLongClick(selectedItem)
        }


        /**
         * ??????????????????
         */
        private fun onNormaLongClick(item: IMMessage) {
            val items = arrayListOf<String>()
            //??????
            if (item.msgType == MsgTypeEnum.text || item.msgType == MsgTypeEnum.robot && item.attachment != null && !(item.attachment as RobotAttachment).isRobotSend) {
                items.add(container.activity.getString(R.string.copy_has_blank))
            }


            //??????
            if (!recordOnly) {
                items.add(container.activity.getString(R.string.delete_has_blank))
            }
            //?????????
            if (item.msgType == MsgTypeEnum.audio) {
                items.add(
                    if (UserPreferences.isEarPhoneModeEnable())
                        container.activity.getString(R.string.switch_speaker)
                    else
                        container.activity.getString(R.string.switch_receiver)
                )
            }

            //????????????
            if (item.status == MsgStatusEnum.fail) {
                items.add(container.activity.getString(R.string.repeat_send_has_blank))
            }

            //??????
            if (item.direct == MsgDirectionEnum.Out && (item.attachment == null || (item.attachment != null && item.attachment !is SendGiftAttachment && item.attachment !is ContactAttachment))) {
                items.add(container.activity.getString(R.string.withdrawn_msg))
            }

            if (item.msgType != MsgTypeEnum.custom && item.fromAccount != Constants.ASSISTANT_ACCID)
            //??????(?????????????????????????????????)
                items.add(container.activity.getString(R.string.report))


            BottomMenu.show(
                container.activity as AppCompatActivity,
                items.toList()
            ) { text, index ->
                when (text) {
                    container.activity.getString(R.string.copy_has_blank) -> {// ???????????????--??????
                        onCopyMessageItem(item)
                    }

                    container.activity.getString(R.string.delete_has_blank) -> {
                        deleteItem(item, true)
                    }
                    if (UserPreferences.isEarPhoneModeEnable())
                        container.activity.getString(R.string.switch_speaker)
                    else container.activity.getString(R.string.switch_speaker) -> {
                        ToastUtil.toast(text)
                        setEarPhoneMode(!UserPreferences.isEarPhoneModeEnable(), true)
                    }
                    container.activity.getString(R.string.repeat_send_has_blank) -> {
                        onResendMessageItem(item)
                    }
                    container.activity.getString(R.string.withdrawn_msg) -> {
                        MessageDialog.show(
                            container.activity as AppCompatActivity,
                            container.activity.getString(R.string.msg_revoke),
                            container.activity.getString(R.string.confirm_revoke_msg),
                            container.activity.getString(R.string.revoke),
                            container.activity.getString(R.string.cancel)
                        ).setOnOkButtonClickListener { baseDialog, v ->
                            if (!NetworkUtils.isConnected()) {
                                ToastUtil.toast(container.activity.getString(R.string.network_is_not_available))
                                false
                            }
                            var payload: Map<String, Any> = mapOf()
                            val customConfig = NimUIKitImpl.getCustomPushContentProvider()
                            if (customConfig != null) {
                                payload = customConfig.getPushPayload(item)
                            }
                            NIMClient.getService(MsgService::class.java)
                                .revokeMessage(
                                    item,
                                    container.activity.getString(R.string.revoke_a_msg),
                                    payload,
                                    true
                                )
                                .setCallback(object : RequestCallback<Void> {
                                    override fun onSuccess(param: Void?) {
                                        deleteItem(item, false)
                                        MessageHelper.getInstance()
                                            .onRevokeMessage(item, NimUIKit.getAccount())
                                    }

                                    override fun onFailed(code: Int) {
                                        if (code == ResponseCode.RES_OVERDUE.toInt()) {
                                            ToastUtil.toast(container.activity.getString(R.string.revoke_failed))
                                        } else {
                                            ToastUtil.toast("revoke msg failed, code:${code}")
                                        }
                                    }

                                    override fun onException(exception: Throwable?) {
                                    }

                                })
                            false
                        }
                            .setOnCancelButtonClickListener { baseDialog, v ->
                                false
                            }

                    }
                    container.activity.getString(R.string.report) -> {
                        container.proxy.addReport(item)
                    }
                }
            }

        }

        private fun onResendMessageItem(item: IMMessage) {
            if (getItemIndex(item.uuid) >= 0) {
                MessageDialog.show(
                    container.activity as AppCompatActivity,
                    container.activity.getString(R.string.resend),
                    container.activity.getString(R.string.confirm_resend),
                    container.activity.getString(R.string.ok),
                    container.activity.getString(R.string.cancel)
                ).setOnOkButtonClickListener { baseDialog, v ->
                    resendMessage(item)//??????????????????
                    false
                }
            }
        }

        private fun onCopyMessageItem(item: IMMessage) {
            ClipboardUtil.clipboardCopyText(container.activity, item.content)
        }

    }


    fun setHeadData(nimBean: ChatInfoBean) {
        // both_gift_list
        for (stateBean in nimBean.both_gift_list) {
            for (i in items.indices.reversed()) {
                val message = items[i]
                if (message.attachment is SendGiftAttachment
                    && (message.attachment as SendGiftAttachment).orderId == stateBean.id
                ) {
                    (message.attachment as SendGiftAttachment).giftStatus = stateBean.state
                    items[i] = message
                    refreshViewHolderByIndex(i)
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshCandyMessageEvent(event: RefreshCandyMessageEvent) {
        for (i in items.indices.reversed()) {
            val lastMessage = items[i]
            if (lastMessage.attachment is SendGiftAttachment
                && (lastMessage.attachment as SendGiftAttachment).orderId == event.orderId
            ) {
                (lastMessage.attachment as SendGiftAttachment).giftStatus = event.state
                items[i] = lastMessage
                refreshViewHolderByIndex(i)
                break
            }
        }
    }
}