//package com.sdy.luxurytravelapplication.app
//
//import android.content.Context
//import com.blankj.utilcode.util.AppUtils
//import com.blankj.utilcode.util.DeviceUtils
//import com.blankj.utilcode.util.LogUtils
//import com.blankj.utilcode.util.SPUtils
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import com.netease.nimlib.sdk.NIMClient
//import com.netease.nimlib.sdk.auth.LoginInfo
//import com.qiniu.android.dns.util.MD5
//import com.qiniu.android.storage.UpCancellationSignal
//import com.sdy.baselibrary.utils.MediaParamBean
//import com.sdy.sweetdateapplication.liveface.FaceLivenessExpActivity
//import com.sdy.sweetdateapplication.model.LoginBean
//import com.sdy.sweetdateapplication.model.TalkInfoBean
//import com.sdy.sweetdateapplication.model.Userinfo
//import com.sdy.sweetdateapplication.nim.config.preference.UserPreferences
//import com.sdy.sweetdateapplication.nim.impl.cache.DemoCache
//import com.sdy.sweetdateapplication.ui.activity.LoginInfoActivity
//import com.sdy.sweetdateapplication.ui.activity.MainActivity
//import org.greenrobot.eventbus.EventBus
//
///**
// *    author : ZFM
// *    date   : 2019/7/1115:23
// *    desc   :
// *    version: 1.0
// */
//object UserManager {
//
//    /*************发布缓存参数*********************/
//    //手动取消上传
//    var cancelUpload = false
//
//    //帮助取消上传的handler
//    val cancellationHandler = UpCancellationSignal { cancelUpload }
//
//    //发布动态的状态
//    var publishState: Int = 0  //0未发布  1进行中   -1失败--违规400  -2失败--发布失败
//
//    //发布
//    var publishParams: HashMap<String, Any> = hashMapOf()
//
//    //发布的媒体资源对象
//    var mediaBeans: MutableList<MediaParamBean> = mutableListOf()
//
//    //发布的对象keylist
//    var keyList: MutableList<String> = mutableListOf()
//
//    /**
//     * 清除发布的参数
//     */
//    fun clearPublishParams() {
//        publishState = 0
//        publishParams.clear()
//        mediaBeans.clear()
//        keyList = mutableListOf()
//        cancelUpload = false
//    }
//
//
//    /*************动态语音播放位置记录****************/
//    var currentPlayPosition = -1
//
//
//    /*************登录注册公共方法*********************/
//
//    fun startToPersonalInfoActivity(context: Context, nothing: LoginInfo?, data: LoginBean) {
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
//        savePersonalInfo(data.userinfo)
//
//        //初始化消息提醒配置
//        initNotificationConfig()
//        if (data.userinfo.gender == 0) {
//            LoginInfoActivity.startToLogin(context, data.extra_data.invite_code)
//        } else if (data.userinfo.gender == 2 && data.extra_data.living_btn) {
//            living_btn = data.extra_data.living_btn
//            FaceLivenessExpActivity.startActivity(
//                context,
//                FaceLivenessExpActivity.TYPE_LIVE_CAPTURE
//            )
//        } else {
////            SelfVoiceIntroduceActivity.start(context,SelfVoiceIntroduceActivity.FROM_REGISTER)
////            context.startActivity<SelfVoiceIntroduceActivity>()
//            MainActivity.startToMain(context)
//        }
//    }
//
//
//    /**
//     * 登录成功保存个人信息
//     */
//    fun savePersonalInfo(userinfo: Userinfo) {
//        if (userinfo.avatar.isNotEmpty() && !userinfo.avatar.contains(Constants.DEFAULT_AVATAR))
//            avatar = userinfo.avatar
//        if (userinfo.birth != 0)
//            SPUtils.getInstance(Constants.SPNAME).put("birth", userinfo.birth)
//        if (userinfo.gender != 0)
//            SPUtils.getInstance(Constants.SPNAME).put("gender", userinfo.gender)
//        if (userinfo.nickname.isNotEmpty())
//            SPUtils.getInstance(Constants.SPNAME).put("nickname", userinfo.nickname)
//    }
//
//
//    /**
//     * 本地缓存的通话记录
//     */
//    var cacheTalkDatas: String
//        get() = SPUtils.getInstance(Constants.SPNAME).getString("talkInfo")
//        set(value) = SPUtils.getInstance(Constants.SPNAME).put("talkInfo", value)
//
//
//    fun getCacheTalkData(): ArrayList<TalkInfoBean> {
//        return if (SPUtils.getInstance(Constants.SPNAME).getString("talkInfo").isNullOrEmpty()) {
//            arrayListOf()
//        } else {
//            Gson().fromJson<ArrayList<TalkInfoBean>>(
//                SPUtils.getInstance(Constants.SPNAME).getString("talkInfo"),
//                object : TypeToken<ArrayList<TalkInfoBean>>() {}.type
//            )
//        }
//    }
//
//    fun updateTalkData(talkBean: TalkInfoBean, isadd: Boolean = true) {
//        val arrayList = getCacheTalkData()
//        val index = arrayList.indexOfFirst { it.accid == talkBean.accid }
//        if (isadd) {
//            if (index != -1) {
//                arrayList[index] = talkBean
//            } else {
//                arrayList.add(talkBean)
//            }
//        } else {
//            if (index != -1)
//                arrayList.removeAt(index)
//        }
//        if (arrayList.isNotEmpty())
//            cacheTalkDatas = Gson().toJson(arrayList)
//        else
//            SPUtils.getInstance(Constants.SPNAME).remove("talkInfo")
//    }
//
//
//    /**
//     * 是否是门槛会员
//     */
//    var isFootVip: Boolean
//        get() = SPUtils.getInstance(Constants.SPNAME).getBoolean("isFootVip", false)
//        set(isFootVip) = SPUtils.getInstance(Constants.SPNAME).put("isFootVip", isFootVip)
//
//
//    /**
//     * 是否是会员
//     */
//    var isVip: Boolean
//        get() = SPUtils.getInstance(Constants.SPNAME).getBoolean("isVip", false)
//        set(isVip) = SPUtils.getInstance(Constants.SPNAME).put("isVip", isVip)
//
//    /**
//     * 视频认证
//     */
//    var mvFaced: Boolean
//        get() = SPUtils.getInstance(Constants.SPNAME).getBoolean("mvFaced", false)
//        set(mvFaced) = SPUtils.getInstance(Constants.SPNAME).put("mvFaced", mvFaced)
//
//    /**
//     * 是否通过真人认证
//     */
//    var isFaced: Boolean
//        get() = SPUtils.getInstance(Constants.SPNAME).getBoolean("isFaced", false)
//        set(isFaced) = SPUtils.getInstance(Constants.SPNAME).put("isFaced", isFaced)
//
//    /**
//     * 是否有人脸
//     */
//    var hasFaceUrl: Boolean
//        get() = SPUtils.getInstance(Constants.SPNAME).getBoolean("hasFaceUrl", false)
//        set(isFaced) = SPUtils.getInstance(Constants.SPNAME).put("hasFaceUrl", isFaced)
//
//    /**
//     * 是否需要做活体检测 true需要做，false不需要
//     */
//    var living_btn: Boolean
//        get() = SPUtils.getInstance(Constants.SPNAME).getBoolean("living_btn", true)
//        set(living_btn) = SPUtils.getInstance(Constants.SPNAME).put("living_btn", living_btn)
//
//
//    /**
//     * 登录成功保存用户信息
//     */
//    fun isUserInfoMade(): Boolean {
//        return !(getNickname().isEmpty() ||
//                avatar.isEmpty() || avatar.contains(Constants.DEFAULT_AVATAR) ||
//                getGender() == -1 ||
//                SPUtils.getInstance(Constants.SPNAME).getInt("birth") == -1
//                || (getGender() == 2 && living_btn))
//    }
//
//
//    // 如果已经存在IM用户登录信息，返回LoginInfo，否则返回null即可
//    fun loginInfo(): LoginInfo? {
//        if (SPUtils.getInstance(Constants.SPNAME).getString("imToken") != null
//            && SPUtils.getInstance(Constants.SPNAME).getString("imAccid") != null
//        ) {
//            DemoCache.setAccount(SPUtils.getInstance(Constants.SPNAME).getString("imAccid"))
//
//            return LoginInfo(
//                SPUtils.getInstance(Constants.SPNAME).getString("imAccid"),
//                SPUtils.getInstance(Constants.SPNAME).getString("imToken")
//            )
//        }
//        return null
//    }
//
//
//    /**
//     * 清除登录信息
//     */
//    fun clearLoginData() {
//        //IM信息
//        SPUtils.getInstance(Constants.SPNAME).remove("imToken")
//        SPUtils.getInstance(Constants.SPNAME).remove("imAccid")
//        //用户信息
//        SPUtils.getInstance(Constants.SPNAME).remove("accid")
//        SPUtils.getInstance(Constants.SPNAME).remove("token")
//        SPUtils.getInstance(Constants.SPNAME).remove("nickname")
//        SPUtils.getInstance(Constants.SPNAME).remove("avatar")
//        SPUtils.getInstance(Constants.SPNAME).remove("gender")
//        SPUtils.getInstance(Constants.SPNAME).remove("birth")
//
//
//        SPUtils.getInstance(Constants.SPNAME).remove("isVip")
//        SPUtils.getInstance(Constants.SPNAME).remove("isFootVip")
//        SPUtils.getInstance(Constants.SPNAME).remove("isFaced")
//        SPUtils.getInstance(Constants.SPNAME).remove("hasFaceUrl")
//        SPUtils.getInstance(Constants.SPNAME).remove("living_btn")
//        SPUtils.getInstance(Constants.SPNAME).remove("mvFaced")
//
//
//        SPUtils.getInstance(Constants.SPNAME).remove("myVerifyBtn")
//        SPUtils.getInstance(Constants.SPNAME).remove("myRedPacketBtn")
//
//
//
//        SPUtils.getInstance(Constants.SPNAME).remove("limit_age_high")
//        SPUtils.getInstance(Constants.SPNAME).remove("limit_age_low")
//        SPUtils.getInstance(Constants.SPNAME).remove("isvip")
//        SPUtils.getInstance(Constants.SPNAME).remove("ishuman")
//        SPUtils.getInstance(Constants.SPNAME).remove("iscall")
//
//        EventBus.getDefault().removeAllStickyEvents()//移除全部
//    }
//
//    /**
//     * 保存位置信息
//     */
//    fun saveLocation(
//        latitude: String?,
//        longtitude: String?,
//        province: String?,
//        city: String?,
//        district: String?,
//        code: String?
//    ) {
//        if (latitude != null)
//            this.latitude = latitude
//        if (longtitude != null)
//            this.longtitude = longtitude
//        if (province != null)
//            this.province = province
//        if (city != null)
//            this.city = city
//        if (district != null)
//            SPUtils.getInstance(Constants.SPNAME).put("district", district)
//        if (code != null)
//            SPUtils.getInstance(Constants.SPNAME).put("citycode", code)
//
//    }
//
//    fun getToken(): String {
//        return SPUtils.getInstance(Constants.SPNAME).getString("token")
//    }
//
//
//    fun getAccid(): String {
//        return SPUtils.getInstance(Constants.SPNAME).getString("accid")
//    }
//
//    var avatar: String
//        get() = SPUtils.getInstance(Constants.SPNAME).getString("avatar")
//        set(value) = SPUtils.getInstance(Constants.SPNAME).put("avatar", value)
//
//
//    fun getQNToken(): String {
//        return SPUtils.getInstance(Constants.SPNAME).getString("qntoken")
//    }
//
//    fun getQNPrefix(): String {
//        return SPUtils.getInstance(Constants.SPNAME).getString("qn_prefix")
//    }
//
//
//    /**
//     * 获取维度
//     */
//
//    var latitude: String
//        get() = SPUtils.getInstance(Constants.SPNAME).getString("latitude", "0")
//        set(value) = SPUtils.getInstance(Constants.SPNAME).put("latitude", value)
//
//
//    /**
//     * 获取经度
//     */
//    var longtitude: String
//        get() = SPUtils.getInstance(Constants.SPNAME).getString("longtitude", "0")
//        set(value) = SPUtils.getInstance(Constants.SPNAME).put("longtitude", value)
//
//    /**
//     * 获取省份
//     */
//    var province: String
//        get() = SPUtils.getInstance(Constants.SPNAME).getString("province", "")
//        set(value) = SPUtils.getInstance(Constants.SPNAME).put("province", value)
//
//
//    /**
//     * 获取城市
//     */
//    var city: String
//        get() = SPUtils.getInstance(Constants.SPNAME).getString("city", "")
//        set(value) = SPUtils.getInstance(Constants.SPNAME).put("city", value)
//
//    /**
//     * 获取城市码
//     */
//    fun getCityCode(): String {
//        return SPUtils.getInstance(Constants.SPNAME).getString("citycode", "")
//    }
//
//
//    fun getGender(): Int {
//        return SPUtils.getInstance(Constants.SPNAME).getInt("gender", 1)
//    }
//
//    fun saveGender(gender: Int) {
//        SPUtils.getInstance(Constants.SPNAME).put("gender", gender)
//    }
//
//
//    fun getNickname(): String {
//        return SPUtils.getInstance(Constants.SPNAME).getString("nickname")
//    }
//
//
//    fun getFilterParams(): HashMap<String, Any> {
//        val params = hashMapOf<String, Any>()
//        if (SPUtils.getInstance().getBoolean("ishuman", false))
//            params["ishuman"] = if (SPUtils.getInstance().getBoolean("ishuman", false)) {
//                1
//            } else {
//                2
//            }
//        else
//            params.remove("ishuman")
//
//        if (getGender() == 1) {
//            if (SPUtils.getInstance().getBoolean("iscall", false))
//                params["iscall"] = if (SPUtils.getInstance().getBoolean("iscall", false)) {
//                    1
//                } else {
//                    2
//                }
//            else {
//                params.remove("iscall")
//            }
//        } else {
//            if (SPUtils.getInstance().getBoolean("isvip", false)) {
//                params["isvip"] = if (SPUtils.getInstance().getBoolean("isvip", false)) {
//                    1
//                } else {
//                    2
//                }
//            } else {
//                params.remove("isvip")
//            }
//        }
//
//
//        params["limit_age_low"] = SPUtils.getInstance().getInt("limit_age_low", 16)
//        params["limit_age_high"] = SPUtils.getInstance().getInt("limit_age_high", 30)
//
//
//        return params
//    }
//
//    fun getBaseParams(): HashMap<String, Any> {
//        return hashMapOf(
//            "token" to getToken(),
//            "accid" to getAccid(),
////            "_timestamp" to System.currentTimeMillis(),
//            "_timestamp" to 1615974026198L,
//            "device_id" to DeviceUtils.getUniqueDeviceId(),
//            "city_name" to "",
//            "province_name" to "",
//            "lat" to "",
//            "lng" to ""
//        )
//    }
//
//    fun getSignParams(params: HashMap<String, Any> = hashMapOf()): HashMap<String, Any> {
//        if (params["_signature"] != null)
//            params.remove("_signature")
//
//        params.putAll(getBaseParams())
//        var sign = "${AppUtils.getAppVersionName()}dcyfyf"
//        var params1 = params
//        for (param in params.toSortedMap()) {
//            sign = sign.plus("${param.key}=${param.value}&")
//        }
//        LogUtils.d(sign)
////        EncryptUtils.encryptMD5ToString(sign.substring(0, sign.length - 1))
//        params1["_signature"] = MD5.encrypt(sign.substring(0, sign.length - 1))
//        LogUtils.d(params1["_signature"])
//        return params1
//    }
//
//
//    fun getSignParams1(params: HashMap<String, Any?> = hashMapOf()): HashMap<String, Any?> {
//        if (params["_signature"] != null)
//            params.remove("_signature")
//        params.putAll(getBaseParams())
//        var sign = "ppsns${AppUtils.getAppVersionName()}dcyfyf"
//        var params1 = params
//        for (param in params.toSortedMap()) {
//            sign = sign.plus("${param.key}=${param.value}&")
//        }
//
//        params1["_signature"] = MD5.encrypt(sign.substring(0, sign.length - 1))
//        return params1
//    }
//
//
//    private fun initNotificationConfig() {
//        // 初始化消息提醒
//        NIMClient.toggleNotification(UserPreferences.getNotificationToggle())
////        // 加载状态栏配置
//        var statusBarNotificationConfig = UserPreferences.getStatusConfig()
//        if (statusBarNotificationConfig == null) {
//            statusBarNotificationConfig = DemoCache.getNotificationConfig()
//            UserPreferences.setStatusConfig(statusBarNotificationConfig)
//        }
////        //更新配置
//        NIMClient.updateStatusBarNotificationConfig(statusBarNotificationConfig)
//    }
//
//
//    var alertProtocol
//        get() = SPUtils.getInstance(Constants.SPNAME).getBoolean("AlertProtocol", false)
//        set(value) = SPUtils.getInstance(Constants.SPNAME).put("AlertProtocol", value)
//
//
//}