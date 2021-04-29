package com.sdy.luxurytravelapplication.ui.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.LogUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpFragment
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.FragmentFindContentBinding
import com.sdy.luxurytravelapplication.databinding.HeaderviewRecommendBannerBinding
import com.sdy.luxurytravelapplication.event.AnnounceEvent
import com.sdy.luxurytravelapplication.event.RefreshDeleteSquareEvent
import com.sdy.luxurytravelapplication.event.RefreshLikeEvent
import com.sdy.luxurytravelapplication.mvp.contract.FindContentContract
import com.sdy.luxurytravelapplication.mvp.model.bean.RecommendSquareListBean
import com.sdy.luxurytravelapplication.mvp.presenter.FindContentPresenter
import com.sdy.luxurytravelapplication.ui.activity.FindAllTagActivity
import com.sdy.luxurytravelapplication.ui.activity.TagDetailCategoryActivity
import com.sdy.luxurytravelapplication.ui.adapter.RecommendSquareAdapter
import com.sdy.luxurytravelapplication.ui.adapter.SquareHeadTopicAdapter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.startActivity


/**
 * 广场动态
 */
class FindContentFragment(val type: Int = TYPE_RECOMMEND) :
    BaseMvpFragment<FindContentContract.View, FindContentContract.Presenter, FragmentFindContentBinding>(),
    FindContentContract.View, OnRefreshLoadMoreListener {
    override fun createPresenter(): FindContentContract.Presenter = FindContentPresenter()

    //    	1推荐 2附近 3最新 4喜欢  5我的点赞 6我的动态  7动态
    companion object {
        const val TYPE_RECOMMEND = 1
        const val TYPE_NEARBY = 2
        const val TYPE_NEWEST = 3

        const val TYPE_LIKE = 4
        const val TYPE_ZAN = 5
        const val TYPE_MINE = 6
        const val TYPE_COMMENT = 7
    }

    private val adapter by lazy { RecommendSquareAdapter() }
    private val topicAdapter by lazy { SquareHeadTopicAdapter() }
    private var page = 1
    private var isLoadingMore = false

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
            adapter.setEmptyView(R.layout.layout_empty_view)
            adapter.headerWithEmptyEnable = true
            findRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val range = intArrayOf(0, 0)
                    val first = intArrayOf(0, 0)
                    manager.findFirstVisibleItemPositions(first)
                    val last = intArrayOf(0, 0)
                    manager.findLastVisibleItemPositions(last)
                    range[1] = first[0]
                    last.sort()
                    range[1] = last.last()
                    LogUtils.d("first = ${first.contentToString()}")
                    LogUtils.d("last = ${last.contentToString()}")
                    LogUtils.d("range = ${range.contentToString()}")
                    val lastVisible = range[1] - range[0]
                    LogUtils.d("lastVisible = $lastVisible")
                    if (lastVisible >= adapter.data.size - 5 && dy > 0) {
                        if (!isLoadingMore) {
                            if (adapter.data.size == Constants.PAGESIZE * page) {
                                onLoadMore(refreshFind)
                                isLoadingMore = true
                            } else {
                                refreshFind.finishLoadMoreWithNoMoreData()
                            }
                        }
                    }
                }
            })
//            adapter.isUseEmpty = false
        }
    }

    override fun lazyLoad() {
        mPresenter?.squareEliteList(params, type)
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
                startActivity<TagDetailCategoryActivity>(
                    "id" to topicAdapter.data[position].id,
                    "type" to TagDetailCategoryActivity.TYPE_TAG
                )
            }
        }
        return headBinding.root
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        if (adapter.data.size < Constants.PAGESIZE * page) {
            refreshLayout.finishLoadMoreWithNoMoreData()
        } else {
            page++
            params["page"] = page
            mPresenter?.squareEliteList(params, type)
        }

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshLayout.resetNoMoreData()
        page = 1
        params["page"] = page
        mPresenter?.squareEliteList(params, type)
    }


    override fun onGetSquareRecommendResult(data: RecommendSquareListBean?, b: Boolean) {
        if (b) {
            mLayoutStatusView?.showContent()
        } else {
            mLayoutStatusView?.showError()
        }

        if (binding.refreshFind.state != RefreshState.Loading) {
            //android 瀑布流
            if (type == TYPE_RECOMMEND && !data?.banner.isNullOrEmpty()) {
                adapter.setHeaderView(initHeadBannerView())
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
        isLoadingMore = false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun monRefreshDeleteSquareEvent(event: RefreshDeleteSquareEvent) {
        binding.refreshFind.autoRefresh()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onAnnounceEvent(event: AnnounceEvent) {
        if (event.serverSuccess && TYPE_NEWEST == 3) {
            binding.refreshFind.autoRefresh()
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshLikeEvent(event: RefreshLikeEvent) {
        val dataPosition = event.position - adapter.headerLayoutCount
        adapter.getItem(event.position)
        if (event.position != -1 && event.squareId == adapter.data[dataPosition].id) {
            adapter.data[dataPosition].originalLike = event.isLike == 1
            adapter.data[dataPosition].isliked = event.isLike == 1
            adapter.data[dataPosition].like_cnt =
                if (event.likeCount >= 0) {
                    event.likeCount
                } else {
                    if (event.isLike == 1) {
                        adapter.data[dataPosition].like_cnt + 1
                    } else {
                        adapter.data[dataPosition].like_cnt - 1
                    }
                }
            adapter.notifyItemChanged(event.position)
        }
    }
}