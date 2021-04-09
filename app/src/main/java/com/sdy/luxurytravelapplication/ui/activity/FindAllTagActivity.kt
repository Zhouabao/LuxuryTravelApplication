package com.sdy.luxurytravelapplication.ui.activity

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ActivityUtils
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityFindAllTagBinding
import com.sdy.luxurytravelapplication.mvp.contract.FindAllTagContract
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareTagBean
import com.sdy.luxurytravelapplication.mvp.presenter.FindAllTagPresenter
import com.sdy.luxurytravelapplication.ui.adapter.TagSquareAdapter
import org.jetbrains.anko.startActivity

/**
 * 全部标签
 */
class FindAllTagActivity :
    BaseMvpActivity<FindAllTagContract.View, FindAllTagContract.Presenter, ActivityFindAllTagBinding>(),
    FindAllTagContract.View, OnRefreshListener {
    override fun createPresenter(): FindAllTagContract.Presenter = FindAllTagPresenter()

    //广场列表内容适配器
    private val adapter by lazy { TagSquareAdapter(4) }
    val layoutManager by lazy { LinearLayoutManager(this, RecyclerView.VERTICAL, false) }


    override fun initData() {
        binding.apply {
            barCl.actionbarTitle.text = "全部标签"
            barCl.btnBack.setOnClickListener {
                finish()
            }
            refreshTagSquare.setOnRefreshListener(this@FindAllTagActivity)
            rvTagSquare.layoutManager = layoutManager
            rvTagSquare.adapter = adapter
            adapter.headerWithEmptyEnable = false
            adapter.setEmptyView(R.layout.layout_empty_view)
            adapter.isUseEmpty = false
            adapter.setOnItemClickListener { _, view, position ->
//                if (UserManager.touristMode) {
//                    TouristDialog(activity!!).show()
//                } else
                    if (!ActivityUtils.isActivityExistsInStack(TagDetailCategoryActivity::class.java))
                        startActivity<TagDetailCategoryActivity>(
                            "id" to adapter.data[position].id,
                            "type" to TagDetailCategoryActivity.TYPE_TAG
                        )
            }

        }
    }

    override fun start() {
        mPresenter?.squareTagList()

    }

    override fun onGetSquareTagResult(data: MutableList<SquareTagBean>?, result: Boolean) {
        if (result) {
            if (binding.refreshTagSquare.state == RefreshState.Refreshing) {
                adapter.data.clear()
                adapter.notifyDataSetChanged()
                binding.rvTagSquare.scrollToPosition(0)
            }
            data?.forEach {
                it.cover_list.add(it.cover_list[0])
            }
            adapter.addData(data ?: mutableListOf())
        } else {
            mLayoutStatusView?.showError()
            adapter.notifyDataSetChanged()
        }
        binding.refreshTagSquare.finishRefresh(result)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPresenter?.squareTagList()

    }
}