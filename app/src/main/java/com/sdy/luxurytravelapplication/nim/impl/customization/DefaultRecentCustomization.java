package com.sdy.luxurytravelapplication.nim.impl.customization;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.sdy.luxurytravelapplication.nim.api.model.recent.RecentCustomization;
import com.sdy.luxurytravelapplication.nim.attachment.SendGiftAttachment;
import com.sdy.luxurytravelapplication.nim.attachment.SendWechatAttachment;
import com.sdy.luxurytravelapplication.nim.attachment.ShareSquareAttachment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangjun on 2017/9/29.
 */

public class DefaultRecentCustomization extends RecentCustomization {

    /**
     * 最近联系人列表项文案定制
     *
     * @param recent 最近联系人
     * @return 默认文案
     */
    public String getDefaultDigest(RecentContact recent) {
        switch (recent.getMsgType()) {
            case text:
                return recent.getContent();
            case image:
                return "[图片]";
            case video:
                return "[视频]";
            case audio:
                return "[语音消息]";
            case location:
                return "[位置]";
            case file:
                return "[文件]";
            case tip:
                List<String> uuids = new ArrayList<>();
                uuids.add(recent.getRecentMessageId());
                List<IMMessage> messages = NIMClient.getService(MsgService.class).queryMessageListByUuidBlock(uuids);
                if (messages != null && messages.size() > 0) {
                    return messages.get(0).getContent();
                }
                return "[通知提醒]";
            case notification:
                return "[通知提醒]";
            case robot:
                return "[机器人消息]";
            case custom:
                if (recent.getAttachment() instanceof SendGiftAttachment) {
                    return "[礼物消息]";
                } else if (recent.getAttachment() instanceof ShareSquareAttachment) {
                    return "[动态分享消息]";
                } else if (recent.getAttachment() instanceof SendWechatAttachment) {
                    return "[联系方式消息]";
                } else {
                    return "";
                }
            default:
                return "[自定义消息] ";
        }
    }
}
