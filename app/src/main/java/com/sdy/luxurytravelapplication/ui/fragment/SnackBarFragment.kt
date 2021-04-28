package com.sdy.luxurytravelapplication.ui.fragment

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.FragmentSnackBarBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.CustomerMsgBean
import com.sdy.luxurytravelapplication.nim.business.session.activity.ChatActivity
import com.tapadoo.alerter.Alerter

/**
 * 顶级通知fragment
 */
object SnackBarFragment {
    const val SOMEONE_LIKE_YOU = 31//喜欢了你
    const val SOMEONE_MATCH_SUCCESS = 32//匹配成功
    const val GREET_SUCCESS = 41//招呼
    const val FLASH_SUCCESS = 42//闪聊
    const val CHAT_SUCCESS = 43//发消息
    const val HELP_CANDY = 51//助力
    const val GIVE_GIFT = 52//赠送礼物
    const val SEND_FAILED = 53//发送消息失败

    private val colors by lazy {
        intArrayOf(
            Color.parseColor("#FF6318"),
            Color.parseColor("#7CBAFD"),
            Color.parseColor("#2BD683"),
            Color.parseColor("#FFFF7736")
        )
    }

    fun showAlert(msgBean: CustomerMsgBean) {
        val alert = Alerter.create(ActivityUtils.getTopActivity(), R.layout.fragment_snack_bar)
            .enableSwipeToDismiss()
            .setOnClickListener(View.OnClickListener {
                ChatActivity.start(ActivityUtils.getTopActivity(), msgBean.accid)
            })
            .setBackgroundColorRes(R.color.transparent)
            .setDuration(250L)
            .also { alerter ->
                val binding = FragmentSnackBarBinding.bind(alerter.getLayoutContainer()!!)
                binding.apply {
                    val params = root.layoutParams as ViewGroup.LayoutParams
                    params.height = SizeUtils.dp2px(68F)
                    GlideUtil.loadCircleImg(
                        ActivityUtils.getTopActivity(),
                        msgBean.avatar,
                        matchIcon
                    )
                    matchName.text = msgBean.title
                    moreInfoTitle.text = msgBean.content

                    when (msgBean.type) {
                        SOMEONE_LIKE_YOU, SOMEONE_MATCH_SUCCESS -> {
                            root.setCardBackgroundColor(colors[0])
                        }
                        FLASH_SUCCESS,
                        CHAT_SUCCESS -> {
                            root.setCardBackgroundColor(colors[1])
                        }
                        HELP_CANDY,
                        GIVE_GIFT -> {
                            root.setCardBackgroundColor(colors[2])

                        }
                        SEND_FAILED -> {
                            root.setCardBackgroundColor(colors[3])

                        }
                    }
                }

            }

        alert.show()
    }
}