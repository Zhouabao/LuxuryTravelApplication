package com.sdy.luxurytravelapplication.ui.fragment

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ClickUtils
import com.google.android.material.appbar.AppBarLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpFragment
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.FragmentTravelBinding
import com.sdy.luxurytravelapplication.event.DatingStopPlayEvent
import com.sdy.luxurytravelapplication.event.OneVoicePlayEvent
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.mvp.contract.TravelContract
import com.sdy.luxurytravelapplication.mvp.model.bean.TravelCityBean
import com.sdy.luxurytravelapplication.mvp.model.bean.TravelPlanBean
import com.sdy.luxurytravelapplication.mvp.presenter.TravelPresenter
import com.sdy.luxurytravelapplication.ui.adapter.TravelAdapter
import com.sdy.luxurytravelapplication.ui.adapter.TravelCityAdapter
import com.sdy.luxurytravelapplication.ui.adapter.TravelCityAdapterSmall
import com.sdy.luxurytravelapplication.widgets.CenterLayoutManager
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.abs

/**
 * 伴游
 */
class TravelFragment :
    BaseMvpFragment<TravelContract.View, TravelContract.Presenter, FragmentTravelBinding>(),
    TravelContract.View, View.OnClickListener, OnRefreshLoadMoreListener {
    override fun createPresenter(): TravelContract.Presenter = TravelPresenter()

    private val travelAdapter by lazy { TravelAdapter() }
    private val travelCityAdapter by lazy { TravelCityAdapter() }
    private val travelCityAdapter1 by lazy { TravelCityAdapter(true) }
    private val travelCityAdapter2 by lazy { TravelCityAdapterSmall() }

    private val params by lazy {
        hashMapOf<String, Any>(
            "pagesize" to Constants.PAGESIZE,
            "page" to 1
        )
    }

    override fun initView(view: View) {
        super.initView(view)
        binding.apply {
            mLayoutStatusView = root
            ClickUtils.applySingleDebouncing(
                arrayOf(travelPublishBtn, collapseBtn, expandBtn,expandBtn1),
                this@TravelFragment
            )

            refreshTravel.setOnRefreshLoadMoreListener(this@TravelFragment)

            travelPlanRv.layoutManager =
                LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
            travelPlanRv.adapter = travelAdapter
            travelAdapter.setEmptyView(R.layout.layout_empty_view)


            travelCitys.layoutManager =
                CenterLayoutManager(activity!!, RecyclerView.HORIZONTAL, false)
            travelCitys.adapter = travelCityAdapter
            travelCitys1.layoutManager = GridLayoutManager(activity!!, 5)
            travelCitys1.adapter = travelCityAdapter1

            travelCitys2.layoutManager =
                CenterLayoutManager(activity!!, RecyclerView.HORIZONTAL, false)
            travelCitys2.adapter = travelCityAdapter2

            travelCityAdapter.setOnItemClickListener { _, view, position ->
                travelCityAdapter.data.forEach {
                    it.checked = it == travelCityAdapter.data[position]
                }
                travelCityAdapter.notifyDataSetChanged()
                travelCityAdapter1.notifyDataSetChanged()
                travelCityAdapter2.notifyDataSetChanged()
                params["goal_city"] = travelCityAdapter.data[position].id
                binding.refreshTravel.autoRefresh()
            }
            travelCityAdapter1.setOnItemClickListener { _, view, position ->
                travelCityAdapter1.data.forEach {
                    it.checked = it == travelCityAdapter1.data[position]
                }
                travelCitys.smoothScrollToPosition(position)
                travelCityAdapter.notifyDataSetChanged()
                travelCityAdapter1.notifyDataSetChanged()
                travelCityAdapter2.notifyDataSetChanged()
                params["goal_city"] = travelCityAdapter.data[position].id
                binding.refreshTravel.autoRefresh()
            }
            travelCityAdapter2.setOnItemClickListener { _, view, position ->
                travelCityAdapter2.data.forEach {
                    it.checked = it == travelCityAdapter2.data[position]
                }
                travelCitys.smoothScrollToPosition(position)
                travelCityAdapter.notifyDataSetChanged()
                travelCityAdapter1.notifyDataSetChanged()
                travelCityAdapter2.notifyDataSetChanged()
                params["goal_city"] = travelCityAdapter.data[position].id
                binding.refreshTravel.autoRefresh()
            }

            userAppbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                citysCl.alpha = 1 - abs(verticalOffset) * 1F / (userAppbar.height - toolbar.height)
                travelCitys2.isVisible = citysCl.alpha == 0f
                expandBtn1.isVisible = citysCl.alpha == 0f
                divider.isVisible = citysCl.alpha == 0f
            })
        }

    }

    override fun lazyLoad() {
        mPresenter?.getMenuList()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.expandBtn,binding.expandBtn1 -> {
                binding.travelListCl.isVisible = true
            }
            binding.collapseBtn -> {
                binding.travelListCl.isVisible = false
            }
            binding.travelPublishBtn -> {
                CommonFunction.checkPublishDating(activity!!)
            }
        }
    }


    override fun planList(success: Boolean, datas: MutableList<TravelPlanBean>) {
        if (success) {
            mLayoutStatusView?.showContent()
        } else {
            mLayoutStatusView?.showError()
        }
        if (binding.refreshTravel.state == RefreshState.Loading) {
            travelAdapter.addData(datas)
            if (datas.size < Constants.PAGESIZE) {
                binding.refreshTravel.finishLoadMoreWithNoMoreData()
            } else {
                binding.refreshTravel.finishLoadMore(success)
            }
        } else {
            travelAdapter.setNewInstance(datas)
            binding.travelPlanRv.smoothScrollToPosition(0)
            if (datas.size < Constants.PAGESIZE) {
                binding.refreshTravel.finishRefreshWithNoMoreData()
            } else {
                binding.refreshTravel.finishRefresh(success)
            }
        }
    }

    override fun getMenuList(datas: MutableList<TravelCityBean>) {
        if (datas.isNotEmpty()) {
            datas[0].checked = true
            params["goal_city"] = datas[0].id
        }
        travelCityAdapter.setNewInstance(datas)
        travelCityAdapter1.setNewInstance(datas)
        travelCityAdapter2.setNewInstance(datas)
        mPresenter?.planList(params)
    }

    private var page = 1
    override fun onLoadMore(refreshLayout: RefreshLayout) {
        if (travelAdapter.data.size < Constants.PAGESIZE * page) {
            refreshLayout.finishLoadMoreWithNoMoreData()
        } else {
            page++
            params["page"] = page
            mPresenter?.planList(params)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        travelAdapter.resetMyAudioViews()
        page = 1
        params["page"] = page
        refreshLayout.resetNoMoreData()
        mPresenter?.planList(params)
    }

    override fun onStop() {
        super.onStop()
        travelAdapter.resetMyAudioViews()
    }

    override fun onPause() {
        super.onPause()
        travelAdapter.resetMyAudioViews()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateFindByTagEvent(eve: DatingStopPlayEvent) {
        travelAdapter.resetMyAudioViews()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDatingOnePlayEvent(eve: OneVoicePlayEvent) {
        travelAdapter.notifySomeOneAudioView(eve.playPosition)
    }
}