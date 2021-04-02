package com.sdy.luxurytravelapplication.ui.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sdy.luxurytravelapplication.base.BaseMvpFragment
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.FragmentIndexLuxuryBinding
import com.sdy.luxurytravelapplication.mvp.contract.IndexLuxuryContract
import com.sdy.luxurytravelapplication.mvp.model.bean.IndexRecommendBean
import com.sdy.luxurytravelapplication.mvp.presenter.IndexLuxuryPresenter
import com.sdy.luxurytravelapplication.ui.adapter.IndexLuxuryAdapter
import com.sdy.luxurytravelapplication.ui.adapter.IndexRecommendAdapter


/**
 * 首页-推荐
 */
class IndexLuxuryFragment :
    BaseMvpFragment<IndexLuxuryContract.View, IndexLuxuryContract.Presenter, FragmentIndexLuxuryBinding>(),
    IndexLuxuryContract.View, OnRefreshLoadMoreListener {

    override fun createPresenter(): IndexLuxuryContract.Presenter = IndexLuxuryPresenter()


    private val adapter by lazy { IndexLuxuryAdapter() }
    override fun initView(view: View) {
        super.initView(view)

        binding.apply {
            mLayoutStatusView = root
            swipeRefreshLayout.setOnRefreshLoadMoreListener(this@IndexLuxuryFragment)
            recyclerView.layoutManager =
                LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
            recyclerView.adapter = adapter
        }
    }

    override fun lazyLoad() {
        mLayoutStatusView?.showLoading()
        mPresenter?.sweetheart(params)

    }

    override fun sweetheart(data: IndexRecommendBean?) {

    }

    private var page = 1
    private val params by lazy {
        hashMapOf<String, Any>(
            "pagesize" to Constants.PAGESIZE,
            "page" to page
        )
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        if (adapter.data.size < Constants.PAGESIZE * page) {
            refreshLayout.finishLoadMoreWithNoMoreData()
        } else {
            page++
            params["page"] = page
            mPresenter?.sweetheart(params)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        page = 1
        params["page"] = page
        refreshLayout.resetNoMoreData()
        mPresenter?.sweetheart(params)
    }
}