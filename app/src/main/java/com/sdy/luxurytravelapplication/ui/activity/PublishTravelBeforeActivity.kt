package com.sdy.luxurytravelapplication.ui.activity

import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityPublishTravelBeforeBinding
import com.sdy.luxurytravelapplication.mvp.model.bean.BannerGuideBean
import com.sdy.luxurytravelapplication.ui.adapter.GuideTravelBannerAdapter
import com.zhpan.bannerview.BannerViewPager

/**
 * 发布旅行规则
 */
class PublishTravelBeforeActivity : BaseActivity<ActivityPublishTravelBeforeBinding>() {
    override fun initData() {

    }

    private val guideBannerAdapter by lazy { GuideTravelBannerAdapter() }
    private val data by lazy {
        mutableListOf<BannerGuideBean>(
            BannerGuideBean(
                R.drawable.icon_travel_before1,
                "旅行计划需真实可信",
                "确保活动真实，意向明确，并在描述处清晰的注明自己的时间及要求，潦草的编写可能不会通过"
            ),
            BannerGuideBean(
                R.drawable.icon_travel_before2,
                "聊天/内容绿色不违规",
                "杜绝色情、招嫖、广告等违规信息，违者封号处理"
            ),
            BannerGuideBean(
                R.drawable.icon_travel_before3,
                "对报名者主动一些",
                "请以友善的态度对待主动向你报名的用户，并针对旅行内容进行讨论和沟通，不要谩骂、侮辱他人等行为"
            ),
            BannerGuideBean(
                R.drawable.icon_travel_before4,
                "仅可保留1条旅行路线",
                "旅行计划每日最多可发布1次，且仅保留最新发布的旅行计划，旧的计划将自动删除"
            )
        )
    }

    override fun initView() {
        binding.apply {
            barCl.actionbarTitle.text = "发布旅行规则"
            barCl.divider.isVisible = true
            ClickUtils.applySingleDebouncing(arrayOf(barCl.btnBack, nextBtn),0) {
                when (it) {
                    barCl.btnBack -> {
                        finish()
                    }
                    nextBtn -> {
                        if (bannerTravelRule.currentItem == data.lastIndex) {
                            PublishTravelActivity.start(this@PublishTravelBeforeActivity)
                            UserManager.isTipDating = true
                        } else {
                            bannerTravelRule.setCurrentItem(bannerTravelRule.currentItem + 1, true)
                        }

                    }
                }
            }

            (bannerTravelRule as BannerViewPager<BannerGuideBean>).apply {
                adapter = guideBannerAdapter
                setIndicatorSliderWidth(SizeUtils.dp2px(8F))
                setIndicatorHeight(SizeUtils.dp2px(4F))
                setIndicatorSliderGap(SizeUtils.dp2px(4F))
                setLifecycleRegistry(lifecycle)
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        nextBtn.text = if (position == data.size - 1) {
                            "我已知晓"
                        } else {
                            "下一个"
                        }
                    }
                })
            }.create()
            bannerTravelRule.refreshData(data)
        }
    }

    override fun start() {
    }

}