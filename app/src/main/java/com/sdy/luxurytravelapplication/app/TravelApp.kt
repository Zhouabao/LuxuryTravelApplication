package com.sdy.luxurytravelapplication.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.os.Process
import android.util.Log
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.multidex.MultiDex
import com.baidu.idl.face.platform.LivenessTypeEnum
import com.blankj.utilcode.util.*
import com.chuanglan.shanyan_sdk.OneKeyLoginManager
import com.google.gson.Gson
import com.heytap.msp.push.HeytapPushManager
import com.kongzue.dialog.util.DialogSettings
import com.kongzue.dialog.v3.MessageDialog
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.StatusCode
import com.netease.nimlib.sdk.auth.AuthServiceObserver
import com.netease.nimlib.sdk.auth.OnlineClient
import com.netease.nimlib.sdk.mixpush.NIMPushClient
import com.netease.nimlib.sdk.msg.MsgServiceObserve
import com.netease.nimlib.sdk.msg.model.CustomNotification
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.event.*
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.liveface.FaceLivenessExpActivity
import com.sdy.luxurytravelapplication.mvp.model.bean.CustomerMsgBean
import com.sdy.luxurytravelapplication.nim.NIMInitManager
import com.sdy.luxurytravelapplication.nim.api.NimUIKit
import com.sdy.luxurytravelapplication.nim.api.UIKitOptions
import com.sdy.luxurytravelapplication.nim.business.session.SessionHelper
import com.sdy.luxurytravelapplication.nim.config.NimSDKOptionConfig
import com.sdy.luxurytravelapplication.nim.config.preference.UserPreferences
import com.sdy.luxurytravelapplication.nim.impl.cache.DemoCache
import com.sdy.luxurytravelapplication.nim.impl.provider.DemoOnlineStateContentProvider
import com.sdy.luxurytravelapplication.nim.impl.provider.NimDemoLocationProvider
import com.sdy.luxurytravelapplication.nim.mixpush.DemoMixPushMessageHandler
import com.sdy.luxurytravelapplication.nim.mixpush.DemoPushContentProvider
import com.sdy.luxurytravelapplication.ui.activity.*
import com.sdy.luxurytravelapplication.ui.dialog.AccountDangerDialog
import com.sdy.luxurytravelapplication.ui.dialog.ContactNotPassDialog
import com.sdy.luxurytravelapplication.ui.dialog.VerifyNormalResultDialog
import com.sdy.luxurytravelapplication.ui.fragment.MessageFragment
import com.sdy.luxurytravelapplication.ui.fragment.SnackBarFragment
import com.sdy.luxurytravelapplication.utils.ChannelUtils
import com.sdy.luxurytravelapplication.utils.UriUtils
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.tencent.bugly.beta.upgrade.UpgradeStateListener
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.socialize.PlatformConfig
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.unit.Subunits
import org.greenrobot.eventbus.EventBus
import kotlin.properties.Delegates

/**
 * Created by chenxz on 2018/4/21.
 */
class TravelApp : Application() {

    private var refWatcher: RefWatcher? = null

    companion object {
        val TAG = "TravelApp"
        var context: Context by Delegates.notNull()
            private set
        lateinit var instance: Application

        fun getRefWatcher(context: Context): RefWatcher? {
            val app = context.applicationContext as TravelApp
            return app.refWatcher
        }

        public var livenessList = mutableListOf<LivenessTypeEnum>()
        public var isLivewnessRandom = false
    }

    init {

        SmartRefreshLayout.setDefaultRefreshInitializer { context, layout ->
            layout.setPrimaryColorsId(R.color.colorWhite)
                .setReboundDuration(200)
                .setEnableHeaderTranslationContent(false)
//                .setDisableContentWhenRefresh(true)
//                .setEnableLoadMoreWhenContentNotFull(false)
//                .setDisableContentWhenLoading(true)
        }
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.colorWhite)
                .setReboundDuration(200)
//                .setDisableContentWhenRefresh(true)
//                .setDisableContentWhenLoading(true)
            MaterialHeader(context).setColorSchemeResources(R.color.colorAccent)
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.colorWhite)
                .setReboundDuration(200)
                .setEnableAutoLoadMore(false)
