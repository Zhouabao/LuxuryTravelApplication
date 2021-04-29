package com.sdy.luxurytravelapplication.liveface

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.baidu.idl.face.platform.FaceEnvironment
import com.baidu.idl.face.platform.FaceSDKManager
import com.baidu.idl.face.platform.FaceStatusNewEnum
import com.baidu.idl.face.platform.listener.IInitCallback
import com.baidu.idl.face.platform.model.ImageInfo
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.*
import com.kongzue.dialog.v3.MessageDialog
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.app.TravelApp
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.QNUploadManager
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.event.AccountDangerEvent
import com.sdy.luxurytravelapplication.ext.ss
import com.sdy.luxurytravelapplication.http.RetrofitHelper
import com.sdy.luxurytravelapplication.ui.activity.MainActivity
import com.sdy.luxurytravelapplication.ui.dialog.AccountDangerDialog
import com.sdy.luxurytravelapplication.ui.dialog.HumanVerifyDialog
import com.sdy.luxurytravelapplication.ui.dialog.LoadingDialog
import com.sdy.luxurytravelapplication.utils.RandomUtils
import com.sdy.luxurytravelapplication.utils.ToastUtil
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import java.io.ByteArrayOutputStream
import java.util.*

class FaceLivenessExpActivity : FaceLivenessActivity() {

    private val type by lazy { intent.getIntExtra("type", TYPE_ACCOUNT_NORMAL) }

    companion object {
        const val TYPE_ACCOUNT_DANGER = 1 //账户异常发起
        const val TYPE_ACCOUNT_NORMAL = 2  //真人认证
        const val TYPE_LIVE_CAPTURE = 3  //注册流程活体检测

        @JvmOverloads
        fun startActivity(
            context1: Context,
            type: Int = TYPE_ACCOUNT_NORMAL,
            requestCode: Int = -1
        ) {
            if (type == TYPE_LIVE_CAPTURE) {
                if (requestCode != -1)
                    (context1 as Activity).startActivityForResult<FaceLivenessExpActivity>(
                        requestCode,
                        "type" to type
                    )
                else
                    context1.startActivity<FaceLivenessExpActivity>("type" to type)
            } else {
                if (!UserManager.hasFaceUrl) {
                    MessageDialog.show(
                        context1 as AppCompatActivity,
                        "认证提醒",
                        "审核将与用户头像做比对，请确认头像为本人\n验证信息只用作审核，不会对外展示",
                        "确定",
                        "取消"
                    )
                        .setOnOkButtonClickListener { _, v ->
                            if (requestCode != -1)
                                (context1 as Activity).startActivityForResult<FaceLivenessExpActivity>(
                                    requestCode,
                                    "type" to type
                                )
                            else
                                context1.startActivity<FaceLivenessExpActivity>("type" to type)
                            false
                        }
                        .setOnCancelButtonClickListener { _, v ->
                            false
                        }
                } else {
                    HumanVerifyDialog(type, true).show()
                }
            }
        }

    }


    private var clickInit = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SpanUtils.with(binding.faceContent)
            .append(getString(R.string.take_live_info))
            .append(getString(R.string.only_for_plat_verification))
            .setForegroundColor(Color.parseColor("#FFFB1919"))
            .create()
        ClickUtils.applySingleDebouncing(binding.startFaceBtn) {
            clickInit = true
            if (!initFace) {
                initLicense()
            } else {
                initPreview()
            }
        }

