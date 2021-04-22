package com.sdy.luxurytravelapplication.constant

import android.content.Context
import com.blankj.utilcode.util.SPUtils
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.auth.LoginInfo
import com.qiniu.android.storage.UpCancellationSignal
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.liveface.FaceLivenessExpActivity
import com.sdy.luxurytravelapplication.mvp.model.bean.LoginBean
import com.sdy.luxurytravelapplication.mvp.model.bean.MediaParamBean
import com.sdy.luxurytravelapplication.mvp.model.bean.MoreMatchBean
import com.sdy.luxurytravelapplication.nim.config.preference.UserPreferences
import com.sdy.luxurytravelapplication.nim.impl.cache.DemoCache
import com.sdy.luxurytravelapplication.ui.activity.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.startActivity

/**
 *    author : ZFM
 *    date   : 2021/3/1714:14
 *    desc   :
 *    version: 1.0
 */
object UserManager {
    var showIndexRecommend: Boolean = false

    //每次进入APP弹完善个人资料弹窗
    var showCompleteUserCenterDialog: Boolean = false



    /*************发布缓存参数*********************/
    //手动取消上传
    var cancelUpload = false

    //帮助取消上传的handler
    val cancellationHandler = UpCancellationSignal { cancelUpload }

    //发布动态的状态
    var publishState: Int = 0  //0未发布  1进行中   -1失败--违规400  -2失败--发布失败

    //发布
    var publishParams: HashMap<String, Any> = hashMapOf()

    //发布的媒体资源对象
    var mediaBeans: MutableList<MediaParamBean> = mutableListOf()

    //发布的对象keylist
    var keyList: MutableList<String> = mutableListOf()

    /**
     * 清除发布的参数
     */
    fun clearPublishParams() {
        publishState = 0
        publishParams.clear()
        mediaBeans.clear()
        keyList = mutableListOf()
        cancelUpload = false
    }


    var showCandyMessage: Boolean = true//聊天是否显示旅券
    var showCandyTime: Long = 0L//聊天显示旅券的时间


    /*************动态语音播放位置记录****************/
    var currentPlayPosition = -1

    /*************登录注册公共方法*********************/

    /**
     * 清除登录信息
     */
    fun clearLoginData() {
        //IM信息
        SPUtils.getInstance(Constants.SPNAME).remove("imToken")
        SPUtils.getInstance(Constants.SPNAME).remove("imAccid")
        //用户信息
        SPUtils.getInstance(Constants.SPNAME).remove("accid")
        SPUtils.getInstance(Constants.SPNAME).remove("token")
        SPUtils.getInstance(Constants.SPNAME).remove("nickname")
        SPUtils.getInstance(Constants.SPNAME).remove("avatar")
        SPUtils.getInstance(Constants.SPNAME).remove("gender")
        SPUtils.getInstance(Constants.SPNAME).remove("birth")


        SPUtils.getInstance(Constants.SPNAME).remove("isFootVip")
        SPUtils.getInstance(Constants.SPNAME).remove("isFaced")
        SPUtils.getInstance(Constants.SPNAME).remove("hasFaceUrl")
        SPUtils.getInstance(Constants.SPNAME).remove("living_btn")
        SPUtils.getInstance(Constants.SPNAME).remove("mvFaced")
        SPUtils.getInstance(Constants.SPNAME).remove("isPtvip")
        SPUtils.getInstance(Constants.SPNAME).remove("isvip1")


        SPUtils.getInstance(Constants.SPNAME).remove("myVerifyBtn")
        SPUtils.getInstance(Constants.SPNAME).remove("myRedPacketBtn")



        SPUtils.getInstance(Constants.SPNAME).remove("limit_age_high")
        SPUtils.getInstance(Constants.SPNAME).remove("limit_age_low")
        SPUtils.getInstance(Constants.SPNAME).remove("isvip")
        SPUtils.getInstance(Constants.SPNAME).remove("ishuman")
        SPUtils.getInstance(Constants.SPNAME).remove("iscall")

        SPUtils.getInstance(Constants.SPNAME).remove("isTipDating")

        EventBus.getDefault().removeAllStickyEvents()//移除全部
    }


