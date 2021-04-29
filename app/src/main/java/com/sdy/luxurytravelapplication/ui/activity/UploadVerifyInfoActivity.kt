package com.sdy.luxurytravelapplication.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import androidx.core.view.isVisible
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SizeUtils
import com.google.gson.Gson
import com.luck.picture.lib.PictureSelector
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityUploadVerifyInfoBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.mvp.contract.UploadVerifyInfoContract
import com.sdy.luxurytravelapplication.mvp.model.bean.MediaParamBean
import com.sdy.luxurytravelapplication.mvp.presenter.UploadVerifyInfoPresenter
import com.sdy.luxurytravelapplication.ui.dialog.UploadInfoNormalDialog
import com.sdy.luxurytravelapplication.utils.ToastUtil
import org.jetbrains.anko.startActivity

/**
 * 奢旅圈认证上传
 * 男性的
 * 豪车认证 需要上传——手持身份证—身份证正面—车辆行驶证
 * 资产认证  需要上传——手持身份证—身份证正面—房产证
 * 女性的
 * 身材/职业  都是上传——手持身份证—身份证正面
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
            ChooseVerifyActivity.TYPE_EDUCATION -> {
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
                mPresenter?.uploadPhoto(uploadImgs[index].url,index)
            }

            binding.deleteIdHandBtn -> {
                binding.uploadIdHandBtn.setImageResource(R.drawable.icon_upload)
                binding.deleteIdHandBtn.isVisible = false
                uploadImgs[0] = MediaParamBean()
                checkApplyEnable()
            }
            binding.deleteIdFaceBtn -> {
                binding.uploadIdFaceBtn.setImageResource(R.drawable.icon_upload)
                binding.deleteIdFaceBtn.isVisible = false
                uploadImgs[1] = MediaParamBean()
                checkApplyEnable()
            }

            binding.deleteInfoBtn -> {
                binding.uploadInfoBtn.setImageResource(R.drawable.icon_upload)
                binding.deleteInfoBtn.isVisible = false
                uploadImgs[2] = MediaParamBean()
                checkApplyEnable()
            }
            binding.uploadInfoBtn -> {
                if (!infoPathTip) {
                    UploadInfoNormalDialog(type, REQUEST_CODE_INFO).show()
                    infoPathTip = true
                } else {
                    CommonFunction.onTakePhoto(this, 1, REQUEST_CODE_INFO,compress = true)
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
                    CommonFunction.onTakePhoto(this, 1, REQUEST_CODE_ID_FACE,compress = true)
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
                    CommonFunction.onTakePhoto(this, 1, REQUEST_CODE_ID_HAND,compress = true)
                }
            }

        }
    }

    private var handPathTip = false
    private var facePathTip = false
    private var infoPathTip = false
    private val uploadImgs = arrayListOf<MediaParamBean>(MediaParamBean(), MediaParamBean(), MediaParamBean())

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_ID_HAND -> {
                    uploadImgs[0] = MediaParamBean(
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P
                            && !PictureSelector.obtainMultipleResult(data)[0].androidQToPath.isNullOrEmpty()
                        ) {
                            PictureSelector.obtainMultipleResult(data)[0].androidQToPath
                        } else {
                            PictureSelector.obtainMultipleResult(data)[0].path
                        },
                        0,
                        PictureSelector.obtainMultipleResult(data)[0].width,
                        PictureSelector.obtainMultipleResult(data)[0].height
                    )
                    GlideUtil.loadRoundImgCenterCrop(
                        this,
                        uploadImgs[0].url,
                        binding.uploadIdHandBtn,
                        SizeUtils.dp2px(11F)
                    )
                    binding.deleteIdHandBtn.isVisible = true

                    checkApplyEnable()
                }
                REQUEST_CODE_ID_FACE -> {
                    uploadImgs[1] = MediaParamBean(
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P
                            && !PictureSelector.obtainMultipleResult(data)[0].androidQToPath.isNullOrEmpty()
                        ) {
                            PictureSelector.obtainMultipleResult(data)[0].androidQToPath
                        } else {
                            PictureSelector.obtainMultipleResult(data)[0].path
                        },
                        0,
                        PictureSelector.obtainMultipleResult(data)[0].width,
                        PictureSelector.obtainMultipleResult(data)[0].height
                    )

                    GlideUtil.loadRoundImgCenterCrop(
                        this,
                        uploadImgs[1].url,
                        binding.uploadIdFaceBtn,
                        SizeUtils.dp2px(11F)
                    )
                    binding.deleteIdFaceBtn.isVisible = true
                    checkApplyEnable()
                }
                REQUEST_CODE_INFO -> {
                    uploadImgs[2] = MediaParamBean(
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P
                            && !PictureSelector.obtainMultipleResult(data)[0].androidQToPath.isNullOrEmpty()
                        ) {
                            PictureSelector.obtainMultipleResult(data)[0].androidQToPath
                        } else {
                            PictureSelector.obtainMultipleResult(data)[0].path
                        },
                        0,
                        PictureSelector.obtainMultipleResult(data)[0].width,
                        PictureSelector.obtainMultipleResult(data)[0].height
                    )
                    GlideUtil.loadRoundImgCenterCrop(
                        this,
                        uploadImgs[2].url,
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
        var hasEmpty = false
        if (uploadImgs.isEmpty()) {
            hasEmpty = true
        } else
            uploadImgs.forEach {
                if (it.url.isEmpty()) {
                    hasEmpty = true
                    return@forEach
                }
            }
        binding.applyVerfyBtn.isEnabled = !hasEmpty
    }

    private var index = 0
    private val keys = arrayListOf<MediaParamBean>()
    override fun uploadImgResult(success: Boolean, key: String, index1: Int) {
        if (success) {
            keys.add(
                MediaParamBean(
                    key,
                    0,
                    uploadImgs[index1].width,
                    uploadImgs[index1].height
                )
            )
            if (index == uploadImgs.size - 1) {
                mPresenter?.uploadData(1, type, Gson().toJson(keys))
            } else {
                index++
                mPresenter?.uploadPhoto(uploadImgs[index].url,index)
            }
        } else {
            index = 0
            keys.clear()
            ToastUtil.toast(getString(R.string.pic_upload_fail))
        }

    }

    override fun uploadDataResult(success: Boolean) {
        if (success) {
            index = 0
            keys.clear()
            UploadVerifyPublicActivity.startVerifyPublic(this, type)
        }
    }
}