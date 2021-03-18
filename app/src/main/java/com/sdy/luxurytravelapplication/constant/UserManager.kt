package com.sdy.luxurytravelapplication.constant

import android.content.Context
import com.blankj.utilcode.util.SPUtils
import com.netease.nimlib.sdk.auth.LoginInfo
import com.sdy.luxurytravelapplication.mvp.model.bean.LoginBean
import com.sdy.luxurytravelapplication.mvp.model.bean.Userinfo
import com.sdy.luxurytravelapplication.ui.activity.LoginInfoActivity
import com.sdy.luxurytravelapplication.ui.activity.MainActivity
import org.jetbrains.anko.startActivity

/**
 *    author : ZFM
 *    date   : 2021/3/1714:14
 *    desc   :
 *    version: 1.0
 */
object UserManager {


    /*************登录注册公共方法*********************/

    fun startToPersonalInfoActivity(context: Context, nothing: LoginInfo?, data: LoginBean) {
        SPUtils.getInstance(Constants.SPNAME).put("imToken", nothing?.token)
        SPUtils.getInstance(Constants.SPNAME).put("imAccid", nothing?.account)
//        DemoCache.setAccount(nothing?.account)
        token = data.token
        accid = data.accid
        qnToken = data.qntk
        if (data.qn_prefix.isNotEmpty())
            SPUtils.getInstance(Constants.SPNAME).put("qn_prefix", data.qn_prefix[0])

        //昵称 生日 性别 头像
        savePersonalInfo(data.userinfo)

        //初始化消息提醒配置
        initNotificationConfig()
        if (data.userinfo.gender == 0) {
            context.startActivity<LoginInfoActivity>()
        } else if (data.userinfo.gender == 2 && data.extra_data.living_btn) {
            living_btn = data.extra_data.living_btn
//            FaceLivenessExpActivity.startActivity(
//                context,
//                FaceLivenessExpActivity.TYPE_LIVE_CAPTURE
//            )
        } else {
//            SelfVoiceIntroduceActivity.start(context,SelfVoiceIntroduceActivity.FROM_REGISTER)
//            context.startActivity<SelfVoiceIntroduceActivity>()
            MainActivity.startToMain(context)
        }
    }

    var avatar: String
        get() = SPUtils.getInstance(Constants.SPNAME).getString("avatar")
        set(value) = SPUtils.getInstance(Constants.SPNAME).put("avatar", value)

    var nickname: String
        get() = SPUtils.getInstance(Constants.SPNAME).getString("nickname")
        set(value) = SPUtils.getInstance(Constants.SPNAME).put("nickname", value)

    var accid: String
        get() = SPUtils.getInstance(Constants.SPNAME).getString("accid")
        set(value) = SPUtils.getInstance(Constants.SPNAME).put("accid", value)

    var token: String
        get() = SPUtils.getInstance(Constants.SPNAME).getString("token")
        set(value) = SPUtils.getInstance(Constants.SPNAME).put("token", value)

    var qnToken: String
        get() = SPUtils.getInstance(Constants.SPNAME).getString("qntoken")
        set(value) = SPUtils.getInstance(Constants.SPNAME).put("qntoken", value)

    /**
     * 获取维度
     */
    var latitude: String
        get() = SPUtils.getInstance(Constants.SPNAME).getString("latitude", "0")
        set(value) = SPUtils.getInstance(Constants.SPNAME).put("latitude", value)

    var longtitude: String
        get() = SPUtils.getInstance(Constants.SPNAME).getString("longtitude", "0")
        set(value) = SPUtils.getInstance(Constants.SPNAME).put("longtitude", value)

    var city: String
        get() = SPUtils.getInstance(Constants.SPNAME).getString("city", "0")
        set(value) = SPUtils.getInstance(Constants.SPNAME).put("city", value)

    var province: String
        get() = SPUtils.getInstance(Constants.SPNAME).getString("province", "0")
        set(value) = SPUtils.getInstance(Constants.SPNAME).put("province", value)

    var gender: Int
        get() = SPUtils.getInstance(Constants.SPNAME).getInt("gender", 1)
        set(value) = SPUtils.getInstance(Constants.SPNAME).put("gender", value)

    var login: Boolean
        get() = SPUtils.getInstance(Constants.SPNAME).getBoolean("login", false)
        set(value) = SPUtils.getInstance(Constants.SPNAME).put("login", value)

    /**
     * 是否需要做活体检测 true需要做，false不需要
     */
    var living_btn: Boolean
        get() = SPUtils.getInstance(Constants.SPNAME).getBoolean("living_btn", true)
        set(living_btn) = SPUtils.getInstance(Constants.SPNAME).put("living_btn", living_btn)


    /**
     * 登录成功保存用户信息
     */
    fun isUserInfoMade(): Boolean {
        return !(nickname.isEmpty() ||
                avatar.isEmpty() || avatar.contains(Constants.DEFAULT_AVATAR) ||
                gender == -1 ||
                SPUtils.getInstance(Constants.SPNAME).getInt("birth") == -1
                || (gender == 2 && living_btn))
    }


    /**
     * 登录成功保存个人信息
     */
    fun savePersonalInfo(userinfo: Userinfo) {
        if (userinfo.avatar.isNotEmpty() && !userinfo.avatar.contains(Constants.DEFAULT_AVATAR))
            avatar = userinfo.avatar
        if (userinfo.birth != 0)
            SPUtils.getInstance(Constants.SPNAME).put("birth", userinfo.birth)
        if (userinfo.gender != 0)
            SPUtils.getInstance(Constants.SPNAME).put("gender", userinfo.gender)
        if (userinfo.nickname.isNotEmpty())
            SPUtils.getInstance(Constants.SPNAME).put("nickname", userinfo.nickname)
    }

    private fun initNotificationConfig() {
        // 初始化消息提醒
//        NIMClient.toggleNotification(UserPreferences.getNotificationToggle())
////        // 加载状态栏配置
//        var statusBarNotificationConfig = UserPreferences.getStatusConfig()
//        if (statusBarNotificationConfig == null) {
//            statusBarNotificationConfig = DemoCache.getNotificationConfig()
//            UserPreferences.setStatusConfig(statusBarNotificationConfig)
//        }
////        //更新配置
//        NIMClient.updateStatusBarNotificationConfig(statusBarNotificationConfig)
    }

}