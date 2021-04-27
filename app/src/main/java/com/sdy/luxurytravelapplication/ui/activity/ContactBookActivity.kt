package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.KeyboardUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.ActivityContactBookBinding
import com.sdy.luxurytravelapplication.databinding.HeaderviewContactBinding
import com.sdy.luxurytravelapplication.databinding.ItemContactBookBinding
import com.sdy.luxurytravelapplication.mvp.contract.ContactBookContract
import com.sdy.luxurytravelapplication.mvp.model.LetterComparator
import com.sdy.luxurytravelapplication.mvp.model.bean.ContactBean
import com.sdy.luxurytravelapplication.mvp.model.bean.ContactDataBean
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareBean
import com.sdy.luxurytravelapplication.mvp.presenter.ContactBookPresenter
import com.sdy.luxurytravelapplication.nim.business.session.activity.ChatActivity
import com.sdy.luxurytravelapplication.ui.adapter.ContactAdapter
import com.sdy.luxurytravelapplication.ui.adapter.ContactStarAdapter
import com.sdy.luxurytravelapplication.ui.dialog.ShareToFriendsDialog
import com.sdy.luxurytravelapplication.widgets.sortcontacts.Cn2Spell
import com.sdy.luxurytravelapplication.widgets.sortcontacts.PinnedHeaderDecoration
import com.sdy.luxurytravelapplication.widgets.sortcontacts.WaveSideBarView
import org.jetbrains.anko.startActivity
import java.util.*

/**
 * 通讯录
 * 包括转发到好友也是在这里面操作
 */
