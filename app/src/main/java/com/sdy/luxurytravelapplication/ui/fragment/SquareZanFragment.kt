package com.sdy.luxurytravelapplication.ui.fragment

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kongzue.dialog.util.DialogSettings
import com.kongzue.dialog.util.TextInfo
import com.kongzue.dialog.v3.BottomMenu
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpFragment
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.EmptyLayoutCommentBinding
import com.sdy.luxurytravelapplication.databinding.FragmentSquareZanBinding
import com.sdy.luxurytravelapplication.event.GetNewMsgEvent
import com.sdy.luxurytravelapplication.mvp.contract.MessageSquareContract
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareMsgBean
import com.sdy.luxurytravelapplication.mvp.presenter.MessageSquarePresenter
import com.sdy.luxurytravelapplication.ui.activity.SquareCommentDetailActivity
import com.sdy.luxurytravelapplication.ui.adapter.MessageSquareAdapter
import org.greenrobot.eventbus.EventBus

/**
 * 广场点赞
 */
class SquareZanFragment(val type: Int = TYPE_ZAN) :
    BaseMvpFragment<MessageSquareContract.View, MessageSquareContract.Presenter, FragmentSquareZanBinding>(),
    MessageSquareContract.View, OnRefreshLoadMoreListener {
    companion object {
        const val TYPE_ZAN = 1
        const val TYPE_COMMNET = 2
    }

    private var page = 1
    private val params by lazy {
        hashMapOf<String, Any>(
            "page" to page,
            "pagesize" to Constants.PAGESIZE,
            "type" to type
        )
    }
    private val adapter by lazy { MessageSquareAdapter() }

    override fun createPresenter(): MessageSquareContract.Presenter = MessageSquarePresenter()

    override fun initView(view: View) {
        super.initView(view)
        binding.apply {
            mLayoutStatusView = root
            refreshSquareMsg.setOnRefreshLoadMoreListener(this@SquareZanFragment)
            rvMsgSquare.layoutManager =
                LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
            rvMsgSquare.adapter = adapter
            adapter.setEmptyView(R.layout.layout_empty_view)
            adapter.setOnItemClickListener { _, view, position ->
                val item = adapter.data[position]
                SquareCommentDetailActivity.start(activity!!, squareId = item.id)
            }
            adapter.setOnItemLongClickListener { _, view, position ->
                BottomMenu.show(activity as AppCompatActivity, arrayOf("删除"))
                { text, index ->
                    val item = adapter.data[position]
                    mPresenter?.delSquareMsg(
                        hashMapOf(
                            "target_accid" to item.accid,
                            "msg_id" to item.msg_id,
                            "type" to item.type
                        )
                    )
                }
                    .setStyle(DialogSettings.STYLE.STYLE_IOS)
                    .setShowCancelButton(false)
                    .menuTextInfo =
                    TextInfo().setFontColor(resources.getColor(R.color.color333)).setFontSize(16)
                true
            }
        }

    }

    override fun lazyLoad() {
        mLayoutStatusView?.showLoading()
        mPresenter?.squareLists(params)
    }

    override fun onSquareListsResult(data: MutableList<SquareMsgBean>?) {
        if (data != null) {
            mPresenter?.markSquareRead(params)
            if (data.isNullOrEmpty()) {
                if (binding.refreshSquareMsg.state == RefreshState.Loading) {
                    binding.refreshSquareMsg.finishLoadMoreWithNoMoreData()
                } else {
                    binding.refreshSquareMsg.finishRefresh()
                    adapter.isUseEmpty = true
                }
            } else {
                if (binding.refreshSquareMsg.state == RefreshState.Loading) {
                    binding.refreshSquareMsg.finishLoadMore(true)
                    adapter.addData(data)
                } else {
                    binding.refreshSquareMsg.finishRefresh(true)
                    adapter.setNewInstance(data)
                }
            }
            mLayoutStatusView?.showContent()
        } else {
            mLayoutStatusView?.showError()
        }

    }

    override fun onDelSquareMsgResult(success: Boolean) {
        if (success) {
            binding.refreshSquareMsg.autoRefresh()
        }
    }

    override fun markSquareReadResult(success: Boolean) {
        if (success) {
            EventBus.getDefault().post(GetNewMsgEvent())
        }

    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        if (adapter.data.size < Constants.PAGESIZE * page) {
            refreshLayout.finishLoadMoreWithNoMoreData()
        } else {
            page++
            params["page"] = page
            mPresenter?.squareLists(params)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        page = 1
        params["page"] = page
        refreshLayout.resetNoMoreData()
        mPresenter?.squareLists(params)
    }


}