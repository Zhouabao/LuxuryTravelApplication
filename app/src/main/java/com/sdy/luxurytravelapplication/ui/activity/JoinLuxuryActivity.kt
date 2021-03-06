package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import android.graphics.Color
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.SpanUtils
import com.kongzue.dialog.v3.MessageDialog
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityJoinLuxuryBinding
import com.sdy.luxurytravelapplication.event.UpdateLuxuryEvent
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.mvp.contract.JoinLuxuryContract
import com.sdy.luxurytravelapplication.mvp.model.bean.BannerGuideBean
import com.sdy.luxurytravelapplication.mvp.model.bean.SweetProgressBean
import com.sdy.luxurytravelapplication.mvp.presenter.JoinLuxuryPresenter
import com.sdy.luxurytravelapplication.ui.adapter.GuideBannerAdapter
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.zhpan.bannerview.BannerViewPager
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity

class JoinLuxuryActivity :
    BaseMvpActivity<JoinLuxuryContract.View, JoinLuxuryContract.Presenter, ActivityJoinLuxuryBinding>(),
    JoinLuxuryContract.View {
    private val sweetProgressBean by lazy { intent.getSerializableExtra("sweetProgressBean") as SweetProgressBean }

    companion object {
        fun startJoinLuxuxy(context: Context, sweetProgressBean: SweetProgressBean) {
            context.startActivity<JoinLuxuryActivity>("sweetProgressBean" to sweetProgressBean)
        }
    }


    private val guideBannerAdapter by lazy { GuideBannerAdapter() }
    override fun createPresenter(): JoinLuxuryContract.Presenter {
        return JoinLuxuryPresenter()

    }

    override fun initData() {

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
                normalMoney.text =
                    getString(R.string.charge_more_than, sweetProgressBean.normal_money)
                wealthVerify.text = getString(R.string.pass_wealth_verify)
                SpanUtils.with(normalMoney)
                    .append("充值金额大于${sweetProgressBean.normal_money}")
                    .append("（${sweetProgressBean.now_money}/${sweetProgressBean.normal_money}）")
                    .setForegroundColor(Color.parseColor("#FFC7CAD4"))
                    .create()

                if (sweetProgressBean.now_money.toFloat() > sweetProgressBean.normal_money.toFloat()) {
                    purchaseBtn.text = getString(R.string.join_now)
                    ClickUtils.applySingleDebouncing(purchaseBtn) {
                        mPresenter?.joinSweetApply()
                    }
                } else {
                    purchaseBtn.text = "立即充值"
                    ClickUtils.applySingleDebouncing(purchaseBtn) {
                        CommonFunction.startToVip(
                            this@JoinLuxuryActivity
                        )
                    }
                }
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
                        purchaseBtn.text = "已认证"
                        purchaseBtn.isEnabled = false
                    }
                    else -> {
                        purchaseBtn.text = "去认证"
                        purchaseBtn.isEnabled = true
                        ClickUtils.applySingleDebouncing(purchaseBtn) {
                            //0未认证/认证不成功     1认证通过     2认证中
                            when (sweetProgressBean.isfaced) {
                                1 -> {
                                    CommonFunction.startToVideoIntroduce(this@JoinLuxuryActivity, -1)
                                }
                                2 -> {
                                    ToastUtil.toast("请等待真人认证完成")
                                }
                                else -> {
                                    MessageDialog.show(this@JoinLuxuryActivity,"提示","请先完成真人认证","去认证","取消")
                                        .setOnOkButtonClickListener { _, v ->
                                            CommonFunction.startToFace(this@JoinLuxuryActivity)
                                            false
                                        }.setOnCancelButtonClickListener { _, v ->
                                            false
                                        }

                                }
                            }

                        }
                    }
                }
            }

            //认证 1没有 2认证中 3认证通过
            when (sweetProgressBean.assets_audit_state) {
                2 -> {
                    verifyBtn.text = "审核中"
                    verifyBtn.isEnabled = false
                }
                3 -> {
                    verifyBtn.text = "已认证"
                    verifyBtn.isEnabled = false
                }
                else -> {
                    verifyBtn.text = "去认证"
                    verifyBtn.isEnabled = true
                    ClickUtils.applySingleDebouncing(verifyBtn) {
                        ChooseVerifyActivity.start(this@JoinLuxuryActivity)
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
                            "你的消息不会折叠隐藏，直接进入对方消息首页列表并置顶显示，您发布的伴游活动也将优先展示"
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

    override fun joinSweetApplyResult(success: Boolean) {
        EventBus.getDefault().post(UpdateLuxuryEvent())
        if (success) {
            finish()
        }
    }

}