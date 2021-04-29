package com.sdy.luxurytravelapplication.ui.fragment

import android.os.Handler
import android.text.TextUtils
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SPUtils
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.MsgServiceObserve
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.netease.nimlib.sdk.msg.model.MessageReceipt
import com.netease.nimlib.sdk.msg.model.RecentContact
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpFragment
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.FragmentMessageBinding
import com.sdy.luxurytravelapplication.databinding.HeaderMessageAllBinding
import com.sdy.luxurytravelapplication.databinding.HeaderviewLikeMeBinding
import com.sdy.luxurytravelapplication.event.GetNewMsgEvent
import com.sdy.luxurytravelapplication.event.UpdateHiEvent
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.mvp.contract.MessageContract
import com.sdy.luxurytravelapplication.mvp.model.bean.MessageListBean
import com.sdy.luxurytravelapplication.mvp.model.bean.MessageListBean1
import com.sdy.luxurytravelapplication.mvp.presenter.MessagePresenter
import com.sdy.luxurytravelapplication.nim.api.NimUIKit
import com.sdy.luxurytravelapplication.nim.api.model.contact.ContactChangedObserver
import com.sdy.luxurytravelapplication.nim.api.model.main.OnlineStateChangeObserver
import com.sdy.luxurytravelapplication.nim.api.model.user.UserInfoObserver
import com.sdy.luxurytravelapplication.nim.attachment.ContactAttachment
import com.sdy.luxurytravelapplication.nim.attachment.SendCustomTipAttachment
import com.sdy.luxurytravelapplication.nim.business.session.activity.ChatActivity
import com.sdy.luxurytravelapplication.nim.common.util.sys.TimeUtil
import com.sdy.luxurytravelapplication.nim.impl.NimUIKitImpl
import com.sdy.luxurytravelapplication.ui.activity.AccostListActivity
import com.sdy.luxurytravelapplication.ui.activity.ContactBookActivity
import com.sdy.luxurytravelapplication.ui.activity.MessageSquareActivity
import com.sdy.luxurytravelapplication.ui.adapter.MessageCenterAllAdapter
import com.sdy.luxurytravelapplication.ui.adapter.MessageListAdapter
import com.sdy.luxurytravelapplication.ui.adapter.MessageListHeadAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.startActivity
import java.util.*
import kotlin.Comparator

/**
 * 消息
 */
