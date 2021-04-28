package com.sdy.luxurytravelapplication.ui.activity

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.baidu.idl.face.platform.utils.DensityUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.google.android.flexbox.*
import com.kongzue.dialog.v3.BottomMenu
import com.kongzue.dialog.v3.MessageDialog
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.friend.FriendService
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.ActivityTargetUserBinding
import com.sdy.luxurytravelapplication.databinding.ItemTargetTopBinding
import com.sdy.luxurytravelapplication.event.UpdateBlackEvent
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
import com.shuyu.gsyvideoplayer.GSYVideoManager
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.collections.forEachWithIndex
import org.jetbrains.anko.startActivity

/**
 * 对方个人页
 */
class TargetUserActivity :
    BaseMvpActivity<TargetUserContract.View, TargetUserContract.Presenter, ActivityTargetUserBinding>(),
    TargetUserContract.View, OnLoadMoreListener, View.OnClickListener {
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
    private val bigPhotoAdapter by lazy {
        TargetBigPhotoAdapter().apply {
            playVideoCallBack = object : TargetBigPhotoAdapter.PlayVideoCallBack {
                override fun callback() {
                    mPresenter?.playMv(targetAccid)
                }
            }
        }
    }
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
            bigPhotoAdapter.setOnItemClickListener { _, view, position ->
                if (bigPhotoAdapter.data[position].isVideo && !bigPhotoAdapter.autoPlay) {
                    CommonFunction.checkUnlockIntroduceVideo(
                        this@TargetUserActivity,
                        targetAccid,
                        matchBean.mv_url
                    )
                }
            }
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

    //判断控件是否可见
    fun getLocalVisibleRect(context: Context, view: View, offsetY: Int): Boolean {
        val p = Point()
        (context as Activity).windowManager.defaultDisplay.getSize(p)
        val screenWidth: Int = p.x
        val screenHeight: Int = p.y
        val rect = Rect(0, 0, screenWidth, screenHeight)
        val location = IntArray(2)
        location[1] = location[1] + DensityUtils.dip2px(context, offsetY.toFloat())
        view.getLocationInWindow(location)
        view.tag = location[1] //存储y方向的位置
        return view.getLocalVisibleRect(rect)
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
            ClickUtils.applySingleDebouncing(
                arrayOf(
                    barlCl.btnBack,
                    barlCl.rightIconBtn,
                    contactCl,
                    detailUserChatBtn,
                    cancelBlack
                ), this@TargetUserActivity
            )

            val manager1 = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            manager1.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
            mySquareRv.layoutManager = manager1
            mySquareRv.animation = null
            mySquareRv.adapter = adapter


            mySquareRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if(getLocalVisibleRect(this@TargetUserActivity,headBinding.bigPhotosRv,dy)){
                        //可见
                        BarUtils.transparentStatusBar(this@TargetUserActivity)
                        BarUtils.setStatusBarColor(this@TargetUserActivity,Color.TRANSPARENT)
                        barlCl.actionbarTitle.text = matchBean.online_time
                        barlCl.actionbarTitle.setTextColor(Color.WHITE)
                        barlCl.btnBack.setImageResource(R.drawable.icon_back_white)
                        barlCl.rightIconBtn.setImageResource(R.drawable.icon_more_gray)
                        barlCl.root.setBackgroundColor(Color.TRANSPARENT)
                    }else{
                        //不可见
                        BarUtils.setStatusBarColor(this@TargetUserActivity,resources.getColor(R.color.white))
                        barlCl.actionbarTitle.text = matchBean.nickname+"\n"+matchBean.online_time
                        barlCl.actionbarTitle.setTextColor(resources.getColor(R.color.color333))
                        barlCl.btnBack.setImageResource(R.drawable.icon_return_arrow)
                        barlCl.rightIconBtn.setImageResource(R.drawable.icon_more_black)
                        barlCl.root.setBackgroundColor(resources.getColor(R.color.white))
                    }
                }
            })
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
            adapter.headerWithEmptyEnable = true

            adapter.setOnItemClickListener { _, view, position ->
                ToastUtil.toast("$position")
            }
        }
    }

    fun setData() {
        binding.apply {
            if (this@TargetUserActivity::matchBean.isInitialized) {
                barlCl.actionbarTitle.text = matchBean.online_time
                baseInfoAdapter.setNewInstance(matchBean.personal_info_arr)
                bigPhotoAdapter.setNewInstance(arrayListOf<UserPhotoBean>().apply {
                    if (matchBean.mv_btn) {
                        add(UserPhotoBean(true, matchBean.mv_cover_url, true, matchBean.mv_detail_url))
                    }
                    matchBean.photos.forEachWithIndex { index, s ->
                        add(UserPhotoBean(!matchBean.mv_btn && index == 0, s))
                    }
                })

                smallPhotoAdapter.setNewInstance(arrayListOf<UserPhotoBean>().apply {
                    if (matchBean.mv_btn) {
                        add(UserPhotoBean(true, matchBean.mv_cover_url, true,matchBean.mv_detail_url))
                    }
                    matchBean.photos.forEachWithIndex { index, s ->
                        add(UserPhotoBean(!matchBean.mv_btn && index == 0, s))
                    }
                })
                smallPhotoAdapter.matchBean = matchBean
                bigPhotoAdapter.targetAccid = matchBean.accid
                bigPhotoAdapter.autoPlay =
                    matchBean.personal_auto_play && (matchBean.residue_auto_count > 0 || matchBean.isplatinumvip)
                smallPhotoAdapter.autoPlay = bigPhotoAdapter.autoPlay
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
                    if (matchBean.dating != null && matchBean.dating!!.id != 0) {
                        matchBean.dating!!.apply {
                            travelTitle.text = dating_title
                            travelProvince.text = rise_province
                            travelAddress.text = rise_city
                            travelDestProvince.text = goal_province
                            travelDestAddress.text = goal_city
                            travelAduio.isVisible = content_type == 2
                            travelDescr.isVisible = content_type == 1
                            if (content_type == 2) {
                                travelAduio.prepareAudio(content, duration, 0, 0, false)
                                travelAduio.initResource(
                                    R.drawable.shape_rectangle_white_15dp,
                                    resources.getColor(R.color.colorAccent),
                                    R.drawable.icon_voice_green
                                    , "audio_play_green.json"
                                )
                            } else {
                                travelDescr.text = content

                            }

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
                        contactCl.isVisible = true
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
                    } else {
                        contactCl.isVisible = false
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


    override fun onClick(v: View) {
        binding.apply {
            when (v) {
                barlCl.btnBack -> {
                    finish()
                }
                barlCl.rightIconBtn -> {
                    if (this@TargetUserActivity::matchBean.isInitialized) {
                        val datas = arrayListOf<CharSequence>().apply {
                            if (this@TargetUserActivity::matchBean.isInitialized && matchBean.isfriend == 1) {
                                add("解除配对")
                            }
                            add("举报")
                            add("拉黑")
                        }
                        BottomMenu.show(
                            this@TargetUserActivity as AppCompatActivity,
                            datas
                        ) { text, index ->
                            when (text) {
                                "解除配对" -> {
                                    mPresenter?.dissolutionFriend(hashMapOf("target_accid" to targetAccid))
                                }
                                "举报" -> {
                                    ReportReasonActivity.startReport(
                                        this@TargetUserActivity,
                                        targetAccid
                                    )
                                }
                                "拉黑" -> {
                                    mPresenter?.shieldingFriend(hashMapOf("target_accid" to targetAccid))
                                }
                            }
                        }
                    }

                }
                contactCl -> {//获取联系方式
                    if (this@TargetUserActivity::matchBean.isInitialized)
                        CommonFunction.checkUnlockContact(
                            this@TargetUserActivity,
                            matchBean.accid,
                            matchBean.gender
                        )
                }
                detailUserChatBtn -> { //聊天check
                    if (this@TargetUserActivity::matchBean.isInitialized) {
                        CommonFunction.checkChat(this@TargetUserActivity, matchBean.accid)
                    }
                }

                cancelBlack -> {//取消拉黑
                    mPresenter?.removeBlock(
                        hashMapOf("target_accid" to matchBean.accid)
                    )
                }
            }
        }

    }

    override fun getMatchUserInfo(code: Int, msg: String, matchBean: MatchBean?) {
        if (code == 200) {
            mLayoutStatusView?.showContent()
            this.matchBean = matchBean!!
            updateBlockStatus()
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

    override fun onGetUserActionResult(success: Boolean, isDissolve: Boolean) {
        if (isDissolve) {
            CommonFunction.dissolveRelationshipLocal(matchBean.accid)
        } else {
            NIMClient.getService(FriendService::class.java).addToBlackList(matchBean.accid)
            NIMClient.getService(MsgService::class.java)
                .deleteRecentContact2(matchBean.accid, SessionTypeEnum.P2P)
            NIMClient.getService(MsgService::class.java)
                .clearChattingHistory(matchBean.accid, SessionTypeEnum.P2P)
            matchBean.isblock = 2
            updateBlockStatus()
        }

    }

    override fun onRemoveBlockResult(success: Boolean) {
        if (success) {
            NIMClient.getService(FriendService::class.java).removeFromBlackList(matchBean!!.accid)
            //1 互相没有拉黑  2 我拉黑了他  3  ta拉黑了我   4 互相拉黑
            if (matchBean.isblock == 4) {
                matchBean.isblock = 3
            } else if (matchBean.isblock == 2) {
                matchBean.isblock = 1
            }
            EventBus.getDefault().post(UpdateBlackEvent())
            updateBlockStatus()
        }
    }

    /**
     * 更新拉黑状态
     */
    //1 互相没有拉黑  2 我拉黑了他  3  ta拉黑了我   4 互相拉黑
    private fun updateBlockStatus() {
        binding.apply {
            when (matchBean.isblock) {
                1 -> {
                    userContent.isVisible = true
                    llBlackContent.isVisible = false
                }
                2 -> {
                    llBlackContent.isVisible = true
                    userContent.isVisible = false
                    cancelBlack.isVisible = true
                    blackContent.text = getString(R.string.black_you_did_content)
                }
                3 -> {
                    llBlackContent.isVisible = true
                    userContent.isVisible = false
                    cancelBlack.isVisible = false
                    blackContent.text = getString(R.string.black_she_did_content)
                }
                4 -> {
                    llBlackContent.isVisible = true
                    userContent.isVisible = false
                    cancelBlack.isVisible = true
                    blackContent.text = getString(R.string.black_you_did_content)
                }
                else -> {
                    userContent.isVisible = true
                    llBlackContent.isVisible = false
                }
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


    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
        headBinding.travelAduio.release()
    }

}