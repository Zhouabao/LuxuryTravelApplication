package com.sdy.luxurytravelapplication.ui.activity

import android.os.Build
import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.google.android.material.appbar.AppBarLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.ActivityTagDetailCategoryBinding
import com.sdy.luxurytravelapplication.event.RefreshLikeEvent
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.contract.TagDetailCategoryContract
import com.sdy.luxurytravelapplication.mvp.model.bean.TagSquareListBean
import com.sdy.luxurytravelapplication.mvp.presenter.TagDetailCategoryPresenter
import com.sdy.luxurytravelapplication.ui.adapter.RecommendSquareAdapter
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.abs

/**
 * 标签或者话题分类详情
 *
 * 标签分类带了标签
 *
 * 话题分类带了话题
 *
 * type 1兴趣的 2话题
 */
class TagDetailCategoryActivity :
    BaseMvpActivity<TagDetailCategoryContract.View, TagDetailCategoryContract.Presenter, ActivityTagDetailCategoryBinding>(),
    TagDetailCategoryContract.View, OnRefreshListener, OnLoadMoreListener {
    override fun createPresenter(): TagDetailCategoryContract.Presenter =
        TagDetailCategoryPresenter()

    private var page: Int = 1
    private val adapter by lazy { RecommendSquareAdapter() }
    private val id by lazy { intent.getIntExtra("id", 0) }
    private val type by lazy { intent.getIntExtra("type", 0) }

    companion object {
        val TYPE_TAG = 2
        val TYPE_TOPIC = 2
    }

    //请求广场的参数 TODO要更新tagid
    private val params by lazy {
        hashMapOf<String, Any>(
            "page" to page,
            "id" to id,
            "type" to type,
            "pagesize" to Constants.PAGESIZE
        )
    }


    override fun initData() {
        binding.apply {
            mLayoutStatusView = root
            samePersonTitle.textSize = if (type == TYPE_TAG) {
                24F
            } else {
                18F
            }

            refreshSamePerson.setOnRefreshListener(this@TagDetailCategoryActivity)
            refreshSamePerson.setOnLoadMoreListener(this@TagDetailCategoryActivity)
            refreshSamePerson.setPrimaryColorsId(R.color.colorTransparent)
            refreshHeader.setColorSchemeColors(resources.getColor(R.color.colorAccent))


            btnBack.setOnClickListener {
                finish()
            }
            btnBack1.setOnClickListener {
                finish()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                (statusView.layoutParams as RelativeLayout.LayoutParams).height =
                    BarUtils.getStatusBarHeight()
            } else {
                statusView.isVisible = false
            }

            rightBtn1.isVisible = true
            ClickUtils.applySingleDebouncing(arrayOf(rightBtn1, publish)) {
                mPresenter?.checkBlock()
            }


            sameAppbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { p0, verticalOffset ->
                llTitle.isVisible =
                    abs(verticalOffset) >= SizeUtils.dp2px(56F) + BarUtils.getStatusBarHeight()

                if (llTitle.isVisible) {
                    llSame.visibility = View.INVISIBLE
                    samePersonBg.isVisible = false
                    samePersonBg1.isVisible = false
                    btnBack.isVisible = false
                } else {
                    btnBack.isVisible = true
                    llSame.visibility = View.VISIBLE
                    samePersonBg.isVisible = true
                    samePersonBg1.isVisible = true

                }
            })


            samePersonRv.setHasFixedSize(true)
            val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            manager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
            samePersonRv.layoutManager = manager
            samePersonRv.adapter = adapter
            adapter.setEmptyView(R.layout.layout_empty_view)
        }


    }

    override fun start() {
        mLayoutStatusView?.showLoading()
        mPresenter?.squareTagInfo(params)
    }

    override fun onGetSquareRecommendResult(data: TagSquareListBean?, b: Boolean) {
        if (!b) {
            mLayoutStatusView?.showError()
        } else {
            mLayoutStatusView?.showContent()
        }

        if (binding.refreshSamePerson.state != RefreshState.Loading) {
            data?.banner?.let {
                GlideUtil.loadRoundImgCenterCrop(
                    this,
                    it.icon,
                    binding.samePersonBg,
                    SizeUtils.dp2px(20F),
                    RoundedCornersTransformation.CornerType.BOTTOM
                )
                binding.samePersonTitle.text = it.title
                binding.joinCnt.text = "${it.visit_cnt}人参与·${it.used_cnt}条帖子"
                binding.hotT1.text = it.title
            }
            adapter.data.clear()
        }

        if (binding.refreshSamePerson.state == RefreshState.Refreshing) {
            adapter.notifyDataSetChanged()
            binding.samePersonRv.scrollToPosition(0)
        }

        adapter.addData(data?.list ?: mutableListOf())
        binding.refreshSamePerson.finishRefresh(b)
        if ((data?.list ?: mutableListOf()).isNullOrEmpty()) {
            binding.refreshSamePerson.finishLoadMoreWithNoMoreData()
        } else {
            binding.refreshSamePerson.finishLoadMore(b)
        }


    }

    override fun onCheckBlockResult(b: Boolean) {
        if (b) {
            PublishActivity.startToPublish(this, binding.samePersonTitle.text.toString())
            finish()
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        page = 1
        params["page"] = page
        mPresenter?.squareTagInfo(params)

    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        page++
        params["page"] = page
        mPresenter?.squareTagInfo(params)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshLikeEvent(event: RefreshLikeEvent) {
        if (event.position != -1 && event.squareId == adapter.data[event.position].id) {
            adapter.data[event.position].originalLike = event.isLike == 1
            adapter.data[event.position].isliked = event.isLike == 1
            adapter.data[event.position].like_cnt =
                if (event.likeCount >= 0) {
                    event.likeCount
                } else {
                    if (event.isLike == 1) {
                        adapter.data[event.position].like_cnt + 1
                    } else {
                        adapter.data[event.position].like_cnt - 1
                    }
                }


            adapter.notifyItemChanged(event.position)
        }
    }
}