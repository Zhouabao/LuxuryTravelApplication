package com.sdy.luxurytravelapplication.ui.dialog

import android.os.Bundle
import android.text.style.ClickableSpan
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SpanUtils
import com.kongzue.dialog.v3.MessageDialog
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.DialogSendGiftBinding
import com.sdy.luxurytravelapplication.event.CloseDialogEvent
import com.sdy.luxurytravelapplication.event.UpdateChatCallGiftEvent
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.model.bean.SendGiftBean
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
//        params?.width = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(15F) * 2
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT

        params?.windowAnimations = R.style.MyDialogBottomAnimation
//        params?.y = SizeUtils.dp2px(20F)
        window?.attributes = params
        //点击外部可取消
        setCanceledOnTouchOutside(true)
    }


    private val sendGiftAdapter by lazy { SendGiftBannerAdapter() }

    private val datas by lazy { arrayListOf<List<SendGiftBean>>() }

    private val gifts by lazy {
        arrayListOf<SendGiftBean>()
    }

    private fun initView() {
        binding.apply {
            GlideUtil.loadAvatorImg(
                context,
                UserInfoHelper.getAvatar(container.account),
                targetAvatar
            )
            targetNickname.text = UserInfoHelper.getUserName(container.account)
            (vpGift as BannerViewPager<List<SendGiftBean>>).apply {
                adapter = sendGiftAdapter
                setOnPageClickListener { clickedView, position ->
                    ToastUtil.toast("${position}")
                }
            }.create(datas)

            ClickUtils.applySingleDebouncing(giveBtn) {
                var giftBean: SendGiftBean? = null
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
                            CandyRechargeActivity.gotoCandyRecharge(ActivityUtils.getTopActivity())
//                 CommonFunction.sendGift(
//                     container.account,
//                     giftBean,
//                     SendGiftAttachment.GIFT_TYPE_NORMAL,
//                     container
//                 )
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
                    .append("${it.data.my_coin_amount}")
                    .append("\t\t充值")
                    .setClickSpan(object :ClickableSpan(){
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
            (binding.vpGift as BannerViewPager<List<SendGiftBean>>).refreshData(datas)
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateGiftCheckState(event: UpdateChatCallGiftEvent) {
        for (data in datas) {
            for (gift in data) {
                gift.checked = gift.title == event.giftbean.title
            }
        }
        (binding.vpGift as BannerViewPager<List<SendGiftBean>>).refreshData(datas)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun closeDialogEvent(event: CloseDialogEvent) {
        dismiss()
    }

    override fun show() {
        super.show()
        EventBus.getDefault().register(this)
    }

    override fun dismiss() {
        super.dismiss()
        EventBus.getDefault().unregister(this)
    }


}
