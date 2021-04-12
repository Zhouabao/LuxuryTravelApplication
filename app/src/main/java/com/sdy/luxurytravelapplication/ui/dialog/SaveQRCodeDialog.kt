package com.sdy.luxurytravelapplication.ui.dialog

import android.graphics.Bitmap
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.ImageUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.DialogSaveQrcodeBinding
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.glide.GlideUtil
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog

class SaveQRCodeDialog : BaseBindingDialog<DialogSaveQrcodeBinding>() {
    var qrCodeUrl: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        initView()

        getQRCode()
    }

    private fun initView() {
        binding.apply {
            ClickUtils.applySingleDebouncing(saveAndToWechatBtn) {
                if (qrCodeUrl.isNotEmpty()) {
                    Thread().run {
                        GlideUtil.downLoadImage(context, qrCodeUrl,object:GlideUtil.BitmapLoadCallbacks{
                            override fun getBitmapResult(bitmap: Bitmap?) {
                                if (bitmap != null) {
                                    ImageUtils.save2Album(bitmap, Bitmap.CompressFormat.JPEG)
                                }
                            }

                        })
                    }
                    AppUtils.launchApp("com.tencent.mm")
                    dismiss()
                }
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
//        params?.y = SizeUtils.dp2px(15F)
        window?.attributes = params
        //点击外部可取消
        setCanceledOnTouchOutside(true)

    }

    fun getQRCode() {
        RetrofitHelper.service.getQrCode(hashMapOf())
            .ss(null, null, false, { t ->
                if (t.code == 200) {
                    qrCodeUrl = t.data.url
                    GlideUtil.loadImg(context, qrCodeUrl, binding.qrCodeIv)
                }
            })
    }
}
