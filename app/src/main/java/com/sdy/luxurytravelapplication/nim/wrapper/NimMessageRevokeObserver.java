package com.sdy.luxurytravelapplication.nim.wrapper;

import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.model.RevokeMsgNotification;
import com.sdy.luxurytravelapplication.nim.business.helper.MessageHelper;

/**
 * 云信消息撤回观察者
 */

public class NimMessageRevokeObserver implements Observer<RevokeMsgNotification> {

    @Override
    public void onEvent(RevokeMsgNotification notification) {
        if (notification == null || notification.getMessage() == null) {
            return;
        }

        MessageHelper.getInstance().onRevokeMessage(notification.getMessage(), notification.getRevokeAccount());
    }
}
