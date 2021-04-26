package com.sdy.luxurytravelapplication.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.PermissionUtils
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.DialogUploadAvatorBinding
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog

class UploadAvatorDialog : BaseBindingDialog<DialogUploadAvatorBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        binding.apply {
            cancel.setOnClickListener {
                dismiss()
            }
            ClickUtils.applySingleDebouncing(takePhoto) {
                PermissionUtils.permissionGroup(
                    PermissionConstants.CAMERA,
                    PermissionConstants.STORAGE
                ).callback(object : PermissionUtils.SimpleCallback {
                    override fun onGranted() {
                        CommonFunction.openCamera(
                            context,
                            PictureConfig.REQUEST_CAMERA,
                            PictureMimeType.ofImage(),
                            cropEnable = true,
                            aspect_ratio_x = 1,
                            aspect_ratio_y = 1
                        )
                        dismiss()
                    }

                    override fun onDenied() {
                    }

                })
                    .request()

            }
            ClickUtils.applySingleDebouncing(choosePhoto) {
                PermissionUtils.permissionGroup(
                    PermissionConstants.CAMERA,
                    PermissionConstants.STORAGE
                )
                    .callback(object : PermissionUtils.SimpleCallback {
                        override fun onGranted() {
                            CommonFunction.onTakePhoto(
                                context,
                                1,
                                PictureConfig.CHOOSE_REQUEST,
                                PictureMimeType.ofImage(),
                                rotateEnable = true,
                                cropEnable = true,
                                aspect_ratio_x = 1,
                                aspect_ratio_y = 1
                            )
                            dismiss()
                        }

                        override fun onDenied() {
                        }

                    })
                    .request()
            }
        }
    }


    private fun initWindow() {
        val window = this.window
        window?.setGravity(Gravity.BOTTOM)
        val params = window?.attributes
//        params?.width = ScreenUtils.getScreenWidth() - SizeUtils.dp2px(15F) * 2
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT

        params?.windowAnimations = R.style.MyDialogBottomAnimation
        window?.attributes = params
        //点击外部可取消
        setCanceledOnTouchOutside(true)
    }
}
