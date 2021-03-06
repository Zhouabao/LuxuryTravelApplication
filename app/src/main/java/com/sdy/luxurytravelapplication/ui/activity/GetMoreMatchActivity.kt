package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import android.view.KeyEvent
import com.blankj.utilcode.util.ClickUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityGetMoreMatchBinding
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.model.bean.MoreMatchBean
import org.jetbrains.anko.startActivity

/**
 * 精准寻找旅行伴侣
 */
class GetMoreMatchActivity : BaseActivity<ActivityGetMoreMatchBinding>() {
    companion object {
        fun start(context: Context, moreMatchBean: MoreMatchBean) {
            context.startActivity<GetMoreMatchActivity>("moreMatchBean" to moreMatchBean)
        }
    }

    private val moreMatchBean by lazy { intent.getSerializableExtra("moreMatchBean") as MoreMatchBean }
    override fun initData() {
        if (moreMatchBean.gender == 1) {
            binding.lottieUsers.setAnimation("lottie_find_more_match_man.json")
        } else {
            binding.lottieUsers.setAnimation("lottie_find_more_match_woman.json")
        }
        binding.userCnt.text = getString(
            R.string.user_cnt,
            moreMatchBean.people_amount,
            if (moreMatchBean.gender == 1) {
                "女"
            } else
                "男",
            if (moreMatchBean.gender == 1) {
                "她"
            } else
                "他"
        )
        GlideUtil.loadCircleImg(this, moreMatchBean.avatar, binding.myAvatar)
    }

    override fun initView() {
        ClickUtils.applySingleDebouncing(binding.nextBtn) {
            GuideActivity.start(this, moreMatchBean)
        }
    }

    override fun start() {
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return keyCode == KeyEvent.KEYCODE_BACK
    }
}