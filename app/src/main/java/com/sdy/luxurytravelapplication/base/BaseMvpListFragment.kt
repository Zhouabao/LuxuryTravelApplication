package com.sdy.luxurytravelapplication.base

import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sdy.luxurytravelapplication.widgets.SpaceItemDecoration

/**
 * @author chenxz
 * @date 2019/11/17
 * @desc BaseMvpListFragment
 */
abstract class BaseMvpListFragment<in V : IView, P : IPresenter<V>, VB : ViewBinding> :
    BaseMvpFragment<V, P, VB>(), OnRefreshLoadMoreListener {

    /**
     * 每页数据的个数
     */
    protected var pageSize = 20

    /**
     * 是否是下拉刷新
     */
    protected var isRefresh = true
    protected lateinit var swipeRefreshLayout: SmartRefreshLayout

    /**
     * LinearLayoutManager
     */
    protected val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(activity)
    }

    /**
     * RecyclerView Divider
     */
    private val recyclerViewItemDecoration by lazy {
        activity?.let {
            SpaceItemDecoration(it)
        }
    }

    /**
     * RefreshListener
     */
    protected val onRefreshListener = object :OnRefreshLoadMoreListener {
        override fun onLoadMore(refreshLayout: RefreshLayout) {
            isRefresh = false
            swipeRefreshLayout.finishRefresh()
            onLoadMoreList()
        }

        override fun onRefresh(refreshLayout: RefreshLayout) {
            isRefresh = true
            onRefreshList()
        }

    }
    /**
     * 下拉刷新
     */
    abstract fun onRefreshList()

    /**
     * 上拉加载更多
     */
    abstract fun onLoadMoreList()

    override fun initView(view: View) {
        super.initView(view)
        swipeRefreshLayout.run {
            setOnRefreshListener(onRefreshListener)
        }
//        recyclerView.run {
//            layoutManager = linearLayoutManager
//            itemAnimator = DefaultItemAnimator()
//            recyclerViewItemDecoration?.let { addItemDecoration(it) }
//        }

    }


    override fun showLoading() {

    }

    override fun hideLoading() {
        swipeRefreshLayout.finishRefresh()
    }

    override fun showError(errorMsg: String) {
        super.showError(errorMsg)
        mLayoutStatusView?.showError()
    }

}