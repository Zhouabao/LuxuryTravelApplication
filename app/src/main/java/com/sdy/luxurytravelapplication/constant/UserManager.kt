package com.sdy.luxurytravelapplication.constant

import android.content.Context
import com.cxz.wanandroid.constant.Constants
import com.netease.nimlib.sdk.auth.LoginInfo
import com.sdy.luxurytravelapplication.ext.Preference
import com.sdy.luxurytravelapplication.mvp.model.bean.LoginBean
import com.sdy.luxurytravelapplication.mvp.model.bean.Userinfo

/**
 *    author : ZFM
 *    date   : 2021/3/1714:14
 *    desc   :
 *    version: 1.0
 */
object UserManager {

    var token: String by Preference(Constants.TOKEN, "")
    var accid: String by Preference(Constants.ACCID, "")
    var city: String by Preference(Constants.CITY, "")
    var province: String by Preference(Constants.PROVINCE, "")
    var latitude: String by Preference(Constants.LAT, "")
    var longtitude: String by Preference(Constants.LNG, "")



    /*************登录注册公共方法*********************/

    fun startToPersonalInfoActivity(context: Context, nothing: LoginInfo?, data: LoginBean) {
//        Preference("imToken", nothing?.token)
//        Preference("imAccid", nothing?.account)
//        SPUtils.getInstance(Constants.SPNAME).put("imToken", nothing?.token)
//        SPUtils.getInstance(Constants.SPNAME).put("imAccid", nothing?.account)
//        DemoCache.setAccount(nothing?.account)
//
//        SPUtils.getInstance(Constants.SPNAME).put("token", data.token)
//        SPUtils.getInstance(Constants.SPNAME).put("accid", data.accid)
//        SPUtils.getInstance(Constants.SPNAME).put("qntoken", data.qntk)
//        if (data.qn_prefix.isNotEmpty())
//            SPUtils.getInstance(Constants.SPNAME).put("qn_prefix", data.qn_prefix[0])
//
//        //昵称 生日 性别 头像
//        UserManager.savePersonalInfo(data.userinfo)
//
//        //初始化消息提醒配置
//        UserManager.initNotificationConfig()
//        if (data.userinfo.gender == 0) {
//            LoginInfoActivity.startToLogin(context, data.extra_data.invite_code)
//        } else if (data.userinfo.gender == 2 && data.extra_data.living_btn) {
//            UserManager.living_btn = data.extra_data.living_btn
//            FaceLivenessExpActivity.startActivity(
//                context,
//                FaceLivenessExpActivity.TYPE_LIVE_CAPTURE
//            )
//        } else {
////            SelfVoiceIntroduceActivity.start(context,SelfVoiceIntroduceActivity.FROM_REGISTER)
////            context.startActivity<SelfVoiceIntroduceActivity>()
//            MainActivity.startToMain(context)
//        }
    }

    /**
     * 登录成功保存个人信息
     */
    fun savePersonalInfo(userinfo: Userinfo) {
//        if (userinfo.avatar.isNotEmpty() && !userinfo.avatar.contains(Constants.DEFAULT_AVATAR))
//            UserManager.avatar = userinfo.avatar
//        if (userinfo.birth != 0)
//            SPUtils.getInstance(Constants.SPNAME).put("birth", userinfo.birth)
//        if (userinfo.gender != 0)
//            SPUtils.getInstance(Constants.SPNAME).put("gender", userinfo.gender)
//        if (userinfo.nickname.isNotEmpty())
//            SPUtils.getInstance(Constants.SPNAME).put("nickname", userinfo.nickname)
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