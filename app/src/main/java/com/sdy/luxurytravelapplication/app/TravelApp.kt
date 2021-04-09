package com.sdy.luxurytravelapplication.app

import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.Process
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.multidex.MultiDex
import com.alibaba.fastjson.JSONObject
import com.blankj.utilcode.util.*
import com.chuanglan.shanyan_sdk.OneKeyLoginManager
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
import com.sdy.luxurytravelapplication.event.RefreshMessageCenterEvent
import com.sdy.luxurytravelapplication.ext.CommonFunction
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
import com.sdy.luxurytravelapplication.ui.activity.MainActivity
import com.sdy.luxurytravelapplication.utils.UriUtils
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.tencent.bugly.beta.upgrade.UpgradeStateListener
import com.tencent.bugly.crashreport.CrashReport
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.unit.Subunits
import org.greenrobot.eventbus.EventBus
import kotlin.properties.Delegates
import kotlin.random.Random

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
    }

    init {

        SmartRefreshLayout.setDefaultRefreshInitializer { context, layout ->
            layout.setPrimaryColorsId(R.color.colorWhite)
                .setReboundDuration(200)
                .setEnableHeaderTranslationContent(false)
                .setDisableContentWhenRefresh(true)
                .setEnableLoadMoreWhenContentNotFull(false)
                .setDisableContentWhenLoading(true)
        }
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.colorWhite)
                .setReboundDuration(200)
                .setDisableContentWhenRefresh(true)
                .setDisableContentWhenLoading(true)
            MaterialHeader(context).setColorSchemeResources(R.color.colorAccent)
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.colorWhite)
                .setReboundDuration(200)
                .setDisableContentWhenRefresh(true)
                .setDisableContentWhenLoading(true)
                .setEnableScrollContentWhenLoaded(true)
            ClassicsFooter(context).setFinishDuration(200).setDrawableSize(20F)
        }

        DialogSettings.style = DialogSettings.STYLE.STYLE_IOS
        DialogSettings.theme = DialogSettings.THEME.LIGHT
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
            LogUtils.d(it.content)
            LogUtils.d(AppUtils.isAppForeground())
            val object1 = JSONObject.parseObject(it.content)
            val type = if (object1["type"] != null) object1["type"] as Int else -1
            val msg = if (object1["msg"] != null) object1["msg"] as String else ""
            LogUtils.d("msg = $msg")

            when (type) {
                //1 新消息更新红点点
                1 -> {
                    EventBus.getDefault().post(RefreshMessageCenterEvent())
                    val msgs = msg.split("\n")
                    val pendingIntent = PendingIntent.getActivity(
                        this,
                        type,
                        Intent(this, MainActivity::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    val id = Random.nextInt()
//                    sendNotification(
//                        if (msgs.size > 1) {
//                            msgs[0]
//                        } else {
//                            msg
//                        }, if (msgs.size > 1) {
//                            msgs[1]
//                        } else {
//                            msg
//                        }, pendingIntent, id
//                    )

                }

                //2.对方删除自己,本地不删除会话列表
                2 -> {
                    val accid =
                        JSONObject.parseObject(object1["ext"].toString())["accid"].toString()
                    CommonFunction.dissolveRelationshipLocal(accid, true)
                }

                // 4人脸认证未通过
                4 -> {
                    UserManager.isFaced = false
//                    EventBus.getDefault().post(FaceVerifyPassEvent())
                }

                // 9真人认证通过
                9 -> {
                    UserManager.isFaced = true
//                    EventBus.getDefault().post(FaceVerifyPassEvent())
                }

                //  21 语音认证认证通过
                21 -> {
//                    EventBus.getDefault().postSticky(RefreshIndexVerifyEvent(ChatVoiceCallActivity.FLASH_CHAT_VOICE, true))
//                    if (ActivityUtils.isActivityExistsInStack(VerifyInfoCheckingActivity::class.java)) {
//                        ActivityUtils.finishActivity(VerifyInfoCheckingActivity::class.java)
//                    }
//                    EventBus.getDefault().post(UpdateVerifyEvent())
                }

                //31有人喜欢了我
                31 -> {
//                    val customerMsgBean = Gson().fromJson<CustomerMsgBean<FocusBean>>(
//                            it.content,
//                            object : TypeToken<CustomerMsgBean<FocusBean>>() {}.type
//                        )
//                    if (ActivityUtils.getTopActivity() is ChatActivity) {
//                        EventBus.getDefault()
//                            .post(SendLikeTipMessageEvent(customerMsgBean.ext.accid, true))
//                    } else if (ActivityUtils.getTopActivity() is ChatVoiceCallActivity) {
//                        EventBus.getDefault().post(FocusSuccessEvent(customerMsgBean.ext))
//                    }
                    EventBus.getDefault().post(RefreshMessageCenterEvent())
                }

                //金币充值通知 以及金币提现退回通知
                109 -> {
//                    val coin = JSONObject.parseObject(object1["ext"].toString())["mycoin"] as Int
//                    if (ActivityUtils.getTopActivity() is ChatVoiceCallActivity) {
//                        EventBus.getDefault().post(ChargeSuccessEvent(coin))
//                    }
//                    EventBus.getDefault().post(RefreshGoldEvent())

                }

                //{"type":6000,"msg":{"channelName":"channelName","token":"token","expire_time":1743332323}}
                6000 -> { //接受别人的邀请
//                    val classType =
//                        object : TypeToken<CustomerMsgBean<VoiceCallReceiveBean>>() {}.type
//                    val bean = Gson().fromJson<CustomerMsgBean<VoiceCallReceiveBean>>(
//                        it.content,
//                        classType
//                    )

//                    val id = Random.nextInt()
//                    if (!AppUtils.isAppForeground()) {
//                        val pendingIntent = PendingIntent.getActivity(
//                            this,
//                            type,
//                            Intent(this, MainActivity::class.java),
//                            PendingIntent.FLAG_UPDATE_CURRENT
//                        )
//                        sendNotification(
//                            when (bean.ext.chat_type) {
//                                1 -> {
//                                    getString(R.string.flash_text)
//                                }
//                                2 -> {
//                                    getString(R.string.flash_voice)
//                                }
//                                else -> {
//                                    getString(R.string.flasg_video)
//                                }
//                            }, msg, pendingIntent, id
//                        )
//                    }
//                    EventBus.getDefault().postSticky(ShowCallReceiveEvent(bean.ext, id))
                }

                //6100文字闪聊有人接收本地主动发起的请求(更新过度聊天页面)
                6100 -> {
//                    val classType =
//                        object : TypeToken<CustomerMsgBean<VoiceCallReceiveBean>>() {}.type
//                    val bean = Gson().fromJson<CustomerMsgBean<VoiceCallReceiveBean>>(
//                        it.content,
//                        classType
//                    )
//                    EventBus.getDefault().post(MatchFlashChatSuccessEvent(bean.ext))
                }

                //1V1发起方被拒绝
                6200 -> {
//                    if (ActivityUtils.getTopActivity() is ChatVoiceCallActivity) {
//                        EventBus.getDefault().post(RefuseVoiceCallEvent())
//                    }
                }

                //10000礼物消息状态变更
//                Constants.NOTIFICATION_GIFT_STATUS -> {
//                    val customerMsgBean =
//                        Gson().fromJson<CustomerMsgBean<NotificationGiftBean>>(
//                            it.content,
//                            object : TypeToken<CustomerMsgBean<NotificationGiftBean>>() {}.type
//                        )
//
//                    if (ActivityUtils.getTopActivity() is ChatActivity) {
//                        EventBus.getDefault().post(
//                            RefreshGiftStatusEvent(
//                                customerMsgBean.ext.giftReceiveStatus,
//                                customerMsgBean.ext.messageId
//                            )
//                        )
//                    } else {
//                        val params = hashMapOf<String, Any>()
//                        params[SendGiftAttachment.KEY_STATUS] =
//                            customerMsgBean.ext.giftReceiveStatus
//                        val messages =
//                            NIMClient.getService(MsgService::class.java)
//                                .queryMessageListByUuidBlock(
//                                    listOf(customerMsgBean.ext.messageId)
//                                )
//                        if (!messages.isNullOrEmpty())
//                            CommonFunction.updateMessageExtension(
//                                messages[0],
//                                SendGiftAttachment.KEY_STATUS,
//                                customerMsgBean.ext.giftReceiveStatus
//                            )
//                    }
//                    EventBus.getDefault().post(RefreshGoldEvent())
////                    CommonFunction.updateRecentExtension(customerMsgBean.ext.sessionId,params)
//                    //不仅要更新消息的扩展字段 也要更新消息的扩展字段   更新最近联系人的扩展字段
//                }
//
//                //10001对方已经接受联系方式礼物，主动给自己发送对方微信
//                Constants.NOTIFICATION_SEND_WECHAT -> {
//                    val customerMsgBean =
//                        Gson().fromJson<CustomerMsgBean<NotificationWechatBean>>(
//                            it.content,
//                            object : TypeToken<CustomerMsgBean<NotificationWechatBean>>() {}.type
//                        )
//                    val attachment = SendWechatAttachment()
//                    attachment.url = customerMsgBean.ext.wechatUrl
//                    val messge = MessageBuilder.createCustomMessage(
//                        it.fromAccount,
//                        SessionTypeEnum.P2P,
//                        attachment
//                    )
//                    NIMClient.getService(MsgService::class.java)
//                        .sendMessage(messge, false)
//
//                }
//
//                //10002 闪聊过程中对方金币不足挂断通话
//                Constants.NOTIFICATION_NO_COIN -> {
//                    if (ActivityUtils.getTopActivity() is ChatVoiceCallActivity) {
//                        EventBus.getDefault().post(RefuseNoCoinEvent())
//                    }
//                }
            }

        }
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

        // AutoDensityUtil.init()
    }


    private fun initNimUIKit() {
        DemoCache.setContext(this)
        NIMClient.init(this, UserManager.loginInfo(), NimSDKOptionConfig.getSDKOptions(this))

        if (ProcessUtils.isMainProcess()) {
            //华为推送配置
//            com.huawei.hms.support.common.ActivityMgr.INST.init(this)
            //oppo推送初始化
//            HeytapPushManager.init(this, true)
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
        // CrashReport.initCrashReport(applicationContext, Constant.BUGLY_ID, BuildConfig.DEBUG, strategy)
        Bugly.init(applicationContext, Constants.BUGLY_ID, AppUtils.isAppDebug(), strategy)
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