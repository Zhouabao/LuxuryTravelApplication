package com.sdy.luxurytravelapplication.nim.api;

import android.app.Activity;
import android.content.Context;

import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.sdy.luxurytravelapplication.nim.api.model.contact.ContactChangedObservable;
import com.sdy.luxurytravelapplication.nim.api.model.contact.ContactEventListener;
import com.sdy.luxurytravelapplication.nim.api.model.contact.ContactProvider;
import com.sdy.luxurytravelapplication.nim.api.model.location.LocationProvider;
import com.sdy.luxurytravelapplication.nim.api.model.main.CustomPushContentProvider;
import com.sdy.luxurytravelapplication.nim.api.model.main.OnlineStateChangeObservable;
import com.sdy.luxurytravelapplication.nim.api.model.main.OnlineStateContentProvider;
import com.sdy.luxurytravelapplication.nim.api.model.recent.RecentCustomization;
import com.sdy.luxurytravelapplication.nim.api.model.session.SessionCustomization;
import com.sdy.luxurytravelapplication.nim.api.model.session.SessionEventListener;
import com.sdy.luxurytravelapplication.nim.api.model.user.IUserInfoProvider;
import com.sdy.luxurytravelapplication.nim.api.model.user.UserInfoObservable;
import com.sdy.luxurytravelapplication.nim.business.module.MsgRevokeFilter;
import com.sdy.luxurytravelapplication.nim.impl.NimUIKitImpl;
import com.sdy.luxurytravelapplication.nim.impl.customization.DefaultP2PSessionCustomization;
import com.sdy.luxurytravelapplication.nim.impl.provider.DefaultContactProvider;
import com.sdy.luxurytravelapplication.nim.impl.provider.DefaultUserInfoProvider;
import com.sdy.luxurytravelapplication.nim.support.glide.ImageLoaderKit;
import com.sdy.luxurytravelapplication.nim.business.session.viewholder.MsgViewHolderBase;

import java.util.Set;

/**
 * 云信UI组件接口/定制化入口
 * Created by huangjun on 2017/9/29.
 */

public class NimUIKit {

    /**
     * 初始化UIKit, 用户信息、联系人信息使用 {@link DefaultUserInfoProvider}，{@link DefaultContactProvider}
     * 若用户自行提供 userInfoProvider，contactProvider，请使用 {@link NimUIKitImpl#init(Context, IUserInfoProvider, ContactProvider)}
     *
     * @param context 上下文
     */
    public static void init(Context context) {
        NimUIKitImpl.init(context);
    }

    /**
     * 初始化UIKit, 用户信息、联系人信息使用 {@link DefaultUserInfoProvider}，{@link DefaultContactProvider}
     * 若用户自行提供 userInfoProvider，contactProvider，请使用 {@link NimUIKitImpl#init(Context, IUserInfoProvider, ContactProvider)}
     *
     * @param context 上下文
     * @param option  自定义选项
     */
    public static void init(Context context, UIKitOptions option) {
        NimUIKitImpl.init(context, option);
    }

    /**
     * 初始化UIKit，须传入context以及用户信息提供者
     *
     * @param context          上下文
     * @param userInfoProvider 用户信息提供者
     * @param contactProvider  通讯录信息提供者
     */
    public static void init(Context context, IUserInfoProvider userInfoProvider, ContactProvider contactProvider) {
        NimUIKitImpl.init(context, userInfoProvider, contactProvider);
    }

    /**
     * 初始化UIKit，须传入context以及用户信息提供者
     *
     * @param context          上下文
     * @param option           自定义选项
     * @param userInfoProvider 用户信息提供者
     * @param contactProvider  通讯录信息提供者
     */
    public static void init(Context context, UIKitOptions option, IUserInfoProvider userInfoProvider, ContactProvider contactProvider) {
        NimUIKitImpl.init(context, option, userInfoProvider, contactProvider);
    }

    /**
     * 获取配置项
     *
     * @return UIKitOptions
     */
    public static UIKitOptions getOptions() {
        return NimUIKitImpl.getOptions();
    }

    /**
     * 设置位置信息提供者
     *
     * @param locationProvider 位置信息提供者
     */
    public static void setLocationProvider(LocationProvider locationProvider) {
        NimUIKitImpl.setLocationProvider(locationProvider);
    }

    /**
     * 设置聊天界面的事件监听器
     *
     * @param sessionListener 事件监听器
     */
    public static void setSessionListener(SessionEventListener sessionListener) {
        NimUIKitImpl.setSessionListener(sessionListener);
    }


