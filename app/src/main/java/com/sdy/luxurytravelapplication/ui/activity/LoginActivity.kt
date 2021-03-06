package com.sdy.luxurytravelapplication.ui.activity

import android.content.Context
import android.graphics.Color
import com.blankj.utilcode.util.ClickUtils
import com.blankj.utilcode.util.SpanUtils
import com.chuanglan.shanyan_sdk.OneKeyLoginManager
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityLoginBinding
import com.sdy.luxurytravelapplication.ui.dialog.ChooseLoginWayDialog
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import org.jetbrains.anko.startActivity

/**
 * 启动页面
 */
class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    private var syCode = -1

    companion object {
        const val SYCODE = "syCode"
        fun startToLogin(context: Context, syCode: Int = -1) {
            context.startActivity<LoginActivity>(SYCODE to syCode)
        }
    }


    override fun initData() {
//        ScreenUtils.setFullScreen(this)
//        BarUtils.transparentStatusBar(this)
        syCode = intent.getIntExtra(SYCODE, -1)

    }

    override fun initView() {
        binding.apply {
            GSYVideoType.setShowType(GSYVideoType.SCREEN_TYPE_FULL)
            loginMv.isLooping = true
            loginMv.setUp(Constants.WELCOME_MV_URL, false, "")
            loginMv.startPlayLogic()
            SpanUtils.with(loginPrivacy)
                .append("登录注册代表你已同意")
                .append(getString(R.string.user_protocol))
                .setClickSpan(Color.WHITE, true) {
                    ProtocolActivity.start(this@LoginActivity, ProtocolActivity.TYPE_USER_PROTOCOL)
                }
                .append("、")
                .append(getString(R.string.privacy_protocol))
                .setClickSpan(Color.WHITE, true) {
                    ProtocolActivity.start(
                        this@LoginActivity,
                        ProtocolActivity.TYPE_PRIVACY_PROTOCOL
                    )
                }
                .create()

            //判断用户是否存在登录
            if (UserManager.token.isNotEmpty() && UserManager.isUserInfoMade()) {
                MainActivity.startToMain(this@LoginActivity)
            } else {
                UserManager.clearLoginData()
                //闪验预取号，code为1022即为成功，失败了则再次获取，确保准确
                if (syCode == -1)
                    OneKeyLoginManager.getInstance().getPhoneInfo { code, result ->
                        syCode = code
                    }
            }

            //加入尤玩旅行
            ClickUtils.applySingleDebouncing(loginBtn) {
                if (agreeCb.isChecked) {
                    ChooseLoginWayDialog(this@LoginActivity, syCode).show()
                } else {
                    ToastUtil.toast("请先同意隐私协议及用户协议")
                }
            }
        }

    }

    override fun start() {
    }

    override fun onPause() {
        super.onPause()
        binding.loginMv.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        binding.loginMv.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.loginMv.release()
    }

}