class MessageFragment :
    BaseMvpFragment<MessageContract.View, MessageContract.Presenter, FragmentMessageBinding>(),
    MessageContract.View {
    companion object {
        const val RECENT_TAG_STICKY: Long = 0x0000000000000001 // 联系人置顶tag
    }

    override fun createPresenter(): MessageContract.Presenter = MessagePresenter()

    private val adapter by lazy { MessageListAdapter() }

    override fun initView(view: View) {
        super.initView(view)
        registerObservers(true)
        registerOnlineStateChangeListener(true)
        binding.apply {
            (statues.layoutParams as ConstraintLayout.LayoutParams).height = BarUtils.getStatusBarHeight()
            mLayoutStatusView = stateView
            messageListRv.layoutManager =
                LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
            messageListRv.adapter = adapter
            adapter.addHeaderView(initMessageAllHeader(), 0)
            adapter.addHeaderView(initAssistHeadsView(), 1)
            adapter.headerWithEmptyEnable = true
            ClickUtils.applySingleDebouncing(contactBookBtn) {
                ContactBookActivity.start(activity!!)
            }
        }
    }

    override fun lazyLoad() {
        mPresenter?.messageCensus()

    }

    /**
     * 消息汇总中心
     */
    private val accostAdapter by lazy { MessageCenterAllAdapter() }
    lateinit var headChatupBinding: HeaderMessageAllBinding
    private fun initMessageAllHeader(): View {
        headChatupBinding = HeaderMessageAllBinding.inflate(layoutInflater)
        headChatupBinding.apply {
            messageCenterRv.layoutManager =
                LinearLayoutManager(activity!!, RecyclerView.HORIZONTAL, false)
            messageCenterRv.adapter = accostAdapter
            ClickUtils.applySingleDebouncing(moreChatUpBtn) {
                startActivity<AccostListActivity>()
            }
        }
        accostAdapter.setOnItemClickListener { _, _, position ->
            ChatActivity.start(activity!!, accostAdapter.data[position].accid)
            accostAdapter.data[position].unreadCnt = 0
            accostAdapter.notifyItemChanged(position)
        }


        return headChatupBinding.root
    }


    /**
     * 创建小助手布局
     */
    private val headAdapter by lazy { MessageListHeadAdapter() }
    var checked = false

    //官方助手
    private val ass by lazy {
        MessageListBean(
            getString(R.string.assistant),
            getString(R.string.no_assistant_msg),
            0,
            "",
            R.drawable.icon_assistant
        )
    }
    private val square by lazy {
        MessageListBean(
            getString(R.string.squre_message),
            getString(R.string.no_square_message),
            0,
            "",
            R.drawable.icon_square
        )
    }
    lateinit var headMessageBinding: HeaderviewLikeMeBinding
    private fun initAssistHeadsView(): View {
        //初始化第一个项目
        headAdapter.addData(ass)
        headAdapter.addData(square)
        headAdapter.addChildClickViewIds(R.id.content)
        headAdapter.setOnItemChildClickListener { _, view, position ->
            when (view.id) {
                R.id.content -> {
                    when (position) {
                        0 -> {//官方助手

                            ChatActivity.start(activity!!, Constants.ASSISTANT_ACCID)

                        }
                        1 -> {
                            startActivity<MessageSquareActivity>()
                        }
                    }
                    EventBus.getDefault().post(GetNewMsgEvent())
                    headAdapter.data[position].count = 0
                    headAdapter.notifyItemChanged(position)
                }
            }
        }
        headMessageBinding = HeaderviewLikeMeBinding.inflate(layoutInflater)
        headMessageBinding.apply {
            val linearLayoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
            headRv.layoutManager = linearLayoutManager
            headRv.adapter = headAdapter

        }

        return headMessageBinding.root
    }


    private fun refreshMessages() {
        sortRecentContacts(adapter.data)
        adapter.notifyDataSetChanged()
    }

    /**
     * **************************** 排序 ***********************************
     */
    private fun sortRecentContacts(list: List<RecentContact>) {
        if (list.isEmpty()) {
            return
        }
        Collections.sort(list, comp)
    }

    private val comp = Comparator<RecentContact> { o1, o2 ->
        // 先比较置顶tag
        val sticky = (o1.tag and RECENT_TAG_STICKY) - (o2.tag and RECENT_TAG_STICKY)
        if (sticky != 0L) {
            if (sticky > 0) -1 else 1
        } else {
            val time = o1.time - o2.time
            if (time == 0L) 0 else if (time > 0) -1 else 1
        }
    }

    /**
     * ********************** 收消息，处理状态变化 ************************
     */
    private fun registerObservers(register: Boolean) {
        val service = NIMClient.getService(MsgServiceObserve::class.java)
        service.observeReceiveMessage(messageReceiverObserver, register)
        service.observeRecentContact(messageObserver, register)
        service.observeMsgStatus(statusObserver, register)
        service.observeRecentContactDeleted(deleteObserver, register)
//        // 已读回执监听
        if (NimUIKitImpl.getOptions().shouldHandleReceipt) {
            service.observeMessageReceipt(messageReceiptObserver, register)
        }

        NimUIKit.getContactChangedObservable().registerObserver(friendDataChangedObserver, register)
        if (register) {
            registerUserInfoObserver()
        } else {
            unregisterUserInfoObserver()
        }
    }

    private var userInfoObserver: UserInfoObserver? = null

    private fun registerUserInfoObserver() {
        if (userInfoObserver == null) {
            userInfoObserver = UserInfoObserver { refreshMessages() }
        }
        NimUIKit.getUserInfoObservable().registerObserver(userInfoObserver, true)
    }

    private fun unregisterUserInfoObserver() {
        if (userInfoObserver != null) {
            NimUIKit.getUserInfoObservable().registerObserver(userInfoObserver, false)
        }
    }


    internal var friendDataChangedObserver: ContactChangedObserver =
        object : ContactChangedObserver {

            override fun onAddedOrUpdatedFriends(accounts: List<String>) {
                refreshMessages()
            }

            override fun onDeletedFriends(accounts: List<String>) {
                refreshMessages()
            }

            override fun onAddUserToBlackList(account: List<String>) {
                refreshMessages()
            }

            override fun onRemoveUserFromBlackList(account: List<String>) {
                refreshMessages()
            }
        }

    //监听在线消息中是否有@我
    private val messageReceiverObserver =
        Observer<List<IMMessage>> { imMessages ->
            if (imMessages != null) {
                //首先剔除自定义的tip消息
                val iterator = imMessages.iterator()
                while (iterator.hasNext()) {
                    val message = iterator.next()
                    val isSend = message.direct == MsgDirectionEnum.Out
                    if ((message.attachment is SendCustomTipAttachment && (message.attachment as SendCustomTipAttachment).ifSendUserShow != isSend)
                        || (message.attachment is ContactAttachment && message.direct == MsgDirectionEnum.Out)
                    ) {
                        NIMClient.getService(MsgService::class.java).deleteMsgSelf(message, "")
                    }
                }
            }
        }

    internal var messageObserver: Observer<MutableList<RecentContact>> =
        Observer { recentContacts ->
            onRecentContactChanged()
        }

    private fun onRecentContactChanged() {
        mPresenter?.messageCensus()
    }

    private fun registerOnlineStateChangeListener(register: Boolean) {
        if (!NimUIKitImpl.enableOnlineState()) {
            return
        }
        NimUIKitImpl.getOnlineStateChangeObservable().registerOnlineStateChangeListeners(
            onlineStateChangeObserver,
            register
        )
    }


    private var onlineStateChangeObserver: OnlineStateChangeObserver =
        OnlineStateChangeObserver { adapter.notifyDataSetChanged() }


    internal var statusObserver: Observer<IMMessage> =
        Observer { message ->
            val index = getItemIndex(message.uuid)
            if (index >= 0 && index < adapter.data.size) {
                val item = adapter.data.get(index)
                item.msgStatus = message.status

                adapter.notifyItemChanged(index + adapter.headerLayoutCount)
            }
        }

    internal var deleteObserver: Observer<RecentContact> =
        Observer { recentContact ->
            if (recentContact != null) {
                for (item in adapter.data) {
                    if (TextUtils.equals(item.getContactId(), recentContact.contactId)
                        && item.getSessionType() == recentContact.sessionType
                    ) {
                        adapter.data.remove(item)
                        refreshMessages()
                        break
                    }
                }
            } else {
                adapter.data.clear()
                refreshMessages()
            }
        }


    /**
     * 已读回执观察者
     */
    private val messageReceiptObserver = Observer<List<MessageReceipt>> {
        //收到已读回执,调用接口,改变此时招呼或者消息的状态
//        mPresenter.messageCensus(params)
    }

    private fun getItemIndex(uuid: String): Int {
        for (i in adapter.data.indices) {
            val item = adapter.data.get(i)
            if (TextUtils.equals(item.getRecentMessageId(), uuid)) {
                return i
            }
        }
        return -1
    }

    override fun onDestroy() {
        super.onDestroy()
        registerObservers(false)
        registerOnlineStateChangeListener(false)
        Handler().removeCallbacksAndMessages(null)
    }

    /**
     * 获取消息中心的顶部数据
     */
    private var accostIds = mutableListOf<String>()
    override fun messageCensus(data: MessageListBean1?) {
        if (data != null) {
            mLayoutStatusView?.showContent()
            if (data.square_count > 0) {
                EventBus.getDefault().post(GetNewMsgEvent())
            }
            headAdapter.data[1].msg = when (data.square_type) {
                1 -> {
                    getString(R.string.someone_zan_your_square, data.square_nickname)
                }
                2 -> {
                    getString(R.string.someone_comment_your_square, data.square_nickname)
                }
                3 -> {
                    getString(R.string.someone_zan_your_comment, data.square_nickname)
                }
                else -> {
                    getString(R.string.no_new_square)
                }
            }

            headAdapter.data[1].count = data.square_count
            headAdapter.data[1].time = data.square_time
            headAdapter.notifyItemChanged(1)
            adapter.session_list_arr = data.session_list_arr
            accostAdapter.setNewInstance(data.chatup_list)
            if (data.chatup_list.size > 0) {
                headChatupBinding.moreChatUpBtn.isVisible =
                    data.chatup_list.size > 4
                adapter.headerLayout?.getChildAt(0)?.isVisible = true
            } else {
                adapter.headerLayout?.getChildAt(0)?.isVisible = false
            }
            accostIds = data.chatup_rid_list
            //获取最近联系人列表
            mPresenter?.getRecentContacts()
        } else {
            mLayoutStatusView?.showError()
        }
    }

    override fun getRecentContacts(result: MutableList<RecentContact>) {
        for (loadedRecent in result) {
            if (loadedRecent.contactId == Constants.ASSISTANT_ACCID) {
                ass.msg = CommonFunction.getRecentContent(loadedRecent)
                ass.count = loadedRecent.unreadCount
                ass.time = TimeUtil.getTimeShowString(loadedRecent.time, true)
                ass.id = loadedRecent.contactId
                //本地小助手发送通知通过认证通知，本地修改认证状态
                if (loadedRecent.content.contains(getString(R.string.pass_verify))) {
                    //更改本地的认证状态
                    UserManager.isverify = 1
                    if (SPUtils.getInstance(Constants.SPNAME).getInt("audit_only", -1) != -1) {
                        SPUtils.getInstance(Constants.SPNAME).remove("audit_only")
                    }
                }
                headAdapter.setData(0, ass)
                headAdapter.notifyItemChanged(0)
                result.remove(loadedRecent)
                break
            }
        }

        val iterator = result.iterator()
        while (iterator.hasNext()) {
            val contact = iterator.next()
            for (accid in accostIds) {
                if (contact.contactId == accid) {
                    iterator.remove()
                    break
                }
            }
        }
        adapter.setNewInstance(result)
        refreshMessages()
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onUpdateHiEvent(event: UpdateHiEvent) {
        try {
            mPresenter?.messageCensus()
        } catch (e: Exception) {

        }
    }

}