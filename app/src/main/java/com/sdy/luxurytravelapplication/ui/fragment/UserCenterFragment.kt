package com.sdy.luxurytravelapplication.ui.fragment

import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.flyco.tablayout.listener.OnTabSelectListener
import com.google.android.material.appbar.AppBarLayout
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpFragment
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.FragmentMineBinding
import com.sdy.luxurytravelapplication.event.UserCenterEvent
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.contract.UserCenterContract
import com.sdy.luxurytravelapplication.mvp.model.bean.UserInfoBean
import com.sdy.luxurytravelapplication.mvp.presenter.UserCenterPresenter
import com.sdy.luxurytravelapplication.ui.activity.*
import com.sdy.luxurytravelapplication.ui.adapter.MainPagerAdapter
import com.sdy.luxurytravelapplication.ui.adapter.VisitUserAvatorAdater
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.support.v4.startActivity
import kotlin.math.abs


/**
 * 个人中心
 */
class UserCenterFragment :
    BaseMvpFragment<UserCenterContract.View, UserCenterContract.Presenter, FragmentMineBinding>(),
    UserCenterContract.View, View.OnClickListener {
    override fun createPresenter(): UserCenterContract.Presenter {
        return UserCenterPresenter()
    }

    companion object {
        const val REQUEST_INFO_SETTING = 11
        const val REQUEST_MY_SQUARE = 12
        const val REQUEST_ID_VERIFY = 13
        const val REQUEST_PUBLISH = 14
        const val REQUEST_INTENTION = 15

        const val POSITION_SQUARE = 0
        const val POSITION_DATING = 1
        const val POSITION_TAG = 2
    }

    //我的访客adapter
    private val visitsAdapter by lazy { VisitUserAvatorAdater() }

    private var userInfoBean: UserInfoBean? = null

    var appbarTop = false
    var userCenterVisible = false
    override fun initView(view: View) {
        super.initView(view)
        binding.apply {
            ClickUtils.applySingleDebouncing(
                arrayOf(
                    shareBtn,
                    settingBtn,
                    myAvatar,
                    myInfoEditBtn,
                    userVisit,
                    userFootprint,
                    userTravelCard,
                    isVipPowerBtn,
                    addPowerBtn
                ), this@UserCenterFragment
            )


            //我的访客封面
            val visitLayoutmanager = LinearLayoutManager(activity!!, RecyclerView.HORIZONTAL, false)
            visitLayoutmanager.stackFromEnd = true
            userVisitRv.layoutManager = visitLayoutmanager
            userVisitRv.adapter = visitsAdapter


            if (UserManager.gender == 1) {
                femalePowerCl.isInvisible = true
                malePowerLl.isVisible = true
            } else {
                femalePowerCl.isVisible = true
                malePowerLl.isVisible = false
            }

            userAppbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                if (userCenterVisible) {
                    if (abs(verticalOffset) == (userAppbar.height - flMySquareAndTag.height)) {
                        myNickname0.isVisible = true
                        shareBtn.setImageResource(R.drawable.icon_user_share_black)
                        settingBtn.setImageResource(R.drawable.icon_user_setting_black)
                        settingCl.setBackgroundColor(resources.getColor(R.color.colorWhite))
                        BarUtils.setStatusBarColor(
                            activity!!,
                            resources.getColor(R.color.colorWhite)
                        )
                        appbarTop = true
                    } else {
                        myNickname0.isVisible = false
                        shareBtn.setImageResource(R.drawable.icon_user_share_white)
                        settingBtn.setImageResource(R.drawable.icon_setting_white)
                        settingCl.setBackgroundColor(resources.getColor(R.color.colorAccent))
                        BarUtils.setStatusBarColor(
                            activity!!,
                            resources.getColor(R.color.colorAccent)
                        )
                        appbarTop = false
                    }

                }
            })
        }
        initFragment()
    }


    override fun lazyLoad() {
        mPresenter?.myInfoCandy()
    }

    private val mySquareFragment by lazy { MyFindContentFragment() }
    private val myTravelPlanFragment by lazy { MyTravelFragment() }

    //fragment栈管理
    private val mStack = arrayListOf<Fragment>()
    private val titles by lazy {
        arrayOf(
            getString(R.string.tab_square),
            getString(R.string.tab_activities)
        )
    }


    private fun initFragment() {
        mStack.add(mySquareFragment)  //我的广场
        mStack.add(myTravelPlanFragment)  //我的旅行计划
        binding.apply {

            tabMySquareAndTag.setTabData(titles)
            tabMySquareAndTag.currentTab = 0
            tabMySquareAndTag.setOnTabSelectListener(object : OnTabSelectListener {
                override fun onTabSelect(position: Int) {
                    vpMySquareAndTag.currentItem = position
                }

                override fun onTabReselect(position: Int) {

                }

            })
//            vpMySquareAndTag.adapter = MainPager2Adapter(activity!!, mStack)
            vpMySquareAndTag.adapter = MainPagerAdapter(fragmentManager!!, mStack, titles)
            vpMySquareAndTag.offscreenPageLimit = mStack.size
            vpMySquareAndTag.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    tabMySquareAndTag.currentTab = position
                }

            })
