package com.sdy.luxurytravelapplication.ui.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.kongzue.dialog.util.DialogSettings
import com.kongzue.dialog.util.TextInfo
import com.kongzue.dialog.v3.BottomMenu
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityTravelDetailBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.mvp.contract.TravelDetailContract
import com.sdy.luxurytravelapplication.mvp.model.bean.AllCommentBean
import com.sdy.luxurytravelapplication.mvp.model.bean.BaseResp
import com.sdy.luxurytravelapplication.mvp.model.bean.CommentBean
import com.sdy.luxurytravelapplication.mvp.model.bean.TravelPlanBean
import com.sdy.luxurytravelapplication.mvp.presenter.TravelDetailPresenter
import com.sdy.luxurytravelapplication.ui.adapter.MultiListCommentAdapter
import com.sdy.luxurytravelapplication.ui.adapter.TravelAdapter
import com.sdy.luxurytravelapplication.ui.dialog.CommentVipDialog
import com.sdy.luxurytravelapplication.utils.ToastUtil
import org.jetbrains.anko.startActivity

/**
 * 伴游详情
 */
class TravelDetailActivity :
    BaseMvpActivity<TravelDetailContract.View, TravelDetailContract.Presenter, ActivityTravelDetailBinding>(),
    TravelDetailContract.View, OnRefreshLoadMoreListener {
    override fun createPresenter(): TravelDetailContract.Presenter = TravelDetailPresenter()
    private val adapter: MultiListCommentAdapter by lazy { MultiListCommentAdapter() }

    private val travelAdapter by lazy { TravelAdapter() }
    private var page = 1
    private val commentParams = hashMapOf(
        "dating_id" to "",
        "page" to page,
        "pagesize" to Constants.PAGESIZE
    )

    companion object {
        fun start(
            context: Context,
            travelPlanBean: TravelPlanBean? = null,
            dating_id: Int? = null
        ) {
            if (travelPlanBean != null) {
                context.startActivity<TravelDetailActivity>("TravelPlanBean" to travelPlanBean)
            } else if (dating_id != null) {
                context.startActivity<TravelDetailActivity>("dating_id" to dating_id)
            }
        }
    }

    private var travelPlanBean: TravelPlanBean? = null
    private val dating_id by lazy { intent.getIntExtra("dating_id", -1) }


    override fun initData() {
        travelPlanBean = intent.getSerializableExtra("TravelPlanBean") as TravelPlanBean?
        binding.apply {
            mLayoutStatusView = root
            barCl.actionbarTitle.text = "伴游详情"
            barCl.divider.isVisible = true
            barCl.rightIconBtn.isVisible = false
            barCl.rightIconBtn.setImageResource(R.drawable.icon_more_black)
            ClickUtils.applySingleDebouncing(arrayOf(barCl.rightIconBtn, barCl.btnBack)) {
                when (it) {
                    barCl.rightIconBtn -> {
                    }
                    barCl.btnBack -> {
                        finish()
                    }
                }
            }


            refreshLayout.setOnRefreshLoadMoreListener(this@TravelDetailActivity)


            travelPlanRv.layoutManager =
                LinearLayoutManager(this@TravelDetailActivity, RecyclerView.VERTICAL, false)
            travelPlanRv.adapter = travelAdapter

            commentList.layoutManager =
                LinearLayoutManager(this@TravelDetailActivity, RecyclerView.VERTICAL, false)
            commentList.adapter = adapter

            adapter.setEmptyView(R.layout.empty_layout_comment)
            adapter.addChildClickViewIds(R.id.llCommentDianzanBtn, R.id.commentReplyBtn)
            adapter.setOnItemLongClickListener { _, view, position ->
                if (adapter.data[position].itemType == CommentBean.CONTENT) {
                    showCommentDialog(position)
                }
                true
            }
            adapter.setOnItemClickListener { _, _, position ->
                if (adapter.data[position].itemType == CommentBean.CONTENT) {
                    reply = true
                    reply_id = adapter.data[position].id!!.toInt()
                    showCommentEt.isFocusable = true
                    showCommentEt.hint =
                        "『${getString(R.string.reply)}" + "\t${adapter.data[position].nickname}" + "：』"
                    KeyboardUtils.showSoftInput(showCommentEt)
                }
            }

            adapter.setOnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.llCommentDianzanBtn -> {
                        mPresenter?.commentPlanLike(
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
                        if (adapter.data[position].itemType == CommentBean.CONTENT) {
                            reply = true
                            reply_id = adapter.data[position].id!!
                            showCommentEt.hint =
                                "『${getString(R.string.reply)}${adapter.data[position].nickname}：』"
                            KeyboardUtils.showSoftInput(showCommentEt)
                        }
                    }
                }
            }


            showCommentEt.setHorizontallyScrolling(false)
            showCommentEt.maxLines = 2
            showCommentEt.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEND && showCommentEt.text.trim()
                        .isNotEmpty()
                ) {
                    ToastUtil.toast(binding.showCommentEt.text.toString())
                    if (travelPlanBean != null)
                        mPresenter?.addCommentPlan(
                            hashMapOf(
                                "dating_id" to travelPlanBean!!.id,
                                "content" to binding.showCommentEt.text.toString(),
                                "reply_id" to reply_id
                            )
                        )
                    false
                } else
                    true
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
                        mPresenter?.delCommentPlan(
                            adapter.data[position].id!!,
                            position
                        )
                    } else {
                        //举报
                        mPresenter?.commentReport(
                            hashMapOf(
                                "content" to adapter.data[position].id!!,
                                "type" to 8,
                                "target_accid" to adapter.data[position].member_accid!!
                            ),
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


    private fun copyText(position: Int) {
        //获取剪贴板管理器
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        //创建普通字符串clipData
        val clipData = ClipData.newPlainText("label", "${adapter.data[position].content}")
        //将clipdata内容放到系统剪贴板里
        cm.setPrimaryClip(clipData)
        ToastUtil.toast(getString(R.string.has_copy))
    }

    override fun start() {
        mLayoutStatusView?.showLoading()
        if (travelPlanBean != null) {
            setData(travelPlanBean!!)
        } else {
            mPresenter?.planInfo(dating_id)
        }
    }

    private fun setData(travelPlanBean: TravelPlanBean) {
        this.travelPlanBean = travelPlanBean
        commentParams["dating_id"] = travelPlanBean.id
        travelAdapter.addData(travelPlanBean)
        mLayoutStatusView?.showContent()
        mPresenter?.getcommentPlan(commentParams)
    }


    override fun planInfo(travelPlanBean: TravelPlanBean?) {
        if (travelPlanBean == null) {
            mLayoutStatusView?.showError()
        } else {
            mLayoutStatusView?.showContent()
            setData(travelPlanBean)
        }

    }

    override fun getcommentPlan(allCommentBean: AllCommentBean?) {
        if (binding.refreshLayout.state != RefreshState.Loading) {
            binding.refreshLayout.setNoMoreData(false)
            if (allCommentBean != null) {
                adapter.data.clear()
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
                            content = getString(R.string.all_comment) + "（${travelPlanBean!!.comment_cnt}）",
                            itemType = CommentBean.TITLE
                        )
                    )
                    for (i in 0 until allCommentBean.list!!.size) {
                        allCommentBean.list!![i].itemType = CommentBean.CONTENT
                    }
                    adapter.addData(allCommentBean.list!!)
                }
                adapter.notifyDataSetChanged()
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
                        allCommentBean.hotlist!![i].type = CommentBean.CONTENT
                    }
                    adapter.addData(allCommentBean.hotlist!!)
                }
                if (allCommentBean.list != null && allCommentBean.list!!.size > 0) {
                    for (i in 0 until allCommentBean.list!!.size) {
                        allCommentBean.list!![i].type = CommentBean.CONTENT
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

    override fun addCommentPlan(code: Int, msg: String) {
        if (code == 200) {
            binding.refreshLayout.autoRefresh()
        }

        resetCommentEt()
        when (code) {
            200 -> {
                binding.refreshLayout.autoRefresh()
                travelPlanBean!!.comment_cnt = travelPlanBean!!.comment_cnt.plus(1)
            }
            202 -> {
                CommonFunction.startToFootPrice(this)
            }
            203 -> {
                CommentVipDialog().show()
            }
            else -> {
                ToastUtil.toast(msg)
            }
        }

    }

    override fun commentPlanLike(success: Int, msg: String, position: Int) {
        if (success == 200) {
            adapter.data[position].isliked = if (adapter.data[position].isliked == 0) {
                adapter.data[position].like_count = adapter.data[position].like_count!!.plus(1)
                1
            } else {
                adapter.data[position].like_count = adapter.data[position].like_count!!.minus(1)
                0
            }
            adapter.notifyItemChanged(position)
        } else {
            ToastUtil.toast(msg)
        }

    }

    override fun delCommentPlan(code: Int, msg: String, position: Int) {
        ToastUtil.toast(msg)
        if (code == 200)
            adapter.removeAt(position)

    }

    override fun commentReport(data: BaseResp<Any>, position: Int) {
        ToastUtil.toast(data.msg)

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

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshLayout.resetNoMoreData()
        page = 1
        adapter.data.clear()
        commentParams["page"] = page
        mPresenter?.getcommentPlan(commentParams)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        page++
        commentParams["page"] = page
        refreshLayout.finishLoadMore()
        mPresenter?.getcommentPlan(commentParams)
    }

    override fun onDestroy() {
        super.onDestroy()
        resetCommentEt()
    }

}