//                .setDisableContentWhenRefresh(false)
//                .setDisableContentWhenLoading(false)
//                .setEnableScrollContentWhenLoaded(false)
            ClassicsFooter(context).setFinishDuration(200).setDrawableSize(20F)
        }

//        DialogSettings.buttonPositiveTextInfo = TextInfo().setFontColor(Color.parseColor("#1ED0A7"))
        DialogSettings.style = DialogSettings.STYLE.STYLE_IOS
        DialogSettings.theme = DialogSettings.THEME.LIGHT
        DialogSettings.tipBackgroundResId = 0
        DialogSettings.tipTheme = DialogSettings.THEME.LIGHT
//        DialogSettings.init()
//        DialogSettings.buttonTextInfo = TextInfo().setFontColor(Color.parseColor("#FFC6CAD4"))

        livenessList.add(LivenessTypeEnum.Mouth)
        livenessList.add(LivenessTypeEnum.HeadLeft)
        livenessList.add(LivenessTypeEnum.HeadRight)
    }


    private val onLineClient: Observer<List<OnlineClient>> = Observer {
        if (it == null || it.isEmpty()) {
            return@Observer
        } else {
            if (ActivityUtils.getTopActivity() != null)
                MessageDialog.show(
                    ActivityUtils.getTopActivity() as AppCompatActivity,
                    getString(R.string.login_expired),
                    getString(R.string.please_relogin)
                ).setOnOkButtonClickListener { baseDialog, v ->
                    CommonFunction.loginOut(ActivityUtils.getTopActivity())
                    false
                }
        }

    }
    private val userStatusObserver: Observer<StatusCode> by lazy {
        Observer<StatusCode> {
            if (it.wontAutoLogin()) {
                if (ActivityUtils.getTopActivity() != null)
                    MessageDialog.show(
                        ActivityUtils.getTopActivity() as AppCompatActivity,
                        getString(R.string.login_expired),
                        getString(R.string.please_relogin)
                    )
                        .setCancelable(false)
                        .setOnOkButtonClickListener { baseDialog, v ->
                            CommonFunction.loginOut(ActivityUtils.getTopActivity())
                            false
                        }
            }
        }
    }


    /**
     * 系统通知监听
     */
    private val customNotificationObserver: Observer<CustomNotification> = Observer {
        if (it.content != null) {
            val customerMsgBean =
                Gson().fromJson<CustomerMsgBean>(
                    it.content,
                    CustomerMsgBean::class.java
                )
            Log.d("customerMsgBean", "${customerMsgBean.toString()}====")
            when (customerMsgBean.type) {
                //1.系统通知新的消息数量
                1 -> {
                    EventBus.getDefault().postSticky(GetNewMsgEvent())
                    EventBus.getDefault().postSticky(UpdateHiEvent())
                    initNotificationManager(customerMsgBean.msg)
                }
                //2.对方删除自己,本地不删除会话列表
                2 -> {
                    CommonFunction.dissolveRelationshipLocal(customerMsgBean.accid, true)
                }
                //4人脸认证不通过
                4 -> {
                    //更改本地的认证状态
                    UserManager.isverify = 0
                    //更改本地的筛选认证状态
                    if (SPUtils.getInstance(Constants.SPNAME).getInt("audit_only", -1) == 2) {
                        SPUtils.getInstance(Constants.SPNAME).remove("audit_only")

                    }
                    EventBus.getDefault().post(FemaleVerifyEvent(0))
                    //如果账号存在异常，就发送认证不通过弹窗
                    if (UserManager.getAccountDanger() || UserManager.getAccountDangerAvatorNotPass()) {
                        EventBus.getDefault()
                            .postSticky(AccountDangerEvent(AccountDangerDialog.VERIFY_NOT_PASS))
                    } else {
                        if (ActivityUtils.getTopActivity() !is RegisterInfoOneActivity
                            && ActivityUtils.getTopActivity() !is RegisterInfoTwoActivity
                            && ActivityUtils.getTopActivity() !is InviteCodeActivity
                            && ActivityUtils.getTopActivity() !is GetMoreMatchActivity
                            && ActivityUtils.getTopActivity() !is FaceLivenessExpActivity
                            && ActivityUtils.getTopActivity() !is PurchaseFootActivity
                        )
                            CommonFunction.startToFace(
                                ActivityUtils.getTopActivity(),
                                FaceLivenessExpActivity.TYPE_ACCOUNT_NORMAL
                            )
                    }
                }
                //7强制替换头像
                7 -> {
                    EventBus.getDefault()
                        .postSticky(ReVerifyEvent(customerMsgBean.type, customerMsgBean.msg))
                    UserManager.saveChangeAvator(customerMsgBean.msg)
                    UserManager.saveChangeAvatorType(1)
                }
                //11真人头像不通过弹窗
                11 -> {
                    EventBus.getDefault()
                        .postSticky(ReVerifyEvent(customerMsgBean.type, customerMsgBean.msg))
                    UserManager.saveChangeAvator(customerMsgBean.msg)
                    UserManager.saveChangeAvatorType(2)
                }

                //8账号异常提示去变更账号
                //const SHUMEI_APPROVE = 8; // 数美强制认证（没有人脸时别过）
                //const SHUMEI_APPROVE_FACE = 81; // 数美强制认证（有人脸时别过）
                //const SUCCESS_FORCE = 10; // 强制认证（没有人脸时别过）
                //const SUCCESS_FORCE_FACE = 101; // 强制认证（有人脸时别过）
                8, 81 -> {
                    UserManager.saveAccountDanger(true)
                    EventBus.getDefault()
                        .postSticky(AccountDangerEvent(AccountDangerDialog.VERIFY_NEED_ACCOUNT_DANGER))
                }
                //9人脸认证通过的通知
                9 -> {
                    if (UserManager.getAccountDanger()) {
                        UserManager.saveAccountDanger(false)
                    }
                    if (UserManager.getAccountDangerAvatorNotPass()) {
                        UserManager.saveAccountDangerAvatorNotPass(false)
                    }
                    EventBus.getDefault().post(FemaleVerifyEvent(1))
                    UserManager.isverify = 1
                    UserManager.hasFaceUrl = true
                    if (SPUtils.getInstance(Constants.SPNAME).getInt("audit_only", -1) != -1) {
                        SPUtils.getInstance(Constants.SPNAME).remove("audit_only")
                        EventBus.getDefault().postSticky(UserCenterEvent(true))
                    }

                    EventBus.getDefault()
                        .postSticky(AccountDangerEvent(AccountDangerDialog.VERIFY_PASS))
                }

                //视频介绍审核通过
                91 -> {
                    VerifyNormalResultDialog(VerifyNormalResultDialog.VERIFY_NORMAL_PASS).show()
                    //更新录制视频介绍
                    EventBus.getDefault().post(FemaleVideoEvent(1))
                    EventBus.getDefault().post(UpdateFeaturedEvent())
                    EventBus.getDefault().post(UpdateLuxuryEvent())
                }
                //视频介绍审核不通过
                93 -> {
                    VerifyNormalResultDialog(VerifyNormalResultDialog.VERIFY_NORMAL_NOTPASS_CHANGE_VIDEO).show()

                    //更新录制视频介绍
                    EventBus.getDefault().post(FemaleVideoEvent(0))
                    EventBus.getDefault().post(UpdateLuxuryEvent())
                }

                //联系方式审核未通过
                99 -> {
                    ContactNotPassDialog().show()
                    EventBus.getDefault().post(UserCenterContactEvent(0))
                }
                //10头像未通过审核去进行人脸认证
                10, 101 -> {
                    UserManager.saveAccountDangerAvatorNotPass(true)
                    EventBus.getDefault()
                        .postSticky(AccountDangerEvent(AccountDangerDialog.VERIFY_NEED_AVATOR_INVALID))
                }
                SnackBarFragment.SOMEONE_LIKE_YOU,
                SnackBarFragment.SOMEONE_MATCH_SUCCESS,
                SnackBarFragment.FLASH_SUCCESS,
                SnackBarFragment.HELP_CANDY,
                SnackBarFragment.CHAT_SUCCESS,
                SnackBarFragment.GREET_SUCCESS,
                SnackBarFragment.GIVE_GIFT -> {
                    EventBus.getDefault().postSticky(GetNewMsgEvent())
                    if (ActivityUtils.getTopActivity() is MainActivity && (ActivityUtils.getTopActivity() as MainActivity).binding.vpMain.currentItem != 3)
                        SnackBarFragment.showAlert(customerMsgBean)
                }
                106, 300, 301 -> {
                    //106门槛支付成功
                    //300通过甜心认证,301甜心认证不通过
                    EventBus.getDefault().post(UpdateLuxuryEvent())
                    EventBus.getDefault().postSticky(UserCenterEvent(true))
                    EventBus.getDefault().postSticky(RefreshMyCandyEvent())
                }

                111, 112 -> {//微信公众号绑定成功
                    EventBus.getDefault()
                        .post(UpdateWechatSettingsEvent(customerMsgBean.type == 111))
                    UserManager.saveShowGuideWechat(true)

                }
                401 -> { //todo 系统假消息，以及真人的第一条搭讪语打招呼成功 发送系统消息
                    if (ActivityUtils.getTopActivity() != null && ActivityUtils.getTopActivity() is MainActivity
                        && FragmentUtils.getTopShow((ActivityUtils.getTopActivity() as MainActivity).supportFragmentManager) != null
                        && FragmentUtils.getTopShow((ActivityUtils.getTopActivity() as MainActivity).supportFragmentManager) !is MessageFragment
                    ) {
                    }
//                        GreetHiDialog(customerMsgBean).show()
//                            FragmentUtils.add(
//                                (ActivityUtils.getTopActivity() as AppCompatActivity).supportFragmentManager,
//                                GreetHiFragment(customerMsgBean),
//                                android.R.id.content
//                            )

                }

            }

        }
    }


    private fun initNotificationManager(msg: String) {
        val msgs = msg.split("\\n")

        var manager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        //8.0 以后需要加上channelId 才能正常显示
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "subscribe"
            val channelName = getString(R.string.default_notification)
            manager.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH
                )
            )
        }
        //为了版本兼容  选择V7包下的NotificationCompat进行构造
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(this, "subscribe")
            .setContentText(
                if (msgs.size > 1) {
                    msgs[1]
                } else {
                    msg
                }
            )
            .setContentTitle(
                if (msgs.size > 1) {
                    msgs[0]
                } else {
                    ""
                }
            )
            .setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.icon_default_avatar)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.icon_default_avatar
                )
            )
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        manager.notify(1, notification)

    }


    override fun onCreate() {
        super.onCreate()
        instance = this
        context = applicationContext
        refWatcher = setupLeakCanary()
        configUnits()
        Utils.init(this)
        CrashUtils.init(UriUtils.getCacheDir(context))
        initBugly()
        initSy()
        initNimUIKit()
        initUmeng()

        // AutoDensityUtil.init()
    }


    private fun initNimUIKit() {
        DemoCache.setContext(this)
        NIMClient.init(this, UserManager.loginInfo(), NimSDKOptionConfig.getSDKOptions(this))

        if (ProcessUtils.isMainProcess()) {
            //华为推送配置
            com.huawei.hms.support.common.ActivityMgr.INST.init(this)
            //oppo推送初始化
            HeytapPushManager.init(this, true)
            //注册自定义推送消息处理，可选项
            NIMPushClient.registerMixPushMessageHandler(DemoMixPushMessageHandler())
            //初始化消息提醒
            initUiKit()

            //初始化消息提醒
            NIMClient.toggleNotification(UserPreferences.getNotificationToggle())
            //关闭撤回消息提醒
            NIMClient.toggleRevokeMessageNotification(false)

            //云信SDK相关业务初始化
            NIMInitManager.getInstance().init(true)

            //初始化音视频模块
//            initAVChatKit()
            //多端互踢
            NIMClient.getService(AuthServiceObserver::class.java)
                .observeOtherClients(onLineClient, true)
            //在线状态
            NIMClient.getService(AuthServiceObserver::class.java)
                .observeOnlineStatus(userStatusObserver, true)

            //自定义通知监听
            NIMClient.getService(MsgServiceObserve::class.java)
                .observeCustomNotification(customNotificationObserver, true)

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WebView.setDataDirectorySuffix("${Process.myPid()}")
        }

    }


    private fun initUiKit() {
        //初始化
        NimUIKit.init(this, buildUIKitOptions())
        //设置地理位置提供者
        NimUIKit.setLocationProvider(NimDemoLocationProvider())
        //IM会话窗口的定制初始化
        SessionHelper.init()
        //通讯录列表定制初始化
//        ContactHelper.init()
        // 添加自定义推送文案以及选项，请开发者在各端（Android、IOS、PC、Web）消息发送时保持一致，以免出现通知不一致的情况
        NimUIKit.setCustomPushContentProvider(DemoPushContentProvider())
        NimUIKit.setOnlineStateContentProvider(DemoOnlineStateContentProvider())


    }


    fun buildUIKitOptions(): UIKitOptions {
        val options = UIKitOptions()
        // 设置app图片/音频/日志等缓存目录
        options.appCacheDir = NimSDKOptionConfig.getAppCacheDir(this)
        return options
    }


    private fun configUnits() {
        AutoSizeConfig.getInstance().unitsManager.setSupportDP(true).supportSubunits = Subunits.PT
    }

    /**
     * 初始化 Bugly
     */
    private fun initBugly() {
        if (AppUtils.isAppDebug()) {
            return
        }
        /**
         * true表示app启动自动初始化升级模块; false不会自动初始化;
         * 开发者如果担心sdk初始化影响app启动速度，可以设置为false，
         * 在后面某个时刻手动调用Beta.init(getApplicationContext(),false);
         */
        //Beta.autoInit = false
        /**
         * true表示初始化时自动检查升级; false表示不会自动检查升级,需要手动调用Beta.checkUpgrade()方法;
         */
        Beta.autoCheckUpgrade = false
        /**
         * 设置升级检查周期为60s(默认检查周期为0s)，60s内SDK不重复向后台请求策略);
         */
        //Beta.upgradeCheckPeriod = 60 * 1000
        /**
         * 设置启动延时为1s（默认延时3s），APP启动1s后初始化SDK，避免影响APP启动速度;
         */
        Beta.initDelay = 1 * 1000
        /**
         * 设置通知栏大图标，largeIconId为项目中的图片资源;
         */
        Beta.largeIconId = R.mipmap.ic_launcher
        /**
         * 设置状态栏小图标，smallIconId为项目中的图片资源Id;
         */
        Beta.smallIconId = R.mipmap.ic_launcher
        /**
         * 设置更新弹窗默认展示的banner，defaultBannerId为项目中的图片资源Id;
         * 当后台配置的banner拉取失败时显示此banner，默认不设置则展示“loading“;
         */
        Beta.defaultBannerId = R.mipmap.ic_launcher
        /**
         * 设置sd卡的Download为更新资源保存目录;
         * 后续更新资源会保存在此目录，需要在manifest中添加WRITE_EXTERNAL_STORAGE权限;
         */
        Beta.storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        /**
         * 已经确认过的弹窗在APP下次启动自动检查更新时会再次显示;
         */
        Beta.showInterruptedStrategy = false
        /**
         * 只允许在MainActivity上显示更新弹窗，其他activity上不显示弹窗; 不设置会默认所有activity都可以显示弹窗;
         */
        //Beta.canShowUpgradeActs.add(MainActivity::class.java)

        // 设置是否为上报进程
        val strategy = CrashReport.UserStrategy(applicationContext)
        strategy.isUploadProcess = false || ProcessUtils.isMainProcess()

        Beta.upgradeStateListener = upgradeStateListener

        // 自定义更新布局要设置在 init 之前
        // R.layout.layout_upgrade_dialog 文件要注意两点
        // 注意1: 半透明背景要自己加上
        // 注意2: 即使自定义的弹窗不需要title, info等这些信息, 也需要将对应的tag标出出来, 一共有5个
        Beta.upgradeDialogLayoutId = R.layout.dialog_update_app
        Beta.strUpgradeDialogUpgradeBtn = getString(R.string.upgrade_now)
        Beta.strUpgradeDialogCancelBtn = getString(R.string.say_next_time)
        //Beta.dialogFullScreen = true
        Bugly.init(context, Constants.BUGLY_ID, AppUtils.isAppDebug(), strategy)
    }


    /**
     * 初始化数美
     */
    private fun initSy() {
        //code为1022:成功；其他：失败
        OneKeyLoginManager.getInstance().init(applicationContext, Constants.SY_APP_ID) { p0, p1 ->
            LogUtils.d("code=$p0,result=$p1")
        }
    }

    private fun initUmeng() {
        if (ThreadUtils.isMainThread()) {
            /**
             * 初始化common库
             * 参数1:上下文，不能为空
             * 参数2:【友盟+】 AppKey
             * 参数3:【友盟+】 Channel
             * 参数4:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
             * 参数5:Push推送业务的secret
             */
            UMConfigure.init(
                this,
                getString(R.string.UMENG_APPKEY),
                ChannelUtils.getChannel(this),
                UMConfigure.DEVICE_TYPE_PHONE,
                null
            )

            /**
             * 设置组件化的Log开关
             * 参数: boolean 默认为false，如需查看LOG设置为true
             */
            UMConfigure.setLogEnabled(Constants.TEST)
            /**
             * 设置日志加密
             * 参数：boolean 默认为false（不加密）
             */
            UMConfigure.setEncryptEnabled(true)
            // 选用AUTO页面采集模式
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)


            //微信平台
            PlatformConfig.setWeixin(Constants.WECHAT_APP_ID, Constants.WECHAT_APP_KEY)
            PlatformConfig.setWXFileProvider("com.sdy.luxurytravelapplication.fileProvider")
            //qq空间平台
            PlatformConfig.setQQZone(Constants.QQ_APP_KEY, Constants.QQ_APP_SECRET)
            PlatformConfig.setQQFileProvider("com.sdy.luxurytravelapplication.fileProvider")


        }
    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }


    private fun setupLeakCanary(): RefWatcher {
        return if (LeakCanary.isInAnalyzerProcess(this)) {
            RefWatcher.DISABLED
        } else LeakCanary.install(this)
    }


    private val upgradeStateListener = object : UpgradeStateListener {
        override fun onDownloadCompleted(isManual: Boolean) {
        }

        override fun onUpgradeSuccess(isManual: Boolean) {
        }

        override fun onUpgradeFailed(isManual: Boolean) {
            if (isManual) {
                ToastUtils.showShort(getString(R.string.check_version_fail))
            }
        }

        override fun onUpgrading(isManual: Boolean) {
            if (isManual) {
                ToastUtils.showShort(getString(R.string.check_version_ing))
            }
        }

        override fun onUpgradeNoVersion(isManual: Boolean) {
            if (isManual) {
                ToastUtils.showShort(getString(R.string.check_no_version))
            }
        }
    }


}