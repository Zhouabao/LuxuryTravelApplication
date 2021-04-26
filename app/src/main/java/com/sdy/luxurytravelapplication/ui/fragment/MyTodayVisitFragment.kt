package com.sdy.luxurytravelapplication.ui.fragment

import android.view.View
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.SizeUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpFragment
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.FragmentMyTodayVisitBinding
import com.sdy.luxurytravelapplication.databinding.HeadItemVisitBinding
import com.sdy.luxurytravelapplication.databinding.HeadviewItemTodayVisitBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.mvp.contract.MyTodayVisitContract
import com.sdy.luxurytravelapplication.mvp.model.bean.VisitorBean
import com.sdy.luxurytravelapplication.mvp.presenter.MyTodayVisitPresenter
import com.sdy.luxurytravelapplication.ui.activity.TargetUserActivity
import com.sdy.luxurytravelapplication.ui.adapter.MyTodayVisitAdater


class MyTodayVisitFragment(
    val freeShow: Boolean,
    val from: Int = FROM_ME,
    val today: Int = 0,
    val todayExplosure: Int = 0,
    val all: Int = 0
) :
    BaseMvpFragment<MyTodayVisitContract.View, MyTodayVisitContract.Presenter, FragmentMyTodayVisitBinding>(),
    MyTodayVisitContract.View,
    OnRefreshListener,
    OnLoadMoreListener {

    companion object {
        const val FROM_TODAY = 1
        const val FROM_ME = 2
        const val FROM_TOP_RECOMMEND = 3
    }

    private var page = 1

    //    private val visitAdapter by lazy { MyVisitAdater(intent.getBooleanExtra("freeShow", false)) }
    private val visitAdapter by lazy { MyTodayVisitAdater(freeShow) }
    private val params by lazy {
        hashMapOf<String, Any>(
            "pagesize" to Constants.PAGESIZE,
            "page" to page,
            "type" to from
        )
    }

    override fun createPresenter(): MyTodayVisitContract.Presenter {
        return MyTodayVisitPresenter()

    }

    override fun lazyLoad() {
        mPresenter?.myVisitedList(params)
    }

    override fun initView(view: View) {
        super.initView(view)
        binding.apply {
            refreshLayout.setOnRefreshListener(this@MyTodayVisitFragment)
            refreshLayout.setOnLoadMoreListener(this@MyTodayVisitFragment)

            visitRv.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
            visitRv.adapter = visitAdapter
            visitAdapter.setEmptyView(R.layout.layout_empty_view)

            if (from == FROM_TODAY) {
                visitAdapter.addHeaderView(initHeadViewToday())
            } else {
                visitAdapter.addHeaderView(initHeadViewAll())
            }
            visitAdapter.headerWithEmptyEnable = true
            visitAdapter.isUseEmpty = false

            lockToSee.setOnClickListener {
                CommonFunction.startToVip(activity!!)

            }

            visitAdapter.setOnItemClickListener { _, view, position ->
                if (visitAdapter.freeShow && UserManager.accid != visitAdapter.data[position].accid)
                    TargetUserActivity.start(activity!!, visitAdapter.data[position].accid ?: "")
            }

        }
    }

    private fun initHeadViewAll(): View {
        val headBinding = HeadItemVisitBinding.inflate(layoutInflater)
        headBinding.apply {
            visitTodayCount.text = getString(R.string.today_visit1, today)
            visitAllCount.text = getString(R.string.all_visit_1, all)
        }
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params.topMargin = SizeUtils.dp2px(20F)
        headBinding.root.layoutParams = params
        return headBinding.root
    }

    private fun initHeadViewToday(): View {
        val headBinding = HeadviewItemTodayVisitBinding.inflate(layoutInflater)
        headBinding.apply {
            visitTodayCount.text = getString(R.string.today_expose, todayExplosure)
        }
        return headBinding.root
    }


    override fun onLoadMore(refreshLayout: RefreshLayout) {
        if (visitAdapter.data.size < Constants.PAGESIZE * page) {
            refreshLayout.finishLoadMoreWithNoMoreData()
        } else {
            page++
            params["page"] = page
            mPresenter?.myVisitedList(params)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        visitAdapter.data.clear()
        refreshLayout.setNoMoreData(false)
        page = 1
        params["page"] = page
        mPresenter?.myVisitedList(params)
    }


    override fun onMyVisitResult(visitor: MutableList<VisitorBean>?) {
        binding.apply {
            if (refreshLayout.state == RefreshState.Loading) {
                if (visitor != null && visitor.size < Constants.PAGESIZE * page) {
                    refreshLayout.finishRefreshWithNoMoreData()
                } else {
                    refreshLayout.finishLoadMore(visitor != null)
                }
                visitAdapter.addData(visitor ?: mutableListOf())
            } else {
                if (visitor != null && visitor.size < Constants.PAGESIZE * page) {
                    refreshLayout.finishRefreshWithNoMoreData()
                } else {
                    refreshLayout.finishLoadMore(visitor != null)
                }
                visitAdapter.setNewInstance(visitor ?: mutableListOf())
            }

            if (visitAdapter.data.isEmpty()) {
                if (!freeShow) {
                    lockToSee.isVisible = false
                }
                visitAdapter.isUseEmpty = true
            } else {
                lockToSee.isVisible = !visitAdapter.freeShow
            }

        }

    }

}