    /**
     * 设置消息撤回的监听器
     *
     * @param msgRevokeFilter 监听器
     */
    public static void setMsgRevokeFilter(MsgRevokeFilter msgRevokeFilter) {
        NimUIKitImpl.setMsgRevokeFilter(msgRevokeFilter);
    }

    /**
     * 注册自定义推送文案
     *
     * @param mixPushCustomConfig 文案内容提供者
     */
    public static void setCustomPushContentProvider(CustomPushContentProvider mixPushCustomConfig) {
        NimUIKitImpl.setCustomPushContentProvider(mixPushCustomConfig);
    }

    /**
     * 设置通讯录列表的事件监听器
     *
     * @param contactEventListener 事件监听器
     */
    public static void setContactEventListener(ContactEventListener contactEventListener) {
        NimUIKitImpl.setContactEventListener(contactEventListener);
    }

    /**
     * 设置单聊界面定制 SessionCustomization
     *
     * @param commonP2PSessionCustomization 聊天界面定制化
     */
    public static void setCommonP2PSessionCustomization(SessionCustomization commonP2PSessionCustomization) {
        NimUIKitImpl.setCommonP2PSessionCustomization(commonP2PSessionCustomization);
    }

    /**
     * 获取单聊界面定制 SessionCustomization
     *
     * @return 聊天界面定制化
     */
    public static SessionCustomization getCommonP2PSessionCustomization() {
        return NimUIKitImpl.getCommonP2PSessionCustomization();
    }


    /**
     * 设置最近联系人列表界面定制 RecentCustomization
     *
     * @param recentCustomization 最近联系人界面定制
     */
    public static void setRecentCustomization(RecentCustomization recentCustomization) {
        NimUIKitImpl.setRecentCustomization(recentCustomization);
    }

    /**
     * 根据IM消息附件类型注册对应的消息项展示ViewHolder
     *
     * @param attach     附件类型
     * @param viewHolder 消息ViewHolder
     */
    public static void registerMsgItemViewHolder(Class<? extends MsgAttachment> attach, Class<? extends MsgViewHolderBase> viewHolder) {
        NimUIKitImpl.registerMsgItemViewHolder(attach, viewHolder);
    }



    /**
     * 注册Tip类型消息项展示ViewHolder
     *
     * @param viewHolder Tip消息ViewHolder
     */
    public static void registerTipMsgViewHolder(Class<? extends MsgViewHolderBase> viewHolder) {
        NimUIKitImpl.registerTipMsgViewHolder(viewHolder);
    }

    /**
     * 手动登陆，由于手动登陆完成之后，UIKit 需要设置账号、构建缓存等，使用此方法登陆 UIKit 会将这部分逻辑处理好，开发者只需要处理自己的逻辑即可
     *
     * @param loginInfo 登陆账号信息
     * @param callback  登陆结果回调
     */
    public static AbortableFuture<LoginInfo> login(LoginInfo loginInfo, final RequestCallback<LoginInfo> callback) {
        return NimUIKitImpl.login(loginInfo, callback);
    }

    /**
     * 手动登陆成功
     *
     * @param account 登陆成功账号
     */
    public static void loginSuccess(String account) {
        NimUIKitImpl.loginSuccess(account);
    }

    /**
     * 释放缓存，一般在注销时调用
     */
    public static void logout() {
        NimUIKitImpl.logout();
    }


    /**
     * 获取上下文
     *
     * @return 必须初始化后才有值
     */
    public static Context getContext() {
        return NimUIKitImpl.getContext();
    }


    /**
     * 设置当前登录用户的帐号
     *
     * @param account 帐号
     */
    public static void setAccount(String account) {
        NimUIKitImpl.setAccount(account);
    }

    /**
     * 获取当前登录的账号
     *
     * @return 必须登录成功后才有值
     */
    public static String getAccount() {
        return NimUIKitImpl.getAccount();
    }

    /**
     * 打开单聊界面，若开发者未设置 {@link NimUIKitImpl#setCommonP2PSessionCustomization(SessionCustomization)},
     * 则定制化信息 SessionCustomization 为{@link DefaultP2PSessionCustomization}
     * <p>
     * 若需要为目标会话提供单独定义的SessionCustomization，请使用{@link NimUIKitImpl#startChatting(Context, String, SessionTypeEnum, SessionCustomization, IMMessage)}
     *
     * @param context 上下文
     * @param account 目标账号
     */
    public static void startP2PSession(Context context, String account) {
        NimUIKitImpl.startP2PSession(context, account);
    }

