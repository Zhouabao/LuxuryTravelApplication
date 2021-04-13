package com.sdy.luxurytravelapplication.ui.fragment

import android.view.View
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpFragment
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.FragmentFindContentBinding
import com.sdy.luxurytravelapplication.mvp.contract.MyCollectionAndLikeContract
import com.sdy.luxurytravelapplication.mvp.model.bean.RecommendSquareListBean
import com.sdy.luxurytravelapplication.mvp.presenter.MyCollectionAndLikePresenter
import com.sdy.luxurytravelapplication.ui.adapter.RecommendSquareAdapter


/**
 * 广场动态
 * 1,我的所有动态 2我点过赞的 3 我收藏的
 */
class MyCollectionAndLikeFragment(val type: Int) :
    BaseMvpFragment<MyCollectionAndLikeContract.View, MyCollectionAndLikeContract.Presenter, FragmentFindContentBinding>(),
    MyCollectionAndLikeContract.View, OnRefreshLoadMoreListener {
    override fun createPresenter(): MyCollectionAndLikeContract.Presenter =
        MyCollectionAndLikePresenter()

    companion object {
        const val TYPE_MINE = 1
        const val TYPE_LIKE = 2
        const val TYPE_COLLECT = 3
    }

    private val adapter by lazy { RecommendSquareAdapter() }
    private var page = 1

    //请求广场的参数 TODO要更新tagid
    private val params by lazy {
        hashMapOf<String, Any>(
            "page" to page,
            "pagesize" to Constants.PAGESIZE,
            "type" to type
        )
    }

    override fun initView(view: View) {
        super.initView(view)
        binding.apply {
            mLayoutStatusView = multipleStatusView
            refreshFind.setOnRefreshLoadMoreListener(this@MyCollectionAndLikeFragment)

            val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
            findRv.layoutManager = manager
            findRv.adapter = adapter
            adapter.setEmptyView(R.layout.layout_empty_view)
        }
    }

    override fun lazyLoad() {
        mPresenter?.getMySquare(params)
    }


    override fun onLoadMore(refreshLayout: RefreshLayout) {
        page++
        params["page"] = page
        mPresenter?.getMySquare(params)

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        page = 1
        params["page"] = page
        mPresenter?.getMySquare(params)
    }


    override fun onGetSquareListResult(data: RecommendSquareListBean?, b: Boolean) {
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
        }

    }
}