package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import android.graphics.Color
import androidx.core.view.isVisible
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.SpanUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityJoinLuxuryBinding
import com.sdy.luxurytravelapplication.mvp.model.bean.BannerGuideBean
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


            (bannerLuxury as BannerViewPager<BannerGuideBean>).apply {
                adapter = guideBannerAdapter
                setIndicatorSliderWidth(SizeUtils.dp2px(8F))
                setIndicatorHeight(SizeUtils.dp2px(4F))
                setIndicatorSliderGap(SizeUtils.dp2px(4F))
                setLifecycleRegistry(lifecycle)
            }.create()

            val data = mutableListOf<BannerGuideBean>()
            if (UserManager.gender == 1) {
                data.apply {
                    add(
                        BannerGuideBean(
                            R.drawable.icon_luxury_boy_1,
                            "精选1%意向女性",
                            "意在打造高端社交圈层，精选空乘、名校学生、教师等优质女性，严格筛选，当前通申请过率仅为1.31%"
                        )
                    )
                    add(
                        BannerGuideBean(
                            R.drawable.icon_luxury_boy_2,
                            "奢旅圈身份标识",
                            "让所有人感知你的与众不同与尊贵，更容易找到心领意会的异性"
                        )
                    )
                    add(
                        BannerGuideBean(
                            R.drawable.icon_luxury_boy_4,
                            "聊天、活动内容置顶",
                            "你的消息不会折叠隐藏，直接进入对方消息首页列表并置顶显示，您发布的出游活动也将优先展示"
                        )
                    )
                    add(
                        BannerGuideBean(
                            R.drawable.icon_luxury_boy_3,
                            "全局优先推荐",
                            "在所有位置优先曝光，让你更受女性欢迎"
                        )
                    )
                }
            } else {
                data.apply {
                    add(
                        BannerGuideBean(
                            R.drawable.icon_luxury_girl_1,
                            "精选1%意向男性",
                            "筛选高端有实力用户，如CEO/投资人等避免用户被骚扰和无效沟通"
                        )
                    )
                    add(
                        BannerGuideBean(
                            R.drawable.icon_luxury_girl_2,
                            "奢旅圈身份标识",
                            "让所有人感知你的优秀，当前申请通过率仅为1.31%"
                        )
                    )
                    add(
                        BannerGuideBean(
                            R.drawable.icon_luxury_girl_3,
                            "全局优先推荐",
                            "在所有位置优先曝光，并优先向高端男性用户推荐，获得更多曝光"
                        )
                    )
                    add(
                        BannerGuideBean(
                            R.drawable.icon_luxury_girl_4,
                            "聊天旅券礼物",
                            "聊天礼物功能解锁，其他用户可在聊天时向你赠送旅券礼物"
                        )
                    )
                }
            }

            bannerLuxury.refreshData(data)
        }



    }

    override fun start() {
    }

    override fun initData() {


    }
}