package com.sdy.luxurytravelapplication.ui.fragment

import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sdy.luxurytravelapplication.base.BaseMvpFragment
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.EmptyMysquareLayoutBinding
import com.sdy.luxurytravelapplication.databinding.FragmentMyTravelBinding
import com.sdy.luxurytravelapplication.databinding.HeaderviewMyFragmentBinding
import com.sdy.luxurytravelapplication.event.DatingStopPlayEvent
import com.sdy.luxurytravelapplication.event.OneVoicePlayEvent
import com.sdy.luxurytravelapplication.mvp.contract.MyTravelContract
import com.sdy.luxurytravelapplication.mvp.model.bean.TravelCityBean
import com.sdy.luxurytravelapplication.mvp.model.bean.TravelPlanBean
import com.sdy.luxurytravelapplication.mvp.presenter.MyTravelPresenter
import com.sdy.luxurytravelapplication.nim.common.util.sys.TimeUtil
import com.sdy.luxurytravelapplication.ui.activity.PublishTravelActivity
import com.sdy.luxurytravelapplication.ui.activity.PublishTravelBeforeActivity
import com.sdy.luxurytravelapplication.ui.adapter.TravelAdapter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.startActivity
import java.util.*

/**
 * 我的伴游
 */
class MyTravelFragment :
    BaseMvpFragment<MyTravelContract.View, MyTravelContract.Presenter, FragmentMyTravelBinding>(),
    MyTravelContract.View, OnRefreshLoadMoreListener {
    override fun createPresenter(): MyTravelContract.Presenter = MyTravelPresenter()

    private val travelAdapter by lazy { TravelAdapter() }

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
            refreshTravel.setOnRefreshLoadMoreListener(this@MyTravelFragment)

            travelPlanRv.layoutManager =
                LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
            travelPlanRv.adapter = travelAdapter
            travelAdapter.addHeaderView(initHeadView())
            travelAdapter.setEmptyView(initEmptyView())
            travelAdapter.headerWithEmptyEnable = true
        }
    }

    override fun lazyLoad() {
        mLayoutStatusView?.showLoading()
        mPresenter?.planList(params)
    }


    private fun initEmptyView(): View {
        val emptyBinding = EmptyMysquareLayoutBinding.inflate(layoutInflater)

        emptyBinding.apply {
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            params.leftMargin = SizeUtils.dp2px(15F)
            params.topMargin = SizeUtils.dp2px(15F)
            params.bottomMargin = SizeUtils.dp2px(15F)
            root.layoutParams = params
            mySquareDate.text =
                TimeUtil.getNowDateTime("MM月dd日") + "，" + TimeUtil.getWeekOfDate(Date(System.currentTimeMillis()))

            ClickUtils.applySingleDebouncing(addSquareBtn) {
                mPresenter?.checkPlan()
            }
            addSqaureTv.text = "发布旅行计划"
            emptyTitle.text = "发个旅行计划吧"
        }

        return emptyBinding.root
    }

    private fun initHeadView(): View {
        val headBinding = HeaderviewMyFragmentBinding.inflate(layoutInflater)
        headBinding.apply {
            ClickUtils.applySingleDebouncing(root) {
                mPresenter?.checkPlan()
            }

        }
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            SizeUtils.dp2px(50F)
        )
        params.leftMargin = SizeUtils.dp2px(15F)
        params.rightMargin = SizeUtils.dp2px(15F)
        params.topMargin = SizeUtils.dp2px(15F)
        params.bottomMargin = SizeUtils.dp2px(15F)
        headBinding.root.layoutParams = params
        headBinding.publishBtn.text = "发布旅行计划"
        return headBinding.root
    }


    override fun checkPlan(result: Boolean) {
        if (result) {
            if (UserManager.isTipDating)
                PublishTravelActivity.start(activity!!)
            else
                startActivity<PublishTravelBeforeActivity>()
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

        if (travelAdapter.data.isEmpty()) {
            travelAdapter.isUseEmpty = true
            binding.refreshTravel.setEnableLoadMore(false)
        } else {
            binding.refreshTravel.setEnableLoadMore(true)
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
        page = 1
        params["page"] = page
        refreshLayout.resetNoMoreData()
        mPresenter?.planList(params)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateFindByTagEvent(eve: DatingStopPlayEvent) {
        travelAdapter.resetMyAudioViews()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDatingOnePlayEvent(eve: OneVoicePlayEvent) {
        travelAdapter.notifySomeOneAudioView(eve.playPosition)
    }

    override fun onStop() {
        super.onStop()
        travelAdapter.resetMyAudioViews()
    }

    override fun onPause() {
        super.onPause()
        travelAdapter.resetMyAudioViews()
    }

}