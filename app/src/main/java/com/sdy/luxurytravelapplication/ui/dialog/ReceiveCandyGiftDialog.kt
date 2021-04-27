package com.sdy.luxurytravelapplication.ui.dialog

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.DialogReceiveCandyGiftBinding
import com.sdy.luxurytravelapplication.event.RefreshCandyMessageEvent
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.ext.ssss
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.mvp.model.bean.GiftStateBean
import com.sdy.luxurytravelapplication.nim.attachment.SendGiftAttachment
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog
import org.greenrobot.eventbus.EventBus

/**
 *  *    author : ZFM
 *    date   : 2019/11/99:44
 *    desc   :接收旅券礼物弹窗
 *    const GIFT_SEND_WAIT = 1;//待领取状态
const GIFT_SUCCESS = 2;//领取成功 or 发送成功
const GIFT_TIMEOUT_BACK = 3;//超时退回
 *    version: 1.0
 *
 */
class ReceiveCandyGiftDialog(
    val isReceive: Boolean,
    val giftStatus: Int,
    val giftStateBean: GiftStateBean,
    val orderId: Int,
    val context1: Context,
    val target_accid: String
) : BaseBindingDialog<DialogReceiveCandyGiftBinding>() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        initView()
    }

    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.CENTER)
        val params = window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.MATCH_PARENT
        params?.windowAnimations = R.style.MyDialogCenterAnimation
        window?.attributes = params
        setCanceledOnTouchOutside(true)
    }

    private fun initView() {
        if (!isReceive) {
            if (giftStatus != giftStateBean.state) {
                when (giftStateBean.state) {
                    SendGiftAttachment.GIFT_RECEIVE_STATUS_NORMAL, SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_OPEN -> {
                        EventBus.getDefault().post(
                            RefreshCandyMessageEvent(
                                orderId,
                                SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_OPEN
                            )
                        )
                    }
                    else -> {
                        EventBus.getDefault().post(
                            RefreshCandyMessageEvent(
                                orderId,
                                SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_RETURNED
                            )
                        )
                    }

                }
            }
        }


        binding.apply {
            //接收礼物
            ClickUtils.applySingleDebouncing(receiveGiftBtn) {
                if (isReceive && giftStateBean.state == SendGiftAttachment.GIFT_RECEIVE_STATUS_NORMAL) {
                    getGift()
                } else {
                    dismiss()
                }

            }
            //暂不接收
            ClickUtils.applySingleDebouncing(tempRefuseBtn) {
                dismiss()
            }

            GlideUtil.loadImg(context1, giftStateBean.icon, giftImg)
            if (!isReceive) {
                giftName.text = "我赠送的「 ${giftStateBean.title} 」"
            }else{
                giftName.text = "对方赠送了你「 ${giftStateBean.title} 」"
            }

            giftCandyAmount.text = "+${giftStateBean.amount}"

            when (giftStateBean.state) {
                SendGiftAttachment.GIFT_RECEIVE_STATUS_NORMAL -> {
                    if (!isReceive) {
                        tempRefuseBtn.visibility = View.INVISIBLE
                        receiveGiftBtn.text = context1.getString(R.string.ok)
                    } else {
                        tempRefuseBtn.isVisible = true
                        receiveGiftBtn.text = context1.getString(R.string.receive_candy_gift)
                        receiveCandyLight.playAnimation()
                    }
                }
                SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_OPEN -> {
                    receiveGiftBtn.text = context1.getString(R.string.ok)
                    tempRefuseBtn.visibility = View.INVISIBLE
                }
                SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_RETURNED -> {
                    receiveGiftBtn.text = context1.getString(R.string.ok)
                    tempRefuseBtn.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun startWaitReceiveAnimation() {
        //底部light的旋转动画
        val rotateLight =
            ObjectAnimator.ofFloat(binding.receiveCandyLight, "rotation", 0.0f, 360.0f)
        //设定动画的旋转周期
        rotateLight.duration = 4000L
        //设置动画的插值器，这个为匀速旋转
        rotateLight.interpolator = LinearInterpolator()
        //设置动画为无限重复
        rotateLight.repeatCount = -1
        rotateLight.start()

    }



    /**
     * 领取赠送虚拟礼物
     */
    private fun getGift() {
        RetrofitHelper.service
            .getGift(hashMapOf<String, Any>("id" to orderId))
            .ssss { t ->
                if (t.code == 200) {
                    if (t.data.ret_tips_arr.isNotEmpty())
                        CommonFunction.sendTips(
                            target_accid,
                            t.data.ret_tips_arr
                        )
                    if (giftStatus != giftStateBean.state || giftStateBean.state == SendGiftAttachment.GIFT_RECEIVE_STATUS_NORMAL) {
                        when (giftStateBean.state) {
                            SendGiftAttachment.GIFT_RECEIVE_STATUS_NORMAL, SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_OPEN -> {
                                EventBus.getDefault().post(
                                    RefreshCandyMessageEvent(
                                        orderId,
                                        SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_OPEN
                                    )
                                )
                            }
                            else -> {
                                EventBus.getDefault().post(
                                    RefreshCandyMessageEvent(
                                        orderId,
                                        SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_RETURNED
                                    )
                                )
                            }

                        }
                    }


                    dismiss()
                }
            }

    }

    override fun show() {
        super.show()
        //如果点开是过期状态
        if (giftStatus != giftStateBean.state) {
            if (giftStateBean.state == SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_RETURNED) {
                EventBus.getDefault().post(
                    RefreshCandyMessageEvent(
                        orderId,
                        SendGiftAttachment.GIFT_RECEIVE_STATUS_HAS_RETURNED
                    )
                )
            }
        }
    }

}
