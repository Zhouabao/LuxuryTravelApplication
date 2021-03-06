package com.sdy.luxurytravelapplication.ui.fragment

import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.flyco.tablayout.listener.OnTabSelectListener
import com.google.android.material.appbar.AppBarLayout
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpFragment
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.FragmentIndexBinding
import com.sdy.luxurytravelapplication.event.RefreshSweetAddEvent
import com.sdy.luxurytravelapplication.event.UpdateFeaturedEvent
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.contract.IndexContract
import com.sdy.luxurytravelapplication.mvp.model.bean.IndexListBean
import com.sdy.luxurytravelapplication.mvp.model.bean.SweetProgressBean
import com.sdy.luxurytravelapplication.mvp.presenter.IndexPresenter
import com.sdy.luxurytravelapplication.ui.activity.JoinLuxuryActivity
import com.sdy.luxurytravelapplication.ui.adapter.MainPager2Adapter
import com.sdy.luxurytravelapplication.ui.adapter.PeopleRecommendTopAdapter
import com.sdy.luxurytravelapplication.ui.dialog.ToBeSelectedDialog
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * 首页
 */
class IndexFragment :
    BaseMvpFragment<IndexContract.View, IndexContract.Presenter, FragmentIndexBinding>(),
    IndexContract.View, View.OnClickListener {
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
            BarUtils.addMarginTopEqualStatusBarHeight(indexCl)
            recommendUsers.layoutManager =
                LinearLayoutManager(activity!!, RecyclerView.HORIZONTAL, false)
            recommendUsers.adapter = peopleRecommendTopAdapter
            titleIndex.setTabData(titles)
            vpIndex.adapter = MainPager2Adapter(activity!!, fragments)
            vpIndex.isUserInputEnabled = false
            vpIndex.offscreenPageLimit = titles.size
//            titleIndex.setTabData(titles, activity!!, R.id.indexContent, fragments)
            titleIndex.setOnTabSelectListener(object : OnTabSelectListener {
                override fun onTabSelect(position: Int) {
                    addLuxuryCl.isVisible = position == 2 && !isHoney && isInitialize
                    vpIndex.currentItem = position
                }

                override fun onTabReselect(position: Int) {
                }

            })
            titleIndex.currentTab = 0
            vpIndex.currentItem = 0

            ClickUtils.applySingleDebouncing(
                arrayOf(addLuxuryBtn, tobeSelectedBtn),
                this@IndexFragment
            )
        }
    }

    override fun lazyLoad() {
        mPresenter?.indexTop(hashMapOf())
    }


    private lateinit var indexListBean: IndexListBean
    var isPlatinumVip = false
    var mvUrl = false
    override fun indexTop(data: IndexListBean) {
        this.indexListBean = data
        peopleRecommendTopAdapter.setNewInstance(data.list)
        UserManager.gender = data.gender
        if ((data.gender == 1 && data.isplatinumvip) || (data.gender == 2 && data.mv_url)) {
            binding.tobeSelectedBtn.isVisible = true
            binding.tobeSelectedSubscript.isVisible = true
            isPlatinumVip = data.isplatinumvip
            mvUrl = data.mv_url
            GlideUtil.loadRoundImgCenterCrop(
                    activity!!,
                    UserManager.avatar,
                    binding.tobeSelectedBtn,
                    SizeUtils.dp2px(15f)
            )
            binding.alreadyFeaturedAnimation.isVisible = true
            binding.alreadyFeaturedAnimation.playAnimation()

//            val params = (binding.recommendUsers.layoutParams as ConstraintLayout.LayoutParams)
//            params.leftMargin = SizeUtils.dp2px(5F)
        } else {
            binding.tobeSelectedBtn.isVisible = true
            binding.alreadyFeaturedAnimation.isVisible = false
            binding.tobeSelectedSubscript.isVisible = false
            binding.recommendUsers.scrollToPosition(1)
        }
    }

    override fun onClick(v: View) {
        when (v) {
            binding.addLuxuryBtn -> {
                JoinLuxuryActivity.startJoinLuxuxy(activity!!, sweetProgressBean)
            }
            binding.tobeSelectedBtn -> {
                if ((UserManager.gender == 1 && isPlatinumVip) || (UserManager.gender == 2 && mvUrl)) {
                    ToBeSelectedDialog(true,indexListBean).show()
                }else{
                    ToBeSelectedDialog(false,indexListBean).show()
                }

            }
        }

    }

    /**
     * 刷新加入奢旅圈显示
     */
    private var isHoney = false
    private var isInitialize = false
    private lateinit var sweetProgressBean: SweetProgressBean
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTopCardEvent(event: RefreshSweetAddEvent) {
        isInitialize = true
        isHoney = event.isHoney
        this.sweetProgressBean = event.sweetProgressBean
        binding.addLuxuryCl.isVisible = !isHoney && binding.vpIndex.currentItem == 2
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateFeaturedEvent(event: UpdateFeaturedEvent) {
        mPresenter?.indexTop(hashMapOf())
    }


}