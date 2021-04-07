package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.kongzue.dialog.v3.MessageDialog
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.friend.FriendService
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityMessageInfoBinding
import com.sdy.luxurytravelapplication.event.UpdateContactBookEvent
import com.sdy.luxurytravelapplication.event.UpdateStarEvent
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.contract.MessageInfoContract
import com.sdy.luxurytravelapplication.mvp.presenter.MessageInfoPresenter
import com.sdy.luxurytravelapplication.nim.business.helper.MessageListPanelHelper
import com.sdy.luxurytravelapplication.nim.business.uinfo.UserInfoHelper
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity

/**
 * 好友信息界面
 */
class MessageInfoActivity :
    BaseMvpActivity<MessageInfoContract.View, MessageInfoContract.Presenter, ActivityMessageInfoBinding>(),
    MessageInfoContract.View, View.OnClickListener {
    override fun createPresenter(): MessageInfoContract.Presenter = MessageInfoPresenter()
    private var account: String = ""
    private var star: Boolean = false//是否是星标好友
    private var isfriend: Boolean = true//是否是好友

    companion object {
        const val EXTRA_ACCOUNT = "EXTRA_ACCOUNT"

        fun start(context: Context, account: String, isfriend: Boolean, isStar: Boolean) {
            context.startActivity<MessageInfoActivity>(
                EXTRA_ACCOUNT to account,
                "isfriend" to isfriend,
                "isStar" to isStar
            )
        }

    }


    override fun initData() {
        account = intent.getStringExtra(EXTRA_ACCOUNT) ?: ""
        isfriend = intent.getBooleanExtra("isfriend", false)
        star = intent.getBooleanExtra("isStar", false)


        binding.apply {
            barCl.actionbarTitle.text = "更多"
            barCl.btnBack.setOnClickListener {
                finish()
            }

            GlideUtil.loadRoundImgCenterCrop(
                this@MessageInfoActivity,
                UserInfoHelper.getAvatar(account),
                civAvator,
                SizeUtils.dp2px(15F)
            )
            ClickUtils.applySingleDebouncing(
                arrayOf(
                    chatDetailBtn,
                    friendDelete,
                    friendHistory,
                    friendHistoryClean,
                    friendNoBother,
                    friendReport,
                    friendStar
                ), this@MessageInfoActivity
            )


            flStar.isVisible = isfriend
            friendStar.isChecked = star
            friendDelete.isVisible = isfriend
        }
    }

    override fun start() {
    }

    override fun onClick(v: View) {
        when (v) {
            //TA的主页
            binding.chatDetailBtn -> {
                TargetUserActivity.start(this, account)
            }
            //删除好友
            binding.friendDelete -> {
                showDeleteDialog(3)
            }
            //历史聊天记录
            binding.friendHistory -> {
//                SearchMessageActivity.start(this, account, SessionTypeEnum.P2P)
            }
            //清除聊天记录
            binding.friendHistoryClean -> {
                showDeleteDialog(1)
            }
            //消息免打扰
            binding.friendNoBother -> {
                NIMClient.getService(FriendService::class.java).setMessageNotify(
                    account,
                    !NIMClient.getService(FriendService::class.java).isNeedMessageNotify(account)
                )
                    .setCallback(object : RequestCallback<Void?> {
                        override fun onSuccess(param: Void?) {
                            binding.friendNoBother.isChecked = !binding.friendNoBother.isChecked

                        }

                        override fun onFailed(code: Int) {
                        }

                        override fun onException(exception: Throwable) {

                        }
                    })
            }
            //举报
            binding.friendReport -> {
                // 跳转到投诉举报界面
                ReportReasonActivity.startReport(this, account)
            }
            //星标好友
            binding.friendStar -> {
                if (star) {
                    mPresenter?.removeStarTarget(account)
                } else {
                    mPresenter?.addStarTarget(account)
                }
            }
        }

    }


    //显示删除对话
    //1.清空聊天记录  2.投诉举报   3.删除好友
    private fun showDeleteDialog(type: Int) {
        val dialog = MessageDialog.build(this)
        dialog.show()
        when (type) {
            1 -> {
                dialog.title = getString(R.string.clean_message)
                dialog.message = resources.getString(R.string.message_p2p_clear_tips)
                dialog.setOnOkButtonClickListener { baseDialog, v ->
                    NIMClient.getService(MsgService::class.java)
                        .clearServerHistory(account, SessionTypeEnum.P2P, true)
                    MessageListPanelHelper.getInstance().notifyClearMessages(account ?: "")
                    false
                }
                dialog.setOnCancelButtonClickListener { baseDialog, v ->
                    false
                }

            }
            3 -> {
                if (isfriend) {
                    dialog.title = getString(R.string.delete_friend)
                    dialog.message = getString(R.string.is_confirm_delete_friend)
                    dialog.setOnCancelButtonClickListener { baseDialog, v ->
                        false
                    }
                    dialog.setOnOkButtonClickListener { _, v ->
                        mPresenter?.dissolutionFriend(account)
                        false
                    }
                }
            }
        }
    }

    override fun removeStarResult(success: Boolean) {
        star = false
        binding.friendStar.isChecked = false
        EventBus.getDefault().post(UpdateContactBookEvent())
        EventBus.getDefault().post(UpdateStarEvent(false))

    }

    override fun delFriendResult(success: Boolean) {
        CommonFunction.dissolveRelationshipLocal(account)
    }

    override fun addStarTargetResult(success: Boolean) {
        star = true
        binding.friendStar.isChecked = true
        EventBus.getDefault().post(UpdateContactBookEvent())
        EventBus.getDefault().post(UpdateStarEvent(true))
    }
}