        PermissionUtils.permissionGroup(PermissionConstants.CAMERA, PermissionConstants.STORAGE)
            .rationale { activity, shouldRequest ->
                shouldRequest.again(true)
            }
            .callback { isAllGranted, granted, deniedForever, denied ->
                if (isAllGranted) {
                    initLicense()
                } else {
                    ToastUtil.toast(getString(R.string.permission_camera))
                    finish()
                }

            }
            .request()


    }


    override fun onResume() {
        super.onResume()
        if (clickInit) {
            initPreview()
        }
        LogUtils.d("onResume===")
    }

    override fun onPause() {
        super.onPause()
        LogUtils.d("onPause===")
    }

    override fun onStart() {
        super.onStart()
        LogUtils.d("onStart===")
    }

    override fun onRestart() {
        super.onRestart()
//        if (clickInit) {
//            startPreview()
//        }
        LogUtils.d("onRestart===")
    }


    override fun onLivenessCompletion(
        status: FaceStatusNewEnum,
        message: String,
        base64ImageCropMap: HashMap<String, ImageInfo>?,
        base64ImageSrcMap: HashMap<String, ImageInfo>?,
        currentLivenessCount: Int
    ) {
        super.onLivenessCompletion(
            status,
            message,
            base64ImageCropMap,
            base64ImageSrcMap,
            currentLivenessCount
        )
        if (status == FaceStatusNewEnum.OK && mIsCompletion) {
            onPause()
            uploadImg()

        } else if (status == FaceStatusNewEnum.DetectRemindCodeTimeout) {
            onPause()
            binding.faceNotice.text = getString(R.string.take_time_out)
            binding.faceNotice.setTextColor(Color.parseColor("#fffb1919"))
            startPreview()
        }
    }


    override fun finish() {
        if (type != TYPE_LIVE_CAPTURE)
            super.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (loadingDialog.isShowing)
            loadingDialog.dismiss()

        LogUtils.d("onDestroy===")
    }


    /**
     * 初始化人脸config
     */
    private var initFace = false
    private fun initLicense() {
        setFaceConfig()
        FaceSDKManager.getInstance().initialize(this, Constants.licenseID,
            Constants.licenseFileName, object : IInitCallback {
                override fun initSuccess() {
                    runOnUiThread {
                        initFace = true
                        LogUtils.e("初始化成功")
                    }
                }

                override fun initFailure(p0: Int, p1: String?) {
                    runOnUiThread {
                        initFace = false
                        LogUtils.e("初始化失败 = $p0  , $p1")
                    }
                }

            })

    }

    private fun setFaceConfig() {
        val config = FaceSDKManager.getInstance().faceConfig
        // SDK初始化已经设置完默认参数（推荐参数），也可以根据实际需求进行数值调整
        // 设置可检测的最小人脸阈值
//        config.minFaceSize = FaceEnvironment.VALUE_MIN_FACE_SIZE
        config.minFaceSize = 120
        // 设置可检测到人脸的阈值
        config.notFaceValue = FaceEnvironment.VALUE_NOT_FACE_THRESHOLD
        // 设置模糊度阈值
        config.blurnessValue = FaceEnvironment.VALUE_BLURNESS
        // 设置光照阈值（范围0-255）
        config.brightnessValue = FaceEnvironment.VALUE_BRIGHTNESS
        // 设置遮挡阈值
        config.occlusionValue = FaceEnvironment.VALUE_OCCLUSION
        // 设置人脸姿态角阈值
        config.headPitchValue = 45
        config.headYawValue = 45
        // 设置闭眼阈值
        config.eyeClosedValue = FaceEnvironment.VALUE_CLOSE_EYES
        // 设置图片缓存数量
        config.cacheImageNum = FaceEnvironment.VALUE_CACHE_IMAGE_NUM
        // 设置口罩判断开关以及口罩阈值
        config.isOpenMask = FaceEnvironment.VALUE_OPEN_MASK
        config.maskValue = FaceEnvironment.VALUE_MASK_THRESHOLD
        // 设置活体动作，通过设置list，LivenessTypeEunm.Eye, LivenessTypeEunm.Mouth,
        // LivenessTypeEunm.HeadUp, LivenessTypeEunm.HeadDown, LivenessTypeEunm.HeadLeft,
        // LivenessTypeEunm.HeadRight, LivenessTypeEunm.HeadLeftOrRight
        config.livenessTypeList = TravelApp.livenessList
        // 设置动作活体是否随机
        config.isLivenessRandom = true
        // 设置开启提示音
        config.isSound = false
        // 原图缩放系数
        config.scale = FaceEnvironment.VALUE_SCALE
        // 抠图高的设定，为了保证好的抠图效果，我们要求高宽比是4：3，所以会在内部进行计算，只需要传入高即可
        config.cropHeight = FaceEnvironment.VALUE_CROP_HEIGHT
        // 抠图人脸框与背景比例
        config.enlargeRatio = FaceEnvironment.VALUE_CROP_ENLARGERATIO
        // 加密类型，0：Base64加密，上传时image_sec传false；1：百度加密文件加密，上传时image_sec传true
        config.secType = FaceEnvironment.VALUE_SEC_TYPE
        FaceSDKManager.getInstance().faceConfig = config
    }


    private val loadingDialog by lazy { LoadingDialog() }
    private fun uploadImg() {
        if (mBmpStr.isNotEmpty()) {
            val fileKey = "${Constants.FILE_NAME_INDEX}${Constants.AVATOR}" +
                    "${SPUtils.getInstance(Constants.SPNAME).getString("accid")}/" +
                    "${System.currentTimeMillis()}/${RandomUtils.getRandomString(16)}"
            loadingDialog.show()
            QNUploadManager.getInstance().put(
                bitmap2Bytes(mBmpStr),
                fileKey,
                SPUtils.getInstance(Constants.SPNAME).getString("qntoken"), { key, info, response ->
                    if (info != null && info.isOK) {
                        savePersonal(hashMapOf("face" to key))
                    } else {
                        ToastUtil.toast(getString(R.string.review_fail_please_retry))
                    }
                },
                null
            )
        }
    }

    /**
     * 将图片转变为字节数组
     */
    private fun bitmap2Bytes(base64Url: String): ByteArray {
        val baos = ByteArrayOutputStream()
        base64ToBitmap(base64Url).compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray()
    }


    /**
     * 保存个人信息
     */

    private fun savePersonal(params: HashMap<String, Any>) {
        RetrofitHelper.service.setPersonal(params).ss {
            when (type) {
                TYPE_LIVE_CAPTURE -> { //活体检测提交成功
                    UserManager.living_btn = false
                    it.data.apply {
                        UserManager.savePersonalInfo(avatar, birth, gender, nickname)
                    }
                    MainActivity.startToMain(this@FaceLivenessExpActivity)
                }

                else -> {  //真人认证提交成功
                    UserManager.isverify = 2
                    UserManager.hasFaceUrl = true
                    setResult(Activity.RESULT_OK, intent.putExtra("verify", 2))
                    finish()
                    if (type == TYPE_ACCOUNT_DANGER) {//账号异常提交成功
                        EventBus.getDefault()
                            .postSticky(AccountDangerEvent(AccountDangerDialog.VERIFY_ING))
                    }
                }

            }
        }

    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (type == TYPE_LIVE_CAPTURE)
          return  true
        else
            return super.onKeyDown(keyCode, event)
    }
}