package com.sdy.luxurytravelapplication.ui.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ClickUtils
import com.sdy.luxurytravelapplication.base.BaseMvpFragment
import com.sdy.luxurytravelapplication.databinding.FragmentIndexLuxuryBinding
import com.sdy.luxurytravelapplication.mvp.contract.IndexLuxuryContract
import com.sdy.luxurytravelapplication.mvp.model.bean.SweetProgressBean
import com.sdy.luxurytravelapplication.mvp.presenter.IndexLuxuryPresenter
import com.sdy.luxurytravelapplication.ui.activity.JoinLuxuryActivity
import com.sdy.luxurytravelapplication.ui.adapter.IndexLuxuryAdapter


/**
 * 首页-推荐
 */
class IndexLuxuryFragment :
    BaseMvpFragment<IndexLuxuryContract.View, IndexLuxuryContract.Presenter, FragmentIndexLuxuryBinding>(),
    IndexLuxuryContract.View {

    override fun createPresenter(): IndexLuxuryContract.Presenter = IndexLuxuryPresenter()


    private val adapter by lazy { IndexLuxuryAdapter() }
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