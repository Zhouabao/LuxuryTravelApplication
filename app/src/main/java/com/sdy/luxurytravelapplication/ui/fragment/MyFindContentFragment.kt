package com.sdy.luxurytravelapplication.ui.fragment

import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sdy.luxurytravelapplication.base.BaseMvpFragment
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.EmptyMysquareLayoutBinding
import com.sdy.luxurytravelapplication.databinding.FragmentMyFindContentBinding
import com.sdy.luxurytravelapplication.databinding.HeaderviewMyFragmentBinding
import com.sdy.luxurytravelapplication.mvp.contract.MyFindContentContract
import com.sdy.luxurytravelapplication.mvp.model.bean.RecommendSquareListBean
import com.sdy.luxurytravelapplication.mvp.presenter.MyFindContentPresenter
import com.sdy.luxurytravelapplication.nim.common.util.sys.TimeUtil
import com.sdy.luxurytravelapplication.ui.activity.PublishActivity
import com.sdy.luxurytravelapplication.ui.adapter.RecommendSquareAdapter
import java.util.*


/**
 * 我的广场动态
 */
class MyFindContentFragment :
    BaseMvpFragment<MyFindContentContract.View, MyFindContentContract.Presenter, FragmentMyFindContentBinding>(),
    MyFindContentContract.View, OnRefreshLoadMoreListener {
    override fun createPresenter(): MyFindContentContract.Presenter = MyFindContentPresenter()


    private val adapter by lazy { RecommendSquareAdapter() }
    private var page = 1

    //请求广场的参数 TODO要更新tagid
    private val params by lazy {
        hashMapOf<String, Any>(
            "page" to page,
            "pagesize" to Constants.PAGESIZE
        )
    }

    override fun initView(view: View) {
        super.initView(view)
        binding.apply {
//            mLayoutStatusView = multipleStatusView
            refreshFind.setOnRefreshLoadMoreListener(this@MyFindContentFragment)

            val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
            findRv.layoutManager = manager
            adapter.addHeaderView(initHeadView())
            adapter.setEmptyView(initEmptyView())
            adapter.headerWithEmptyEnable = false
            findRv.adapter = adapter
        }
    }


    override fun lazyLoad() {
        mPresenter?.squareEliteList(params)
    }


    private fun initEmptyView(): View {
        val emptyBinding = EmptyMysquareLayoutBinding.inflate(layoutInflater)
        emptyBinding.mySquareDate.text =
            TimeUtil.getNowDateTime("MM月dd日") + "，" + TimeUtil.getWeekOfDate(Date(System.currentTimeMillis()))

        ClickUtils.applySingleDebouncing(emptyBinding.addSquareBtn) {
            mPresenter?.checkBlock()
        }
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = SizeUtils.dp2px(7F)
        params.topMargin = SizeUtils.dp2px(15F)
        params.bottomMargin = SizeUtils.dp2px(15F)
        emptyBinding.root.layoutParams = params

        return emptyBinding.root
    }

    private fun initHeadView(): View {
        val headBinding = HeaderviewMyFragmentBinding.inflate(layoutInflater)
        headBinding.apply {
            ClickUtils.applySingleDebouncing(root) {
                mPresenter?.checkBlock()
            }

        }
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            SizeUtils.dp2px(50F)
        )
        params.leftMargin = SizeUtils.dp2px(7F)
        params.topMargin = SizeUtils.dp2px(15F)
        params.bottomMargin = SizeUtils.dp2px(15F)
        headBinding.root.layoutParams = params
        return headBinding.root
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        if (adapter.data.size < Constants.PAGESIZE * page) {
            refreshLayout.finishLoadMoreWithNoMoreData()
        } else {
            page++
            params["page"] = page
            mPresenter?.squareEliteList(params)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshLayout.resetNoMoreData()
        page = 1
        params["page"] = page
        mPresenter?.squareEliteList(params)
    }


    override fun onGetSquareRecommendResult(data: RecommendSquareListBean?, b: Boolean) {
        if (b) {
            mLayoutStatusView?.showContent()
        } else {
            mLayoutStatusView?.showError()
        }

        if (binding.refreshFind.state == RefreshState.Refreshing) {
            adapter.data.clear()
            adapter.notifyDataSetChanged()
            binding.findRv.scrollToPosition(0)
            binding.refreshFind.finishRefresh(b)
        } else {
            if (data?.list.isNullOrEmpty() || (data?.list
                    ?: mutableListOf()).size < Constants.PAGESIZE
            )
                binding.refreshFind.finishLoadMoreWithNoMoreData()
            else
                binding.refreshFind.finishLoadMore(b)
        }

        if ((data?.list ?: mutableListOf()).size > 0) {
            for (data in data?.list ?: mutableListOf()) {
                data.originalLike = data.isliked
                data.originalLikeCount = data.like_cnt
            }
            adapter.addData(data?.list ?: mutableListOf())
        }

        if (adapter.data.isEmpty()) {
            adapter.isUseEmpty = true
            binding.refreshFind.setEnableLoadMore(false)
        } else {
            binding.refreshFind.setEnableLoadMore(true)
        }

    }

    override fun onCheckBlockResult(b: Boolean) {
        PublishActivity.startToPublish(activity!!)
    }
}