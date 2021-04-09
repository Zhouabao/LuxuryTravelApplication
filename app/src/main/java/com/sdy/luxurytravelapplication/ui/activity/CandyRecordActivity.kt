package com.sdy.luxurytravelapplication.ui.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.ActivityCandyRecordBinding
import com.sdy.luxurytravelapplication.databinding.EmptyFriendLayoutBinding
import com.sdy.luxurytravelapplication.mvp.contract.CandyRecordContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BillBean
import com.sdy.luxurytravelapplication.mvp.presenter.CandyRecordPresenter
import com.sdy.luxurytravelapplication.ui.adapter.CandyRecordAdapter

/**
 * 糖果交易记录
 */
class CandyRecordActivity :
    BaseMvpActivity<CandyRecordContract.View, CandyRecordContract.Presenter, ActivityCandyRecordBinding>(),
    CandyRecordContract.View, OnRefreshLoadMoreListener {

    override fun createPresenter(): CandyRecordContract.Presenter {
        return CandyRecordPresenter()
    }

    private val candyProductAdapter by lazy { CandyRecordAdapter() }

    override fun initData() {
        binding.apply {
            barCl.btnBack.setOnClickListener {
                finish()
            }
            barCl.actionbarTitle.text = "交易记录"
            refreshRecord.setOnRefreshLoadMoreListener(this@CandyRecordActivity)
            rvRecord.layoutManager =
                LinearLayoutManager(this@CandyRecordActivity, RecyclerView.VERTICAL, false)
            rvRecord.adapter = candyProductAdapter
            candyProductAdapter.setEmptyView(R.layout.layout_empty_view)

        }
    }

    private var page = 1
    private val params by lazy {
        hashMapOf<String, Any>(
            "page" to page,
            "pagesize" to Constants.PAGESIZE
        )
    }

    override fun start() {
        mPresenter?.myBillList(params)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        if (candyProductAdapter.data.size < Constants.PAGESIZE * page) {
            refreshLayout.finishLoadMoreWithNoMoreData()
        } else {
            page++
            params["page"] = page
            mPresenter?.myBillList(params)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        page = 1
        params["page"] = page
        refreshLayout.resetNoMoreData()
        mPresenter?.myBillList(params)
    }

    override fun onMyBillList(success: Boolean, billList: MutableList<BillBean>) {
        if (binding.refreshRecord.state == RefreshState.Loading) {
            if (success && billList.size < Constants.PAGESIZE) {
                binding.refreshRecord.finishLoadMoreWithNoMoreData()
            } else {
                binding.refreshRecord.finishLoadMore(success)
            }
            candyProductAdapter.addData(billList)
        } else {
            candyProductAdapter.setNewInstance(billList)
            binding.refreshRecord.finishRefresh(success)
        }

    }
}