package com.sdy.luxurytravelapplication.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.View
import androidx.core.view.isVisible
import com.blankj.utilcode.util.BarUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.databinding.ActivityWomanPowerBinding
import com.sdy.luxurytravelapplication.event.FemaleVerifyEvent
import com.sdy.luxurytravelapplication.event.FemaleVideoEvent
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.utils.ToastUtil
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivityForResult

/**
 * 女性权益
 */
class WomanPowerActivity : BaseActivity<ActivityWomanPowerBinding>(), View.OnClickListener {
    private var contact = 0
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
        contact = intent.getIntExtra("contact", 0) //联系方式  0  没有 1 电话 2微信 3 qq


        currentPosition = 0
        changeButtonContent()

    }

    override fun initView() {
        binding.apply {
            barCl.root.setBackgroundColor(Color.WHITE)
            BarUtils.setStatusBarColor(this@WomanPowerActivity, Color.WHITE)
            barCl.actionbarTitle.text = "个人权益"
            barCl.btnBack.setOnClickListener {
                finish()
            }

            powerContactBtn.setOnClickListener(this@WomanPowerActivity)
            powerVerifyBtn.setOnClickListener(this@WomanPowerActivity)
            powerVideoBtn.setOnClickListener(this@WomanPowerActivity)
            addPowerBtn.setOnClickListener(this@WomanPowerActivity)


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
                        startActivityForResult<ChangeUserContactActivity>(REQUEST_ACCOUNT)
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
                    2 -> { //视频认证
                        if (verify == 1) {
                            //      0 没有视频/拒绝   1视频通过  2视频审核中
                            when (video) {
                                2 -> {
                                    ToastUtil.toast(getString(R.string.verify_checking))
                                }
                                else -> {
                                    CommonFunction.startToVideoIntroduce(this)
                                }
                            }
                        } else {
                            ToastUtil.toast(getString(R.string.video_verify_first_then_introduce))
                        }
                    }
                }

            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.apply {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == REQUEST_ACCOUNT) {
                    if (data?.getIntExtra("contact", 0) != 0) {
                        contact = data!!.getIntExtra("contact", 0)
                    }
                } else if (requestCode == REQUEST_VERIFY) {
                    if (data?.getIntExtra("verify", 0) != 0) {
                        verify = data!!.getIntExtra("verify", 0)
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
                binding.powerIcon.setImageResource(R.drawable.icon_power_contact_img)
                binding.powerContactBtn.alpha = 1.0F
                binding.powerVideoBtn.alpha = 0.5F
                binding.powerVerifyBtn.alpha = 0.5F
                binding.powerArrowContact.isVisible = true
                binding.powerArrowVideo.isVisible = false
                binding.powerArrowVerify.isVisible = false
                when (contact) {
                    0 -> {
                        binding.addPowerBtn.text = getString(R.string.bind_now)
                        binding.addPowerBtn.isEnabled = true
                    }
                    else -> {
                        binding.addPowerBtn.text = getString(R.string.change)
                        binding.addPowerBtn.isEnabled = true
                    }
                }

            }
            1 -> {
                binding.powerIcon.setImageResource(R.drawable.icon_power_verify_img)
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
                        binding.addPowerBtn.isEnabled = true
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
                binding.powerIcon.setImageResource(R.drawable.icon_power_video_img)
                binding.powerArrowVerify.isVisible = false
                binding.powerArrowVideo.isVisible = true
                binding.powerArrowContact.isVisible = false
                binding.powerVideoBtn.alpha = 1.0F
                binding.powerVerifyBtn.alpha = 0.5F
                binding.powerContactBtn.alpha = 0.5F
                //        0 未认证 1通过 2机审中 3人审中 4被拒（弹框）
                when (verify) {
                    0 -> {
                        binding.addPowerBtn.text = "请先真人认证"
                        binding.addPowerBtn.isEnabled = false
                    }
                    1 -> {
                        //      0 没有视频/拒绝   1视频通过  2视频审核中
                        if (video == 1) {
                            binding.addPowerBtn.text = getString(R.string.replace_video)
                            binding.addPowerBtn.isEnabled = true
                        } else if (video == 0) {
                            binding.addPowerBtn.text = getString(R.string.record_video)
                            binding.addPowerBtn.isEnabled = true
                        } else if (video == 2) {
                            binding.addPowerBtn.text = getString(R.string.checking)
                            binding.addPowerBtn.isEnabled = false
                        }
                    }
                    2, 3 -> {
                        binding.addPowerBtn.text = "真人认证审核中"
                        binding.addPowerBtn.isEnabled = false
                    }
                }


            }
        }
    }


    /**
     *
     *         //        0 未认证 1通过 2机审中 3人审中 4被拒（弹框）
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFemaleVerifyEvent(event: FemaleVerifyEvent) {
        verify = event.verifyState
        changeButtonContent()
    }


    /**
     * @param event showTop是否展示topShow
     *       //      0 没有视频/拒绝   1视频通过  2视频审核中
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTopCardEvent(event: FemaleVideoEvent) {
        video = event.videoState
        changeButtonContent()
    }
}