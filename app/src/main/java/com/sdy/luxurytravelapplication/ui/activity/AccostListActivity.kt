package com.sdy.luxurytravelapplication.ui.activity

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.MsgServiceObserve
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.netease.nimlib.sdk.msg.model.RecentContact
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityAccostListBinding
import com.sdy.luxurytravelapplication.event.GetNewMsgEvent
import com.sdy.luxurytravelapplication.event.UpdateAccostListEvent
import com.sdy.luxurytravelapplication.event.UpdateHiEvent
import com.sdy.luxurytravelapplication.mvp.contract.AccostListContract
import com.sdy.luxurytravelapplication.mvp.model.bean.AccostBean
import com.sdy.luxurytravelapplication.mvp.presenter.AccostListPresenter
import com.sdy.luxurytravelapplication.ui.adapter.AccostListAdapter
import com.sdy.luxurytravelapplication.nim.business.session.activity.ChatActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 全部搭讪
 */
class AccostListActivity :
    BaseMvpActivity<AccostListContract.View, AccostListContract.Presenter, ActivityAccostListBinding>(),
    AccostListContract.View,
    OnRefreshLoadMoreListener {
    override fun createPresenter(): AccostListContract.Presenter = AccostListPresenter()
    private val adapter by lazy { AccostListAdapter() }
    private var page = 1
    private val params by lazy {
        hashMapOf<String, Any>(
            "page" to page,
            "pagesize" to Constants.PAGESIZE
        )
    }

    override fun initData() {
        registerObservers(true)
        binding.apply {
            barCl.actionbarTitle.text = "搭讪消息"
            barCl.btnBack.setOnClickListener {
                finish()
            }
            barCl.divider.isVisible = true

            mLayoutStatusView = stateAccost
            blackLl.isVisible = UserManager.gender == 2
            refreshAccost.setOnRefreshLoadMoreListener(this@AccostListActivity)
            rvAccost.layoutManager = LinearLayoutManager(this@AccostListActivity)
            rvAccost.adapter = adapter
            adapter.addChildClickViewIds(R.id.content, R.id.menuDetele)
            adapter.setOnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.content -> {
                        ChatActivity.start(this@AccostListActivity, adapter.data[position].accid)
                        EventBus.getDefault().post(GetNewMsgEvent())
                    }
                    //删除会话
                    R.id.menuDetele -> {
                        mPresenter?.delChat(
                            hashMapOf("target_accid" to adapter.data[position].accid),
                            position
                        )
                    }
                }
            }
        }
    }

    override fun start() {
        mLayoutStatusView?.showLoading()
        mPresenter?.chatupList(params)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        if (adapter.data.size < Constants.PAGESIZE * page) {
            refreshLayout.finishLoadMoreWithNoMoreData()
        } else {
            page++
            params["page"] = page
            mPresenter?.chatupList(params)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshLayout.resetNoMoreData()
        page = 1
        params["page"] = page
        mPresenter?.chatupList(params)
    }

    override fun chatupList(data: MutableList<AccostBean>?) {
        //获取最近联系人列表
        mPresenter?.getRecentContacts(data ?: mutableListOf())

    }

    override fun delChat(success: Boolean, position: Int) {
        if (success) {
            NIMClient.getService(MsgService::class.java)
                .deleteRecentContact2(adapter.data[position].accid, SessionTypeEnum.P2P)
            adapter.removeAt(position)
            EventBus.getDefault().post(UpdateHiEvent())
        }

    }

    override fun getRecentContacts(
        t: MutableList<AccostBean>,
        contacts: MutableList<RecentContact>
    ) {
        if (t.isNullOrEmpty() && t.size < Constants.PAGESIZE) {
            binding.refreshAccost.finishLoadMoreWithNoMoreData()
        } else {
            binding.refreshAccost.finishLoadMore(true)
        }
        if (binding.refreshAccost.state == RefreshState.Refreshing) {
            adapter.data.clear()
            binding.refreshAccost.finishRefresh(true)
        }
        for (recentContactt in contacts) {
            for (data in t) {
                if (recentContactt.contactId == data.accid) {
                    data.unreadCnt = recentContactt.unreadCount
                    data.time = recentContactt.time
                    data.content = recentContactt.content
                }
            }
        }
        adapter.addData(t)
        mLayoutStatusView?.showContent()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateAccostListEvent(event: UpdateAccostListEvent) {
        binding.refreshAccost.autoRefresh()
    }

    /**
     * ********************** 收消息，处理状态变化 ************************
     */
    private fun registerObservers(register: Boolean) {
        NIMClient.getService(MsgServiceObserve::class.java)
            .observeReceiveMessage(messageReceiverObserver, register)
    }


    //监听在线消息中是否有@我
    private val messageReceiverObserver =
        Observer<List<IMMessage>> { imMessages ->
            if (imMessages != null) {
                for (contact in adapter.data.withIndex()) {
                    for (imMessage in imMessages) {
                        if (contact.value.accid == imMessage.fromAccount) {
                            contact.value.unreadCnt = contact.value.unreadCnt + 1
                            contact.value.time = imMessage.time
                            adapter.notifyItemChanged(contact.index)
                        }
                    }
                }
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        registerObservers(false)
    }

}