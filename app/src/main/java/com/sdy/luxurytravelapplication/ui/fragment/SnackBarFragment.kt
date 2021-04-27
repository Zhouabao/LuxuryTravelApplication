package com.sdy.luxurytravelapplication.ui.fragment

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.core.view.isVisible
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.FragmentUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.base.BaseFragment
import com.sdy.luxurytravelapplication.databinding.FragmentSnackBarBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.CustomerMsgBean
import com.sdy.luxurytravelapplication.nim.business.session.activity.ChatActivity

/**
 * 顶级通知fragment
 */
class SnackBarFragment(val msgBean: CustomerMsgBean) : BaseFragment<FragmentSnackBarBinding>() {
    companion object {
        const val SOMEONE_LIKE_YOU = 31//喜欢了你
        const val SOMEONE_MATCH_SUCCESS = 32//匹配成功
        const val GREET_SUCCESS = 41//招呼
        const val FLASH_SUCCESS = 42//闪聊
        const val CHAT_SUCCESS = 43//发消息
        const val HELP_CANDY = 51//助力
        const val GIVE_GIFT = 52//赠送礼物
        const val SEND_FAILED = 53//发送消息失败
    }

    private val colors by lazy {
        intArrayOf(
            Color.parseColor("#FF6318"),
            Color.parseColor("#7CBAFD"),
            Color.parseColor("#2BD683"),
            Color.parseColor("#FFFF7736")
        )
    }

    override fun initView(view: View) {
        binding.apply {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                BarUtils.addMarginTopEqualStatusBarHeight(contentView)
            }

            when (msgBean.type) {
                SOMEONE_LIKE_YOU, SOMEONE_MATCH_SUCCESS -> {
                    contentView.setCardBackgroundColor(colors[0])
                }
                FLASH_SUCCESS,
                CHAT_SUCCESS -> {
                    contentView.setCardBackgroundColor(colors[1])
                }
                HELP_CANDY,
                GIVE_GIFT -> {
                    contentView.setCardBackgroundColor(colors[2])
                }
                SEND_FAILED -> {
                    contentView.setCardBackgroundColor(colors[3])
                }
            }

            ClickUtils.applySingleDebouncing(contentView) {
                contentView.isVisible = false
                FragmentUtils.remove(this@SnackBarFragment)
            }
            contentView.postDelayed(
                {
                    showAnimation(250L)
                }, if (msgBean.type == SEND_FAILED) {
                    1500L
                } else {
                    3000L
                }
            )

            GlideUtil.loadCircleImg(activity!!, msgBean.avatar, matchIcon)
            matchName.text = msgBean.title
            moreInfoTitle.text = msgBean.content

            ClickUtils.applySingleDebouncing(contentView){
                when (msgBean.type) {
                    //喜欢了你
//                SOMEONE_LIKE_YOU -> {
//                    if (ActivityUtils.getTopActivity() !is LikeMeReceivedActivity)
//                        startActivity<LikeMeReceivedActivity>()
//                }
                    //助力
                    HELP_CANDY,
                        //赠送礼物
                    GIVE_GIFT,
                        //发消息
                    CHAT_SUCCESS,
                        //匹配成功
                    SOMEONE_MATCH_SUCCESS -> {
                        ChatActivity.start(activity!!, msgBean.accid ?: "")
                    }
                    //闪聊
                    FLASH_SUCCESS -> {
                    }

                    SEND_FAILED -> {
                        FragmentUtils.remove(this@SnackBarFragment)
                    }
                }
            }

        }

    }

    fun showAnimation(duration: Long) {
        binding.apply {
            val translationY =
                ObjectAnimator.ofFloat(contentView, "translationY", SizeUtils.dp2px(-68F).toFloat())
            val alphaInAnimation = ObjectAnimator.ofFloat(contentView, "alpha", 1f, 0f)
            val set = AnimatorSet()
            set.duration = duration
            set.playTogether(translationY, alphaInAnimation)
            set.start()
            set.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    FragmentUtils.remove(this@SnackBarFragment)
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {

                }

            })
        }
    }

    override fun lazyLoad() {
    }
}