//            vpMySquareAndTag.registerOnPageChangeCallback(object :
//                ViewPager2.OnPageChangeCallback() {
//                override fun onPageSelected(position: Int) {
//                    super.onPageSelected(position)
//                    tabMySquareAndTag.currentTab = position
//                }
//            })
            vpMySquareAndTag.currentItem = POSITION_SQUARE
        }
    }


    override fun onGetMyInfoResult(userinfo: UserInfoBean?) {
        if (userinfo != null) {
            this.userInfoBean = userinfo
            initData()
        }

    }

    private fun initData() {
        if (userInfoBean != null) {
            binding.apply {
                userInfoBean!!.apply {
                    UserManager.avatar = userinfo!!.avatar
                    UserManager.isvip = userinfo.isplatinum
                    UserManager.isPtvip = userinfo.isdirectvip


                    shareBtn.isVisible = red_packet_btn
                    visitsAdapter.freeShow = free_show
                    visitlist = UserManager.tempDatas
                    if (visitlist.size > 5) {
                        visitsAdapter.setNewInstance(visitlist.subList(0, 5))
                        visitsAdapter.todayVisitCount = visitlist.size - 4
                    } else {
                        visitsAdapter.setNewInstance(visitlist)
                    }
                    GlideUtil.loadRoundImgCenterCrop(
                        activity!!,
                        userinfo.avatar,
                        myAvatar,
                        SizeUtils.dp2px(15f)
                    )
                    myNickname.text = userinfo.nickname
                    myNickname0.text = userinfo.nickname
                    if (userinfo.nickname.length > 6) {
                        myNickname.textSize = 16F
                    } else {
                        myNickname.textSize = 25F
                    }
                    mySign.isInvisible = sign.isNullOrEmpty()
                    mySign.text = sign

                    userVip.isVisible = userinfo.isplatinum
                    userPtVip.isVisible = userinfo.isdirectvip
                    setUserPower()
                    checkVip()
                }
            }
        }
    }

    private fun checkVip() {
        binding.vipLevelSaveCount.text = userInfoBean!!.platinum_vip_str
        binding.userVip.isVisible = userInfoBean!!.userinfo!!.isplatinum
        binding.userPtVip.isVisible = userInfoBean!!.userinfo!!.isdirectvip
    }


    //是否认证 0 未认证 1通过 2机审中 3人审中 4被拒（弹框）
    private fun checkVerify() {
        if (userInfoBean?.userinfo?.isfaced == 1) {//已认证
            binding.powerVerify.setImageResource(R.drawable.icon_user_verify)
        } else { //审核中
            binding.powerVerify.setImageResource(R.drawable.icon_user_verify_not)
        }
    }

    /**
     * 设置女性用户的权益栏位
     */
    private fun setUserPower() {
        if (userInfoBean?.userinfo?.mv_faced == 1) {
            binding.powerVideo.setImageResource(R.drawable.icon_user_video)
        } else {
            binding.powerVideo.setImageResource(R.drawable.icon_user_video_not)
        }
        checkVerify()

        if (userInfoBean?.userinfo?.contact_way == 0) {
            binding.powerContact.setImageResource(R.drawable.icon_user_contact_not)
        } else {
            binding.powerContact.setImageResource(R.drawable.icon_user_contact)
        }
    }


    override fun onClick(v: View) {
        when (v) {
            binding.shareBtn -> {
                startActivity<MyInviteActivity>()
            }
            binding.settingBtn -> {
                startActivity<SettingsActivity>()
            }
            //个人资料
            binding.myInfoEditBtn, binding.myAvatar -> {
                startActivity<MyInfoActivity>()
            }
            binding.userVisit -> { //我的访客
                startActivity<MyVisitActivity>(
                    "isVip" to (userInfoBean?.userinfo?.isplatinum ?: false),
                    "today" to userInfoBean?.userinfo?.todayvisit,
                    "all" to userInfoBean?.userinfo?.allvisit,
                    "freeShow" to userInfoBean?.free_show,
                    "from" to MyVisitActivity.FROM_TOP_RECOMMEND
                )
            }
            binding.userFootprint -> { //我的足迹
                startActivity<MyFootPrintActivity>()
            }
            binding.userTravelCard -> {//我的旅券
                CandyRechargeActivity.gotoCandyRecharge(activity!!, CandyRechargeActivity.TYPE_MINE)
            }
            binding.isVipPowerBtn -> {//会员权益
                VipChargeActivity.start(activity!!)
            }
            binding.addPowerBtn -> {//获取更多权益
                startActivity<WomanPowerActivity>(
                    "contact" to userInfoBean?.userinfo?.contact_way,
                    "verify" to userInfoBean?.userinfo?.isfaced,
                    "video" to userInfoBean?.userinfo?.mv_faced,
                    "url" to userInfoBean?.power_url
                )
            }
        }

    }


    //更新用户中心信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshEvent(event: UserCenterEvent) {
        if (!UserManager.touristMode)
            mPresenter?.myInfoCandy()
    }


}