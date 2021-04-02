package com.sdy.luxurytravelapplication.ui.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.google.android.flexbox.*
import com.kongzue.dialog.util.DialogSettings
import com.kongzue.dialog.util.TextInfo
import com.kongzue.dialog.v3.BottomMenu
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivitySquareCommentDetailBinding
import com.sdy.luxurytravelapplication.event.RefreshCommentEvent
import com.sdy.luxurytravelapplication.event.RefreshDeleteSquareEvent
import com.sdy.luxurytravelapplication.event.RefreshLikeEvent
import com.sdy.luxurytravelapplication.event.RefreshSquareEvent
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.contract.SquareCommentDetailContract
import com.sdy.luxurytravelapplication.mvp.model.bean.AllCommentBean
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.CommentBean
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareBean
import com.sdy.luxurytravelapplication.mvp.presenter.SquareCommentDetailPresenter
import com.sdy.luxurytravelapplication.ui.adapter.ListSquareImgsAdapter
import com.sdy.luxurytravelapplication.ui.adapter.MultiListCommentAdapter
import com.sdy.luxurytravelapplication.ui.adapter.SquareTitleAdapter
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.widgets.switchplay.SwitchUtil
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity

/**
 * 动态详情
 */
class SquareCommentDetailActivity :
    BaseMvpActivity<SquareCommentDetailContract.View, SquareCommentDetailContract.Presenter, ActivitySquareCommentDetailBinding>(),
    SquareCommentDetailContract.View, OnRefreshListener, OnLoadMoreListener, View.OnClickListener {

    override fun createPresenter(): SquareCommentDetailContract.Presenter =
        SquareCommentDetailPresenter()


    private val adapter: MultiListCommentAdapter by lazy { MultiListCommentAdapter() }
    private var squareBean: SquareBean? = null
    private var page = 1
    private val commentParams = hashMapOf(
        "square_id" to "",
        "page" to page,
        "pagesize" to Constants.PAGESIZE
    )
    private val type by lazy { intent.getIntExtra("type", TYPE_SQUARE) }

    companion object {
        const val TYPE_SQUARE = 1
        const val TYPE_SWEET = 2
        fun start(
            context: Context,
            squareBean: SquareBean? = null,
            squareId: Int? = null,
            position: Int? = 0,
            type: Int = TYPE_SQUARE, gender: Int = 0
        ) {
            context.startActivity<SquareCommentDetailActivity>(
                if (squareBean != null) {
                    "squareBean" to squareBean
                } else {
                    "" to ""
                },
                if (squareId != null) {
                    "square_id" to squareId
                } else {
                    "" to ""
                },
                "position" to position,
                "type" to type,
                "gender" to gender
            )
        }

    }


    override fun initData() {
        binding.apply {
            mLayoutStatusView = binding.root
            if (type == TYPE_SWEET) {
                barCl.actionbarTitle.text = if (UserManager.gender == 1) {
                    getString(R.string.he)
                } else {
                    getString(R.string.she)
                } + getString(R.string.verify_info)
            } else {
                barCl.actionbarTitle.text = getString(R.string.square_detail)
            }
            barCl.divider.isVisible = true
            refreshLayout.setOnRefreshListener(this@SquareCommentDetailActivity)
            refreshLayout.setOnLoadMoreListener(this@SquareCommentDetailActivity)

            commentList.layoutManager =
                LinearLayoutManager(this@SquareCommentDetailActivity, RecyclerView.VERTICAL, false)
            commentList.adapter = adapter
            adapter.setEmptyView(R.layout.empty_layout_comment)
            adapter.addChildClickViewIds(R.id.llCommentDianzanBtn, R.id.commentReplyBtn)
            adapter.setOnItemLongClickListener { adapter, view, position ->
                showCommentDialog(position)
                true
            }
            adapter.setOnItemClickListener { _, _, position ->
                reply = true
                reply_id = adapter.data[position].id!!.toInt()
                showCommentEt.isFocusable = true
                showCommentEt.hint =
                    "『${getString(R.string.reply)}" + "\t${adapter.data[position].nickname}" + "：』"
                KeyboardUtils.showSoftInput(showCommentEt)
            }

            adapter.setOnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.llCommentDianzanBtn -> {
                        mPresenter?.getCommentLike(
                            hashMapOf(
                                "reply_id" to adapter.data[position].id!!,
                                "type" to if (adapter.data[position].isliked == 0) {
                                    1
                                } else {
                                    2
                                }
                            )
                            , position
                        )

                    }
                    R.id.commentReplyBtn -> {
                        reply = true
                        reply_id = adapter.data[position].id!!
                        showCommentEt.hint =
                            "『${getString(R.string.reply)}${adapter.data[position].replyed_nickname}：』"
                        KeyboardUtils.showSoftInput(showCommentEt)
                    }
                }
            }


            showCommentEt.setHorizontallyScrolling(false)
            showCommentEt.maxLines = 2
            showCommentEt.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEND && showCommentEt.text.trim().isNotEmpty()
                ) {
                    mPresenter?.addComment(
                        hashMapOf(
                            "square_id" to squareBean!!.id!!,
                            "content" to binding.showCommentEt.text.toString(),
                            "reply_id" to reply_id
                        )
                    )
                    false
                } else
                    true
            }


            barCl.btnBack.setOnClickListener(this@SquareCommentDetailActivity)
            bottomLayout.squareZhuanfaBtn1.setOnClickListener(this@SquareCommentDetailActivity)
            bottomLayout.squareDianzanAni.setOnClickListener(this@SquareCommentDetailActivity)
            bottomLayout.squareCommentBtn1.setOnClickListener(this@SquareCommentDetailActivity)
            topLayout.squareChatBtn1.setOnClickListener(this@SquareCommentDetailActivity)
            topLayout.squareUserIv1.setOnClickListener(this@SquareCommentDetailActivity)
            bottomLayout.squareMoreBtn1.setOnClickListener(this@SquareCommentDetailActivity)


        }
        if (intent.getSerializableExtra("squareBean") != null) {
            mLayoutStatusView?.showContent()
            squareBean = intent.getSerializableExtra("squareBean") as SquareBean
            setData()
            commentParams["square_id"] = "${squareBean!!.id}"
        } else {
            mLayoutStatusView?.showLoading()
            commentParams["square_id"] = "${intent.getIntExtra("square_id", 0)}"
        }
    }

    override fun start() {
        if (intent.getSerializableExtra("squareBean") != null) {
            mPresenter?.getCommentList(commentParams, true)
        } else {
            mPresenter?.getSquareInfo(
                hashMapOf(
                    "square_id" to intent.getIntExtra("square_id", 0)
                )
            )
        }
    }

    private fun setData() {
        if (type == TYPE_SWEET && squareBean!!.accid == UserManager.accid) {
            binding.barCl.actionbarTitle.text = getString(R.string.my_info_title)
        }

        when {
            squareBean!!.type == 1 -> {
                binding.squareUserPics.visibility = View.VISIBLE
                initPics()
            }
            squareBean!!.type == 2 -> {
                binding.squareUserVideo.visibility = View.VISIBLE
                initVideo()
            }
            else -> {
                binding.audioCl.isVisible = true
                initAudio()
            }
        }


        GlideUtil.loadAvatorImg(this, squareBean!!.avatar, binding.topLayout.squareUserIv1)
        if (!squareBean!!.tags.isNullOrEmpty()) {
            binding.bottomLayout.squareTagName.text = squareBean!!.tags ?: ""
            binding.bottomLayout.squareTagLl.isVisible = true
        } else {
            binding.bottomLayout.squareTagLl.isVisible = false
        }


        //标题跳转
        val manager = FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP)
        manager.alignItems = AlignItems.STRETCH
        manager.justifyContent = JustifyContent.FLEX_START
        binding.bottomLayout.squareTitleRv.layoutManager = manager
        val titleAdapter = SquareTitleAdapter()
        titleAdapter.addData(squareBean!!.title_list ?: mutableListOf())
        binding.bottomLayout.squareTitleRv.adapter = titleAdapter
        titleAdapter.setOnItemClickListener { _, view, position ->
            startActivity<TagDetailCategoryActivity>(
                "id" to titleAdapter.data[position].id,
                "type" to TagDetailCategoryActivity.TYPE_TOPIC
            )
        }
        binding.bottomLayout.squareTitleRv.isVisible = !squareBean!!.title_list.isNullOrEmpty()
        setLikeStatus(
            squareBean!!.isliked,
            squareBean!!.like_cnt,
            binding.bottomLayout.squareDianzanBtn1,
            binding.bottomLayout.squareDianzanAni,
            false
        )

        if (intent.getIntExtra("position", -1) != -1)
            EventBus.getDefault().post(
                RefreshLikeEvent(
                    squareBean?.id ?: 0,
                    squareBean?.isliked ?: 0,
                    intent.getIntExtra("position", -1),
                    if (squareBean!!.like_cnt < 0) {
                        0
                    } else {
                        squareBean!!.like_cnt
                    }
                )
            )

        binding.bottomLayout.squareCommentBtn1.text = "${squareBean!!.comment_cnt}"
        binding.bottomLayout.squareContent1.isVisible = !squareBean!!.descr.isNullOrEmpty()
        if (!squareBean!!.descr.isNullOrEmpty()) {
            binding.bottomLayout.squareContent1.setContent("${squareBean!!.descr}")
        }

        if (squareBean!!.approve_type != 0) {
            binding.bottomLayout.squareTagLl.isVisible = false
            binding.bottomLayout.squareLocationAndTime1Ll.isVisible = false
            binding.bottomLayout.squareTagName.isVisible = false
            binding.topLayout.squareUserSweetLogo.isVisible = true
            binding.bottomLayout.squareSweetVerifyContentCl.isVisible = true
            val params =
                binding.bottomLayout.squareSweetVerifyContentCl.layoutParams as ConstraintLayout.LayoutParams
            params.width = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(15 * 2F)
            params.height = (params.width * (285F / 1065F)).toInt()

            //// 0普通 1资产认证 2豪车认证 3 身材认证 4 职业认证
//            if (squareBean!!.approve_type == 1 || squareBean!!.approve_type == 2 || squareBean!!.approve_type == 5) {
//                binding.bottomLayout.squareContent1.setTextColor(Color.parseColor("#FFFFCD52"))
//
//                binding.topLayout.squareUserSweetLogo.imageAssetsFolder = "images_sweet_logo_man"
//                binding.topLayout.squareUserSweetLogo.setAnimation("data_sweet_logo_man.json")
//                binding.topLayout.squareUserSweetLogo.playAnimation()
//            } else {
//                binding.bottomLayout.squareContent1.setTextColor(Color.parseColor("#FFFF7CA8"))
//                binding.topLayout.squareUserSweetLogo.imageAssetsFolder =
//                    "images_sweet_logo_woman"
//                binding.topLayout.squareUserSweetLogo.setAnimation("data_sweet_logo_woman.json")
//                binding.topLayout.squareUserSweetLogo.playAnimation()
//
//            }

            binding.bottomLayout.squareSweetVerifyName.text = squareBean!!.assets_audit_descr
            binding.bottomLayout.squareSweetVerifyContent.text = when (squareBean!!.approve_type) {
                1 -> {
                    getString(R.string.sweet_rich_user)
                }
                2 -> {
                    getString(R.string.sweet_luxury_car)
                }
                3 -> {
                    getString(R.string.sweet_good_shencai)
                }
                4 -> {
                    getString(R.string.sweet_job)
                }
                6 -> {
                    getString(R.string.sweet_education)
                }
                else -> {
                    ""
                }

            }
            binding.bottomLayout.squareSweetVerifyContentCl.setBackgroundResource(R.drawable.icon_index_recommend_luxury_bg)
        } else {
            binding.topLayout.squareUserSweetLogo.isVisible = false
            binding.bottomLayout.squareSweetVerifyContentCl.isVisible = false
            binding.bottomLayout.squareContent1.setTextColor(Color.parseColor("#FF191919"))

            if (squareBean!!.puber_address.isNullOrEmpty()) {
                binding.bottomLayout.squareLocationAndTime1Ll.visibility = View.INVISIBLE
            } else {
                binding.bottomLayout.squareLocationAndTime1Ll.isVisible = true
            }

            if (!squareBean!!.tags.isNullOrEmpty()) {
                binding.bottomLayout.squareTagName.text = squareBean!!.tags
                binding.bottomLayout.squareTagLl.isVisible = true
            } else {
                binding.bottomLayout.squareTagLl.isVisible = false
            }

        }



        binding.bottomLayout.squareZhuanfaBtn1.text = "${squareBean!!.share_cnt}"
        binding.topLayout.squareUserName1.text = "${squareBean!!.nickname}"
        binding.topLayout.squareUserVipIv1.isVisible =
            squareBean!!.isplatinumvip || squareBean!!.isdirectvip
        if (squareBean!!.isplatinumvip) {
            binding.topLayout.squareUserVipIv1.setImageResource(R.drawable.icon_vip)
        } else if (squareBean!!.isdirectvip) {
            binding.topLayout.squareUserVipIv1.setImageResource(R.drawable.icon_vip_connnect)
        }

        if (squareBean!!.isfriend) {
            binding.topLayout.squareChatBtn1.isVisible = true
        } else {
            binding.topLayout.squareChatBtn1.visibility =
                if (!(UserManager.accid == squareBean!!.accid || !squareBean!!.greet_switch)) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
        }

        binding.topLayout.squareTime.text = "${squareBean!!.out_time}"
        binding.bottomLayout.squareLocation.text = "${squareBean!!.puber_address}"
    }


    private fun initAudio() {
        binding.audioCl.prepareAudio(
            squareBean!!.audio_json!!.get(0).url,
            squareBean!!.audio_json!!.get(0).duration,autoPlay = true
        )
    }


    private fun initVideo() {
        SwitchUtil.optionPlayer(
            binding.squareUserVideo,
            squareBean!!.video_json?.get(0)?.url ?: "",
            true
        )
        binding.squareUserVideo.setUp(
            squareBean!!.video_json?.get(0)?.url ?: "",
            false,
            null,
            null,
            ""
        )
        binding.squareUserVideo.startPlayLogic()
        binding.squareUserVideo.binding.detailBtn.setOnClickListener {
            if (binding.squareUserVideo.isInPlayingState) {
                SwitchUtil.savePlayState(binding.squareUserVideo)
                binding.squareUserVideo.gsyVideoManager.setLastListener(binding.squareUserVideo)
//                SquarePlayDetailActivity.startActivity(this,  binding. squareUserVideo, squareBean!!, 0)
            }
        }
    }


    /**
     * 初始化图片列表
     */
    private val imgsAdapter by lazy { ListSquareImgsAdapter() }

    private fun initPics() {
        if (squareBean!!.photo_json != null && squareBean!!.photo_json!!.size > 0) {
            if (squareBean!!.photo_json!!.size >= 3) {
                binding.squareUserPics.layoutManager = GridLayoutManager(this, 3)
                imgsAdapter.spanCnt = 3
            } else {
                binding.squareUserPics.layoutManager = GridLayoutManager(this, 2)
                imgsAdapter.spanCnt = 2
            }
            binding.squareUserPics.adapter = imgsAdapter
            imgsAdapter.setNewInstance(squareBean!!.photo_json)
            imgsAdapter.setOnItemClickListener { _, view, position ->
                if (squareBean!!.isliked != 1) {
                    val params = hashMapOf<String, Any>(
                        "type" to if (squareBean!!.isliked == 1) {
                            2
                        } else {
                            1
                        },
                        "square_id" to squareBean!!.id!!
                    )
                    mPresenter?.getSquareLike(params, true)
                }

//                startActivity<BigImageActivity>(
//                    BigImageActivity.IMG_KEY to squareBean!!,
//                    BigImageActivity.IMG_POSITION to position
//                )
            }
        }

    }


    //判断当前是添加评论还是回复评论
    private var reply = false
    private var reply_id = 0


    private fun showCommentDialog(position: Int) {
        val mine = adapter.data[position].member_accid == UserManager.accid
        val datas = if (mine) {
            listOf("复制评论", "回复评论", "删除评论")
        } else {
            listOf("复制评论", "回复评论", "举报评论")
        }
        BottomMenu.show(this, datas) { text: String, index: Int ->
            when (index) {
                0 -> {
                    copyText(position)
                }
                1 -> {
                    reply = true
                    reply_id = adapter.data[position].id!!
                    binding.showCommentEt.hint =
                        "『${getString(R.string.reply)}\t${adapter.data[position].nickname}：』"
                    binding.showCommentEt.postDelayed(
                        { KeyboardUtils.showSoftInput(binding.showCommentEt) },
                        100L
                    )

                }
                2 -> {
                    if (mine) {
                        mPresenter?.deleteComment(
                            hashMapOf("id" to adapter.data[position].id!!),
                            position
                        )
                    } else {
                        //举报
                        mPresenter?.commentReport(
                            hashMapOf("id" to adapter.data[position].id!!),
                            position
                        )
                    }
                }
            }
        }
            .setStyle(DialogSettings.STYLE.STYLE_IOS)
            .setShowCancelButton(false)
            .menuTextInfo =
            TextInfo().setFontColor(resources.getColor(R.color.color333)).setFontSize(16)

    }


    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshLayout.resetNoMoreData()
        page = 1
        adapter.data.clear()
        commentParams["page"] = page
        mPresenter?.getCommentList(commentParams, true)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        page++
        commentParams["page"] = page
        mPresenter?.getCommentList(commentParams, false)
    }


    override fun onGetSquareInfoResults(data: SquareBean?) {
        if (data != null) {
            mLayoutStatusView?.showContent()
            if (data.id == 0) {
                ToastUtil.toast(getString(R.string.remove_square))
                finish()
                return
            }
            data.type = when {
                !data.video_json.isNullOrEmpty() -> SquareBean.VIDEO
                !data.audio_json.isNullOrEmpty() -> SquareBean.AUDIO
                !data.photo_json.isNullOrEmpty() ||
                        (data.photo_json.isNullOrEmpty() && data.audio_json.isNullOrEmpty() && data.video_json.isNullOrEmpty()) -> SquareBean.PIC
                else -> SquareBean.PIC
            }
            squareBean = data
            setData()
            if (type == TYPE_SQUARE) {
                commentParams["square_id"] = "${squareBean!!.id}"
                mPresenter?.getCommentList(commentParams, true)
            }
        } else {
            ToastUtil.toast(getString(R.string.remove_square))
            finish()
        }
    }

    override fun onGetCommentListResult(allCommentBean: AllCommentBean?) {
        if (binding.refreshLayout.state != RefreshState.Loading) {
            binding.refreshLayout.setNoMoreData(false)
            if (allCommentBean != null) {
                if (allCommentBean.hotlist != null && allCommentBean.hotlist!!.size > 0) {
                    adapter.addData(
                        CommentBean(
                            content = getString(R.string.hot_comment),
                            itemType = CommentBean.TITLE

                        )
                    )
                    for (i in 0 until allCommentBean.hotlist!!.size) {
                        allCommentBean.hotlist!![i].itemType = CommentBean.CONTENT
                    }
                    adapter.addData(allCommentBean.hotlist!!)

                }
                if (allCommentBean.list != null && allCommentBean.list!!.size > 0) {
                    adapter.addData(
                        CommentBean(
                            content = getString(R.string.all_comment),
                            itemType = CommentBean.TITLE
                        )
                    )
                    for (i in 0 until allCommentBean.list!!.size) {
                        allCommentBean.list!![i].itemType = CommentBean.CONTENT
                    }
                }
                adapter.addData(CommentBean("", "hhhhhhhhhhhhh", "十分钟之前", nickname = "aaa"))
                adapter.addData(CommentBean("", "hhhhhhhhhhhhh", "十分钟之前", nickname = "aaa"))
                adapter.addData(CommentBean("", "hhhhhhhhhhhhh", "十分钟之前", nickname = "aaa"))
                adapter.addData(CommentBean("", "hhhhhhhhhhhhh", "十分钟之前", nickname = "aaa"))
            }
            binding.refreshLayout.finishRefresh(true)
        } else {
            if (allCommentBean != null) {
                if ((allCommentBean.hotlist == null || allCommentBean.hotlist!!.size == 0) && (allCommentBean.list == null || allCommentBean.list!!.size == 0)) {
                    binding.refreshLayout.finishLoadMoreWithNoMoreData()
                    return
                }

                if (allCommentBean.hotlist != null && allCommentBean.hotlist!!.size > 0) {
                    for (i in 0 until allCommentBean.hotlist!!.size) {
                        allCommentBean.hotlist!![i].type = 1
                    }
                    adapter.addData(allCommentBean.hotlist!!)
                }
                if (allCommentBean.list != null && allCommentBean.list!!.size > 0) {
                    for (i in 0 until allCommentBean.list!!.size) {
                        allCommentBean.list!![i].type = 1
                    }
                    adapter.addData(allCommentBean.list!!)
                }
            }
            binding.refreshLayout.finishLoadMore(true)
        }
        if (adapter.data.isNullOrEmpty() || adapter.data.size < Constants.PAGESIZE) {
            binding.refreshLayout.finishLoadMoreWithNoMoreData()
        }
    }

    override fun onGetSquareCollectResult(data: BaseResp<Any>) {
        ToastUtil.toast(data.msg)
        if (data.code == 200) {
            squareBean!!.iscollected = if (squareBean!!.iscollected == 1) {
                0
            } else {
                1
            }
            EventBus.getDefault().post(RefreshSquareEvent(true, TAG))
        }
    }


    override fun onGetSquareLikeResult(result: Boolean) {
        if (result) {
            squareBean!!.isliked = if (squareBean!!.isliked == 0) {
                squareBean!!.like_cnt = squareBean!!.like_cnt.plus(1)
                1
            } else {
                squareBean!!.like_cnt = squareBean!!.like_cnt.minus(1)
                0
            }
            setLikeStatus(
                squareBean!!.isliked,
                squareBean!!.like_cnt,
                binding.bottomLayout.squareDianzanBtn1,
                binding.bottomLayout.squareDianzanAni,
                true
            )

            if (intent.getIntExtra("position", -1) != -1)
                EventBus.getDefault().post(
                    RefreshLikeEvent(
                        squareBean?.id ?: 0,
                        squareBean?.isliked ?: 0,
                        intent.getIntExtra("position", -1)
                    )
                )
//            EventBus.getDefault().post(RefreshSquareEvent(true, TAG))
        }
    }

    override fun onGetSquareReport(data: BaseResp<Any>) {
        ToastUtil.toast(data.msg)
    }


    override fun onAddCommentResult(data: BaseResp<Any>, result: Boolean) {
        resetCommentEt()
        if (result) {
            page = 1
            adapter.data.clear()
            commentParams["page"] = page
            mPresenter?.getCommentList(commentParams, true)
            squareBean!!.comment_cnt = squareBean!!.comment_cnt.plus(1)
            EventBus.getDefault().post(
                RefreshCommentEvent(
                    squareBean!!.comment_cnt,
                    intent.getIntExtra("position", 0)
                )
            )
            binding.bottomLayout.squareCommentBtn1.text = "${squareBean!!.comment_cnt}"
        }
    }

    override fun onRemoveMySquareResult(b: Boolean) {
        if (b) {
            ToastUtil.toast(getString(R.string.delete_success))
            EventBus.getDefault().post(RefreshSquareEvent(true, TAG))
            EventBus.getDefault().post(
                RefreshDeleteSquareEvent(
                    if (squareBean != null) {
                        squareBean!!.id ?: 0
                    } else {
                        intent.getIntExtra("square_id", 0)
                    }
                )
            )
            finish()
        } else {
            ToastUtil.toast(getString(R.string.delete_fail))
        }
    }

    override fun onLikeCommentResult(data: BaseResp<Any>, position: Int) {
        if (data.code == 200) {
            adapter.data[position].isliked = if (adapter.data[position].isliked == 0) {
                adapter.data[position].like_count = adapter.data[position].like_count!!.plus(1)
                1
            } else {
                adapter.data[position].like_count = adapter.data[position].like_count!!.minus(1)
                0
            }
            adapter.notifyItemChanged(position)
        } else {
            ToastUtil.toast(data.msg)
        }
    }

    override fun onDeleteCommentResult(data: BaseResp<Any>, position: Int) {
        if (data.msg == getString(R.string.delete_success)) {
            adapter.data.removeAt(position)
            adapter.notifyItemRemoved(position)
            squareBean!!.comment_cnt = squareBean!!.comment_cnt.minus(1)
            EventBus.getDefault().post(
                RefreshCommentEvent(
                    squareBean!!.comment_cnt,
                    intent.getIntExtra("position", 0)
                )
            )
            binding.bottomLayout.squareCommentBtn1.text = "${squareBean!!.comment_cnt}"
        } else {
            ToastUtil.toast(data.msg)
        }

    }

    override fun onReportCommentResult(data: BaseResp<Any>, position: Int) {
        ToastUtil.toast(data.msg)
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.squareZhuanfaBtn1 -> {
                showTranspondDialog()
            }
            R.id.btnBack -> {
                onBackPressed()
            }
            R.id.squareDianzanAni -> {
                val params = hashMapOf<String, Any>(
                    "type" to if (squareBean!!.isliked == 1) {
                        2
                    } else {
                        1
                    },
                    "square_id" to squareBean!!.id!!
                )
                mPresenter?.getSquareLike(params)
            }
            R.id.squareCommentBtn1 -> {
                binding.squareScrollView.smoothScrollTo(
                    binding.commentList.left,
                    binding.commentList.top
                )
            }
            R.id.squareMoreBtn1 -> {
                showMoreDialog()
            }
            R.id.squareChatBtn1 -> {
                CommonFunction.checkChat(this, squareBean?.accid ?: "")
            }
            R.id.squareUserIv1 -> {
                if ((squareBean?.accid ?: "") != UserManager.accid)
                    TargetUserActivity.start(this, squareBean?.accid ?: "")
            }
        }
    }

    private fun showMoreDialog() {


    }

    private fun showTranspondDialog() {


    }

    /**
     * 重置输入框，清除焦点，隐藏键盘
     */
    private fun resetCommentEt() {
        reply = false
        reply_id = 0
        binding.showCommentEt.clearFocus()
        binding.showCommentEt.text.clear()
        binding.showCommentEt.hint = getString(R.string.say_your_feel)
        KeyboardUtils.hideSoftInput(binding.showCommentEt)
    }

    private fun setLikeStatus(
        isliked: Int,
        likeCount: Int,
        likeView: TextView,
        likeAni: ImageView,
        animated: Boolean = true
    ) {
        if (isliked == 1) {
            likeAni.setImageResource(R.drawable.icon_zan_comment_checked)
        } else {
            likeAni.setImageResource(R.drawable.icon_zan_normal)
        }

        likeView.text = "${if (likeCount < 0) {
            0
        } else {
            likeCount
        }}"
    }


    private fun copyText(position: Int) {
        //获取剪贴板管理器
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        //创建普通字符串clipData
        val clipData = ClipData.newPlainText("label", "${adapter.data[position].content}")
        //将clipdata内容放到系统剪贴板里
        cm.setPrimaryClip(clipData)
        ToastUtil.toast(getString(R.string.has_copy))
    }


    override fun onPause() {
        super.onPause()
        Log.d(TAG, "super.onPause()")
        if (binding.audioCl.isPlaying())
            binding.audioCl.pauseAudio()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "super.onStart()")
        GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_DEFAULT)
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "super.onResume()")
        binding.squareUserVideo.onVideoResume(false)
        if (binding.audioCl.isPause())
            binding.audioCl.resumeAudio()

//        squareUserVideo.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "super.onDestroy()")
        binding.audioCl.release()
        if (binding.showCommentEt.isFocused)
            resetCommentEt()
    }

    override fun finish() {
        super.finish()
        binding.audioCl.release()
        if (binding.showCommentEt.isFocused)
            resetCommentEt()
        //释放所有
        binding.squareUserVideo.gsyVideoManager.setListener(binding.squareUserVideo.gsyVideoManager.lastListener())
        binding.squareUserVideo.gsyVideoManager.setLastListener(null)
        binding.squareUserVideo.release()
        GSYVideoManager.releaseAllVideos()
        SwitchUtil.release()
    }

    override fun onBackPressed() {
        if (binding.showCommentEt.isFocused) {
            resetCommentEt()
        }
        binding.audioCl.release()

        //释放所有
        binding.squareUserVideo.gsyVideoManager.setListener(binding.squareUserVideo.gsyVideoManager.lastListener())
        binding.squareUserVideo.gsyVideoManager.setLastListener(null)
        binding.squareUserVideo.release()
        GSYVideoManager.releaseAllVideos()
        SwitchUtil.release()
        super.onBackPressed()
    }

}