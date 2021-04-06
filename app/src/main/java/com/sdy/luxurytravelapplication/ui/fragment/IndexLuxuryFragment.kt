package com.sdy.luxurytravelapplication.ui.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sdy.luxurytravelapplication.base.BaseMvpFragment
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.FragmentIndexLuxuryBinding
import com.sdy.luxurytravelapplication.event.RefreshSweetAddEvent
import com.sdy.luxurytravelapplication.mvp.contract.IndexLuxuryContract
import com.sdy.luxurytravelapplication.mvp.model.bean.IndexRecommendBean
import com.sdy.luxurytravelapplication.mvp.model.bean.SweetProgressBean
import com.sdy.luxurytravelapplication.mvp.presenter.IndexLuxuryPresenter
import com.sdy.luxurytravelapplication.ui.adapter.IndexLuxuryAdapter
import org.greenrobot.eventbus.EventBus


/**
 * 首页-推荐
 */
class IndexLuxuryFragment :
    BaseMvpFragment<IndexLuxuryContract.View, IndexLuxuryContract.Presenter, FragmentIndexLuxuryBinding>(),
    IndexLuxuryContract.View, OnRefreshLoadMoreListener {

    override fun createPresenter(): IndexLuxuryContract.Presenter = IndexLuxuryPresenter()

    private val linearLayoutManager by lazy {
        LinearLayoutManager(
            requireActivity(),
            RecyclerView.VERTICAL,
            false
        )
    }
    private val adapter by lazy { IndexLuxuryAdapter() }
    private var isLoadingMore = false
    override fun initView(view: View) {
        super.initView(view)

        binding.apply {
            mLayoutStatusView = root
            refreshLuxury.setOnRefreshLoadMoreListener(this@IndexLuxuryFragment)
            luxuryRv.layoutManager =linearLayoutManager
            luxuryRv.adapter = adapter

            luxuryRv.addOnScrollListener(object :RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (isHoney) {
                        val lastVisible = linearLayoutManager.findLastVisibleItemPosition()
                        val total = linearLayoutManager.itemCount
                        if (lastVisible >= total - 5 && dy > 0) {
                            if (!isLoadingMore) {
                                if (adapter.data.size == Constants.PAGESIZE * page) {
                                    onLoadMore(binding.refreshLuxury)
                                    isLoadingMore = true
                                } else {
                                    binding.refreshLuxury.finishLoadMoreWithNoMoreData()
                                }
                            }
                        }
                    }
                }
            })
        }
    }

    override fun lazyLoad() {
        mLayoutStatusView?.showLoading()
        mPresenter?.sweetheart(params)

    }


    private var isHoney = false
    private lateinit var progressBean: SweetProgressBean

    override fun sweetheart(data: IndexRecommendBean?) {
        if (data != null) {
            mLayoutStatusView?.showContent()
            isHoney = data.is_honey
            progressBean = data.progress


            if (binding.refreshLuxury.state != RefreshState.Loading) {
                //更新奢旅圈状态
                EventBus.getDefault().post(RefreshSweetAddEvent(data.is_honey))
            }

            if (binding.refreshLuxury.state == RefreshState.Refreshing) {
                adapter.setNewInstance(data.list)
                binding.luxuryRv.smoothScrollToPosition(0)
                if (adapter.data.size < Constants.PAGESIZE * page)
                    binding.refreshLuxury.finishRefreshWithNoMoreData()
                else
                    binding.refreshLuxury.finishRefresh(true)
            } else {
                adapter.addData(data.list)
                if (adapter.data.size < Constants.PAGESIZE * page)
                    binding.refreshLuxury.finishLoadMoreWithNoMoreData()
                else
                    binding.refreshLuxury.finishLoadMore(true)
            }

            //保存 VIP信息
            UserManager.isvip = data.isvip
            //保存认证信息
            UserManager.isverify = data.isfaced
            //保存是否进行过人脸验证
            UserManager.hasFaceUrl = data.has_face_url
        } else {
            mLayoutStatusView?.showError()
        }
        isLoadingMore = false
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