package com.sdy.luxurytravelapplication.ui.fragment

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.blankj.utilcode.util.ClickUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpFragment
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.EmptyFriendLayoutBinding
import com.sdy.luxurytravelapplication.databinding.FragmentFindContentBinding
import com.sdy.luxurytravelapplication.databinding.HeaderviewRecommendBannerBinding
import com.sdy.luxurytravelapplication.mvp.contract.FindContentContract
import com.sdy.luxurytravelapplication.mvp.model.bean.RecommendSquareListBean
import com.sdy.luxurytravelapplication.mvp.presenter.FindContentPresenter
import com.sdy.luxurytravelapplication.ui.activity.FindAllTagActivity
import com.sdy.luxurytravelapplication.ui.adapter.RecommendSquareAdapter
import com.sdy.luxurytravelapplication.ui.adapter.SquareHeadTopicAdapter
import com.sdy.luxurytravelapplication.utils.ToastUtil
import org.jetbrains.anko.support.v4.startActivity


/**
 * 广场动态
 */
class FindContentFragment(val type: Int = TYPE_RECOMMEND) :
    BaseMvpFragment<FindContentContract.View, FindContentContract.Presenter, FragmentFindContentBinding>(),
    FindContentContract.View, OnRefreshLoadMoreListener {
    override fun createPresenter(): FindContentContract.Presenter = FindContentPresenter()

    companion object {
        const val TYPE_RECOMMEND = 1
        const val TYPE_NEARBY = 2
        const val TYPE_NEWEST = 3
    }

    private val adapter by lazy { RecommendSquareAdapter() }
    private val topicAdapter by lazy { SquareHeadTopicAdapter() }
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
            mLayoutStatusView = multipleStatusView
            refreshFind.setOnRefreshLoadMoreListener(this@FindContentFragment)

            val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
            findRv.layoutManager = manager
            findRv.adapter = adapter

            //android 瀑布流
            if (type == TYPE_RECOMMEND)
                adapter.setHeaderView(initHeadBannerView())
            adapter.setEmptyView(R.layout.empty_friend_layout)
            val emptyBinding = EmptyFriendLayoutBinding.inflate(layoutInflater)
            emptyBinding.emptyFriendTitle.text = getString(R.string.nearby_empty_content)
            emptyBinding.emptyFriendTip.text = getString(R.string.nearby_empty_content1)
            emptyBinding.emptyImg.setImageResource(R.drawable.icon_verify_ing)
            adapter.isUseEmpty = false
        }
    }

    override fun lazyLoad() {
        mPresenter?.squareEliteList(params)
    }

    private fun initHeadBannerView(): View {
        val headBinding = HeaderviewRecommendBannerBinding.inflate(layoutInflater)
        headBinding.apply {
            ClickUtils.applySingleDebouncing(moreBtn) {
                startActivity<FindAllTagActivity>()
            }
            topicRv.layoutManager = LinearLayoutManager(activity!!, RecyclerView.HORIZONTAL, false)
            topicRv.adapter = topicAdapter
            topicAdapter.setOnItemClickListener { _, view, position ->
                ToastUtil.toast("$position")
            }
        }
        return headBinding.root
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        page++
        params["page"] = page
        mPresenter?.squareEliteList(params)

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
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

        if (binding.refreshFind.state != RefreshState.Loading) {
            if ((data?.banner ?: mutableListOf()).size == 0) {
                adapter.headerLayout?.isVisible = false
            } else {
                adapter.headerLayout?.isVisible = true
                topicAdapter.setNewInstance(data?.banner)
            }
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
        }

    }
}