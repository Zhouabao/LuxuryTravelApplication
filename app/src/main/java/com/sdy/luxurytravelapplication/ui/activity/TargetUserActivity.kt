package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.google.android.flexbox.*
import com.kongzue.dialog.v3.MessageDialog
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.ActivityTargetUserBinding
import com.sdy.luxurytravelapplication.databinding.ItemTargetTopBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.mvp.contract.TargetUserContract
import com.sdy.luxurytravelapplication.mvp.model.bean.MatchBean
import com.sdy.luxurytravelapplication.mvp.model.bean.RecommendSquareListBean
import com.sdy.luxurytravelapplication.mvp.model.bean.UserPhotoBean
import com.sdy.luxurytravelapplication.mvp.presenter.TargetUserPresenter
import com.sdy.luxurytravelapplication.ui.adapter.RecommendSquareAdapter
import com.sdy.luxurytravelapplication.ui.adapter.TargetBaseInfoAdapter
import com.sdy.luxurytravelapplication.ui.adapter.TargetBigPhotoAdapter
import com.sdy.luxurytravelapplication.ui.adapter.TargetSmallPhotoAdapter
import com.sdy.luxurytravelapplication.ui.dialog.VerifyLevelDescrDialog
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.widgets.CenterLayoutManager
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.startActivity

/**
 * 对方个人页
 */
