package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import android.graphics.Color
import androidx.core.view.isVisible
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.SpanUtils
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityJoinLuxuryBinding
import com.sdy.luxurytravelapplication.mvp.model.bean.SweetProgressBean
import com.sdy.luxurytravelapplication.ui.adapter.GuideBannerAdapter
import com.zhpan.bannerview.BannerViewPager
import org.jetbrains.anko.startActivity

class JoinLuxuryActivity : BaseActivity<ActivityJoinLuxuryBinding>() {
    private val sweetProgressBean by lazy { intent.getSerializableExtra("sweetProgressBean") as SweetProgressBean }
    companion object {
        fun startJoinLuxuxy(context: Context, sweetProgressBean: SweetProgressBean) {
            context.startActivity<JoinLuxuryActivity>("sweetProgressBean" to sweetProgressBean)
        }
    }


    private val guideBannerAdapter by lazy { GuideBannerAdapter() }
    override fun initView() {
        binding.apply {
            barCl.actionbarTitle.text = "加入奢旅圈"
            barCl.btnBack.setOnClickListener {
                finish()
            }
            barCl.divider.isVisible = true

            if (UserManager.gender == 1) {
                way1.isVisible = false
                way2.isVisible = false
                t1.text = "您需要达成以下条件之一"
                SpanUtils.with(normalMoney)
                    .append("充值金额大于${sweetProgressBean.normal_money}")
                    .append("（${sweetProgressBean.now_money}/${sweetProgressBean.normal_money}）")
                    .setForegroundColor(Color.parseColor("#FFC7CAD4"))
                    .create()
            } else {
                t1.text = "达成要求自动进入奢旅圈"
                normalMoney.text = "上传认证视频"
                wealthVerify.text = "认证职业或学历"
                way1.isVisible = true
                way2.isVisible = true
                when (sweetProgressBean.female_mv_state) {
                    2 -> {
                        purchaseBtn.text = "审核中"
                        purchaseBtn.isEnabled = false
                    }
                    3 -> {
                        purchaseBtn.text = "已完成"
                        purchaseBtn.isEnabled = false
                    }
                    else -> {
                        purchaseBtn.text = "去上传"
                        purchaseBtn.isEnabled = true
                    }
                }
                when (sweetProgressBean.assets_audit_state) {
                    2 -> {
                        verifyBtn.text = "审核中"
                        verifyBtn.isEnabled = false
                    }
                    3 -> {
                        verifyBtn.text = "已完成"
                        verifyBtn.isEnabled = false
                    }
                    else -> {
                        verifyBtn.text = "去上传"
                        verifyBtn.isEnabled = true
                    }
                }
            }


            (bannerLuxury as BannerViewPager<String>).apply {
                adapter = guideBannerAdapter
                setIndicatorSliderWidth(SizeUtils.dp2px(8F))
                setIndicatorHeight(SizeUtils.dp2px(4F))
                setIndicatorSliderGap(SizeUtils.dp2px(4F))
                setLifecycleRegistry(lifecycle)
            }.create()

            bannerLuxury.refreshData(UserManager.tempDatas)
        }



    }

    override fun start() {
    }

    override fun initData() {


    }
}