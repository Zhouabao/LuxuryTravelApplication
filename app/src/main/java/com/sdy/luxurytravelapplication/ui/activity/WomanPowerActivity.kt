package com.sdy.luxurytravelapplication.ui.activity

import android.graphics.Color
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ScreenUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.databinding.ActivityWomanPowerBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.utils.ToastUtil

/**
 * 女性权益
 */
class WomanPowerActivity : BaseActivity<ActivityWomanPowerBinding>(), View.OnClickListener {
    private val contact by lazy { intent.getIntExtra("contact", 0) }//联系方式  0  没有 1 电话 2微信 3 qq
    private var verify = 0
    private var video = 0
    private val url by lazy { intent.getStringExtra("url") ?: "" }

    companion object {
        const val REQUEST_ACCOUNT = 110
        const val REQUEST_VERIFY = 120
    }

    override fun initData() {
        video = intent.getIntExtra("video", 0)
        verify = intent.getIntExtra("verify", 0)
        GlideUtil.loadImg(this@WomanPowerActivity, url, binding.powerIcon)

        currentPosition = 0
        changeButtonContent()

    }

    override fun initView() {
        binding.apply {
            barCl.root.setBackgroundColor(Color.parseColor("#FFFFDCC1"))
            barCl.actionbarTitle.text = "我的权益"
            barCl.btnBack.setImageResource(R.drawable.icon_back_white)
            barCl.btnBack.setOnClickListener {
                finish()
            }

            powerContactBtn.setOnClickListener(this@WomanPowerActivity)
            powerVerifyBtn.setOnClickListener(this@WomanPowerActivity)
            powerVideoBtn.setOnClickListener(this@WomanPowerActivity)
            addPowerBtn.setOnClickListener(this@WomanPowerActivity)


            val params = powerIcon.layoutParams as ConstraintLayout.LayoutParams
            params.width = ScreenUtils.getScreenWidth()
            params.height = (210 * ScreenUtils.getScreenWidth() / 350F).toInt()
            powerIcon.layoutParams = params

        }
    }


    var currentPosition = 0


    override fun start() {
    }

    override fun onClick(v: View) {
        when (v) {
            binding.powerContactBtn -> {
                currentPosition = 0
                changeButtonContent()
            }
            binding.powerVerifyBtn -> {
                currentPosition = 1
                changeButtonContent()
            }
            binding.powerVideoBtn -> {
                currentPosition = 2
                changeButtonContent()
            }
            binding.addPowerBtn -> {
                when (currentPosition) {
                    0 -> { //联系方式
//                        startActivityForResult<ChangeUserContactActivity>(REQUEST_ACCOUNT)

                    }
                    1 -> { //真人认证
                        when (verify) {
                            1 -> {
                                ToastUtil.toast(getString(R.string.verify_pass))
                            }
                            2, 3 -> {
                                ToastUtil.toast(getString(R.string.verify_checking))
                            }
                            else -> {
                                CommonFunction.startToFace(this, requestCode = REQUEST_VERIFY)
                            }
                        }
                    }
                    2 -> { //真人认证
                        when (verify) {
                            1 -> {
                                ToastUtil.toast(getString(R.string.verify_pass))
                            }
                            2, 3 -> {
                                ToastUtil.toast(getString(R.string.verify_checking))
                            }
                            else -> {
                                CommonFunction.startToFace(this, requestCode = REQUEST_VERIFY)
                            }
                        }
                    }
                }

            }
        }

    }


    /**
     * 修改按钮内容以及样式
     */
    fun changeButtonContent() {
        when (currentPosition) {
            0 -> {
                binding.powerContactBtn.alpha = 1.0F
                binding.powerVideoBtn.alpha = 0.5F
                binding.powerVerifyBtn.alpha = 0.5F
                binding.powerArrowContact.isVisible = true
                binding.powerArrowVideo.isVisible = false
                binding.powerArrowVerify.isVisible = false
                when (contact) {
                    0 -> {
                        binding.addPowerBtn.text = getString(R.string.bind_now)
                    }
                    else -> {
                        binding.addPowerBtn.text = getString(R.string.change)
                    }
                }
            }
            1 -> {
                binding.powerVerifyBtn.alpha = 1.0F
                binding.powerVideoBtn.alpha = 0.5F
                binding.powerContactBtn.alpha = 0.5F
                binding.powerArrowVerify.isVisible = true
                binding.powerArrowVideo.isVisible = false
                binding.powerArrowContact.isVisible = false
                // 0 未认证 1通过 2机审中 3人审中 4被拒（弹框）
                when (verify) {
                    0 -> {
                        binding.addPowerBtn.text = getString(R.string.verify_now)
                    }
                    1 -> {
                        binding.addPowerBtn.text = getString(R.string.pass_verify)
                        binding.addPowerBtn.isEnabled = false
                    }
                    2, 3 -> {
                        binding.addPowerBtn.text = "认证审核中"
                        binding.addPowerBtn.isEnabled = false
                    }
                }
            }
            2 -> {
                binding.powerArrowVerify.isVisible = false
                binding.powerArrowVideo.isVisible = true
                binding.powerArrowContact.isVisible = false
                binding.powerVerifyBtn.alpha = 1.0F
                binding.powerVideoBtn.alpha = 0.5F
                binding.powerContactBtn.alpha = 0.5F
                //        0 未认证 1通过 2机审中 3人审中 4被拒（弹框）
                when (verify) {
                    0 -> {
                        binding.addPowerBtn.text = "请先认证"
                    }
                    1 -> {
                        //      0 没有视频/拒绝   1视频通过  2视频审核中
                        if (video == 1) {
                            binding.addPowerBtn.text = getString(R.string.replace_video)
                        } else if (video == 0) {
                            binding.addPowerBtn.text = getString(R.string.record_video)
                        } else if (video == 2) {
                            binding.addPowerBtn.text = getString(R.string.checking)
                            binding.addPowerBtn.isEnabled = false
                        }
                    }
                    2, 3 -> {
                        binding.addPowerBtn.text = "认证审核中"
                        binding.addPowerBtn.isEnabled = false
                    }
                }
            }
        }
    }
}