package com.sdy.luxurytravelapplication.ui.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sdy.luxurytravelapplication.base.BaseMvpFragment
import com.sdy.luxurytravelapplication.databinding.FragmentIndexRecommendBinding
import com.sdy.luxurytravelapplication.mvp.contract.IndexRecommendContract
import com.sdy.luxurytravelapplication.mvp.presenter.IndexRecommendPresenter
import com.sdy.luxurytravelapplication.ui.adapter.IndexRecommendAdapter


/**
 * 首页-推荐
 */
class IndexRecommendFragment(val type: Int = TYPE_RECOMMEND) :
    BaseMvpFragment<IndexRecommendContract.View, IndexRecommendContract.Presenter, FragmentIndexRecommendBinding>(),
    IndexRecommendContract.View {
    companion object {
        const val TYPE_RECOMMEND = 1
        const val TYPE_SAME_CITY = 2
    }

    override fun createPresenter(): IndexRecommendContract.Presenter = IndexRecommendPresenter()


    private val adapter by lazy { IndexRecommendAdapter() }
    override fun initView(view: View) {
        super.initView(view)

        binding.apply {
            recyclerView.layoutManager =
                LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
            recyclerView.adapter = adapter
        }
        adapter.setNewInstance(arrayListOf("", "", "", ""))


    }

    override fun lazyLoad() {


    }

}