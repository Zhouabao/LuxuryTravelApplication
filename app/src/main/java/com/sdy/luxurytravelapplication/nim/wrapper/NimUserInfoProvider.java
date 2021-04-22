package com.sdy.luxurytravelapplication.nim.wrapper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.model.UserInfo;
import com.sdy.luxurytravelapplication.R;
import com.sdy.luxurytravelapplication.nim.api.NimUIKit;

/**
 * 初始化sdk 需要的用户信息提供者，现主要用于内置通知提醒获取昵称和头像
 * <p>
 * 注意不要与 IUserInfoProvider 混淆，后者是 UIKit 与 demo 之间的数据共享接口
 * <p>
 */

public class NimUserInfoProvider implements UserInfoProvider {

    private Context context;

    public NimUserInfoProvider(Context context) {
        this.context = context;
    }

    @Override
    public UserInfo getUserInfo(String account) {
        return NimUIKit.getUserInfoProvider().getUserInfo(account);
    }

    @Override
    public Bitmap getAvatarForMessageNotifier(SessionTypeEnum sessionType, String sessionId) {
        /*
         * 注意：这里最好从缓存里拿，如果加载时间过长会导致通知栏延迟弹出！该函数在后台线程执行！
         */
        Bitmap bm = null;
        int defResId = R.drawable.icon_default_avator;

        if (SessionTypeEnum.P2P == sessionType) {
            UserInfo user = getUserInfo(sessionId);
            bm = (user != null) ? NimUIKit.getImageLoaderKit().getNotificationBitmapFromCache(user.getAvatar()) : null;
        }

        if (bm == null) {
            Drawable drawable = context.getResources().getDrawable(defResId);
            if (drawable instanceof BitmapDrawable) {
                bm = ((BitmapDrawable) drawable).getBitmap();
            }
        }

        return bm;
    }

    @Override
    public String getDisplayNameForMessageNotifier(String account, String sessionId, SessionTypeEnum sessionType) {
        String nick = null;
        if (sessionType == SessionTypeEnum.P2P) {
            nick = NimUIKit.getContactProvider().getAlias(account);
        }

        if (TextUtils.isEmpty(nick)) {
            return null; // 返回null，交给sdk处理。如果对方有设置nick，sdk会显示nick
        }

        return nick;
    }
}
