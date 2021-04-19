package com.sdy.luxurytravelapplication.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.DialogFaceVerifyBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.mvp.model.bean.VideoVerifyBannerBean
import com.sdy.luxurytravelapplication.ui.adapter.VideoVerifyHolderView
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog
import com.zhpan.bannerview.BannerViewPager

class VerifyAddChatDialog(val myContext: Context, val chatCount: Int) :
    BaseBindingDialog<DialogFaceVerifyBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        initView()
    }


    private fun initView() {
        binding.apply {
            (bannerVideoVerify as BannerViewPager<VideoVerifyBannerBean>)
                .setAdapter(VideoVerifyHolderView())
                .setIndicatorSliderRadius(SizeUtils.dp2px(3F))
                .setIndicatorSliderWidth(SizeUtils.dp2px(6f), SizeUtils.dp2px(18F))
                .setIndicatorHeight(SizeUtils.dp2px(6f))
                .setIndicatorSliderGap(SizeUtils.dp2px(5F))
                .create(
                    mutableListOf(
                        VideoVerifyBannerBean(
                            myContext.getString(R.string.get_candy_reward),
                            myContext.getString(R.string.get_candy_reward_content),
                            R.drawable.icon_verify_to_get_candy
                        ),
                        //增加聊天机会
                        VideoVerifyBannerBean(
                            myContext.getString(R.string.today_time_use_up),
                            myContext.getString(R.string.get_chat_count, chatCount),
                            R.drawable.icon_verify_to_chat
                        ),
                        VideoVerifyBannerBean(
                            myContext.getString(R.string.add_expose),
                            myContext.getString(R.string.add_expose_title),
                            R.drawable.icon_verify_to_exposure
                        ),
                        VideoVerifyBannerBean(
                            myContext.getString(R.string.get_verify_logo),
                            myContext.getString(R.string.get_verify_logo_title),
                            R.drawable.icon_verify_to_logo
                        )
                    )
                )
            bannerVideoVerify.currentItem = 1
            ClickUtils.applySingleDebouncing(goVerifyBtn) {
                CommonFunction.startToFace(myContext)
            }
        }
    }

    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.BOTTOM)
        val params = window?.attributes
        // 设置窗口背景透明度
//        params?.alpha = 0.5f
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
//        params?.y = SizeUtils.dp2px(10F)
        params?.windowAnimations = R.style.MyDialogBottomAnimation
        window?.attributes = params
        //点击外部可取消
        setCanceledOnTouchOutside(true)
        setCancelable(true)
    }
}
