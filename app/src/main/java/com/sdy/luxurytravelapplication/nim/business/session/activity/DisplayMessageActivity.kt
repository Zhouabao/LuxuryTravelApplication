package com.sdy.luxurytravelapplication.nim.business.session.activity

import android.content.Context
import android.content.Intent
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.databinding.ActivityDisplayMessageBinding
import com.sdy.luxurytravelapplication.nim.business.module.Container
import com.sdy.luxurytravelapplication.nim.business.module.ModuleProxy
import com.sdy.luxurytravelapplication.nim.business.session.panel.MessageListPanelEx
import com.sdy.luxurytravelapplication.nim.business.uinfo.UserInfoHelper

/**
 * 搜索结果消息列表界面
 * <p>
 * Created by huangjun on 2017/1/11.
 */
class DisplayMessageActivity : BaseActivity<ActivityDisplayMessageBinding>(), ModuleProxy {
    // context
    private var sessionType: SessionTypeEnum? = null
    private var account: String? = null
    private var anchor: IMMessage? = null

    companion object {
        const val EXTRA_ANCHOR = "anchor"
        fun start(context: Context, anchor: IMMessage) {
            val intent = Intent()
            intent.setClass(context, DisplayMessageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(EXTRA_ANCHOR, anchor)
            context.startActivity(intent)
        }
    }

    private lateinit var messageListPanel: MessageListPanelEx


    override fun initData() {

    }

    override fun initView() {
        onParseIntent()
        val container = Container(this, account, sessionType, this)
        messageListPanel = MessageListPanelEx(container, binding.messageListRv, anchor, true)

    }

    protected fun onParseIntent() {
        anchor = intent.getSerializableExtra(EXTRA_ANCHOR) as IMMessage?
        account = anchor!!.sessionId
        sessionType = anchor!!.sessionType
        binding.barCl.actionbarTitle.text = UserInfoHelper.getUserName(account)
    }


    override fun start() {
    }

    override fun onDestroy() {
        super.onDestroy()
        messageListPanel.onDestory()
    }

    override fun sendMessage(msg: IMMessage?): Boolean {
      return  false
    }

    override fun addReport(message: IMMessage?) {
    }

    override fun onInputPanelExpand() {
    }

    override fun isLongClickEnabled(): Boolean {
        return true
    }

    override fun shouldCollapseInputPanel() {
    }

    override fun onReplyMessage(replyMsg: IMMessage?) {
    }

    override fun onItemFooterClick(message: IMMessage?) {
    }

}