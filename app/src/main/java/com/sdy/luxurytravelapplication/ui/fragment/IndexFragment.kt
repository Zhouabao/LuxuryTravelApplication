package com.sdy.luxurytravelapplication.ui.fragment

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.flyco.tablayout.listener.OnTabSelectListener
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpFragment
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.FragmentIndexBinding
import com.sdy.luxurytravelapplication.mvp.contract.IndexContract
import com.sdy.luxurytravelapplication.mvp.model.bean.IndexListBean
import com.sdy.luxurytravelapplication.mvp.model.bean.SweetProgressBean
import com.sdy.luxurytravelapplication.mvp.presenter.IndexPresenter
import com.sdy.luxurytravelapplication.ui.activity.JoinLuxuryActivity
import com.sdy.luxurytravelapplication.ui.adapter.PeopleRecommendTopAdapter


/**
 * 首页
 */
class IndexFragment :
    BaseMvpFragment<IndexContract.View, IndexContract.Presenter, FragmentIndexBinding>(),
    IndexContract.View {
    private val fragments by lazy {
        arrayListOf<Fragment>(
            IndexRecommendFragment(IndexRecommendFragment.TYPE_RECOMMEND),
            IndexRecommendFragment(IndexRecommendFragment.TYPE_SAME_CITY),
            IndexLuxuryFragment()
        )
    }
    private val titles by lazy {
        arrayOf(
            getString(R.string.tab_recommend),
            getString(R.string.tab_nearby),
            getString(R.string.tab_sweet)
        )
    }

    override fun createPresenter(): IndexContract.Presenter = IndexPresenter()
    private val peopleRecommendTopAdapter by lazy { PeopleRecommendTopAdapter() }

    override fun initView(view: View) {
        super.initView(view)

        binding.apply {
            recommendUsers.layoutManager =
                LinearLayoutManager(activity!!, RecyclerView.HORIZONTAL, false)
            recommendUsers.adapter = peopleRecommendTopAdapter
            titleIndex.setTabData(titles, activity!!, R.id.indexContent, fragments)
            titleIndex.setOnTabSelectListener(object : OnTabSelectListener {
                override fun onTabSelect(position: Int) {
                    addLuxuryCl.isVisible = position == 2
                }

                override fun onTabReselect(position: Int) {
                }

            })
            titleIndex.currentTab = 0

            ClickUtils.applySingleDebouncing(addLuxuryBtn) {
                JoinLuxuryActivity.startJoinLuxuxy(activity!!, SweetProgressBean())
            }
        }
    }

    override fun lazyLoad() {
        mPresenter?.indexTop(hashMapOf())
    }

    override fun indexTop(data: IndexListBean) {
        peopleRecommendTopAdapter.setNewInstance(data.list)
        UserManager.gender = data.gender
        if ((data.gender == 1 && data.isplatinumvip) || (data.gender == 2 && data.mv_url)) {
            binding.tobeSelectedBtn.isVisible = false
            val params = (binding.recommendUsers.layoutParams as ConstraintLayout.LayoutParams)
            params.leftMargin = SizeUtils.dp2px(5F)
        } else {
            binding.tobeSelectedBtn.isVisible = true
            binding.recommendUsers.scrollToPosition(1)
        }
    }
}