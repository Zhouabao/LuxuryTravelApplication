package com.sdy.luxurytravelapplication.nim.impl;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.sdy.luxurytravelapplication.nim.api.UIKitInitStateListener;
import com.sdy.luxurytravelapplication.nim.api.UIKitOptions;
import com.sdy.luxurytravelapplication.nim.api.model.contact.ContactChangedObservable;
import com.sdy.luxurytravelapplication.nim.api.model.contact.ContactEventListener;
import com.sdy.luxurytravelapplication.nim.api.model.contact.ContactProvider;
import com.sdy.luxurytravelapplication.nim.api.model.location.LocationProvider;
import com.sdy.luxurytravelapplication.nim.api.model.main.CustomPushContentProvider;
import com.sdy.luxurytravelapplication.nim.api.model.main.LoginSyncDataStatusObserver;
import com.sdy.luxurytravelapplication.nim.api.model.main.OnlineStateChangeObservable;
import com.sdy.luxurytravelapplication.nim.api.model.main.OnlineStateContentProvider;
import com.sdy.luxurytravelapplication.nim.api.model.recent.RecentCustomization;
import com.sdy.luxurytravelapplication.nim.api.model.session.SessionCustomization;
import com.sdy.luxurytravelapplication.nim.api.model.session.SessionEventListener;
import com.sdy.luxurytravelapplication.nim.api.model.user.IUserInfoProvider;
import com.sdy.luxurytravelapplication.nim.api.model.user.UserInfoObservable;
import com.sdy.luxurytravelapplication.nim.business.audio.MessageAudioControl;
import com.sdy.luxurytravelapplication.nim.business.emoji.StickerManager;
import com.sdy.luxurytravelapplication.nim.business.module.MsgRevokeFilter;
import com.sdy.luxurytravelapplication.nim.business.preference.UserPreferences;
import com.sdy.luxurytravelapplication.nim.business.session.viewholder.MsgViewHolderFactory;
import com.sdy.luxurytravelapplication.nim.common.util.log.LogUtil;
import com.sdy.luxurytravelapplication.nim.common.util.storage.StorageType;
import com.sdy.luxurytravelapplication.nim.common.util.storage.StorageUtil;
import com.sdy.luxurytravelapplication.nim.common.util.sys.ScreenUtil;
import com.sdy.luxurytravelapplication.nim.impl.cache.DataCacheManager;
import com.sdy.luxurytravelapplication.nim.impl.customization.DefaultContactEventListener;
import com.sdy.luxurytravelapplication.nim.impl.customization.DefaultP2PSessionCustomization;
import com.sdy.luxurytravelapplication.nim.impl.customization.DefaultRecentCustomization;
import com.sdy.luxurytravelapplication.nim.impl.provider.DefaultContactProvider;
import com.sdy.luxurytravelapplication.nim.impl.provider.DefaultUserInfoProvider;
import com.sdy.luxurytravelapplication.nim.support.glide.ImageLoaderKit;
import com.sdy.luxurytravelapplication.nim.business.session.activity.ChatActivity;
import com.sdy.luxurytravelapplication.nim.business.session.viewholder.MsgViewHolderBase;

/**
 * UIKit??????????????????
 */
public final class NimUIKitImpl {

    // context
    private static Context context;

    // ?????????????????????
    private static String account;

    private static UIKitOptions options;

    // ?????????????????????
    private static IUserInfoProvider userInfoProvider;

    // ????????????????????????
    private static ContactProvider contactProvider;

    // ???????????????????????????
    private static LocationProvider locationProvider;

    // ????????????????????????????????????
    private static ImageLoaderKit imageLoaderKit;

    // ???????????????????????????????????????????????????????????????
    private static SessionEventListener sessionListener;

    // ??????????????????????????????????????????????????????
    private static ContactEventListener contactEventListener;


    // ?????????????????????
    private static MsgRevokeFilter msgRevokeFilter;

    // ?????????????????????
    private static CustomPushContentProvider customPushContentProvider;

    // ??????????????????
    private static SessionCustomization commonP2PSessionCustomization;

    // ??????????????????
    private static SessionCustomization commonTeamSessionCustomization;

    // ???????????????????????????
    private static RecentCustomization recentCustomization;

    // ????????????????????????
    private static OnlineStateContentProvider onlineStateContentProvider;

    // ????????????????????????
    private static OnlineStateChangeObservable onlineStateChangeObservable;

    // userInfo ????????????
    private static UserInfoObservable userInfoObservable;

    // contact ????????????
    private static ContactChangedObservable contactChangedObservable;


    // ??????????????????
    private static boolean buildCacheComplete = false;

    //?????????????????????
    private static UIKitInitStateListener initStateListener;

    /*
     * ****************************** ????????? ******************************
     */
    public static void init(Context context) {
        init(context, new UIKitOptions(), null, null);
    }

    public static void init(Context context, UIKitOptions options) {
        init(context, options, null, null);
    }

