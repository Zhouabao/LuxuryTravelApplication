package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.kongzue.dialog.v3.MessageDialog
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityTargetUserBinding
import com.sdy.luxurytravelapplication.mvp.contract.TargetUserContract
import com.sdy.luxurytravelapplication.mvp.model.bean.MatchBean
import com.sdy.luxurytravelapplication.mvp.model.bean.RecommendSquareListBean
import com.sdy.luxurytravelapplication.mvp.presenter.TargetUserPresenter
import org.jetbrains.anko.startActivity

/**
 * 对方个人页
 */
class TargetUserActivity :
    BaseMvpActivity<TargetUserContract.View, TargetUserContract.Presenter, ActivityTargetUserBinding>(),
    TargetUserContract.View {
    private val targetAccid by lazy { intent.getStringExtra("target_accid")!! }

    companion object {
        @JvmStatic
        fun start(context: Context, target_accid: String) {
            context.startActivity<TargetUserActivity>("target_accid" to target_accid)
        }
    }

    override fun createPresenter(): TargetUserContract.Presenter = TargetUserPresenter()

    override fun initData() {
        binding.apply {
            barlCl.actionbarTitle.text = ""
            barlCl.btnBack.setImageResource(R.drawable.icon_back_white)
            barlCl.rightIconBtn.setImageResource(R.drawable.icon_more_gray)
        }
    }

    fun setData() {
        binding.apply {
            if (this@TargetUserActivity::matchBean.isInitialized) {
                nickName.text = matchBean.nickname
                barlCl.actionbarTitle.text = matchBean.online_time
                age.text = "${matchBean.age}岁"
                distance.text = matchBean.distance
                userSign.text = matchBean.sign

            }
        }
    }

    override fun start() {
        mPresenter?.getMatchUserInfo(targetAccid)
    }

    private lateinit var matchBean: MatchBean
    override fun getMatchUserInfo(code: Int, msg: String, matchBean: MatchBean?) {
        if (code == 200) {
            this.matchBean = matchBean!!
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

        }

    }


}