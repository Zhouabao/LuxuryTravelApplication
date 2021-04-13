package com.sdy.luxurytravelapplication.ui.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import com.sdy.luxurytravelapplication.databinding.ActivityMyCommentBinding
import com.sdy.luxurytravelapplication.mvp.contract.MyCommentContract
import com.sdy.luxurytravelapplication.mvp.model.bean.MyCommentList
import com.sdy.luxurytravelapplication.mvp.presenter.MyCommentPresenter
import com.sdy.luxurytravelapplication.ui.activity.SquareCommentDetailActivity
import com.sdy.luxurytravelapplication.ui.adapter.MyCommentAdapter
import com.sdy.luxurytravelapplication.utils.ToastUtil

class MyCommentFragment :
    BaseMvpFragment<MyCommentContract.View, MyCommentContract.Presenter, ActivityMyCommentBinding>(),
    MyCommentContract.View, OnRefreshLoadMoreListener {

    private val adapter: MyCommentAdapter by lazy { MyCommentAdapter() }

    private var page = 1
    val params: HashMap<String, Any> = hashMapOf(
        "page" to page,
        "pagesize" to Constants.PAGESIZE
    )


    override fun createPresenter(): MyCommentContract.Presenter {
        return MyCommentPresenter()
    }

    override fun initView(view: View) {
        super.initView(view)
        binding.apply {
            refreshLayout.setOnRefreshLoadMoreListener(this@MyCommentFragment)
            refreshLayout.setEnableLoadMoreWhenContentNotFull(false)
            commentRv.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
            commentRv.adapter = adapter
            adapter.setEmptyView(R.layout.layout_empty_view)

            adapter.setOnItemLongClickListener { adapter, view, position ->
                showCommentDialog(position)
                true
            }
            //点击跳转到广场详情
            adapter.setOnItemClickListener { _, view, position ->
                SquareCommentDetailActivity.start(
                    activity!!,
                    squareId = adapter.data[position].square_id
                )
            }
        }
    }

    override fun lazyLoad() {
        mPresenter?.myCommentList(params)
    }



    private fun copyText(position: Int) {
        //获取剪贴板管理器
        val cm = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        //创建普通字符串clipData
        val clipData = ClipData.newPlainText("label", "${adapter.data[position].content}")
        //将clipdata内容放到系统剪贴板里
        cm.setPrimaryClip(clipData)
        ToastUtil.toast(getString(R.string.has_copy_to_board))
    }


    private fun showCommentDialog(position: Int) {
        val datas = listOf("复制评论", "删除评论")

        BottomMenu.show(activity!! as AppCompatActivity, datas) { text: String, index: Int ->
            when (index) {
                0 -> {
                    copyText(position)
                }

                1 -> {
                    mPresenter?.deleteComment(
                        hashMapOf("id" to adapter.data[position].id),
                        position
                    )

                }
            }
        }
            .setStyle(DialogSettings.STYLE.STYLE_IOS)
            .setShowCancelButton(false)
            .menuTextInfo =
            TextInfo().setFontColor(resources.getColor(R.color.color333)).setFontSize(16)

    }


    override fun onGetCommentListResult(data: MyCommentList?) {
        binding.apply {
            if (refreshLayout.state == RefreshState.Loading) {
                if (data != null && data.list.size < Constants.PAGESIZE * page) {
                    refreshLayout.setNoMoreData(true)
                } else {
                    refreshLayout.finishLoadMore(data != null)
                }
                if (data != null) {
                    adapter.addData(data.list)
                }
            } else {
                refreshLayout.finishRefresh(data != null)
                if (data != null) {
                    adapter.setNewInstance(data.list)
                }
            }


        }

    }

    override fun onDeleteCommentResult(success: Boolean, position: Int) {
        if (success) {
            adapter.removeAt(position)
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        if (adapter.data.size < Constants.PAGESIZE * page) {
            refreshLayout.finishLoadMoreWithNoMoreData()
        } else {
            page++
            params["page"] = page
            mPresenter?.myCommentList(params)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        page = 1
        params["page"] = page
        adapter.data.clear()
        refreshLayout.setNoMoreData(false)
        mPresenter?.myCommentList(params)
    }


}
