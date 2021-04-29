package com.sdy.luxurytravelapplication.ui.activity

import android.view.View
import android.widget.CompoundButton
import androidx.core.view.isVisible
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.NotificationUtils
import com.netease.nimlib.sdk.NIMClient
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityNotificationBinding
import com.sdy.luxurytravelapplication.event.UpdateSettingEvent
import com.sdy.luxurytravelapplication.event.UpdateWechatSettingsEvent
import com.sdy.luxurytravelapplication.mvp.contract.NotificationContract
import com.sdy.luxurytravelapplication.mvp.model.bean.SettingsBean
import com.sdy.luxurytravelapplication.mvp.presenter.NotificationPresenter
import com.sdy.luxurytravelapplication.nim.impl.cache.DemoCache
import com.sdy.luxurytravelapplication.ui.dialog.SaveQRCodeDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 通知提醒
 */
class NotificationActivity :
    BaseMvpActivity<NotificationContract.View, NotificationContract.Presenter, ActivityNotificationBinding>(),
    NotificationContract.View, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    override fun createPresenter(): NotificationContract.Presenter {
        return NotificationPresenter()
    }

    override fun initData() {
        binding.apply {

            barC.btnBack.setOnClickListener {
                finish()
            }
            barC.actionbarTitle.text = getString(R.string.notification_title)
            barC.divider.isVisible = true
            ClickUtils.applySingleDebouncing(
                arrayOf(
                    tvSwitchComment,
                    tvSwitchDianzan,
                    switchMessageBtn,
                    openPushBtn,
                    switchWechatBtn,
                    wechatPublic
                ), this@NotificationActivity
            )
            //switchDianzan.setOnCheckedChangeListener(this)
            //switchComment.setOnCheckedChangeListener(this)
            switchReply.setOnCheckedChangeListener(this@NotificationActivity)
            switchMusic.setOnCheckedChangeListener(this@NotificationActivity)
            switchVibrator.setOnCheckedChangeListener(this@NotificationActivity)


            //// notify_square_like_state  notify_square_comment_state


            switchMusic.isChecked = DemoCache.getNotificationConfig().ring
            switchVibrator.isChecked = DemoCache.getNotificationConfig().vibrate

            if (NotificationUtils.areNotificationsEnabled()) {
                openPushStatus.text = getString(R.string.has_open)
            } else {
                openPushStatus.text = getString(R.string.not_open)
            }

        }
    }

    override fun start() {
        mPresenter?.mySettings()
    }

    var wechatPublicState = false //微信公众号是否绑定状态
    var wechatState = false //微信推送开启状态
    override fun onClick(view: View) {

        when (view) {
            binding.tvSwitchDianzan -> {//点赞提醒
                mPresenter?.squareNotifySwitch(1)
            }
            binding.tvSwitchComment -> {//评论提醒
                mPresenter?.squareNotifySwitch(2)
            }
            binding.switchMessageBtn -> {//短信通知开关
                mPresenter?.switchSet(
                    1, if (binding.switchMessage.isChecked) {
                        2
                    } else {
                        1
                    }
                )

            }
            binding.openPushBtn -> { //开启推送通知,跳转到设置界面
                AppUtils.launchAppDetailsSettings()
            }

            binding.switchWechatBtn -> { //开启微信推送开关
                mPresenter?.switchSet(
                    4, if (binding.switchWechat.isChecked) {
                        2
                    } else {
                        1
                    }
                )

            }
            binding.wechatPublic -> {//开启微信公众号
                if (!wechatPublicState)
                    SaveQRCodeDialog().show()
            }
        }
    }

    override fun onCheckedChanged(button: CompoundButton, check: Boolean) {
        when (button.id) {
            //回复提醒
            R.id.switchReply -> {
                NIMClient.toggleNotification(check)
            }
            //通知音效
            R.id.switchMusic -> {
                val config = DemoCache.getNotificationConfig()
                config.ring = check
                NIMClient.updateStatusBarNotificationConfig(config)
            }
            //震动开关
            R.id.switchVibrator -> {
                val config = DemoCache.getNotificationConfig()
                config.vibrate = check
                NIMClient.updateStatusBarNotificationConfig(config)
            }

        }
    }


    override fun onGreetApproveResult(type: Int, success: Boolean) {
        binding.apply {
            EventBus.getDefault().post(UpdateSettingEvent())
            when (type) {
                1 -> {
                    switchDianzan.isChecked = !switchDianzan.isChecked
                }

                2 -> {
                    switchComment.isChecked = !switchComment.isChecked
                }
            }
        }
    }

    //接收推送开关 参数 type（int）型    1短信提醒   4 微信推送
    override fun switchSetResult(type: Int, success: Boolean) {
        binding.apply {
            EventBus.getDefault().post(UpdateSettingEvent())
            when (type) {
                1 -> {
                    switchMessage.isChecked = !switchMessage.isChecked
                }
                4 -> {
                    switchWechat.isChecked = !switchWechat.isChecked
                    if (switchWechat.isChecked) {
                        wechatPublicTv.isVisible = true
                        wechatPublic.isVisible = true
                        if (wechatPublicState) {
                            wechatPublic.text = getString(R.string.Binded)
                        } else {
                            wechatPublic.text = getString(R.string.Bind_now)
                        }

                        if (switchWechat.isChecked && !wechatPublicState) {
                            SaveQRCodeDialog().show()
                        }
                    }

                }
            }
        }
    }

    override fun onSettingsBeanResult(success: Boolean, settingsBean: SettingsBean?) {

        if (success) {
            binding.switchComment.isChecked = settingsBean!!.notify_square_comment_state
            binding.switchDianzan.isChecked = settingsBean.notify_square_like_state
            binding.switchMessage.isChecked = settingsBean.sms_state
            wechatPublicState = settingsBean.we_openid
            wechatState = settingsBean.wechat_tem_state
            binding.switchWechat.isChecked = wechatState
            binding.wechatPublicTv.isVisible = wechatState
            binding.wechatPublic.isVisible = wechatState

            if (wechatPublicState) {
                binding.wechatPublic.text = getString(R.string.Binded)
            } else {
                binding.wechatPublic.text = getString(R.string.Bind_now)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateSettingEvent(event: UpdateWechatSettingsEvent) {
        binding.apply {
            if (event.isFollowPublic)
                wechatState = true
            wechatPublicState = event.isFollowPublic
            switchWechat.isChecked = wechatState
            wechatPublicTv.isVisible = wechatState
            wechatPublic.isVisible = wechatState

            if (wechatPublicState) {
                wechatPublic.text = getString(R.string.Binded)
            } else {
                wechatPublic.text = getString(R.string.Bind_now)
            }
        }

    }
}