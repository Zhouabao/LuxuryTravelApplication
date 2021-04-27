package com.sdy.luxurytravelapplication.ui.dialog

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SpanUtils
import com.kongzue.dialog.v3.MessageDialog
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.DialogSendGiftBinding
import com.sdy.luxurytravelapplication.event.CloseDialogEvent
import com.sdy.luxurytravelapplication.event.UpdateChatCallGiftEvent
import com.sdy.luxurytravelapplication.event.UpdateSendGiftEvent
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.model.bean.GiftBean
import com.sdy.luxurytravelapplication.nim.attachment.SendGiftAttachment
import com.sdy.luxurytravelapplication.nim.business.module.Container
import com.sdy.luxurytravelapplication.nim.business.uinfo.UserInfoHelper
import com.sdy.luxurytravelapplication.ui.activity.CandyRechargeActivity
import com.sdy.luxurytravelapplication.ui.adapter.SendGiftBannerAdapter
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog
import com.zhpan.bannerview.BannerViewPager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SendGiftDialog(val container: Container) : BaseBindingDialog<DialogSendGiftBinding>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        iniWindow()
        initView()
        getGiftList()
    }


    fun iniWindow() {
        val window = this.window
        window?.setGravity(Gravity.BOTTOM)
        val params = window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT

        params?.windowAnimations = R.style.MyDialogBottomAnimation
        window?.attributes = params
        //点击外部可取消
        setCanceledOnTouchOutside(true)
    }


    private val sendGiftAdapter by lazy { SendGiftBannerAdapter() }

    private val datas by lazy { arrayListOf<List<GiftBean>>() }

    private val gifts by lazy {
        arrayListOf<GiftBean>()
    }

    private fun initView() {
        binding.apply {
            GlideUtil.loadAvatorImg(
                context,
                UserInfoHelper.getAvatar(container.account),
                targetAvatar
            )
            targetNickname.text = UserInfoHelper.getUserName(container.account)
            (vpGift as BannerViewPager<List<GiftBean>>).apply {
                adapter = sendGiftAdapter
                setOnPageClickListener { clickedView, position ->
                    ToastUtil.toast("${position}")
                }
            }.create(datas)

            ClickUtils.applySingleDebouncing(giveBtn) {
                var giftBean: GiftBean? = null
                out@ for (data in datas) {
                    for (gift in data) {
                        if (gift.checked) {
                            giftBean = gift
                            break@out
                        }
                    }
                }

                /**
                 * 发送礼物消息
                 */
                if (giftBean != null) {
                    MessageDialog.show(
                        ActivityUtils.getTopActivity() as AppCompatActivity,
                        "提示",
                        context.getString(R.string.sure_send_left, giftBean.title),
                        "赠送礼物",
                        "取消"
                    )
                        .setOnOkButtonClickListener { _, v ->
                            sendGift(giftBean)
                            false
                        }
                        .setOnCancelButtonClickListener { _, v ->
                            false
                        }


                }

            }

        }
    }


    private fun getGiftList() {

        RetrofitHelper.service.getGiftList(hashMapOf())?.ssss {
            if (it.code == 200) {
                SpanUtils.with(binding.goldAmount)
                    .setTypeface(Typeface.createFromAsset(context.assets, "DIN_Alternate_Bold.ttf"))
                    .append("${it.data.candy_amount}")
                    .append("\t充值")
                    .setForegroundColor(Color.parseColor("#FF1ED0A7"))
                    .setClickSpan(object : ClickableSpan() {
                        override fun updateDrawState(ds: TextPaint) {
                            ds.color = Color.parseColor("#FF1ED0A7")
                            ds.isUnderlineText = false
                        }
                        override fun onClick(widget: View) {
                            CandyRechargeActivity.gotoCandyRecharge(context)
                        }

                    })
                    .create()
            }
            gifts.addAll(it.data.list)
            val cnt = gifts.size / 8 + if (gifts.size % 8 == 0) {
                0
            } else {
                1
            }
            for (index in 0 until cnt) {
                if (index == cnt - 1 && gifts.size % 8 != 0) {
                    datas.add(gifts.subList(index * 8, index * 8 + gifts.size % 8))
                } else {
                    datas.add(gifts.subList(index * 8, (index + 1) * 8))
                }
            }
            (binding.vpGift as BannerViewPager<List<GiftBean>>).refreshData(datas)
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateGiftCheckState(event: UpdateChatCallGiftEvent) {
        for (data in datas) {
            for (gift in data) {
                gift.checked = gift.title == event.giftbean.title
            }
        }
        (binding.vpGift as BannerViewPager<List<GiftBean>>).refreshData(datas)
    }




    fun sendGift(giftName: GiftBean) {
        val params = hashMapOf<String, Any>()
        params["target_accid"] = container.account
        params["gift_id"] = giftName.id
        RetrofitHelper.service
            .giveGift(params)
            .ssss { t ->
                if (t.code == 200) {
                    sendGiftMessage(t.data.order_id)
                    ToastUtil.toast(t.msg)
                } else if (t.code == 419) {
                    MessageDialog.show(
                        ActivityUtils.getTopActivity() as AppCompatActivity,
                        "提示",
                        "您账户内旅券不足，请充值后再试",
                        "立即充值",
                        "取消"
                    ).setOnOkButtonClickListener { _, v ->
                        CommonFunction.gotoCandyRecharge(container.activity)
                        false
                    }.setOnCancelButtonClickListener { _, v ->
                        false
                    }


                } else {
                    ToastUtil.toast(t.msg)
                }
            }

    }


    private fun sendGiftMessage(orderId: Int) {
        val config = CustomMessageConfig()
        config.enableUnreadCount = true
        config.enablePush = false
        val shareSquareAttachment =
            SendGiftAttachment(orderId, SendGiftAttachment.GIFT_RECEIVE_STATUS_NORMAL)
        val message = MessageBuilder.createCustomMessage(
            container.account,
            SessionTypeEnum.P2P,
            "",
            shareSquareAttachment,
            config
        )
        NIMClient.getService(MsgService::class.java).sendMessage(message, false)
            .setCallback(object :
                RequestCallback<Void?> {
                override fun onSuccess(param: Void?) {
                    //更新消息列表
                    EventBus.getDefault().post(UpdateSendGiftEvent(message))
                    //关闭自己的弹窗
                    dismiss()
                    //关闭礼物弹窗
                    EventBus.getDefault().post(CloseDialogEvent())

                }

                override fun onFailed(code: Int) {
                    dismiss()
                }

                override fun onException(exception: Throwable) {

                }
            })
    }


    override fun show() {
        super.show()
        EventBus.getDefault().register(this)
    }

    override fun dismiss() {
        super.dismiss()
        EventBus.getDefault().unregister(this)
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun closeDialogEvent(event: CloseDialogEvent) {
        dismiss()
    }
}
