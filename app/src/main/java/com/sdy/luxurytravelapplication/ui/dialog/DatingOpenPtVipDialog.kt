package com.sdy.luxurytravelapplication.ui.dialog

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
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
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.DialogDatingOpenPtVipBinding
import com.sdy.luxurytravelapplication.event.*
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.model.bean.CheckPublishDatingBean
import com.sdy.luxurytravelapplication.mvp.model.bean.TravelPlanBean
import com.sdy.luxurytravelapplication.nim.attachment.ChatDatingAttachment
import com.sdy.luxurytravelapplication.nim.business.session.activity.ChatActivity
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DatingOpenPtVipDialog(
    val context1: Context,
    val type: Int = TYPE_DATING_PUBLISH,
    val chatUpBean: CheckPublishDatingBean? = null,
    val datingBean: TravelPlanBean? = null
) : BaseBindingDialog<DialogDatingOpenPtVipBinding>() {
    companion object {
        const val TYPE_DATING_PUBLISH = 1 //发布约会
        const val TYPE_DATING_APPLYFOR = 2 //报名约会
        const val TYPE_DATING_APPLYFOR_PRIVACY = 3 //限制会员
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        initChatData()
        EventBus.getDefault().register(this)

    }


    private fun initChatData() {
        binding.apply {
            when (type) {
                TYPE_DATING_PUBLISH -> { //发布约会
                    GlideUtil.loadImg(context1, UserManager.avatar, datingAvator)
                    openPtVipBtn.setBackgroundResource(R.drawable.gradient_buy_vip_bg_25dp)
                    datingTitle.text = context1.getString(R.string.dating_publish_only_vip)
                    datingContent.text = context1.getString(R.string.dating_tobe_vip_to_date)
                    openPtVipBtn.text = context1.getString(R.string.tobe_gold_vip)
                    applyForDatingBtn.isVisible = false
                    ClickUtils.applySingleDebouncing(openPtVipBtn) {
                        CommonFunction.startToVip(context1)
                    }
                }
                TYPE_DATING_APPLYFOR -> {
                    //1.先判断有无高级限制
                    if (chatUpBean != null) {
                        GlideUtil.loadImg(context1, datingBean?.avatar!!, datingAvator)
                        if (chatUpBean!!.private_chat && !chatUpBean.isplatinum) {
                            openPtVipBtn.setBackgroundResource(R.drawable.gradient_buy_vip_bg_25dp)
                            datingTitle.text = context1.getString(R.string.dating_allow_gold_apply)
                            datingContent.text = context1.getString(R.string.dating_apply_dont_miss)
                            openPtVipBtn.text = context1.getString(R.string.tobe_gold_vip)
                            applyForDatingBtn.isVisible = false
                            ClickUtils.applySingleDebouncing(openPtVipBtn) {
                                CommonFunction.startToVip(context1)
                            }
                        } else {
                            //2.再判断有无次数
                            if (chatUpBean!!.residue_cnt <= 0) {
                                if (chatUpBean!!.isplatinum) {
                                    datingTitle.text = context1.getString(R.string.is_sure_dating)
                                    datingContent.text =
                                        context1.getString(R.string.chatup_chance_run_up)
                                    applyForDatingBtn.isVisible = false
                                    openPtVipBtn.setBackgroundResource(R.drawable.gradient_buy_vip_bg_25dp)
                                    openPtVipBtn.text = context1.getString(
                                        R.string.apply_dating_left,
                                        chatUpBean!!.dating_amount
                                    )
                                    ClickUtils.applySingleDebouncing(openPtVipBtn) {
                                        datingApply()
                                    }
                                } else {
                                    openPtVipBtn.setBackgroundResource(R.drawable.gradient_buy_vip_bg_25dp)
                                    applyForDatingBtn.text =
                                        context1.getString(
                                            R.string.apply_dating_left,
                                            chatUpBean!!.dating_amount
                                        )
                                    applyForDatingBtn.isVisible = true
                                    datingTitle.text = context1.getString(R.string.is_sure_dating)
                                    datingContent.text =
                                        context1.getString(R.string.prove_in_candy_is_better)
                                    openPtVipBtn.text =
                                        context1.getString(R.string.tobe_gold_more_free)
                                    ClickUtils.applySingleDebouncing(openPtVipBtn) {
                                        CommonFunction.startToVip(
                                            context1
                                        )
                                    }
                                    ClickUtils.applySingleDebouncing(applyForDatingBtn) {
                                        datingApply()
                                    }
                                }

                            } else {
                                //3.报名约会
                                openPtVipBtn.setBackgroundResource(R.drawable.gradient_buy_vip_bg_25dp)
                                applyForDatingBtn.isVisible = false
                                datingTitle.text = context1.getString(R.string.is_sure_dating)
                                datingContent.text = context1.getString(
                                    R.string.dating_apply_cost_left,
                                    chatUpBean.residue_cnt
                                )
                                openPtVipBtn.text = context1.getString(R.string.dating_apply)
                                ClickUtils.applySingleDebouncing(openPtVipBtn) {
                                    datingApply()
                                }
                            }
                        }
                    }


                }

                TYPE_DATING_APPLYFOR_PRIVACY -> {
                    GlideUtil.loadImg(context1, datingBean?.avatar!!, datingAvator)
                    openPtVipBtn.setBackgroundResource(R.drawable.gradient_buy_vip_bg_25dp)
                    datingTitle.text = context1.getString(R.string.dating_allow_gold_apply)
//                dating_apply_dont_miss
                    datingContent.text = context1.getString(R.string.dating_apply_super_dont_miss)
                    openPtVipBtn.text = context1.getString(R.string.tobe_gold_vip)
                    applyForDatingBtn.isVisible = false
                    ClickUtils.applySingleDebouncing(openPtVipBtn) {
                        CommonFunction.startToVip(context1)
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

    fun datingApply() {
        val loadingDialog = LoadingDialog()
        loadingDialog.show()
        RetrofitHelper.service
            .datingApply(hashMapOf("dating_id" to datingBean!!.id))
            .ssss (loadingDialog = loadingDialog){ t ->
                if (t.code == 200 && t.data != null) {
                    val attachment =
                        ChatDatingAttachment(
                            "${t.data.content}",
                            t.data.icon,
                            t.data.datingId
                        )
                    val message = MessageBuilder.createCustomMessage(
                        datingBean.accid,
                        SessionTypeEnum.P2P,
                        "",
                        attachment,
                        CustomMessageConfig()
                    )
                    NIMClient.getService(MsgService::class.java).sendMessage(message, false)
                        .setCallback(object : RequestCallback<Void?> {
                            override fun onSuccess(param: Void?) {
                                EventBus.getDefault().post(UpdateApproveEvent())
                                EventBus.getDefault().post(UpdateHiEvent())
                                EventBus.getDefault().post(UpdateAccostListEvent())
                                if (ActivityUtils.getTopActivity() !is ChatActivity) {
                                    Handler().postDelayed({
                                        loadingDialog.dismiss()
                                        ChatActivity.start(
                                            ActivityUtils.getTopActivity(),
                                            datingBean.accid
                                        )
                                    }, 600L)
                                } else {
                                    EventBus.getDefault().post(UpdateSendGiftEvent(message))
                                    loadingDialog.dismiss()
                                }
                                dismiss()
                            }

                            override fun onFailed(code: Int) {
                                loadingDialog.dismiss()
                            }

                            override fun onException(exception: Throwable) {
                                loadingDialog.dismiss()
                            }
                        })

                } else if (t.code == 419) {
                    loadingDialog.dismiss()
                    CommonFunction.gotoCandyRecharge(context1)
                    dismiss()
                } else {
                    loadingDialog.dismiss()
                    ToastUtil.toast(t.msg)
                    dismiss()
                }
            }
    }


}
