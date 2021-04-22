package com.sdy.luxurytravelapplication.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.DialogCompleteInfoBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.IndexRecommendBean
import com.sdy.luxurytravelapplication.mvp.model.bean.TodayFateBean
import com.sdy.luxurytravelapplication.ui.activity.MyInfoActivity
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog
import org.jetbrains.anko.startActivity

/**
 *    author : ZFM
 *    date   : 2021/3/1911:22
 *    desc   :完善个人信息
 *    version: 1.0
 */
class CompleteInfoDialog(
    val nearBean: IndexRecommendBean?,
    val indexRecommends: TodayFateBean?
) : BaseBindingDialog<DialogCompleteInfoBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        initView()
    }

    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.BOTTOM)
        val params = window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT

        params?.windowAnimations = R.style.MyDialogBottomAnimation
        window?.attributes = params
        //点击外部可取消
        setCanceledOnTouchOutside(false)
        setCancelable(false)
    }


    private fun initView() {

        binding.completePercent.text =
            "您目前个人信息未完善，请优先补充个人信息，\n信息填充大于${nearBean?.complete_percent_normal}%的用户才会对外优先展示"
        GlideUtil.loadImg(context, UserManager.avatar, binding.userAvator)
        binding.completeInfoBtn.setOnClickListener {
            context.startActivity<MyInfoActivity>()
            dismiss()
        }
    }

    override fun dismiss() {
        super.dismiss()
        if (!indexRecommends?.list.isNullOrEmpty() && indexRecommends?.today_pull == false && !UserManager.showIndexRecommend) {
            if (UserManager.gender == 2 && nearBean != null)
                TodayFateWomanDialog(nearBean, indexRecommends).show()
        } else if (!UserManager.showCompleteUserCenterDialog) {
            if (nearBean?.today_pull_share == false) {
                InviteFriendDialog().show()
            } else if (nearBean?.today_pull_dating == false) {
                PublishDatingDialog().show()
            }
        }
    }
}