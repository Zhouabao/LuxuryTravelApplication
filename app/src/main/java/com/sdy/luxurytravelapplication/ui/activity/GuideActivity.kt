package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import android.view.KeyEvent
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityGuideBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.liveface.FaceLivenessExpActivity
import com.sdy.luxurytravelapplication.mvp.model.bean.BannerGuideBean
import com.sdy.luxurytravelapplication.mvp.model.bean.MoreMatchBean
import com.sdy.luxurytravelapplication.ui.adapter.GuideBannerAdapter
import com.zhpan.bannerview.BannerViewPager
import org.jetbrains.anko.startActivity

/**
 * 引导页面
 */
class GuideActivity : BaseActivity<ActivityGuideBinding>() {
    private val moreMatchBean by lazy { intent.getSerializableExtra("moreMatchBean") as MoreMatchBean }

    companion object {
        fun start(context: Context, moreMatchBean: MoreMatchBean) {
            context.startActivity<GuideActivity>("moreMatchBean" to moreMatchBean)
        }
    }


    override fun start() {
    }

    override fun initData() {

    }

    private val guideBannerAdapter by lazy { GuideBannerAdapter() }

    var mPosition = 0
    override fun initView() {
        (binding.bannerGuide as BannerViewPager<BannerGuideBean>).apply {
            adapter = guideBannerAdapter
            setIndicatorSliderWidth(SizeUtils.dp2px(8F))
            setIndicatorHeight(SizeUtils.dp2px(4F))
            setIndicatorSliderGap(SizeUtils.dp2px(4F))
            setLifecycleRegistry(lifecycle)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    mPosition = position
                }
            })
        }.create()


        val data = mutableListOf<BannerGuideBean>().apply {
            add(
                BannerGuideBean(
                    "",
                    "无限约会可能",
                    "美食探寻、潜水浮潜、游艇party、海景别墅应有尽有",
                    "lottie_guide_1.json",
                    "images_guide_1"
                )
            )
            add(
                BannerGuideBean(
                    "",
                    "高端定制旅行",
                    "谁说旅行千篇一律？定制属于自己的旅行",
                    "lottie_guide_2.json",
                    "images_guide_2"
                )
            )
            add(
                BannerGuideBean(
                    "",
                    "精选会员构成",
                    "高校学生、高知白领、模特、企业家、CEO等",
                    "lottie_guide_3.json",
                    "images_guide_3"
                )
            )
        }
        binding.bannerGuide.refreshData(data)

        ClickUtils.applySingleDebouncing(binding.nextBtn) {

            when (mPosition) {
                0 -> {
                    binding.bannerGuide.setCurrentItem(1)
                }
                1 -> {
                    binding.bannerGuide.setCurrentItem(2)
                }
                else -> {
                    if (moreMatchBean.gender == 1 && moreMatchBean.threshold && !moreMatchBean.isvip) {
                        InviteCodeActivity.start(this, moreMatchBean)
                    } else if (moreMatchBean.gender == 2 && moreMatchBean.living_btn) {
                        CommonFunction.startToFace(this, FaceLivenessExpActivity.TYPE_LIVE_CAPTURE)
                    } else {
                        moreMatchBean.apply {
                            UserManager.savePersonalInfo(avatar, birth, gender, nickname)
                        }
                        MainActivity.startToMain(this, true)
                    }
                }
            }

        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return keyCode == KeyEvent.KEYCODE_BACK
    }

    override fun onDestroy() {
        super.onDestroy()
        guideBannerAdapter.clearAnimation()
    }
}