    fun startToPersonalInfoActivity(context: Context, nothing: LoginInfo?, data: LoginBean) {
        SPUtils.getInstance(Constants.SPNAME).put("imToken", nothing?.token)
        SPUtils.getInstance(Constants.SPNAME).put("imAccid", nothing?.account)
        token = data.token
        accid = data.accid
        qnToken = data.qntk
        if (data.qn_prefix.isNotEmpty())
            SPUtils.getInstance(Constants.SPNAME).put("qn_prefix", data.qn_prefix[0])

        DemoCache.setAccount(nothing?.account)


        //初始化消息提醒配置
        initNotificationConfig()

        if (data.userinfo.gender == 0) {
            context.startActivity<RegisterInfoOneActivity>()
        } else if (data.userinfo.nickname.isEmpty()) {
            context.startActivity<RegisterInfoTwoActivity>()
        } else if (data.userinfo.gender == 1 && data.extra_data.threshold && !data.extra_data.isvip) {
            //todo 男性判断是否付费
            data.extra_data.apply {
                data.userinfo.apply {
                    InviteCodeActivity.start(
                        context,
                        MoreMatchBean(
                            nickname,
                            gender,
                            birth,
                            avatar,
                            threshold,
                            living_btn,
                            isvip
                        )
                    )
                }
            }
        } else if (data.userinfo.gender == 2 && living_btn) {
            //todo 女性判断是否做过活体认证
            living_btn = data.extra_data.living_btn
            if (living_btn) {
                CommonFunction.startToFace(context, FaceLivenessExpActivity.TYPE_LIVE_CAPTURE)
            }
        } else {
            //昵称 生日 性别 头像
            data.userinfo.apply {
                savePersonalInfo(avatar, birth, gender, nickname)
            }
            MainActivity.startToMain(context)
        }
    }


    /**
     * 跳至登录界面
     */
    fun startToLogin(activity: Context, fromNimNotification: Boolean = false) {
        touristMode = false
        clearLoginData()
        NIMClient.getService(AuthService::class.java).logout()
        val intent = activity.intentFor<WelcomeActivity>().clearTask().newTask()
        activity.startActivity(intent)
//            activity.startActivity<SplashActivity>()
    }

    var touristMode: Boolean = false
    val tempDatas = arrayListOf(
        "https://dss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2509550317,2669241293&fm=26&gp=0.jpg",
        "https://dss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1459307042,2397699953&fm=26&gp=0.jpg",
        "https://dss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1459307042,2397699953&fm=26&gp=0.jpg",
        "https://dss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1459307042,2397699953&fm=26&gp=0.jpg",
        "https://dss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=1459307042,2397699953&fm=26&gp=0.jpg",
        "https://dss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3167430823,2130012097&fm=26&gp=0.jpg"
    )
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
     * 保存位置信息
     */
    fun saveLocation(
        latitude: String?,
        longtitude: String?,
        province: String?,
        city: String?
    ) {
        if (latitude != null)
            this.latitude = latitude
        if (longtitude != null)
            this.longtitude = longtitude
        if (province != null)
            this.province = province
        if (city != null)
            this.city = city
    }

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
        get() = SPUtils.getInstance(Constants.SPNAME).getString("city", "")
        set(value) = SPUtils.getInstance(Constants.SPNAME).put("city", value)

    var province: String
        get() = SPUtils.getInstance(Constants.SPNAME).getString("province", "")
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
     * 是否提示过旅行约会
     */
    var isTipDating: Boolean
        get() = SPUtils.getInstance(Constants.SPNAME).getBoolean("isTipDating", false)
        set(isTipDating) = SPUtils.getInstance(Constants.SPNAME).put("isTipDating", isTipDating)

    /**
     * 视频认证
     */
    var mvFaced: Boolean
        get() = SPUtils.getInstance(Constants.SPNAME).getBoolean("mvFaced", false)
        set(mvFaced) = SPUtils.getInstance(Constants.SPNAME).put("mvFaced", mvFaced)


    var isvip: Boolean
        get() = SPUtils.getInstance(Constants.SPNAME).getBoolean("isvip1", false)
        set(vip) = SPUtils.getInstance(Constants.SPNAME).put("isvip1", vip)

    var isPtvip: Boolean
        get() = SPUtils.getInstance(Constants.SPNAME).getBoolean("isPtvip", false)
        set(vip) = SPUtils.getInstance(Constants.SPNAME).put("isPtvip", vip)


    //0未认证/认证不成功     1认证通过     2认证中
    var isverify: Int
        get() = SPUtils.getInstance(Constants.SPNAME).getInt("verify", 0)
        set(verify) = SPUtils.getInstance(Constants.SPNAME).put("verify", verify)


    //0未认证/认证不成功     1认证通过     2认证中
    var HasFaceUrl: Int
        get() = SPUtils.getInstance(Constants.SPNAME).getInt("verify", 0)
        set(verify) = SPUtils.getInstance(Constants.SPNAME).put("verify", verify)

    /**
     * 是否有人脸
     */
    var hasFaceUrl: Boolean
        get() = SPUtils.getInstance(Constants.SPNAME).getBoolean("hasFaceUrl", false)
        set(isFaced) = SPUtils.getInstance(Constants.SPNAME).put("hasFaceUrl", isFaced)


    var alertProtocol
        get() = SPUtils.getInstance(Constants.SPNAME).getBoolean("AlertProtocol", false)
        set(value) = SPUtils.getInstance(Constants.SPNAME).put("AlertProtocol", value)




