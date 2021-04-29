package com.sdy.luxurytravelapplication.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.WindowManager
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ClickUtils
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.DialogChatUpOpenPtVipBinding
import com.sdy.luxurytravelapplication.event.*
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.model.bean.ChatUpBean
import com.sdy.luxurytravelapplication.nim.attachment.ContactCandyAttachment
import com.sdy.luxurytravelapplication.nim.business.session.activity.ChatActivity
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ChatUpOpenPtVipDialog(
    val context1: Context,
    val target_accid: String,
    val type: Int = TYPE_CHAT,
    val chatUpBean: ChatUpBean
) : BaseBindingDialog<DialogChatUpOpenPtVipBinding>() {

    companion object {
        const val TYPE_CHAT = 1
        const val TYPE_CONTACT = 2
        const val TYPE_ROAMING = 3
        const val TYPE_CHAT_DETAIL = 4//在聊天内页
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initWindow()
        initChatData()
        EventBus.getDefault().register(this)
    }


    private fun initChatData() {
        binding.apply {
            //	0没有留下联系方式 1 电话 2 微信 3 qq 99隐藏
            when (chatUpBean.contact_way) {
                1 -> {
                    chatupContact.setBackgroundResource(R.drawable.shape_rectangle_light_green_phone_8dp)
                    chatupContact.setCompoundDrawablesWithIntrinsicBounds(
                        context1.resources.getDrawable(R.drawable.icon_unlock_phone),
                        null,
                        null,
                        null
                    )
                    chatupContact.setTextColor(Color.parseColor("#FF1ED0A7"))
                    chatupContact.text="手机号\t${chatUpBean.contact}"
                }
                2 -> {
                    chatupContact.setBackgroundResource(R.drawable.shape_rectangle_contact_wechat_8dp)
                    chatupContact.setCompoundDrawablesWithIntrinsicBounds(
                        context1.resources.getDrawable(R.drawable.icon_unlock_wechat),
                        null,
                        null,
                        null
                    )
                    chatupContact.setTextColor(Color.parseColor("#FF1DC944"))
                    chatupContact.text="微信\t${chatUpBean.contact}"
                }
                3 -> {
                    chatupContact.setBackgroundResource(R.drawable.shape_rectangle_blue_qq_8dp)
                    chatupContact.setCompoundDrawablesWithIntrinsicBounds(
                        context1.resources.getDrawable(R.drawable.icon_unlock_qq),
                        null,
                        null,
                        null
                    )

                    chatupContact.setTextColor(Color.parseColor("#FF1E9CF0"))
                    chatupContact.text="QQ\t${chatUpBean.contact}"
                }
            }

            when (type) {
                /**
                 * 1.非高级会员
                 *      1.1设置私聊权限
                 *
                 *          1.1.1 仅高级用户能联系（非甜心圈）
                 *              a.她仅允许高级会员联系她
                 *              b.立即成为高级会员，不要错过
                 *              c.成为高级会员，免费无限次聊天
                 *          1.2.1  仅高级用户能联系 （甜心圈）
                 *              a.当前会员等级无法与她联系
                 *              b.因避免甜心圈用户被骚扰，普通会员不能直接与甜心圈用户建立联系
                 *              c.升级高级会员，立即与她取得联系
                 *
                 *      1.2未设置私聊权限
                 *          1.2.1次数未用尽
                 *              a.获得聊天机会
                 *              b.今日还有3次免费聊天机会
                 *              (解锁聊天)
                 *              c.成为高级会员，免费无限次聊天
                 *
                 *          1.2.2次数用尽
                 *              a.获得聊天机会
                 *              b.今日聊天机会已用完
                 *              (解锁聊天  30旅券)
                 *              c.成为高级会员，免费无限次聊天
                 *
                 * 2.高级会员
                 *      2.1聊天次数未用尽
                 *          a.要给她打个招呼吗
                 *          b.今日还可以免费10次聊天
                 *          （微信 wei****5）
                 *          c.解锁聊天
                 *      2.2聊天次数用尽
                 *          a.今日免费次数已用完
                 *          b.今日免费聊天次数已用完
                 *          （微信 wei****5）
                 *          c.解锁聊天（30旅券）
                 *
                 */

                TYPE_CHAT -> { //解锁聊天
                    openPtVipBtn.setBackgroundResource(R.drawable.shape_rectangle_green_25dp)
                    chatupContact.isVisible = false
                    if (chatUpBean.isplatinum) {
                        if (chatUpBean.avatar.isNotEmpty())
                            GlideUtil.loadImg(context1, chatUpBean.avatar, chatupAvator)
                        chatupUnlockChat.isVisible = false
                        chatupTitle.text = context1.getString(R.string.chatup_is_chat_her)
                        openPtVipBtn.text = context1.getString(R.string.chatup_to_be_vip)
                        if (chatUpBean.plat_cnt > 0) {
                            chatupContent.text =
                                context1.getString(
                                    R.string.chatup_free_time_left,
                                    chatUpBean.plat_cnt
                                )
                            openPtVipBtn.text = context1.getString(R.string.chatup_unlock)

                        } else {
                            chatupContent.text = context1.getString(R.string.chatup_chance_run_up)
                            openPtVipBtn.text =
                                context1.getString(
                                    R.string.unlock_chat_left,
                                    chatUpBean.chat_amount
                                )
                        }
                        // 解锁聊天
                        ClickUtils.applySingleDebouncing(openPtVipBtn) {
                            unlockChat()
                        }
                    } else {
                        //成为高级会员
                        ClickUtils.applySingleDebouncing(openPtVipBtn) {
                            CommonFunction.startToVip(context1)
                            dismiss()
                        }
                        if (chatUpBean.ishoney) {
                            chatupAvator.setImageResource(R.drawable.icon_luxury)
                            chatupTitle.text = context1.getString(R.string.cannot_contact_in_level)
                            chatupContent.text =
                                context1.getString(R.string.avoid_trouble_with_normal_vip)
                            chatupUnlockChat.isVisible = false
                            openPtVipBtn.text =
                                context1.getString(R.string.tobe_gold_to_contact_her)
                        } else if (chatUpBean.private_chat_btn) {
                            if (chatUpBean.avatar.isNotEmpty())
                                GlideUtil.loadImg(context1, chatUpBean.avatar, chatupAvator)
                            openPtVipBtn.setBackgroundResource(R.drawable.shape_rectangle_orange_25dp)
                            //2.对方用户是普通用户
                            chatupTitle.text = context1.getString(R.string.her_level_privay)
                            chatupContent.text =
                                context1.getString(R.string.she_allow_gold_contact_her)
                            chatupUnlockChat.isVisible = false
                            openPtVipBtn.text = context1.getString(R.string.tobe_gold_approve_power)
                        } else {
                            if (chatUpBean.avatar.isNotEmpty())
                                GlideUtil.loadImg(context1, chatUpBean.avatar, chatupAvator)
                            openPtVipBtn.text = context1.getString(R.string.chatup_to_be_vip)
                            chatupTitle.text = context1.getString(R.string.chatup_is_chat_her)
                            chatupUnlockChat.isVisible = true
                            if (chatUpBean.plat_cnt > 0) {
                                chatupContent.text =
                                    context1.getString(
                                        R.string.today_has,
                                        chatUpBean.chat_amount,
                                        chatUpBean.vip_normal_cnt
                                    )
                                chatupUnlockChat.text = context1.getString(R.string.chatup_unlock)
                            } else {
                                chatupContent.text = context1.getString(R.string.chat_cost_candy)
                                chatupUnlockChat.text =
                                    context1.getString(
                                        R.string.unlock_chat_left,
                                        chatUpBean.chat_amount
                                    )
                            }
                            // 解锁聊天
                            ClickUtils.applySingleDebouncing(chatupUnlockChat) {
                                unlockChat()
                            }
                        }
                    }
                }
                TYPE_CONTACT -> { //解锁联系方式
                    /**
                     * 1.是否是直联卡
                     *  1.1 聊天次数未用尽
                     *      a.微信 Wei****5
                     *      b.是否解锁她的联系方式
                     *      c.您当日还可免费解锁10次联系方式
                     *      d.解锁她的联系方式
                     *
                     *  1.2 聊天次数用尽
                     *      a.微信 Wei****5
                     *      b.今日免费解锁次数已用完
                     *      c.您当日还可以免费解锁0次联系方式\n使用旅券解锁，不错过心仪的她
                     *      d.解锁她的联系方式（200旅券）
                     * 2.非高级会员
                     *      a.微信 Wei****5
                     *      b.解锁心仪的她
                     *      c.解锁联系方式（200旅券）
                     *      d.购买至尊直联卡，免费解锁联系方式
                     *
                     */
                    if (chatUpBean.avatar.isNotEmpty())
                        GlideUtil.loadImg(context1, chatUpBean.avatar, chatupAvator)
                    chatupContact.isVisible = true
                    if (chatUpBean.private_chat_btn && !chatUpBean.isplatinum) {
                        openPtVipBtn.setBackgroundResource(R.drawable.shape_rectangle_orange_25dp)
                        chatupTitle.text = context1.getString(
                            R.string.her_level_privay
                        )
                        chatupContent.text = context1.getString(
                            R.string.she_allow_gold_contact_her
                        )
                        chatupUnlockChat.isVisible = false
                        openPtVipBtn.text = context1.getString(
                            R.string.tobe_gold_approve_power
                        )
                        ClickUtils.applySingleDebouncing(openPtVipBtn) {
                            CommonFunction.startToVip(context1)
                            dismiss()
                        }

                    } else {
                        openPtVipBtn.setBackgroundResource(R.drawable.gradient_buy_ptvip_bg_25dp)
                        if (chatUpBean.isdirect) {//是直联卡会员,判断有没有次数
                            chatupUnlockChat.isVisible = false
                            chatupContent.isVisible = true
                            if (chatUpBean.direct_residue_cnt > 0) {
                                chatupTitle.text = context1.getString(R.string.is_unlock_contact)
                                chatupContent.text = context1.getString(
                                    R.string.today_free_chat,
                                    chatUpBean.direct_residue_cnt
                                )
                                openPtVipBtn.text = context1.getString(R.string.unlock_her)
                            } else {

                                chatupTitle.text = context1.getString(R.string.free_unlock_use_up)
                                chatupContent.text =
                                    context1.getString(R.string.use_candy_unlock_her_dont_miss)
                                openPtVipBtn.text =
                                    context1.getString(
                                        R.string.unlock_contact_left,
                                        chatUpBean.contact_amount
                                    )
                            }

                            // 解锁联系方式
                            ClickUtils.applySingleDebouncing(openPtVipBtn) {
                                unlockContact()
                            }
                        } else {  //不是的话,弹起购买直联卡
                            chatupUnlockChat.isVisible = true
                            chatupContent.isVisible = true
                            chatupTitle.text = context1.getString(R.string.unlock_lovely_girl)
                            chatupUnlockChat.text =
                                context1.getString(
                                    R.string.unlock_contact_left,
                                    chatUpBean.contact_amount
                                )
                            chatupContent.text = "解锁联系方式是至尊直连卡独享功能\n" +
                                    "免费开启聊天和解锁她的联系方式"

                            // 解锁联系方式
                            ClickUtils.applySingleDebouncing(chatupUnlockChat) {
                                unlockContact()
                            }
                            openPtVipBtn.text =
                                context1.getString(R.string.buy_contact_card_to_free)
                            // 购买直联卡
                            ClickUtils.applySingleDebouncing(openPtVipBtn) {
                                CommonFunction.startToVip(
                                    context1
                                )

                            }


                        }
                    }
                }
                TYPE_CHAT_DETAIL->{
                    if (chatUpBean.avatar.isNotEmpty())
                        GlideUtil.loadImg(context1, chatUpBean.avatar, chatupAvator)
                    //2.对方用户是普通用户
                    chatupTitle.text = context1.getString(R.string.her_level_privay)
                    chatupContent.text =
                        context1.getString(R.string.she_allow_gold_contact_her)
                    chatupUnlockChat.isVisible = false
                    openPtVipBtn.text = context1.getString(R.string.tobe_gold_approve_power)
                    ClickUtils.applySingleDebouncing(openPtVipBtn) {
                        CommonFunction.startToVip(context1)
                        dismiss()
                    }
                }

            }
        }
    }

    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.BOTTOM)
