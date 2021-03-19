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
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.AppUtils
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
import com.netease.nimlib.sdk.msg.model.*
import com.netease.nimlib.sdk.robot.model.RobotAttachment
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityChatBinding
import com.sdy.luxurytravelapplication.event.RefreshGiftStatusEvent
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.nim.api.NimUIKit
import com.sdy.luxurytravelapplication.nim.api.model.user.UserInfoObserver
import com.sdy.luxurytravelapplication.nim.attachment.SendGiftAttachment
import com.sdy.luxurytravelapplication.nim.attachment.SendWechatAttachment
import com.sdy.luxurytravelapplication.nim.attachment.WarmingNoticeAttachment
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
import com.sdy.luxurytravelapplication.utils.RandomUtils
import com.sdy.luxurytravelapplication.utils.SaveNetPhotoUtils
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.sweetdateapplication.nim.business.session.activity.ChatActivity
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
    private val rootView: View,
    private val binding: ActivityChatBinding,
    private val anchor: IMMessage? = null,
    val recordOnly: Boolean = false,//仅显示消息记录，不接收和发送消息
    private val remote: Boolean = false,//从服务器拉取消息记录
    val persistClear: Boolean = true//是否忽略缓存记录，拉取消息时存储被清除的消息
) {
    companion object {
        private const val TAG = "MessageListPanelEx"

        const val REQUEST_CODE_FORWARD_PERSON = 0x01
        const val REQUEST_CODE_FORWARD_TEAM = 0x02

        /** MsgSelectActivity的识别码  */
        const val REQUEST_CODE_FORWARD_MULTI_RETWEET = 0x03
    }

    /** message list view */
    private var items: ArrayList<IMMessage> = arrayListOf()
    private val adapter by lazy { MsgAdapter(messageListView, items, container) }

    private lateinit var uiHandler: Handler

    /** 待转发消息  */
    private var forwardMessage: IMMessage? = null
    private val incomingLocalMessageObserver: MessageListPanelHelper.LocalMessageObserver
    private val userInfoObserver: UserInfoObserver
    private val messageStatusObserver: Observer<IMMessage>
    private val attachmentProgressObserver: Observer<AttachmentProgress>
    private val revokeMessageObserver: Observer<RevokeMsgNotification>
    private val deleteMsgSelfObserver: Observer<IMMessage>

    /** 如果在发需要拍照 的消息时，拍照回来时页面可能会销毁重建，重建时会在MessageLoader 的构造方法中调一次 loadFromLocal
     * 而在发送消息后，list 需要滚动到底部，又会通过RequestFetchMoreListener 调用一次 loadFromLocal
     * 所以消息会重复
     */
    private var mIsInitFetchingLocal = false

    init {
        /**
         * 本地消息接收观察者
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
         * 用户信息观察者
         */
        userInfoObserver = UserInfoObserver {
            if (container.sessionType == SessionTypeEnum.P2P) {
                if (it.contains(container.account) || it.contains(NimUIKit.getAccount())) {
                    adapter.notifyDataSetChanged()
                }
            } else { // 群的，简单的全部重刷
                adapter.notifyDataSetChanged()
            }
        }

        /**
         * 消息状态变化观察者
         */
        messageStatusObserver = Observer<IMMessage> {
            if (isMyMessage(it)) {
                onMessageStatusChange(it)
            }
        }

        /**
         * 消息附件上传/下载进度观察者
         */
        attachmentProgressObserver = Observer<AttachmentProgress> {
            onAttachmentProgressChange(it)
        }

        /**
         * 消息撤回观察者
         */
        revokeMessageObserver = Observer<RevokeMsgNotification> { notification ->
            if (notification == null || notification.message == null) {
                return@Observer
            }
            val message = notification.message
            // 获取通知类型： 1表示是离线，2表示是漫游 ，默认 0
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
         * 消息删除观察者
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

    fun hasWarmingNoticeMessage(): Boolean {
        for (data in items) {
            if (data.attachment is WarmingNoticeAttachment) {
                return true
                break
            }
        }
        return false
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
        messageListView = binding.messageListRv
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
            //双向load
            adapter.setOnFetchMoreListener(loader)
            adapter.setOnLoadMoreListener(loader)
        } else {
            //只下来加载Old数据
            adapter.setOnFetchMoreListener(loader)
        }
    }

    //刷新消息列表
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


    /**
     * 是否存在有接收到的消息
     */
    fun hasReceiveMessage() = adapter.data.indexOfFirst { it.direct == MsgDirectionEnum.In } != -1

    /**
     * 是否发送过礼物
     */
    fun sendedGift() =
        adapter.data.indexOfFirst { it.attachment is SendGiftAttachment && it.direct == MsgDirectionEnum.Out } != -1

    fun messageSize(): Int {
        return adapter.data.size
    }

    /**
     * 消息先出 假装发送成功
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
        //刷新金币待领取状态和待认证状态
        if (UserManager.gender == 2) {
            refreshCoinState()
            //刷新最后一条之前自己发出去的消息
            if (!UserManager.isFaced && adapter.bottomDataPosition - 1 != -1 && isMyMessage(
                    adapter.data[adapter.bottomDataPosition - 1]
                )
            ) {
                refreshViewHolderByIndex(adapter.bottomDataPosition - 1)
            }
        }
    }

    /**
     * 更新金币领取状态
     */
    private var lastCoinMessage: IMMessage? = null
    private fun refreshCoinState() {
        var index = 0
        if (lastCoinMessage != null && items.indexOf(lastCoinMessage!!) != -1)
            index = items.indexOf(lastCoinMessage!!)

        for (i in items.size - 1 downTo index) {
            if (!isOutMessage(items[i])) {
                //通过远程扩展去获取金币过期时间
                val remoteExtension = items[i].remoteExtension
                var candyTimeoutTime = 0L
                if (remoteExtension != null) {
                    if (remoteExtension[ChatActivity.COIN_TIMEOUT_TIME] != null)
                        candyTimeoutTime =
                            (remoteExtension[ChatActivity.COIN_TIMEOUT_TIME] as Int).toLong()
                }
                //通过本地扩展拿到当前礼物的状态,如果是待领取就设置为已领取,如果是过期和已经领取就不做操作
                val localExtension = items[i].localExtension
                val state =
                    if (localExtension != null && localExtension[ChatActivity.COIN_RECEIVE_STATE] != null)
                        localExtension[ChatActivity.COIN_RECEIVE_STATE] as Int
                    else
                        SendGiftAttachment.STATUS_WAIT
                //如果认证通过、并且金币未过期、并且当前金币状态为待领取，就更改为已领取
                if (UserManager.isFaced && isOutMessage(items.last()) && state == SendGiftAttachment.STATUS_WAIT && candyTimeoutTime > System.currentTimeMillis() / 1000L) {
                    CommonFunction.updateMessageExtension(
                        items[i],
                        ChatActivity.COIN_RECEIVE_STATE,
                        SendGiftAttachment.STATUS_RECEIVED
                    )
                    refreshMessageItem(items[i].uuid)
                    if (lastCoinMessage == null || lastCoinMessage!!.time < items[i].time) {
                        lastCoinMessage = items[i]
                    }

                    //如果当前金币状态为待领取、并且金币时间已经过期，就更改为已过期
                } else if (state == SendGiftAttachment.STATUS_WAIT && candyTimeoutTime > 0L && candyTimeoutTime < System.currentTimeMillis() / 1000L) {
                    CommonFunction.updateMessageExtension(
                        items[i],
                        ChatActivity.COIN_RECEIVE_STATE,
                        SendGiftAttachment.STATUS_TIMEOUT
                    )
                    refreshMessageItem(items[i].uuid)
                    if (lastCoinMessage == null || lastCoinMessage!!.time < items[i].time) {
                        lastCoinMessage = items[i]
                    }
                }
            }
        }
    }

    private fun doScrollToBottom() {
        messageListView.scrollToPosition(adapter.bottomDataPosition)
    }


    //删除消息
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


    //附件上传进度监听
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


    //消息状态更改
    private fun onMessageStatusChange(message: IMMessage) {
        val index = getItemIndex(message.uuid)
        if (index >= 0 && index < items.size) {
            items[index] = message

            // resend的的情况，可能时间已经变化了，这里要重新检查是否要显示时间
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
     * **************************** 排序 ***********************************
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
     * ***************************************** 数据加载 *********************************************
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

            // 在第一次加载的过程中又收到了新消息，做一下去重
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


            // 加入anchor
            if (firstLoad && anchor != null) {
                messages.add(anchor)
            }

            for (message in messages) {
                if (!isOutMessage(message)) {
                    val remoteExtension = message.remoteExtension
                    if (remoteExtension != null) {
                        var candyTimeoutTime = 0L
                        if (remoteExtension[ChatActivity.COIN_TIMEOUT_TIME] != null)
//                            candyTimeoutTime = (remoteExtension[ChatActivity.COIN_TIMEOUT_TIME] as Int).toLong()
                            candyTimeoutTime =
                                (remoteExtension[ChatActivity.COIN_TIMEOUT_TIME] as Int).toLong()
                        if (candyTimeoutTime > 0L && candyTimeoutTime < System.currentTimeMillis() / 1000L) {
                            CommonFunction.updateMessageExtension(
                                message,
                                ChatActivity.COIN_RECEIVE_STATE,
                                SendGiftAttachment.STATUS_TIMEOUT
                            )
                        }
                    }

                }
//                    if (message.localExtension != null && message.localExtension["isShowRealTip"] != null) {
//                        adapter.isShowRealTip = message.localExtension["isShowRealTip"] != null
//                    }
            }

            // 在更新前，先确定一些标记
            val total: MutableList<IMMessage> = ArrayList<IMMessage>(items)
            val isBottomLoad = direction == QueryDirectionEnum.QUERY_NEW
            if (isBottomLoad) {
                total.addAll(messages)
            } else {
                total.addAll(0, messages)
            }
            // 更新要显示时间的消息
            adapter.updateShowTimeItem(total, true, firstLoad)
            // 更新已读回执标签
            updateReceipt(total)


            // 加载状态修改,刷新界面
            if (isBottomLoad) {
                // 底部加载
                if (noMoreMessage) {
                    adapter.loadMoreEnd(messages, true)
                } else {
                    adapter.loadMoreComplete(messages)
                }
            } else {
                // 顶部加载
                if (noMoreMessage) {
                    adapter.fetchMoreEnd(messages, true)
                } else {
                    adapter.fetchMoreComplete(messages)
                }
            }

            // 如果是第一次加载，updateShowTimeItem返回的就是lastShowTimeItem
            if (firstLoad) {
                doScrollToBottom()
                sendReceipt() // 发送已读回执
            }

            // 通过历史记录加载的群聊消息，需要刷新一下已读未读最新数据
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

            // 在更新前，先确定一些标记
            adapter.updateShowTimeItem(messages, true, firstLoad) // 更新要显示时间的消息
            updateReceipt(messages) // 更新已读回执标签

            // new data
            if (loadCount < loadMsgCount) {
                adapter.loadMoreEnd(messages, true)
            } else {
                adapter.appendData(messages)
            }
            firstLoad = false
        }

        override fun onFetchMoreRequested() {
            // 顶部加载历史数据
            if (remote) {
                loadFromRemote()
            } else {
                loadFromLocal(QueryDirectionEnum.QUERY_OLD)
            }
        }

        override fun onLoadMoreRequested() {
            // 底部加载新数据
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
                    loadAnchorContext() // 加载指定anchor的上下文
                }
            }
        }
    }


    /**
     * 收到已读回执（更新VH的已读label）
     */
    fun receiveReceipt() {
        updateReceipt(items)
        refreshMessageList()
    }

    private fun updateReceipt(messages: MutableList<IMMessage>) {
//        if (messages.size > 0)
//            for (i in messages.size - 1 downTo 0) {
//                if (receiveReceiptCheck(messages[i])) {
//                    adapter.uuid = messages[i].uuid
//                    break
//                }
//            }

    }

    private fun receiveReceiptCheck(msg: IMMessage?): Boolean {
        return msg != null && msg.sessionType == SessionTypeEnum.P2P && isOutMessage(msg) && msg.msgType != MsgTypeEnum.tip && msg.msgType != MsgTypeEnum.notification && msg.isRemoteRead
    }

    /**
     * 发送已读回执（需要过滤）
     */
    fun sendReceipt() {
        // 查询全局已读回执功能开关配置

        // 查询全局已读回执功能开关配置
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
     * adapter操作
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
                //发出的消息，如果是发送失败，直接重发，否则有可能是漫游到的多媒体消息
                if (message.status == MsgStatusEnum.fail) {
                    resendMessage(message)//重发
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
            container.proxy.onNoticeMessageClick(message)


        }

        override fun onCheckStateChanged(index: Int, newState: Boolean) {
        }


        //重新下载
        private fun showReDownloadConfirmDialog(message: IMMessage) {
            MessageDialog.show(
                container.activity as AppCompatActivity,
                container.activity.getString(R.string.re_download),
                container.activity.getString(R.string.confirm_re_download),
                container.activity.getString(R.string.ok),
                container.activity.getString(R.string.cancel)
            ).setOnOkButtonClickListener { baseDialog, v ->
                //正常情况收到消息后附件会自动下载。如果下载失败，可调用该接口重新下载
                if (message.attachment != null && message.attachment is FileAttachment) {
                    NIMClient.getService(MsgService::class.java).downloadAttachment(message, true)
                }
                false
            }.setOnCancelButtonClickListener { baseDialog, v ->
                false
            }
        }


        //重发消息到服务器
        private fun resendMessage(message: IMMessage) {
            //重置为unsend
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
         * ****************************** 长按菜单 ********************************
         */
        // 长按消息Item后弹出菜单控制
        private fun showLongClickAction(selectedItem: IMMessage) {
            onNormaLongClick(selectedItem)
        }


        /**
         * 长按菜单操作
         */
        private fun onNormaLongClick(item: IMMessage) {
            val items = arrayListOf<String>()
            //复制
            if (item.msgType == MsgTypeEnum.text || item.msgType == MsgTypeEnum.robot && item.attachment != null && !(item.attachment as RobotAttachment).isRobotSend) {
                items.add(container.activity.getString(R.string.copy_has_blank))
            }

            //保存二维码
            if (item.attachment != null && item.attachment is SendWechatAttachment) {
                items.add(container.activity.getString(R.string.save_qrcode))
            }

            //删除
            if (!recordOnly) {
                items.add(container.activity.getString(R.string.delete_has_blank))
            }
            //扬声器
            if (item.msgType == MsgTypeEnum.audio) {
                items.add(
                    if (UserPreferences.isEarPhoneModeEnable())
                        container.activity.getString(R.string.switch_speaker)
                    else
                        container.activity.getString(R.string.switch_receiver)
                )
            }

            //重新发送
            if (item.status == MsgStatusEnum.fail) {
                items.add(container.activity.getString(R.string.repeat_send_has_blank))
            }

            //撤回
            if (item.direct == MsgDirectionEnum.Out && (item.attachment == null || (item.attachment != null && item.attachment !is SendGiftAttachment && item.attachment !is SendWechatAttachment))) {
                items.add(container.activity.getString(R.string.withdrawn_msg))
            }

            if (item.msgType != MsgTypeEnum.custom)
            //举报(文本、图片、视频、音频)
                items.add(container.activity.getString(R.string.report))


            BottomMenu.show(
                container.activity as AppCompatActivity,
                items.toList()
            ) { text, index ->
                when (text) {
                    container.activity.getString(R.string.copy_has_blank) -> {// 长按菜单项--复制
                        onCopyMessageItem(item)
                    }
                    container.activity.getString(R.string.save_qrcode) -> {
                        saveQrCode(item)
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
//                        ReportChatContentDialog(container.activity, item).show()
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
                    resendMessage(item)//重新发送消息
                    false
                }
            }
        }

        private fun onCopyMessageItem(item: IMMessage) {
            ClipboardUtil.clipboardCopyText(container.activity, item.content)
        }

        private fun saveQrCode(item: IMMessage) {
            val photoName = "wechat-${RandomUtils.getRandomString(16)}.jpg"
            SaveNetPhotoUtils.savePhoto(
                container.activity,
                (item.attachment as SendWechatAttachment).url, photoName
            )
        }


    }


    /**
     * 设置温馨提示
     */
    fun sendWarmingNotice(chat_expend_aomount: Int) {
        if (container.account != Constants.ASSISTANT_ACCID && adapter.data.isEmpty() && firstLoad && chat_expend_aomount > 0) {
            val attachment = WarmingNoticeAttachment()
            attachment.name = AppUtils.getAppName()
            val confis = CustomMessageConfig()
            confis.enablePush = false
            confis.enableUnreadCount = false

            val message =
                MessageBuilder.createCustomMessage(
                    container.account,
                    SessionTypeEnum.P2P,
                    attachment
                )
            message.config = confis
            NIMClient.getService(MsgService::class.java)
                .insertLocalMessage(message, container.account)
        }
    }

    /**
     * 添加是否非真人认证的提示
     */


//是否展示过非真人
    var isRealMan = true
    var hasNotice = false

    /**
     * 最新接收到的一条消息设置对方是否是真人
     */
    fun setLastReceiveNoticeRealMan() {
        if (!isRealMan) {
            hasNotice =
                items.indexOfFirst { !isOutMessage(it) && it.localExtension != null && it.localExtension[ChatActivity.IS_SHOW_REAL_TIP] != null } != -1
            if (!hasNotice) {
                val lastReceiveMsg =
                    items.firstOrNull { !isOutMessage(it) && it.msgType != MsgTypeEnum.tip && it.msgType != MsgTypeEnum.notification && it.msgType != MsgTypeEnum.custom }
                if (lastReceiveMsg != null)
                    CommonFunction.updateMessageExtension(
                        lastReceiveMsg,
                        ChatActivity.IS_SHOW_REAL_TIP,
                        true
                    )
                adapter.notifyItemChanged(items.indexOf(lastReceiveMsg))
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshGiftStatusEvent(event: RefreshGiftStatusEvent) {
        val message = items.find { it.uuid == event.messageId }
        if (message != null) {
            CommonFunction.updateMessageExtension(
                message,
                SendGiftAttachment.KEY_STATUS,
                event.giftStatus
            )
            if (items.indexOf(message) != -1)
                refreshViewHolderByIndex(items.indexOf(message))
//            refreshMessageItem(message.uuid)
        }
    }
}