class ContactBookActivity :
    BaseMvpActivity<ContactBookContract.View, ContactBookContract.Presenter, ActivityContactBookBinding>(),
    ContactBookContract.View {

    private var sqauareBean: SquareBean? = null

    private val adapter by lazy { ContactAdapter() }
    private val searchAdapter by lazy { ContactStarAdapter(false) }

    companion object {
        fun start(context: Context, squareBean: SquareBean? = null) {
            if (squareBean != null)
                context.startActivity<ContactBookActivity>("square" to squareBean)
            else
                context.startActivity<ContactBookActivity>()
        }
    }

    override fun onGetContactListResult(data: ContactDataBean?) {
        if (data != null) {
            if (!data.list.isNullOrEmpty()) {
                for (data in data.list!!) {
                    data.index = Cn2Spell.getPinYinFirstLetter(
                        if (data.nickname.isNullOrEmpty()) {
                            "#"
                        } else {
                            data.nickname
                        }
                    )
                }

                adapter.setNewInstance(data.list!!)
                Collections.sort(adapter.data, LetterComparator())
                adapter.notifyDataSetChanged()
            } else {
                adapter.notifyDataSetChanged()
            }
            if (!data.asterisk.isNullOrEmpty()) {
                for (data in data.asterisk!!) {
                    data.index = Cn2Spell.getPinYinFirstLetter(
                        if (data.nickname.isEmpty()) {
                            "#"
                        } else {
                            data.nickname
                        }
                    )
                }
                Collections.sort(data.asterisk?: arrayListOf(), LetterComparator())
                headAdapter.setNewInstance(data.asterisk!!)
            } else {
                headAdapter.notifyDataSetChanged()
            }
        } else {
            adapter.notifyDataSetChanged()
            headAdapter.notifyDataSetChanged()
        }
    }

    override fun useEventBus(): Boolean {
        return true
    }

    override fun createPresenter(): ContactBookContract.Presenter {
        return ContactBookPresenter()
    }

    override fun initData() {
        binding.apply {
            barCl.btnBack.setOnClickListener {
                finish()
            }
            if (intent != null && intent.getSerializableExtra("square") != null) {
                barCl.actionbarTitle.text = getString(R.string.choose_friend)
            } else {
                barCl.actionbarTitle.text = getString(R.string.contact_book)
            }


            val manager =
                LinearLayoutManager(this@ContactBookActivity, RecyclerView.VERTICAL, false)
            contactsRv.layoutManager = manager

            val decoration = PinnedHeaderDecoration()
            decoration.registerTypePinnedHeader(1) { _, _ ->
                true
            }
            contactsRv.addItemDecoration(decoration)
            contactsRv.adapter = adapter
            adapter.addHeaderView(initAssistantView())
            adapter.addHeaderView(initHeadsView())
            indexBar.setOnSelectIndexItemListener(object :
                WaveSideBarView.OnSelectIndexItemListener {
                override fun onSelectIndexItem(letter: String) {
                    if (letter.equals("☆")) {
                        (contactsRv.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                            0,
                            0
                        )
                        return
                    }

                    for (data in adapter.data.withIndex()) {
                        if (data.value.index.equals(letter)) {
                            (contactsRv.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                                data.index + 1,
                                0
                            )
                            return
                        }
                    }
                }

            })
            adapter.setOnItemClickListener { _, view, position ->
                chatOrShare(adapter.data[position])
            }

            searchContactsRv.layoutManager =
                LinearLayoutManager(this@ContactBookActivity, RecyclerView.VERTICAL, false)
            searchContactsRv.adapter = searchAdapter
            searchAdapter.setOnItemClickListener { _, view, position ->
                chatOrShare(searchAdapter.data[position])
                searchContactsRv.visibility = View.GONE
                KeyboardUtils.hideSoftInput(searchView)
                searchView.clearFocus()
            }



            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchResult(query)
                    KeyboardUtils.hideSoftInput(searchView)
                    searchView.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    searchResult(newText)
                    return true
                }

            })
        }
    }

    override fun start() {
        mPresenter?.getContactLists()
    }


    private fun searchResult(query: String?) {
        //每次改变输入就清空数据重新查询
        searchAdapter.data.clear()
        searchAdapter.notifyDataSetChanged()
        for (data in adapter.data) {
            val pinyin = Cn2Spell.getPinYin(data.nickname)
            if (!query.isNullOrEmpty() && (pinyin.contains(query) || data.nickname.contains(
                    query
                ))
            ) {
                searchAdapter.addData(data)
            }
        }
        if (searchAdapter.data.size > 0) {
            binding.searchContactsRv.visibility = View.VISIBLE
        } else {
            binding.searchContactsRv.visibility = View.GONE
        }
    }


    /**
     * 创建头布局
     */
    private val headAdapter by lazy { ContactStarAdapter() }

    private fun initHeadsView(): View {
        val binding = HeaderviewContactBinding.inflate(layoutInflater)
        val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.apply {
            headRv.layoutManager = linearLayoutManager
            headRv.adapter = headAdapter
        }
        headAdapter.setOnItemClickListener { _, view, position ->
            chatOrShare(headAdapter.data[position])
        }

        return binding.root
    }

    private fun initAssistantView(): View {
        val headBinding = ItemContactBookBinding.inflate(layoutInflater)

        headBinding.apply {

            tvIndex.isVisible = false
            friendDivider.isVisible = false
            friendIcon.setImageResource(R.drawable.icon_assistant)
            friendName.text = getString(R.string.assist)
            root.setOnClickListener {
                chatOrShare(
                    ContactBean(
                        nickname = getString(R.string.assist),
                        accid = Constants.ASSISTANT_ACCID
                    )
                )
            }
        }
        return headBinding.root
    }

    private fun chatOrShare(squareBean: ContactBean) =
        if (intent != null && intent.getSerializableExtra("square") != null) {
            binding.barCl.actionbarTitle.text = getString(R.string.choose_friend)
            sqauareBean = intent.getSerializableExtra("square") as SquareBean
            ShareToFriendsDialog(
                squareBean.avatar,
                squareBean.nickname,
                squareBean.accid,
                sqauareBean!!
            ).show()
        } else {
            ChatActivity.start(this, squareBean.accid ?: "")
        }

}