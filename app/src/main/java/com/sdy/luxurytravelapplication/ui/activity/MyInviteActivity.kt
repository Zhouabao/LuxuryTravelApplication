package com.sdy.luxurytravelapplication.ui.activity

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ClickUtils
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityMyInviteBinding
import com.sdy.luxurytravelapplication.mvp.contract.MyInviteContract
import com.sdy.luxurytravelapplication.mvp.model.bean.Invite
import com.sdy.luxurytravelapplication.mvp.model.bean.MyInviteBean
import com.sdy.luxurytravelapplication.mvp.presenter.MyInvitePresenter
import com.sdy.luxurytravelapplication.ui.adapter.InviteUserAvatorAdater
import com.sdy.luxurytravelapplication.ui.dialog.MoreActionNewDialog

/**
 * 我的邀请
 */
class MyInviteActivity :
    BaseMvpActivity<MyInviteContract.View, MyInviteContract.Presenter, ActivityMyInviteBinding>(),
    MyInviteContract.View, View.OnClickListener {
    override fun createPresenter(): MyInviteContract.Presenter {
        return MyInvitePresenter()

    }

    override fun initData() {
        binding.apply {
            mLayoutStatusView = stateView
            barCl.actionbarTitle.text = "我的邀请"
            barCl.btnBack.setOnClickListener {
                finish()
            }
//            inviteRewardProgress.setIndicatorTextDecimalFormat("0")
//            inviteRewardProgress.setIndicatorTextStringFormat("%s%%")
            inviteRewardProgress.isEnabled = false
            ClickUtils.applySingleDebouncing(
                arrayOf(inviteBtn, inviteBtn1, purchaseBtn),
                this@MyInviteActivity
            )

            invitedRv.layoutManager =
                LinearLayoutManager(this@MyInviteActivity, RecyclerView.HORIZONTAL, false).apply {
                    stackFromEnd = true
                }
            invitedRv.adapter = adater
        }
    }

    override fun start() {
        mLayoutStatusView?.showLoading()
        mPresenter?.myInvite()
    }

    var myInviteBean: MyInviteBean? = null
    private val adater by lazy { InviteUserAvatorAdater() }
    override fun myInvite(data: MyInviteBean?) {
        if (data != null) {
            mLayoutStatusView?.showContent()
            myInviteBean = data
            binding.apply {
                inviteTimes.text = "您当前有${data.residue_cnt}次邀请机会"
                inviteRewardProgress.setRange(0F, data.all_cnt.toFloat())
                inviteRewardProgress.setProgress(data.now_amount.toFloat())
                inviteRewardProgress.setIndicatorText("${data.now_amount}/${data.all_cnt}")
                inviteRewardCount.text = "${data.coin_all_amount}"
                myInviteTimes.text = "我的邀请码*${data.now_amount}"
                inviteRule.text = data.invite_rule


                UserManager.tempDatas.forEach {
                    data.invite_list.add(Invite("", it))
                }
                if (data.invite_list.size > 4) {
                    adater.setNewInstance(data.invite_list.subList(0, 4))
                } else {
                    adater.setNewInstance(data.invite_list)
                }
                adater.allInviteCount = data.invite_list.size

            }
        } else {
            mLayoutStatusView?.showError()
        }

    }

    override fun onClick(v: View) {
        when (v) {
            binding.inviteBtn,
            binding.inviteBtn1 -> {
                    showShareDialog()
//                if (myInviteBean != null && myInviteBean!!.residue_cnt > 0) {
//                    showShareDialog()
//                    //todo 立即邀请
//                } else {
//                    MessageDialog.show(this, "邀请提示", "您当前没有邀请码，立即充值获得邀请码", "立即充值", "取消")
//                        .setOnOkButtonClickListener { _, v ->
//                            CandyRechargeActivity.gotoCandyRecharge(
//                                this,
//                                CandyRechargeActivity.TYPE_INVITE
//                            )
//                            false
//                        }
//                        .setOnCancelButtonClickListener { _, v ->
//                            false
//                        }
//                        .setButtonPositiveTextInfo(TextInfo().setFontColor(Color.parseColor("#1ED0A7")))
//                        .setButtonTextInfo(TextInfo().setFontColor(Color.parseColor("#FFB6BCC6")))
//
//                }
            }
            binding.purchaseBtn -> {
                CandyRechargeActivity.gotoCandyRecharge(this, CandyRechargeActivity.TYPE_INVITE)
            }
        }
    }




    /**
     * 展示更多操作对话框
     */

//    lateinit var moreActionDialog: MoreActionNewDialog
    private fun showShareDialog() {
//        moreActionDialog =
            MoreActionNewDialog(this,
                url = myInviteBean!!.invite_url,
                type = MoreActionNewDialog.TYPE_SHARE_VIP_URL,
                title = myInviteBean!!.invite_title,
                content = myInviteBean!!.invite_descr,
                pic = myInviteBean!!.invite_pic
            ).showDialog()
//        moreActionDialog.show()
//
//        moreActionDialog.binding.report.isVisible = false
//        moreActionDialog.binding.delete.isVisible = false
//        moreActionDialog.binding.transpondFriend.isVisible = false


    }
}