    /**
     * 同 {@link NimUIKitImpl#startP2PSession(Context, String)},同时聊天界面打开后，列表跳转至anchor位置
     *
     * @param context 上下文
     * @param account 目标账号
     * @param anchor  跳转到指定消息的位置，不需要跳转填null
     */
    public static void startP2PSession(Context context, String account, IMMessage anchor) {
        NimUIKitImpl.startP2PSession(context, account, anchor);
    }

    /**
     * 打开一个聊天窗口，开始聊天
     *
     * @param context       上下文
     * @param id            聊天对象ID（用户帐号account或者群组ID）
     * @param sessionType   会话类型
     * @param customization 定制化信息。针对不同的聊天对象，可提供不同的定制化。
     * @param anchor        跳转到指定消息的位置，不需要跳转填null
     */
    public static void startChatting(Context context, String id, SessionTypeEnum sessionType, SessionCustomization
            customization, IMMessage anchor) {
        NimUIKitImpl.startChatting(context, id, sessionType, customization, anchor);
    }

    /**
     * 打开一个聊天窗口（用于从聊天信息中创建群聊时，打开群聊）
     *
     * @param context       上下文
     * @param id            聊天对象ID（用户帐号account或者群组ID）
     * @param sessionType   会话类型
     * @param customization 定制化信息。针对不同的聊天对象，可提供不同的定制化。
     * @param backToClass   返回的指定页面
     * @param anchor        跳转到指定消息的位置，不需要跳转填null
     */
    public static void startChatting(Context context, String id, SessionTypeEnum sessionType, SessionCustomization customization,
                                     Class<? extends Activity> backToClass, IMMessage anchor) {
        NimUIKitImpl.startChatting(context, id, sessionType, customization,  anchor);
    }




    public static boolean isInitComplete() {
        return NimUIKitImpl.isInitComplete();
    }

    /**
     * 获取 “用户资料” 提供者
     *
     * @return 必须在初始化后获取
     */
    public static IUserInfoProvider getUserInfoProvider() {
        return NimUIKitImpl.getUserInfoProvider();
    }

    /**
     * 获取 “用户资料” 变更监听管理者
     * UIKit 与 app 之间 userInfo 数据更新通知接口
     *
     * @return UserInfoObservable
     */
    public static UserInfoObservable getUserInfoObservable() {
        return NimUIKitImpl.getUserInfoObservable();
    }

    /**
     * 获取 “用户关系” 提供者
     *
     * @return 必须在初始化后获取
     */
    public static ContactProvider getContactProvider() {
        return NimUIKitImpl.getContactProvider();
    }

    /**
     * 获取 “用户关系” 变更监听管理者
     * UIKit 与 app 之间 “用户关系” 数据更新通知接口
     *
     * @return ContactChangedObservable
     */
    public static ContactChangedObservable getContactChangedObservable() {
        return NimUIKitImpl.getContactChangedObservable();
    }



    /**
     * 获取图片缓存
     *
     * @return Glide图片缓存
     */
    public static ImageLoaderKit getImageLoaderKit() {
        return NimUIKitImpl.getImageLoaderKit();
    }

    /**
     * 设置在线状态文案提供者
     *
     * @param onlineStateContentProvider 文案内容提供者
     */
    public static void setOnlineStateContentProvider(OnlineStateContentProvider onlineStateContentProvider) {
        NimUIKitImpl.setOnlineStateContentProvider(onlineStateContentProvider);
    }

    /**
     * 获取配置的用户在线状态文案提供者
     *
     * @return 文案提供者
     */
    public static OnlineStateContentProvider getOnlineStateContentProvider() {
        return NimUIKitImpl.getOnlineStateContentProvider();
    }

    /**
     * 设置了 onlineStateContentProvider 则表示UIKit需要展示在线状态
     *
     * @return 在线状态开关
     */
    public static boolean enableOnlineState() {
        return NimUIKitImpl.enableOnlineState();
    }

    /**
     * 通知在线状态发生变化
     *
     * @param accounts 状态变化的账号
     */
    @Deprecated
    public static void notifyOnlineStateChange(Set<String> accounts) {
        getOnlineStateChangeObservable().notifyOnlineStateChange(accounts);
    }

    /**
     * 获取在线状态变更通知接口
     * @return
     */
    public static OnlineStateChangeObservable getOnlineStateChangeObservable() {
        return NimUIKitImpl.getOnlineStateChangeObservable();
    }

    /**
     * 设置是否听筒模式
     *
     * @param enable
     */
    public static void setEarPhoneModeEnable(boolean enable) {
        NimUIKitImpl.setEarPhoneModeEnable(enable);
    }

    /**
     * 获取是否听筒模式
     */
    public static boolean isEarPhoneModeEnable() {
        return NimUIKitImpl.getEarPhoneModeEnable();
    }
}
