package com.sdy.luxurytravelapplication.ui.dialog

import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import com.blankj.utilcode.util.ActivityUtils
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.databinding.DialogUploadInfoNormalBinding
import com.sdy.luxurytravelapplication.ext.onTakePhoto
import com.sdy.luxurytravelapplication.viewbinding.BaseBindingDialog

/**
 *    author : ZFM
 *    date   : 2021/3/1911:22
 *    desc   :认证上传示例
 *    version: 1.0
 */
class UploadInfoNormalDialog(val type: Int, val requesetCode: Int) :
    BaseBindingDialog<DialogUploadInfoNormalBinding>() {

    companion object {
        const val TYPE_ID_HAND = -2
        const val TYPE_ID_FACE = -1
        const val TYPE_HOUSE = 1
        const val TYPE_CAR = 2
        const val TYPE_FIGURE = 3
        const val TYPE_JOB = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initWindow()
        initView()
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


    private fun initView() {
        when (type) {
            TYPE_ID_HAND -> {
                binding.selectedContent.text="上传清晰的手持身份证正面照片"
                binding.normalImg.setImageResource(R.drawable.icon_normal_idhand)
            }
            TYPE_ID_FACE -> {
                binding.selectedContent.text="上传清晰的身份证正面照片"
                binding.normalImg.setImageResource(R.drawable.icon_normal_idface)
            }
            TYPE_HOUSE -> {
                binding.selectedContent.text="上传与身份证姓名一致的房产证\n要求房产大于200平米"
                binding.normalImg.setImageResource(R.drawable.icon_normal_house)
            }
            TYPE_CAR -> {
                binding.selectedContent.text="上传与身份证姓名一致的车辆行驶证\n要求车价大于50万"
                binding.normalImg.setImageResource(R.drawable.icon_normal_car)
            }
            TYPE_FIGURE -> {
                binding.selectedContent.text="上传的测量图片能清晰看到卷尺标"
                binding.normalImg.setImageResource(R.drawable.icon_normal_figure)
            }
            TYPE_JOB -> {
                binding.selectedContent.text="上传与身份证姓名一致的工作证明信息"
                binding.normalImg.setImageResource(R.drawable.icon_normal_job)
            }
        }
        binding.tobeSelectedBtn.setOnClickListener {
            onTakePhoto(ActivityUtils.getTopActivity(), 1, requesetCode)
            dismiss()
        }

    }
}