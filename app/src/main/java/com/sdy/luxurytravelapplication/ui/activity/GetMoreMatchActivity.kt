package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import com.blankj.utilcode.util.ClickUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityGetMoreMatchBinding
import org.jetbrains.anko.startActivity

/**
 * 精准寻找旅行伴侣
 */
class GetMoreMatchActivity : BaseActivity<ActivityGetMoreMatchBinding>() {
    companion object {
        fun start(context: Context, peopleCnt: Int) {
            context.startActivity<GetMoreMatchActivity>("peopleCnt" to peopleCnt)
        }
    }

    override fun initData() {
        binding.userCnt.text = getString(
            R.string.user_cnt,
            intent.getIntExtra("peopleCnt", 0),
            if (UserManager.gender == 1) {
                "女性"
            } else
                "男性",
            if (UserManager.gender == 1) {
                "她"
            } else
                "他"
        )
    }

    override fun initView() {
        ClickUtils.applySingleDebouncing(binding.nextBtn){
            startActivity<GuideActivity>()
        }
    }

    override fun start() {
    }

}