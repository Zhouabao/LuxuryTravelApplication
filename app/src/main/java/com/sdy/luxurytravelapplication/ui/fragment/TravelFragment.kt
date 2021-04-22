package com.sdy.luxurytravelapplication.ui.fragment

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SizeUtils
import com.google.android.material.appbar.AppBarLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sdy.luxurytravelapplication.base.BaseMvpFragment
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.UserManager
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
    private val datas by lazy { mutableListOf<TravelCityBean>() }

    private val params by lazy {
        hashMapOf(
            "pagesize" to Constants.PAGESIZE,
            "page" to 1,
            "goal_city" to ""
        )
    }

    override fun lazyLoad() {
        binding.apply {
            mLayoutStatusView = root
            ClickUtils.applySingleDebouncing(
                arrayOf(travelPublishBtn, collapseBtn, expandBtn),
                this@TravelFragment
            )

            refreshTravel.setOnRefreshLoadMoreListener(this@TravelFragment)

            travelPlanRv.layoutManager =
                LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
            travelPlanRv.adapter = travelAdapter


            travelCitys.layoutManager =
                CenterLayoutManager(activity!!, RecyclerView.HORIZONTAL, false)
            travelCitys.adapter = travelCityAdapter
            travelCitys1.layoutManager = GridLayoutManager(activity!!, 5)
            travelCitys1.adapter = travelCityAdapter1

            repeat(4) {
                UserManager.tempDatas.forEachIndexed { index, s ->
                    datas.add(
                        TravelCityBean(
                            "$index",
                            it * 13 + index * 10,
                            s,
                            it == 0 && index == 0
                        )
                    )
                }
            }
            travelCityAdapter.setNewInstance(datas)
            travelCityAdapter1.setNewInstance(datas)
            travelCityAdapter.setOnItemClickListener { _, view, position ->
                travelCityAdapter.data.forEach {
                    it.checked = it == travelCityAdapter.data[position]
                }
                travelCityAdapter.notifyDataSetChanged()
                travelCityAdapter1.notifyDataSetChanged()
            }
            travelCityAdapter1.setOnItemClickListener { _, view, position ->
                travelCityAdapter1.data.forEach {
                    it.checked = it == travelCityAdapter.data[position]
                }
                travelCitys.smoothScrollToPosition(position)
                travelCityAdapter1.notifyDataSetChanged()
                travelCityAdapter.notifyDataSetChanged()
            }

            userAppbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                LogUtils.d(
                    "verticalOffset===$verticalOffset,44F=${SizeUtils.dp2px(70F)},44F=${SizeUtils.dp2px(
                        44F
                    )},44F=${userAppbar.height}},44F=${toolbar.height}"
                )
                citysCl.alpha = 1 - abs(verticalOffset) * 1F / (userAppbar.height - toolbar.height)
            })
        }

        mLayoutStatusView?.showLoading()
        mPresenter?.planList(params)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.expandBtn -> {
                binding.travelListCl.isVisible = true
            }
            binding.collapseBtn -> {
                binding.travelListCl.isVisible = false
            }
            binding.travelPublishBtn -> {
                CommonFunction.checkPublishDating(activity!!)
//                mPresenter?.checkPlan()
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
            if (datas.size < Constants.PAGESIZE) {
                binding.refreshTravel.finishRefreshWithNoMoreData()
            } else {
                binding.refreshTravel.finishRefresh(success)
            }
        }
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