    public static void init(Context context, IUserInfoProvider userInfoProvider, ContactProvider contactProvider) {
        init(context, new UIKitOptions(), userInfoProvider, contactProvider);
    }

    public static void init(Context context, UIKitOptions options, IUserInfoProvider userInfoProvider, ContactProvider contactProvider) {
        NimUIKitImpl.context = context.getApplicationContext();
        NimUIKitImpl.options = options;
        // init tools
        StorageUtil.init(context, options.appCacheDir);
        ScreenUtil.init(context);

        if (options.loadSticker) {
            StickerManager.getInstance().init();
        }

        // init log
        String path = StorageUtil.getDirectoryByDirType(StorageType.TYPE_LOG);
        LogUtil.init(path, Log.DEBUG);


        NimUIKitImpl.imageLoaderKit = new ImageLoaderKit(context);

        if (!options.independentChatRoom) {
            initUserInfoProvider(userInfoProvider);
            initContactProvider(contactProvider);
            initDefaultSessionCustomization();
            initDefaultContactEventListener();
            // init data cache
            LoginSyncDataStatusObserver.getInstance().registerLoginSyncDataStatus(true);  // ????????????????????????????????????
            DataCacheManager.observeSDKDataChanged(true);
        }

        if (!TextUtils.isEmpty(getAccount())) {
            if (options.initAsync) {
                DataCacheManager.buildDataCacheAsync(); // build data cache on auto login
            } else {
                DataCacheManager.buildDataCache(); // build data cache on auto login
                buildCacheComplete = true;
            }
            getImageLoaderKit().buildImageCache(); // build image cache on auto login
        }
    }

    public static boolean isInitComplete() {
        return !options.initAsync || TextUtils.isEmpty(account) || buildCacheComplete;
    }

    public static void setInitStateListener(UIKitInitStateListener listener) {
        initStateListener = listener;
    }

    public static void notifyCacheBuildComplete() {
        buildCacheComplete = true;
        if (initStateListener != null) {
            initStateListener.onFinish();
        }
    }

