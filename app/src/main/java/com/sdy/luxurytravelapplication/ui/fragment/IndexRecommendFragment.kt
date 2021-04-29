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
import com.sdy.luxurytravelapplication.databinding.FragmentIndexRecommendBinding
import com.sdy.luxurytravelapplication.mvp.contract.IndexRecommendContract
import com.sdy.luxurytravelapplication.mvp.model.bean.IndexRecommendBean
import com.sdy.luxurytravelapplication.mvp.model.bean.TodayFateBean
import com.sdy.luxurytravelapplication.mvp.presenter.IndexRecommendPresenter
import com.sdy.luxurytravelapplication.ui.adapter.IndexRecommendAdapter
import com.sdy.luxurytravelapplication.ui.dialog.CompleteInfoDialog
import com.sdy.luxurytravelapplication.ui.dialog.TodayFateWomanDialog


/**
 * 首页-推荐
 */
class IndexRecommendFragment(val type: Int = TYPE_RECOMMEND) :
    BaseMvpFragment<IndexRecommendContract.View, IndexRecommendContract.Presenter, FragmentIndexRecommendBinding>(),
    IndexRecommendContract.View, OnRefreshLoadMoreListener {
    companion object {
        const val TYPE_RECOMMEND = 1
        const val TYPE_SAME_CITY = 2
    }

    override fun createPresenter(): IndexRecommendContract.Presenter = IndexRecommendPresenter()


    private val adapter by lazy { IndexRecommendAdapter() }


    private var page = 1
    private val params by lazy {
        hashMapOf<String, Any>(
            "pagesize" to Constants.PAGESIZE,
            "page" to page
        )
    }

    override fun initView(view: View) {
        super.initView(view)
        binding.apply {
            mLayoutStatusView = root
            refreshIndex.setOnRefreshLoadMoreListener(this@IndexRecommendFragment)
            recyclerView.layoutManager =
                LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
            recyclerView.adapter = adapter
        }
    }

    override fun lazyLoad() {
        if (type == TYPE_RECOMMEND) {
            mPresenter?.todayRecommend()
        } else
            mPresenter?.recommendIndex(params, type)
    }

    private var showComplete = false
    override fun recommendIndex(indexRecommendBean: IndexRecommendBean?) {
        if (indexRecommendBean == null) {
            if (binding.refreshIndex.state == RefreshState.Refreshing) {
                binding.refreshIndex.finishRefresh(false)
            } else if (binding.refreshIndex.state == RefreshState.Loading) {
                binding.refreshIndex.finishLoadMore(false)
            } else {
                mLayoutStatusView?.showError()
            }
        } else {
            //保存 VIP信息
            UserManager.isvip = indexRecommendBean.isplatinum
            //保存认证信息
            UserManager.isverify = indexRecommendBean.isfaced
            //保存是否进行过人脸验证
            UserManager.hasFaceUrl = indexRecommendBean.has_face_url

            if (binding.refreshIndex.state == RefreshState.Refreshing) {
                binding.refreshIndex.finishRefresh(true)
                adapter.setNewInstance(indexRecommendBean.list)
            } else if (binding.refreshIndex.state == RefreshState.Loading) {
                adapter.addData(indexRecommendBean.list)
                if (adapter.data.size < Constants.PAGESIZE * page) {
                    binding.refreshIndex.finishLoadMoreWithNoMoreData()
                } else {
                    binding.refreshIndex.finishLoadMore(false)
                }
            } else {
                mLayoutStatusView?.showContent()
                adapter.setNewInstance(indexRecommendBean.list)
            }
            if (type == TYPE_RECOMMEND && !(UserManager.getAccountDanger() || UserManager.getAccountDangerAvatorNotPass())) {
                if (indexRecommendBean.complete_percent < indexRecommendBean.complete_percent_normal && !showComplete) {
                    CompleteInfoDialog(indexRecommendBean,indexRecommends).show()
                    showComplete = true
                } else if (!indexRecommends?.list.isNullOrEmpty() && indexRecommends?.today_pull == false && !UserManager.showIndexRecommend) {
                    if (UserManager.gender == 2) {
                        TodayFateWomanDialog(indexRecommendBean, indexRecommends!!).show()
                    }
                }
            }
        }

    }

    private var indexRecommends: TodayFateBean? = null
    override fun onTodayRecommendResult(data: TodayFateBean?) {
        if (data != null) {
            indexRecommends = data
            mPresenter?.recommendIndex(params, type)
        } else {
            mLayoutStatusView?.showError()
        }

    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        if (adapter.data.size < Constants.PAGESIZE * page) {
            refreshLayout.finishLoadMoreWithNoMoreData()
        } else {
            page++
            params["page"] = page
            mPresenter?.recommendIndex(params, type)
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        page = 1
        params["page"] = page
        refreshLayout.resetNoMoreData()
        mPresenter?.recommendIndex(params, type)
    }

}