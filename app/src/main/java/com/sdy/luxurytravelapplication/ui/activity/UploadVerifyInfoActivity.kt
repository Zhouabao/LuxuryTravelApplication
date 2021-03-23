package com.sdy.luxurytravelapplication.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.luck.picture.lib.PictureSelector
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityUploadVerifyInfoBinding
import com.sdy.luxurytravelapplication.ext.onTakePhoto
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.contract.UploadVerifyInfoContract
import com.sdy.luxurytravelapplication.mvp.presenter.UploadVerifyInfoPresenter
import com.sdy.luxurytravelapplication.ui.dialog.UploadInfoNormalDialog
import org.jetbrains.anko.startActivity

/**
 * 上传认证资料
 */
class UploadVerifyInfoActivity :
    BaseMvpActivity<UploadVerifyInfoContract.View, UploadVerifyInfoContract.Presenter, ActivityUploadVerifyInfoBinding>(),
    UploadVerifyInfoContract.View, View.OnClickListener {
    private val type by lazy { intent.getIntExtra("type", ChooseVerifyActivity.TYPE_HOUSE) }

    companion object {
        const val REQUEST_CODE_ID_HAND = 10001
        const val REQUEST_CODE_ID_FACE = 10002
        const val REQUEST_CODE_INFO = 10003

        fun start(context: Context, type: Int) {
            context.startActivity<UploadVerifyInfoActivity>("type" to type)
        }
    }

    override fun initData() {
        binding.apply {
            ClickUtils.applySingleDebouncing(
                arrayOf(
                    uploadIdHandBtn,
                    uploadIdFaceBtn,
                    uploadInfoBtn,
                    deleteInfoBtn,
                    deleteIdFaceBtn,
                    deleteIdHandBtn,
                    applyVerfyBtn, binding.barCl.btnBack
                ), this@UploadVerifyInfoActivity
            )
        }

        when (type) {
            ChooseVerifyActivity.TYPE_HOUSE -> {
                binding.barCl.actionbarTitle.text = getString(R.string.verify_title_house)
                binding.t2.text = "首先上传手持身份证及身份证正面\n" +
                        "请确保与本人头像一致，此流程不对外公开\n" +
                        "此流程仅认证一次，认证通过后不会再要求填写"
                binding.uploadInfo.text = "上传房产证照片"
            }
            ChooseVerifyActivity.TYPE_CAR -> {
                binding.barCl.actionbarTitle.text = getString(R.string.verify_title_car)
                binding.t2.text = "首先上传手持身份证及身份证正面\n" +
                        "请确保与本人头像一致，此流程不对外公开\n" +
                        "此流程仅认证一次，认证通过后不会再要求填写"
                binding.uploadInfo.text = "上传行驶证照片"
            }
            ChooseVerifyActivity.TYPE_FIGURE -> {
                binding.barCl.actionbarTitle.text = getString(R.string.verify_title_figure)
                binding.t2.text = "上传手持身份证、身份证正面\n" +
                        "请确保与本人头像一致，此流程不对外公开\n" +
                        "此流程仅认证一次，认证通过后不会再要求填写"
                binding.uploadInfo.text = "胸围测量图"
            }
            ChooseVerifyActivity.TYPE_JOB -> {
                binding.barCl.actionbarTitle.text = getString(R.string.verify_title_job)
                binding.t2.text = "上传手持身份证、身份证正面及工作牌等\n" +
                        "请确保与本人头像一致，此流程不对外公开"
                binding.uploadInfo.text = "工牌证明等"
            }
        }


    }

    override fun start() {
    }

    override fun createPresenter(): UploadVerifyInfoContract.Presenter = UploadVerifyInfoPresenter()
    override fun onClick(v: View) {
        when (v) {
            binding.barCl.btnBack -> {
                finish()
            }
            binding.applyVerfyBtn -> {
                UploadVerifyPublicActivity.startVerifyPublic(this,type)
            }
            binding.deleteInfoBtn -> {
                binding.uploadInfoBtn.setImageResource(R.drawable.icon_upload)
                binding.deleteInfoBtn.isVisible = false
                infoPath = ""
                checkApplyEnable()
            }
            binding.deleteIdHandBtn -> {
                binding.uploadIdHandBtn.setImageResource(R.drawable.icon_upload)
                binding.deleteIdHandBtn.isVisible = false
                handPath = ""
                checkApplyEnable()
            }
            binding.deleteIdFaceBtn -> {
                binding.uploadIdFaceBtn.setImageResource(R.drawable.icon_upload)
                binding.deleteIdFaceBtn.isVisible = false
                facePath = ""
                checkApplyEnable()
            }
            binding.uploadInfoBtn -> {
                if (!infoPathTip) {
                    UploadInfoNormalDialog(type, REQUEST_CODE_INFO).show()
                    infoPathTip = true
                } else {
                    onTakePhoto(this, 1, REQUEST_CODE_INFO)
                }
            }
            binding.uploadIdFaceBtn -> {
                if (!facePathTip) {
                    UploadInfoNormalDialog(
                        UploadInfoNormalDialog.TYPE_ID_FACE,
                        REQUEST_CODE_ID_FACE
                    ).show()
                    facePathTip = true
                } else {
                    onTakePhoto(this, 1, REQUEST_CODE_ID_FACE)
                }
            }
            binding.uploadIdHandBtn -> {
                if (!handPathTip) {
                    UploadInfoNormalDialog(
                        UploadInfoNormalDialog.TYPE_ID_HAND,
                        REQUEST_CODE_ID_HAND
                    ).show()
                    handPathTip = true
                } else {
                    onTakePhoto(this, 1, REQUEST_CODE_ID_HAND)
                }
            }

        }
    }

    private var handPath = ""
    private var facePath = ""
    private var infoPath = ""
    private var handPathTip = false
    private var facePathTip = false
    private var infoPathTip = false
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_ID_HAND -> {
                    handPath = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P
                        && !PictureSelector.obtainMultipleResult(data)[0].androidQToPath.isNullOrEmpty()
                    ) {
                        PictureSelector.obtainMultipleResult(data)[0].androidQToPath
                    } else {
                        PictureSelector.obtainMultipleResult(data)[0].path
                    }
                    GlideUtil.loadRoundImgCenterCrop(
                        this,
                        handPath,
                        binding.uploadIdHandBtn,
                        SizeUtils.dp2px(11F)
                    )
                    binding.deleteIdHandBtn.isVisible = true

                    checkApplyEnable()
                }
                REQUEST_CODE_ID_FACE -> {
                    facePath = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P
                        && !PictureSelector.obtainMultipleResult(data)[0].androidQToPath.isNullOrEmpty()
                    ) {
                        PictureSelector.obtainMultipleResult(data)[0].androidQToPath
                    } else {
                        PictureSelector.obtainMultipleResult(data)[0].path
                    }
                    GlideUtil.loadRoundImgCenterCrop(
                        this,
                        facePath,
                        binding.uploadIdFaceBtn,
                        SizeUtils.dp2px(11F)
                    )
                    binding.deleteIdFaceBtn.isVisible = true
                    checkApplyEnable()
                }
                REQUEST_CODE_INFO -> {
                    infoPath = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P
                        && !PictureSelector.obtainMultipleResult(data)[0].androidQToPath.isNullOrEmpty()
                    ) {
                        PictureSelector.obtainMultipleResult(data)[0].androidQToPath
                    } else {
                        PictureSelector.obtainMultipleResult(data)[0].path
                    }
                    GlideUtil.loadRoundImgCenterCrop(
                        this,
                        infoPath,
                        binding.uploadInfoBtn,
                        SizeUtils.dp2px(11F)
                    )
                    binding.deleteInfoBtn.isVisible = true
                    checkApplyEnable()
                }
            }

        }
    }

    private fun checkApplyEnable() {
        binding.applyVerfyBtn.isEnabled =
            facePath.isNotEmpty() && handPath.isNotEmpty() && infoPath.isNotEmpty()
    }
}