//        window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val params = window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        params?.windowAnimations = R.style.MyDialogBottomAnimation
        window?.attributes = params
        setCancelable(true)
        setCanceledOnTouchOutside(true)
    }


    /**
     * 发送本地搭讪好友消息
     */
    private fun sendMatchFriendMessage(loadingDialog: Dialog) {
        EventBus.getDefault().post(UpdateApproveEvent())
        EventBus.getDefault().post(UpdateHiEvent())
        EventBus.getDefault().post(UpdateAccostListEvent())
        if (ActivityUtils.getTopActivity() !is ChatActivity) {
            Handler().postDelayed({
                loadingDialog.dismiss()
                ChatActivity.start(ActivityUtils.getTopActivity(), target_accid)
            }, 400L)
        } else {
            loadingDialog.dismiss()
        }
        dismiss()


    }


    /**
     * 发送解锁聊天消息
     */
    private fun sendContactCandyMessage(loadingDialog: Dialog) {
        val contactCandyMsg = ContactCandyAttachment(chatUpBean.contact_amount)
        val config = CustomMessageConfig()
        config.enablePush = true
        val message = MessageBuilder.createCustomMessage(
            target_accid,
            SessionTypeEnum.P2P,
            "",
            contactCandyMsg,
            config
        )
        NIMClient.getService(MsgService::class.java).sendMessage(message, false)
            .setCallback(object : RequestCallback<Void?> {
                override fun onSuccess(param: Void?) {
                    if (ActivityUtils.getTopActivity() !is ChatActivity) {
                        Handler().postDelayed({
                            loadingDialog.dismiss()
                            ChatActivity.start(
                                ActivityUtils.getTopActivity(),
                                target_accid
                            )
                            dismiss()
                        }, 250L)
                    } else {
                        loadingDialog.dismiss()
                    }
                }

                override fun onFailed(code: Int) {
                    loadingDialog.dismiss()
                }

                override fun onException(exception: Throwable) {
                    loadingDialog.dismiss()
                }
            })
    }


    override fun show() {
        super.show()
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCloseDialogEvent(event: CloseDialogEvent) {
        if (isShowing) {
            dismiss()
        }
    }


    /**
     * 解锁联系方式
     * 200 解锁成功 419 旅券余额不足
     */
    fun unlockContact() {
        val loading = LoadingDialog()
        loading.show()
        RetrofitHelper.service
            .unlockContact(hashMapOf("target_accid" to target_accid))
            .ssss { t ->
                if (t.code == 200) {
                    if (ActivityUtils.getTopActivity() !is ChatActivity) {
                        Handler().postDelayed({
                            loading.dismiss()
                            ChatActivity.start(
                                ActivityUtils.getTopActivity(),
                                target_accid
                            )
                            dismiss()
                        }, 250L)
                    } else {
                        EventBus.getDefault().post(HideContactLlEvent())
                        loading.dismiss()
                    }
                } else if (t.code == 419) {
                    loading.dismiss()
                    CommonFunction.gotoCandyRecharge(context1)
                } else {
                    loading.dismiss()
                    ToastUtil.toast(t.msg)
                }
            }
    }


    /**
     * 解锁聊天
     * 	201 门槛
     * 	206 好友跳聊天
     * 	419 旅券余额不足
     * 	200 解锁成功
     */
    fun unlockChat() {
        val loading = LoadingDialog()
        loading.show()
        RetrofitHelper.service
            .unlockChat(hashMapOf("target_accid" to target_accid))
            .ssss { t ->
                if (t.code == 201) {
                    loading.dismiss()
                    CommonFunction.startToFootPrice(context1)
                } else if (t.code == 200) {
                    sendMatchFriendMessage(loading)
                } else if (t.code == 206) {
                    loading.dismiss()
                    ChatActivity.start(ActivityUtils.getTopActivity(), target_accid)
                } else if (t.code == 419) {
                    loading.dismiss()
                    CommonFunction.gotoCandyRecharge(context1)
                } else {
                    loading.dismiss()
                    ToastUtil.toast(t.msg)
                }
                dismiss()

            }
    }

}
