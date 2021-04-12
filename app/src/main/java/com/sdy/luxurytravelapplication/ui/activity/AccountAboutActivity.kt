package com.sdy.luxurytravelapplication.ui.activity

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.LogUtils
import com.kongzue.dialog.v3.MessageDialog
import com.kongzue.dialog.v3.TipDialog
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityAccountAboutBinding
import com.sdy.luxurytravelapplication.mvp.contract.AccountAboutContract
import com.sdy.luxurytravelapplication.mvp.model.bean.AccountBean
import com.sdy.luxurytravelapplication.mvp.model.bean.WechatNameBean
import com.sdy.luxurytravelapplication.mvp.presenter.AccountAboutPresenter
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import org.jetbrains.anko.startActivityForResult

/**
 * 账号相关
 */
class AccountAboutActivity :
    BaseMvpActivity<AccountAboutContract.View, AccountAboutContract.Presenter, ActivityAccountAboutBinding>(),
    AccountAboutContract.View, View.OnClickListener, UMAuthListener {
    override fun createPresenter(): AccountAboutContract.Presenter {

        return AccountAboutPresenter()
    }

    override fun initData() {
        binding.apply {

            barCl.actionbarTitle.text = getString(R.string.account_title)
            ClickUtils.applySingleDebouncing(
                arrayOf(barCl.btnBack, telChangeBtn, wechatChangeBtn),
                this@AccountAboutActivity
            )
        }
    }

    override fun start() {
        mPresenter?.getAccountInfo()
    }

    override fun getAccountResult(accountBean: AccountBean) {
        binding.apply {
            phone = accountBean.phone
            telNumber.text = accountBean.phone
            if (accountBean.wechat.isNotEmpty()) {
                wechat = accountBean.wechat
                wechatNumber.text = accountBean.wechat
                wechatChangeBtn.text = getString(R.string.account_unbind)
            } else {
                wechatNumber.text = getString(R.string.account_not_bind)
                wechatChangeBtn.text = getString(R.string.account_bind_wechat)
            }
        }
    }

    override fun unbundWeChatResult(result: Boolean) {
        binding.apply {
            wechat = ""
            wechatNumber.text = getString(R.string.account_not_bind)
            wechatChangeBtn.text = getString(R.string.account_bind_wechat)
            TipDialog.show(
                this@AccountAboutActivity as AppCompatActivity,
                R.string.account_unbind_success,
                TipDialog.TYPE.SUCCESS
            ).setTipTime(1000)

        }
    }

    override fun bundWeChatResult(account: WechatNameBean) {
        wechat = account.nickname
        binding.wechatNumber.text = wechat
        binding.wechatChangeBtn.text = getString(R.string.account_unbind)
        TipDialog.show(this, R.string.account_bind_success, TipDialog.TYPE.SUCCESS)

    }

    private var phone = ""
    private var wechat = ""
    override fun onClick(view: View) {
        when (view) {
            binding.barCl.btnBack -> {
                finish()
            }
            //更改号码
            binding.telChangeBtn -> {
                startActivityForResult<ChangeAccountActivity>(100, "phone" to phone)
            }
            //微信绑定
            binding.wechatChangeBtn -> {
                //如果已经绑定，显示微信号及解除绑定按钮,点击后显示二次确认弹窗
                if (wechat.isNotEmpty()) {
                    MessageDialog.show(
                        this@AccountAboutActivity as AppCompatActivity,
                        getString(R.string.tip),
                        getString(R.string.dissolve_wechat),
                        getString(R.string.ok),
                        getString(R.string.cancel)
                    )
                        .setOnOkButtonClickListener { _, v ->
                            mPresenter?.unbundWeChat()
                            false
                        }
                } else {
                    //如果未绑定，显示未绑定及绑定按钮 ,点击后拉起微信、授权 ,完成后显示绑定成功弹窗
                    //微信授权登录
                    UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, this)

                }

            }
        }

    }

    override fun onComplete(p0: SHARE_MEDIA?, p1: Int, p2: MutableMap<String, String>) {
        LogUtils.d("onComplete===${p0!!.getName()}")
        LogUtils.d("onComplete===$p2")
        mPresenter?.bundWeChat(p2["uid"].toString())
    }

    override fun onCancel(p0: SHARE_MEDIA?, p1: Int) {
    }

    override fun onError(p0: SHARE_MEDIA?, p1: Int, p2: Throwable?) {
    }

    override fun onStart(p0: SHARE_MEDIA?) {
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                phone = data?.getStringExtra("phone") ?: ""
                binding.telNumber.text = phone.replaceRange(3, 7, "****")
            }
        }
    }
}