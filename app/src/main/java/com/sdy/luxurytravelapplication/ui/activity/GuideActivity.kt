package com.sdy.luxurytravelapplication.ui.activity

import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.ui.adapter.GuideBannerAdapter
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityGuideBinding
import com.zhpan.bannerview.BannerViewPager

/**
 * 引导页面
 */
class GuideActivity : BaseActivity<ActivityGuideBinding>() {
    override fun initData() {

    }

    private val guideBannerAdapter by lazy { GuideBannerAdapter() }
    override fun initView() {
        (binding.bannerGuide as BannerViewPager<String>).apply {
            adapter = guideBannerAdapter
            setIndicatorSliderWidth(SizeUtils.dp2px(8F))
            setIndicatorHeight(SizeUtils.dp2px(4F))
            setIndicatorSliderGap(SizeUtils.dp2px(4F))
            setLifecycleRegistry(lifecycle)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    binding.nextBtn.isEnabled = position == guideBannerAdapter.itemCount - 1
                }
            })
        }.create()

        binding.bannerGuide.refreshData(UserManager.tempDatas)
        ClickUtils.applySingleDebouncing(binding.nextBtn) {

        }
    }

    override fun start() {
    }

}