class TargetUserActivity :
    BaseMvpActivity<TargetUserContract.View, TargetUserContract.Presenter, ActivityTargetUserBinding>(),
    TargetUserContract.View, OnLoadMoreListener {
    private val targetAccid by lazy { intent.getStringExtra("target_accid")!! }
    override fun createPresenter(): TargetUserContract.Presenter = TargetUserPresenter()

    companion object {
        @JvmStatic
        fun start(context: Context, target_accid: String) {
            context.startActivity<TargetUserActivity>("target_accid" to target_accid)
        }
    }

    /**
     * 初始化个人信息数据
     */
    private val baseInfoAdapter by lazy { TargetBaseInfoAdapter() }
    private val bigPhotoAdapter by lazy { TargetBigPhotoAdapter() }
    private val smallPhotoAdapter by lazy { TargetSmallPhotoAdapter() }
    private val adapter by lazy { RecommendSquareAdapter() }


    lateinit var headBinding: ItemTargetTopBinding
    private fun initHeadBannerView(): View {
        headBinding = ItemTargetTopBinding.inflate(layoutInflater)
        headBinding.apply {
            val manager =
                FlexboxLayoutManager(this@TargetUserActivity, FlexDirection.ROW, FlexWrap.WRAP)
            manager.alignItems = AlignItems.STRETCH
            manager.justifyContent = JustifyContent.FLEX_START
            basicInfoRv.layoutManager = manager
            basicInfoRv.adapter = baseInfoAdapter

            bigPhotosRv.adapter = bigPhotoAdapter
            bigPhotosRv.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    smallPhotosRv.smoothScrollToPosition(position)
                    smallPhotoAdapter.data.forEach {
                        it.checked = it == smallPhotoAdapter.data[position]
                    }
                    smallPhotoAdapter.notifyDataSetChanged()
                }
            })
            smallPhotosRv.layoutManager =
                CenterLayoutManager(this@TargetUserActivity, RecyclerView.HORIZONTAL, false)
            smallPhotosRv.adapter = smallPhotoAdapter
            smallPhotoAdapter.setOnItemClickListener { _, view, position ->
                bigPhotosRv.currentItem = position
                smallPhotoAdapter.data.forEach {
                    it.checked = it == smallPhotoAdapter.data[position]
                }
                smallPhotoAdapter.notifyDataSetChanged()

            }

            ClickUtils.applySingleDebouncing(problemBtn) {
                VerifyLevelDescrDialog().show()
            }
        }
        return headBinding.root
    }

    override fun initData() {
        binding.apply {
            mLayoutStatusView = root
            BarUtils.addMarginTopEqualStatusBarHeight(barlCl.root)
            barlCl.actionbarTitle.setTextColor(Color.WHITE)
            barlCl.actionbarTitle.textSize = 13F
            barlCl.btnBack.setImageResource(R.drawable.icon_back_white)
            barlCl.rightIconBtn.isVisible = true
            barlCl.root.setBackgroundColor(Color.TRANSPARENT)
            barlCl.rightIconBtn.setImageResource(R.drawable.icon_more_gray)
            refreshTargetUser.setOnLoadMoreListener(this@TargetUserActivity)


            val manager1 = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            manager1.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
            mySquareRv.layoutManager = manager1
            mySquareRv.animation = null
            mySquareRv.adapter = adapter
            mySquareRv.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val params = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
                    val spanIndex = params.spanIndex
                    if (params.viewLayoutPosition != 0) {
                        if (spanIndex % 2 == 0) {
                            outRect.left =
                                SizeUtils.applyDimension(8F, TypedValue.COMPLEX_UNIT_DIP).toInt()
                        } else {
                            outRect.right =
                                SizeUtils.applyDimension(15f, TypedValue.COMPLEX_UNIT_DIP).toInt()
                        }
                    }
                }
            })
            adapter.setHeaderView(initHeadBannerView())
            adapter.setEmptyView(R.layout.layout_empty_view)
            adapter.isUseEmpty = false

            adapter.setOnItemClickListener { _, view, position ->
                ToastUtil.toast("$position")
            }


        }
    }

    fun setData() {
        binding.apply {
            if (this@TargetUserActivity::matchBean.isInitialized) {
                barlCl.actionbarTitle.text = matchBean.online_time
                baseInfoAdapter.setNewInstance(matchBean.personal_info)
                bigPhotoAdapter.setNewInstance(arrayListOf<UserPhotoBean>().apply {
                    if (matchBean.mv_btn) {
                        add(UserPhotoBean(true, matchBean.mv_url, true))
                    }
                    matchBean.photos.forEachWithIndex { index, s ->
                        add(UserPhotoBean(!matchBean.mv_btn && index == 0, s))
                    }
                })

                smallPhotoAdapter.setNewInstance(arrayListOf<UserPhotoBean>().apply {
                    if (matchBean.mv_btn) {
                        add(UserPhotoBean(true, matchBean.mv_url, true))
                    }
                    matchBean.photos.forEachWithIndex { index, s ->
                        add(UserPhotoBean(!matchBean.mv_btn && index == 0, s))
                    }
                })
                headBinding.apply {
                    nickName.text = matchBean.nickname
                    age.text = "${matchBean.age}岁"
                    distance.text = matchBean.distance
                    userSign.text = matchBean.sign
                    if (matchBean.gender == 2) {
                        userGender.setImageResource(R.drawable.icon_trget_gender_woman)
                    } else {
                        userGender.setImageResource(R.drawable.icon_trget_gender_man)
                    }
                    if (matchBean.dating != null) {
                        matchBean.dating!!.apply {
                            travelTitle.text = dating_title
                            travelProvince.text = rise_province
                            travelAddress.text = rise_city
                            travelDestProvince.text = goal_province
                            travelDestAddress.text = goal_city
                            travelDescr.text = content
                            ClickUtils.applySingleDebouncing(detailBtn) {
                                TravelDetailActivity.start(this@TargetUserActivity, dating_id = id)
                            }
                        }
                        travelPlanCl.isVisible = true
                    } else {
                        travelPlanCl.isVisible = false
                    }

                    //	0没有认证 1活体 2 真人 3 颜值 4奢旅
                    when (matchBean.face_type) {
                        2 -> {
                            verifyCl.isVisible = true
                            verifyTitle.text = "真人认证"
                            verifyTitle.setTextColor(Color.parseColor("#FF1ED0A7"))
                            t11.setCompoundDrawablesWithIntrinsicBounds(
                                getDrawable(R.drawable.icon_real_people),
                                null,
                                null,
                                null
                            )
                            verifyBtn.isVisible = false
                        }
                        3 -> {
                            verifyCl.isVisible = true
                            verifyTitle.text = "颜值认证"
                            verifyTitle.setTextColor(Color.parseColor("#FFFF6B82"))
                            t11.setCompoundDrawablesWithIntrinsicBounds(
                                getDrawable(R.drawable.icon_beauty),
                                null,
                                null,
                                null
                            )
                            verifyBtn.isVisible = false
                        }
                        4 -> {
                            verifyCl.isVisible = true
                            verifyTitle.text = "奢旅圈认证"
                            verifyTitle.setTextColor(Color.parseColor("#FFFC9010"))
                            t11.setCompoundDrawablesWithIntrinsicBounds(
                                getDrawable(R.drawable.icon_luxury),
                                null,
                                null,
                                null
                            )
                            verifyBtn.isVisible = true
                        }
                        else -> {
                            verifyCl.isVisible = false
                        }

                    }

                    //联系方式  0  没有 1 电话 2微信 3 qq
                    if (matchBean.contact_way != 0) {
                        contactBtn.isVisible = true
                        when (matchBean.contact_way) {
                            1 -> {
                                contactWay.setImageResource(R.drawable.icon_target_phone)
                            }
                            2 -> {
                                contactWay.setImageResource(R.drawable.icon_target_wechat)
                            }
                            3 -> {
                                contactWay.setImageResource(R.drawable.icon_target_qq)
                            }
                        }
                        ClickUtils.applySingleDebouncing(contactBtn) {
                            CommonFunction.checkUnlockIntroduceVideo(
                                this@TargetUserActivity,
                                matchBean.accid
                            )
                        }
                    } else {
                        contactBtn.isVisible = false
                    }

                }

            }
        }
    }

    override fun start() {
        mLayoutStatusView?.showLoading()
        mPresenter?.getMatchUserInfo(targetAccid)
    }

    private lateinit var matchBean: MatchBean
    private var page = 1
    private val params1 by lazy {
        hashMapOf<String, Any>(
            "page" to page,
            "pagesize" to Constants.PAGESIZE,
            "target_accid" to targetAccid
        )
    }

    override fun getMatchUserInfo(code: Int, msg: String, matchBean: MatchBean?) {
        if (code == 200) {
            mLayoutStatusView?.showContent()
            this.matchBean = matchBean!!
            setData()
            mPresenter?.someoneSquareCandy(params1)
        } else if (code == 409) {
            MessageDialog.show(this as AppCompatActivity, "提示", msg, "知道了")
                .setOnOkButtonClickListener { baseDialog, v ->
                    NIMClient.getService(MsgService::class.java)
                        .deleteRecentContact2(targetAccid, SessionTypeEnum.P2P)
                    finish()
                    false
                }
        } else {
            mLayoutStatusView?.showError()
        }

    }

    override fun onGetSquareListResult(data: RecommendSquareListBean?, result: Boolean) {
        if (result) {
            adapter.addData(data!!.list)
            if (adapter.data.isEmpty()) {
                adapter.isUseEmpty = true
            }

            if (data.list.isNullOrEmpty() || data.list.size < Constants.PAGESIZE) {
                binding.refreshTargetUser.finishLoadMoreWithNoMoreData()
            } else {
                binding.refreshTargetUser.finishLoadMore(true)
            }
        } else {
            if (page == 1) {
                mLayoutStatusView?.showError()
            } else {
                binding.refreshTargetUser.finishLoadMore(false)
            }
        }

    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        if (adapter.data.size < Constants.PAGESIZE * page) {
            refreshLayout.finishLoadMoreWithNoMoreData()
        } else {
            page += 1
            params1["page"] = page
            mPresenter?.someoneSquareCandy(params1)
        }

    }


}