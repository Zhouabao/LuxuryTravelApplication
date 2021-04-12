package com.sdy.luxurytravelapplication.ui.activity

import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.CacheDiskUtils
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.SPUtils
import com.kongzue.dialog.util.TextInfo
import com.kongzue.dialog.v3.MessageDialog
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.databinding.ActivitySettingsBinding
import com.sdy.luxurytravelapplication.event.UpdateSettingEvent
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.mvp.contract.SettingsContract
import com.sdy.luxurytravelapplication.mvp.model.bean.SettingsBean
import com.sdy.luxurytravelapplication.mvp.model.bean.StateBean
import com.sdy.luxurytravelapplication.mvp.model.bean.VersionBean
import com.sdy.luxurytravelapplication.mvp.presenter.SettingsPresenter
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.utils.UriUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivity

/**
 * 系统设置
 */
class SettingsActivity :
    BaseMvpActivity<SettingsContract.View, SettingsContract.Presenter, ActivitySettingsBinding>(),
    SettingsContract.View, View.OnClickListener {
    override fun createPresenter(): SettingsContract.Presenter {
        return SettingsPresenter()

    }

    override fun useEventBus(): Boolean {
        return true
    }

    override fun initData() {

        binding.apply {
            ClickUtils.applySingleDebouncing(
                arrayOf(
                    blockContactCl,
                    blockDistanceCl,
                    blockHideCl,
                    blackListBtn,
                    privacyPowerContent,
                    loginOutBtn,
                    blockNotificationCl,
                    blockAboutCl,
                    blockAccountCl,
                    blockCacheCl
                ), this@SettingsActivity
            )
        }
    }

    override fun start() {
        mPresenter?.mySettings()
    }

    override fun onBlockedAddressBookResult(success: Boolean) {
        if (success) {
            binding.switchContacts.isChecked = !binding.switchContacts.isChecked
        }

    }

    override fun onHideDistanceResult(success: Boolean) {
        if (success) {
            binding.switchDistance.isChecked = !binding.switchDistance.isChecked
        }

    }

    override fun onGetVersionResult(versionBean: VersionBean?) {
    }

    private var settingsBean: SettingsBean? = null
    private var invisible_state = StateBean()//1 不隐身 2 离线时间隐身 3 一直隐身
    private var private_chat_state = StateBean()//1 所有用户 2针对黄金会员

    override fun onSettingsBeanResult(success: Boolean, settingsBean: SettingsBean?) {
        if (success) {
            this.settingsBean = settingsBean

            binding.apply {
                switchDistance.isChecked = settingsBean!!.hide_distance
                switchContacts.isChecked = settingsBean!!.hide_book
                invisible_state = settingsBean!!.invisible_state
                private_chat_state = settingsBean!!.private_chat_state
                hideMode = settingsBean!!.invisible_list
                privacyPowers = settingsBean!!.private_chat_list
                hideModeContent.text = invisible_state.title
                privacyPowerContent.text = private_chat_state.title
            }
        }
    }

    override fun onClick(v: View) {
        when (v) {
            binding.blockContactCl -> {
                if (binding.switchContacts.isChecked) {
                    mPresenter?.blockedAddressBook(mutableListOf())
                } else {
                    PermissionUtils.permissionGroup(PermissionConstants.CONTACTS)
                        .callback { isAllGranted, granted, deniedForever, denied ->
                            if (isAllGranted) {
                                //权限申请成功
                                val contacts = UriUtils.getPhoneContacts(this)
                                if (contacts.isNullOrEmpty()) {
                                    ToastUtil.toast(getString(R.string.empty_contact1))
                                    return@callback
                                }
                                val content = mutableListOf<String?>()
                                for (contact in contacts.withIndex()) {
                                    content.add(contact.value.phone)
                                    Log.d(
                                        "contacts",
                                        "${contact.value.name}：${contact.value.phone}"
                                    )
                                }

                                mPresenter?.blockedAddressBook(content)
                            }
                        }
                        .request()
                }
            }
            binding.blockDistanceCl -> {
                mPresenter?.isHideDistance(
                    if (binding.switchDistance.isChecked) {
                        0
                    } else {
                        1
                    }
                )
            }
            binding.blockHideCl -> {
                showHideModePicker(
                    binding.hideModeContent,
                    invisible_state,
                    hideMode,
                    getString(R.string.hide_mode),
                    2
                )
            }
            binding.privacyPowerContent -> {
                showHideModePicker(
                    binding.privacyPowerContent,
                    private_chat_state,
                    privacyPowers,
                    getString(R.string.privacy_chat),
                    3
                )
            }

            binding.blockNotificationCl -> {
                startActivity<NotificationActivity>()
            }
            binding.blockAboutCl -> {
                startActivity<AboutActivity>()
            }

            binding.blockCacheCl -> {
                CacheDiskUtils.getInstance().clear()
                binding.clearCacheBtn.text =
                    "${CacheDiskUtils.getInstance().cacheSize / 1024 / 1024}MB"
            }
            binding.loginOutBtn -> {
                MessageDialog.show(
                    this,
                    getString(R.string.login_out),
                    getString(R.string.is_confirm_login_out),
                    getString(R.string.ok),
                    getString(R.string.cancel)
                )
                    .setButtonPositiveTextInfo(TextInfo().setFontColor(Color.parseColor("#FF94AEFF")))
                    .setButtonTextInfo(TextInfo().setFontColor(Color.parseColor("#FFC6CAD4")))
                    .setCancelable(false)
                    .setOnOkButtonClickListener { baseDialog, v ->
                        CommonFunction.loginOut(this)
                        false
                    }

            }

            binding.blackListBtn -> {
                startActivity<BlackListActivity>()
            }
            binding.blockAccountCl -> {
                startActivity<AccountAboutActivity>()
            }
        }

    }

    /**
     * 展示条件选择器
     */
    private var hideMode: MutableList<StateBean> = mutableListOf()
    private var privacyPowers: MutableList<StateBean> = mutableListOf()

    //type 1隐身模式  2私聊权限
    private fun showHideModePicker(
        textView: TextView,
        checkedState: StateBean,
        states: MutableList<StateBean>,
        title: String,
        type: Int
    ) {
        //条件选择器
        val pvOptions = OptionsPickerBuilder(this,
            OnOptionsSelectListener { options1, options2, options3, v ->
                textView.text = states[options1].title
                mPresenter?.switchSet(type, states[options1].id)
            })
            .setSubmitText(getString(R.string.ok))
            .setTitleText(title)
            .setTitleColor(Color.parseColor("#191919"))
            .setTitleSize(16)
            .setDividerColor(resources.getColor(R.color.colorDivider))
            .setContentTextSize(20)
            .setDecorView(window.decorView.findViewById(android.R.id.content) as ViewGroup)
            .setSubmitColor(resources.getColor(R.color.colorAccent))
            .build<StateBean>()

        pvOptions.setPicker(states)
        pvOptions.setSelectOptions(states.indexOf(checkedState))
        pvOptions.show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateSettingEvent(event: UpdateSettingEvent) {
        mPresenter?.mySettings()
    }
}