    //是否已经强制替换过头像
    fun saveForceChangeAvator(isForceChangeAvator: Boolean) {
        SPUtils.getInstance(Constants.SPNAME).put("isForceChangeAvator", isForceChangeAvator)
    }

    fun isForceChangeAvator(): Boolean {
        return SPUtils.getInstance(Constants.SPNAME).getBoolean("isForceChangeAvator", false)
    }

    //是否需要强制替换头像
    fun saveNeedChangeAvator(isNeedChangeAvator: Boolean) {
        SPUtils.getInstance(Constants.SPNAME).put("isNeedChangeAvator", isNeedChangeAvator)
    }

    fun isNeedChangeAvator(): Boolean {
        return SPUtils.getInstance(Constants.SPNAME).getBoolean("isNeedChangeAvator", false)
    }

    //是否需要强制替换头像  7强制替换头像  11真人头像不通过弹窗
    fun saveChangeAvatorType(changeType: Int) {
        SPUtils.getInstance(Constants.SPNAME).put("ChangeAvatorType", changeType)
    }

    fun getChangeAvatorType(): Int {
        return SPUtils.getInstance(Constants.SPNAME).getInt("ChangeAvatorType", 0)
    }

    //是否需要强制替换头像
    fun saveChangeAvator(isNeedChangeAvator: String) {
        SPUtils.getInstance(Constants.SPNAME).put("ChangeAvator", isNeedChangeAvator)
    }

    fun getChangeAvator(): String {
        return SPUtils.getInstance(Constants.SPNAME).getString("ChangeAvator")
    }


    /**
     * 登录成功保存用户信息
     */
    fun isUserInfoMade(): Boolean {
        return !(nickname.isEmpty() ||
                avatar.isEmpty() || avatar.contains(Constants.DEFAULT_AVATAR) ||
                gender == -1 ||
                SPUtils.getInstance(Constants.SPNAME).getInt("birth") == -1
                /*|| (gender == 2 && living_btn)*/
                )
    }


    // 如果已经存在IM用户登录信息，返回LoginInfo，否则返回null即可
    fun loginInfo(): LoginInfo? {
        if (SPUtils.getInstance(Constants.SPNAME).getString("imToken") != null
            && SPUtils.getInstance(Constants.SPNAME).getString("imAccid") != null
        ) {
            DemoCache.setAccount(SPUtils.getInstance(Constants.SPNAME).getString("imAccid"))

            return LoginInfo(
                SPUtils.getInstance(Constants.SPNAME).getString("imAccid"),
                SPUtils.getInstance(Constants.SPNAME).getString("imToken")
            )
        }
        return null
    }


    /**
     * 登录成功保存个人信息
     */
    fun savePersonalInfo(avatar: String, birth: Int, gender: Int, nickname: String) {
        if (avatar.isNotEmpty() && !avatar.contains(Constants.DEFAULT_AVATAR))
            this.avatar = avatar
        if (birth != 0)
            SPUtils.getInstance(Constants.SPNAME).put("birth", birth)
        if (gender != 0)
            SPUtils.getInstance(Constants.SPNAME).put("gender", gender)
        if (nickname.isNotEmpty())
            SPUtils.getInstance(Constants.SPNAME).put("nickname", nickname)
    }

    private fun initNotificationConfig() {
        // 初始化消息提醒
        NIMClient.toggleNotification(UserPreferences.getNotificationToggle())
//        // 加载状态栏配置
        var statusBarNotificationConfig = UserPreferences.getStatusConfig()
        if (statusBarNotificationConfig == null) {
            statusBarNotificationConfig = DemoCache.getNotificationConfig()
            UserPreferences.setStatusConfig(statusBarNotificationConfig)
        }
//        //更新配置
        NIMClient.updateStatusBarNotificationConfig(statusBarNotificationConfig)
    }



    /**
     * 是否是异常账号
     */
    fun saveAccountDanger(danger: Boolean) {
        SPUtils.getInstance(Constants.SPNAME).put("accountDanger", danger)
    }

    fun getAccountDanger(): Boolean {
        return SPUtils.getInstance(Constants.SPNAME).getBoolean("accountDanger", false)
    }


    /**
     * 是否是账号异常头像不通过
     */
    fun saveAccountDangerAvatorNotPass(danger: Boolean) {
        SPUtils.getInstance(Constants.SPNAME).put("AccountDangerAvatorNotPass", danger)
    }

    fun getAccountDangerAvatorNotPass(): Boolean {
        return SPUtils.getInstance(Constants.SPNAME).getBoolean("AccountDangerAvatorNotPass", false)
    }

    /**
     * 是否展示认证提醒
     */
    fun isShowGuideWechat(): Boolean {
        return SPUtils.getInstance(Constants.SPNAME).getBoolean("isShowGuideWechat", false)
    }

    fun saveShowGuideWechat(isShow: Boolean) {
        SPUtils.getInstance(Constants.SPNAME).put("isShowGuideWechat", isShow)
    }

}