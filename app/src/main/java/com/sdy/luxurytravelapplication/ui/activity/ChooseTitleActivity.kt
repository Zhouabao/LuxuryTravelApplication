package com.sdy.luxurytravelapplication.ui.activity

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ClickUtils
import com.google.android.flexbox.*
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.ActivityChooseTitleBinding
import com.sdy.luxurytravelapplication.mvp.contract.ChooseTitleContract
import com.sdy.luxurytravelapplication.mvp.model.bean.LabelQualityBean
import com.sdy.luxurytravelapplication.mvp.presenter.ChooseTitlePresenter
import com.sdy.luxurytravelapplication.ui.adapter.ChooseTitleAdapter
import com.sdy.luxurytravelapplication.utils.ToastUtil

/**
 * 发布选择话题
 */
class ChooseTitleActivity :
    BaseMvpActivity<ChooseTitleContract.View, ChooseTitleContract.Presenter, ActivityChooseTitleBinding>(),
    ChooseTitleContract.View, OnLoadMoreListener, OnRefreshListener {
    override fun createPresenter(): ChooseTitleContract.Presenter = ChooseTitlePresenter()

    private var page = 1


    private val adapter by lazy { ChooseTitleAdapter() }
    private var limitCount = 0
    override fun initData() {
        binding.apply {
            mLayoutStatusView = binding.root
            refreshTitle.setOnLoadMoreListener(this@ChooseTitleActivity)
            refreshTitle.setOnRefreshListener(this@ChooseTitleActivity)
            barCl.actionbarTitle.text = getString(R.string.label_title_hot)
            barCl.rightTextBtn.text = getString(R.string.complete)
            barCl.rightTextBtn.setTextColor(resources.getColor(R.color.colorAccent))
            barCl.rightTextBtn.isVisible = true
            ClickUtils.applySingleDebouncing(arrayOf(barCl.rightTextBtn, barCl.btnBack)) {
                when (it) {
                    barCl.rightTextBtn -> {
                        var topics = ""
                        if (titleEt.text.trim().isNotEmpty()) {
                            topics = titleEt.text.trim().toString()
                        } else {
                            for (data in adapter.data) {
                                if (data.isfuse) {
                                    topics=data.content
                                    break
                                }
                            }
                        }

                        intent.putExtra("title", topics)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    barCl.btnBack -> {
                        finish()
                    }
                }
            }

            val manager =
                FlexboxLayoutManager(this@ChooseTitleActivity, FlexDirection.ROW, FlexWrap.WRAP)
            manager.alignItems = AlignItems.STRETCH
            manager.justifyContent = JustifyContent.FLEX_START
            titleRv.layoutManager = manager
            titleRv.adapter = adapter
            adapter.setOnItemClickListener { _, view, position ->
//                if (!adapter.data[position].isfuse) {
//                    var chooseCount = 0
//                    for (data in adapter.data) {
//                        if (data.isfuse) {
//                            chooseCount++
//                        }
//                    }
                if (titleEt.text.trim().toString().isNotEmpty()) {
//                        chooseCount++
                    ToastUtil.toast("只能选择一个话题")
                    return@setOnItemClickListener
                }
                adapter.data.forEach {
                    it.isfuse = it == adapter.data[position]
                }
                adapter.notifyDataSetChanged()
//                    if (chooseCount >= limitCount) {
//                        ToastUtil.toast(
//                            getString(R.string.lable_title_most, limitCount)
//                        )
//                        return@setOnItemClickListener
//                    }
//                }


//                adapter.data[position].isfuse = !adapter.data[position].isfuse
//                adapter.notifyItemChanged(position)
            }




            titleEt.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    var chooseCount = 0
                    for (data in adapter.data) {
                        if (data.isfuse) {
                            chooseCount++
                        }
                    }
                    if (chooseCount >= limitCount) {
                        ToastUtil.toast(getString(R.string.lable_title_most, limitCount))
                        titleEt.text.clear()
                    } else
                        titleEt.setSelection(titleEt.text.length)
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (!s.isNullOrEmpty() && s.length > 12) {
                        titleEt.setText(s.subSequence(0, 12))
                        ToastUtil.toast(getString(R.string.label_title_max_length))
                        return
                    }
                }

            })

        }
    }

    override fun start() {
        mLayoutStatusView?.showLoading()
        mPresenter?.getTagTitleList(page)
    }

    override fun getTagTraitInfoResult(
        b: Boolean,
        data: MutableList<LabelQualityBean>?,
        maxCount: Int
    ) {
        limitCount = maxCount
        if (binding.refreshTitle.state == RefreshState.Refreshing) {
            adapter.data.clear()
            binding.refreshTitle.finishRefresh(b)
        } else {
            if (data?.size ?: 0 < Constants.PAGESIZE)
                binding.refreshTitle.finishLoadMoreWithNoMoreData()
            else
                binding.refreshTitle.finishLoadMore(b)
        }
        if (b) {
            mLayoutStatusView?.showContent()
        } else {
            mLayoutStatusView?.showError()
        }

        if (b && !data.isNullOrEmpty()) {
            val topics = intent.getStringArrayListExtra("title")
//            for (topic in topics) {
//                for (topic1 in data) {
//                    if (topic1.content == topic) {
//                        topic1.isfuse = true
//                    }
//                }
//            }
            adapter.addData(data)
        }

    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        page++
        mPresenter?.getTagTitleList(page)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        page = 1
        mPresenter?.getTagTitleList(page)
    }

}