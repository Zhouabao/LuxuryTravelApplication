package com.sdy.luxurytravelapplication.ui.activity

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kongzue.dialog.util.DialogSettings
import com.kongzue.dialog.util.TextInfo
import com.kongzue.dialog.v3.BottomMenu
import com.kongzue.dialog.v3.TipDialog
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.friend.FriendService
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.ActivityBlackListBinding
import com.sdy.luxurytravelapplication.mvp.contract.BlackListContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BlackBean
import com.sdy.luxurytravelapplication.mvp.presenter.BlackListPresenter
import com.sdy.luxurytravelapplication.ui.adapter.BlackListAdapter

/**
 * 黑名单
 */
class BlackListActivity :
    BaseMvpActivity<BlackListContract.View, BlackListContract.Presenter, ActivityBlackListBinding>(),
    BlackListContract.View, OnRefreshLoadMoreListener {

    override fun createPresenter(): BlackListContract.Presenter {
        return BlackListPresenter()
    }


    private var page = 1
    private val params by lazy {
        hashMapOf<String, Any>(
            "page" to page,
            "pagesize" to Constants.PAGESIZE
        )
    }
    private val adapter by lazy { BlackListAdapter() }


    override fun initData() {
        binding.apply {
            barCl.btnBack.setOnClickListener { finish() }
            barCl.actionbarTitle.text = getString(R.string.black_title)



            refreshLayout.setOnRefreshLoadMoreListener(this@BlackListActivity)

            blackList.layoutManager =
                LinearLayoutManager(this@BlackListActivity, RecyclerView.VERTICAL, false)
            blackList.adapter = adapter
            adapter.setEmptyView(R.layout.layout_empty_view)
            adapter.addChildClickViewIds(R.id.removeBtn)
            adapter.setOnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.removeBtn -> {
                        BottomMenu.show(
                            this@BlackListActivity, arrayOf("拉黑")
                        ) { text, index ->
                            mPresenter?.removeBlock(
                                hashMapOf(
                                    "target_accid" to adapter.data[position].accid
                                ), position
                            )

                        }
                            .setStyle(DialogSettings.STYLE.STYLE_IOS)
                            .setShowCancelButton(false)
                            .menuTextInfo =
                            TextInfo().setFontColor(resources.getColor(R.color.color333))
                                .setFontSize(16)

                    }
                }
            }
        }
    }

    override fun start() {
        mPresenter?.myShieldingList(params)
    }

    override fun onMyShieldingListResult(data: MutableList<BlackBean>?) {
        if (binding.refreshLayout.state == RefreshState.Loading) {
            if (data != null) {
                if (data.size < Constants.PAGESIZE * page) {
                    binding.refreshLayout.finishLoadMoreWithNoMoreData()
                } else {
                }
                binding.refreshLayout.finishLoadMore(true)
            } else {
                binding.refreshLayout.finishLoadMore(false)
            }
            if (data != null)
                adapter.addData(data)
        } else {
            binding.refreshLayout.finishRefresh(data != null)
            if (data != null)
                adapter.setNewInstance(data)

        }
    }

    override fun onRemoveBlockResult(success: Boolean, position: Int) {
        if (success) {
            TipDialog.show(
                this as AppCompatActivity,
                R.string.remove_black_success,
                TipDialog.TYPE.SUCCESS
            )
            NIMClient.getService(FriendService::class.java)
                .removeFromBlackList(adapter.data[position].accid)
            adapter.removeAt(position)
        }
    }


    override fun onLoadMore(refreshLayout: RefreshLayout) {
        if (adapter.data.size < Constants.PAGESIZE * page) {
            refreshLayout.finishLoadMoreWithNoMoreData()
        } else {
            page++
            mPresenter?.myShieldingList(params)
        }

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        page = 1
        params["page"] = page
        adapter.data.clear()
        refreshLayout.resetNoMoreData()
        mPresenter?.myShieldingList(params)
    }

}