    /*
    * ****************************** ???????????? ******************************
    */
    public static AbortableFuture<LoginInfo> login(LoginInfo loginInfo, final RequestCallback<LoginInfo> callback) {

        AbortableFuture<LoginInfo> loginRequest = NIMClient.getService(AuthService.class).login(loginInfo);
        loginRequest.setCallback(new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo loginInfo) {
                loginSuccess(loginInfo.getAccount());
                callback.onSuccess(loginInfo);
            }

            @Override
            public void onFailed(int code) {
                callback.onFailed(code);
            }

            @Override
            public void onException(Throwable exception) {
                callback.onException(exception);
            }
        });
        return loginRequest;
    }

    public static void loginSuccess(String account) {
        setAccount(account);
        DataCacheManager.buildDataCache();
        buildCacheComplete = true;
        getImageLoaderKit().buildImageCache();
    }

    public static void logout() {
        DataCacheManager.clearDataCache();
        getImageLoaderKit().clear();
        LoginSyncDataStatusObserver.getInstance().reset();
    }


    public static UIKitOptions getOptions() {
        return options;
    }

    // ??????????????????????????????
    private static void initUserInfoProvider(IUserInfoProvider userInfoProvider) {

        if (userInfoProvider == null) {
            userInfoProvider = new DefaultUserInfoProvider();
        }

        NimUIKitImpl.userInfoProvider = userInfoProvider;
    }

    // ?????????????????????????????????
    private static void initContactProvider(ContactProvider contactProvider) {
        if (contactProvider == null) {
            contactProvider = new DefaultContactProvider();
        }

        NimUIKitImpl.contactProvider = contactProvider;
    }

    // ????????????????????????P2P???Team???ChatRoom
    private static void initDefaultSessionCustomization() {
        if (commonP2PSessionCustomization == null) {
            commonP2PSessionCustomization = new DefaultP2PSessionCustomization();
        }

        if (recentCustomization == null) {
            recentCustomization = new DefaultRecentCustomization();
        }
    }

    // ??????????????????????????????
    private static void initDefaultContactEventListener() {
        if (contactEventListener == null) {
            contactEventListener = new DefaultContactEventListener();
        }
    }

    public static void startP2PSession(Context context, String account) {
        startP2PSession(context, account, null);
    }

    public static void startP2PSession(Context context, String account, IMMessage anchor) {
        NimUIKitImpl.startChatting(context, account, SessionTypeEnum.P2P, commonP2PSessionCustomization, anchor);
    }

    public static void startTeamSession(Context context, String tid) {
        startTeamSession(context, tid, null);
    }

    public static void startTeamSession(Context context, String tid, IMMessage anchor) {
        NimUIKitImpl.startChatting(context, tid, SessionTypeEnum.Team, commonTeamSessionCustomization, anchor);
    }

    public static void startTeamSession(Context context, String tid, SessionCustomization sessionCustomization, IMMessage anchor) {
        NimUIKitImpl.startChatting(context, tid, SessionTypeEnum.Team, sessionCustomization, anchor);
    }

    public static void startChatting(Context context, String id, SessionTypeEnum sessionType, SessionCustomization
            customization, IMMessage anchor) {
        ChatActivity.Companion.start(context,id,null,customization);
    }

    public static IUserInfoProvider getUserInfoProvider() {
        return userInfoProvider;
    }

    public static UserInfoObservable getUserInfoObservable() {
        if (userInfoObservable == null) {
            userInfoObservable = new UserInfoObservable(context);
        }
        return userInfoObservable;
    }

    public static ContactProvider getContactProvider() {
        return contactProvider;
    }



    public static ContactChangedObservable getContactChangedObservable() {
        if (contactChangedObservable == null) {
            contactChangedObservable = new ContactChangedObservable(context);
        }
        return contactChangedObservable;
    }


    public static LocationProvider getLocationProvider() {
        return locationProvider;
    }

    public static ImageLoaderKit getImageLoaderKit() {
        return imageLoaderKit;
    }

    public static void setLocationProvider(LocationProvider locationProvider) {
        NimUIKitImpl.locationProvider = locationProvider;
    }

    public static SessionCustomization getCommonP2PSessionCustomization() {
        return commonP2PSessionCustomization;
    }

    public static void setCommonP2PSessionCustomization(SessionCustomization commonP2PSessionCustomization) {
        NimUIKitImpl.commonP2PSessionCustomization = commonP2PSessionCustomization;
    }

    public static SessionCustomization getCommonTeamSessionCustomization() {
        return commonTeamSessionCustomization;
    }

    public static void setCommonTeamSessionCustomization(SessionCustomization commonTeamSessionCustomization) {
        NimUIKitImpl.commonTeamSessionCustomization = commonTeamSessionCustomization;
    }

    public static void setRecentCustomization(RecentCustomization recentCustomization) {
        NimUIKitImpl.recentCustomization = recentCustomization;
    }


    public static RecentCustomization getRecentCustomization() {
        return recentCustomization;
    }

    public static void registerMsgItemViewHolder(Class<? extends MsgAttachment> attach, Class<? extends MsgViewHolderBase> viewHolder) {
        MsgViewHolderFactory.register(attach, viewHolder);
    }



    public static void registerTipMsgViewHolder(Class<? extends MsgViewHolderBase> viewHolder) {
        MsgViewHolderFactory.registerTipMsgViewHolder(viewHolder);
    }

    public static void setAccount(String account) {
        NimUIKitImpl.account = account;
    }

    public static SessionEventListener getSessionListener() {
        return sessionListener;
    }

    public static void setSessionListener(SessionEventListener sessionListener) {
        NimUIKitImpl.sessionListener = sessionListener;
    }

    public static ContactEventListener getContactEventListener() {
        return contactEventListener;
    }


    public static void setContactEventListener(ContactEventListener contactEventListener) {
        NimUIKitImpl.contactEventListener = contactEventListener;
    }


    public static void setMsgRevokeFilter(MsgRevokeFilter msgRevokeFilter) {
        NimUIKitImpl.msgRevokeFilter = msgRevokeFilter;
    }

    public static MsgRevokeFilter getMsgRevokeFilter() {
        return msgRevokeFilter;
    }

    public static CustomPushContentProvider getCustomPushContentProvider() {
        return customPushContentProvider;
    }

    public static void setCustomPushContentProvider(CustomPushContentProvider mixPushCustomConfig) {
        NimUIKitImpl.customPushContentProvider = mixPushCustomConfig;
    }

    /*
    * ****************************** ???????????? ******************************
    */

    public static void setOnlineStateContentProvider(OnlineStateContentProvider onlineStateContentProvider) {
        NimUIKitImpl.onlineStateContentProvider = onlineStateContentProvider;
    }

    public static OnlineStateContentProvider getOnlineStateContentProvider() {
        return onlineStateContentProvider;
    }

    public static OnlineStateChangeObservable getOnlineStateChangeObservable() {
        if (onlineStateChangeObservable == null) {
            onlineStateChangeObservable = new OnlineStateChangeObservable(context);
        }
        return onlineStateChangeObservable;
    }

    public static boolean enableOnlineState() {
        return onlineStateContentProvider != null;
    }


    public static void setEarPhoneModeEnable(boolean enable) {
        MessageAudioControl.getInstance(context).setEarPhoneModeEnable(enable);
        UserPreferences.setEarPhoneModeEnable(enable);
    }

    public static boolean getEarPhoneModeEnable() {
        return UserPreferences.isEarPhoneModeEnable();
    }

    /*
    * ****************************** basic ******************************
    */
    public static Context getContext() {
        return context;
    }

    public static String getAccount() {
        return account;
    }
}
