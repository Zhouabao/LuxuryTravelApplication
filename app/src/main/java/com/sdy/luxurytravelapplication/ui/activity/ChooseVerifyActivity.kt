package com.sdy.luxurytravelapplication.ui.activity

import android.view.View
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ClickUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityChooseVerifyBinding
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * 选择认证方式
 */
class ChooseVerifyActivity : BaseActivity<ActivityChooseVerifyBinding>(), View.OnClickListener {

    companion object {
        const val TYPE_HOUSE = 1
        const val TYPE_CAR = 2
        const val TYPE_FIGURE = 3
        const val TYPE_JOB = 4
    }


    override fun initData() {
        binding.barCl.actionbarTitle.text="提交认证材料"
        binding.barCl.btnBack.setOnClickListener {
            finish()
        }
        if (UserManager.gender == 1) {
            checkedPosition = TYPE_HOUSE
            binding.verifyType1.setImageResource(R.drawable.icon_verify_house_checked)
            binding.verifyType2.setImageResource(R.drawable.icon_verify_car)
            binding.verifyNotice.text = "需要提交房产大于200平米的证明\n房产认证是个人实力的表现\n认证成功后可直接接入奢旅圈，提高交友效率"
            binding.checkedView1.isVisible = true
            binding.checkedView2.isVisible = false
        } else {
            checkedPosition = TYPE_FIGURE
            binding.verifyType1.setImageResource(R.drawable.icon_verify_figure_checked)
            binding.verifyType2.setImageResource(R.drawable.icon_verify_job)
            binding.verifyNotice.text = "胸围需大于C罩杯\n身材是您个人魅力的体现\n认证后关注度会大幅度提高"
            binding.checkedView1.isVisible = true
            binding.checkedView2.isVisible = false
        }
    }

    private var checkedPosition = -1
    override fun initView() {
        binding.apply {
            ClickUtils.applySingleDebouncing(
                arrayOf(verifyType1, verifyType2, applyVerifyBtn),
                this@ChooseVerifyActivity
            )
        }
    }

    override fun start() {
    }

    override fun onClick(v: View) {
        when (v) {
            binding.applyVerifyBtn -> {
                UploadVerifyInfoActivity.start(this, checkedPosition)
            }
            binding.verifyType1 -> {
                if (UserManager.gender == 1) {
                    checkedPosition = TYPE_HOUSE
                    binding.verifyType1.setImageResource(R.drawable.icon_verify_house_checked)
                    binding.verifyType2.setImageResource(R.drawable.icon_verify_car)
                    binding.verifyNotice.text =
                        "需要提交房产大于200平米的证明\n房产认证是个人实力的表现\n认证成功后可直接接入奢旅圈，提高交友效率"
                    binding.checkedView1.isVisible = true
                    binding.checkedView2.isVisible = false
                } else {
                    checkedPosition = TYPE_FIGURE
                    binding.verifyType1.setImageResource(R.drawable.icon_verify_figure_checked)
                    binding.verifyType2.setImageResource(R.drawable.icon_verify_job)
                    binding.verifyNotice.text = "胸围需大于C罩杯\n身材是您个人魅力的体现\n认证后关注度会大幅度提高"
                    binding.checkedView1.isVisible = true
                    binding.checkedView2.isVisible = false
                }
            }
            binding.verifyType2 -> {
                if (UserManager.gender == 1) {
                    checkedPosition = TYPE_CAR
                    binding.verifyType1.setImageResource(R.drawable.icon_verify_house)
                    binding.verifyType2.setImageResource(R.drawable.icon_verify_car_checked)
                    binding.verifyNotice.text =
                        "需要提交行驶证、且车辆价格大于50万\n认证后对外展示您的车辆图片\n提高您的交友效率，使您的信息更真实可靠"
                    binding.checkedView1.isVisible = false
                    binding.checkedView2.isVisible = true
                } else {
                    checkedPosition = TYPE_JOB
                    binding.verifyType1.setImageResource(R.drawable.icon_verify_figure)
                    binding.verifyType2.setImageResource(R.drawable.icon_verify_job_checked)
                    binding.verifyNotice.text =
                        "要求提交能证明职业的图片\n职业不限于：\n空乘、护士、教师、舞蹈老师、瑜伽教练、白领、幼师、演员、模特、在校大学生"
                    binding.checkedView1.isVisible = false
                    binding.checkedView2.isVisible = true
                }
            }
        }

    }
}