package com.sdy.luxurytravelapplication.nim.business.helper;

import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomMessageConfig;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.sdy.luxurytravelapplication.nim.api.MessageRevokeTip;
import com.sdy.luxurytravelapplication.nim.api.NimUIKit;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by hzxuwen on 2016/8/19.
 */
public class MessageHelper {
    private static final String TAG = "MessageHelper";

    public static MessageHelper getInstance() {
        return InstanceHolder.instance;
    }

    static class InstanceHolder {
        final static MessageHelper instance = new MessageHelper();
    }

    // 消息撤回
    public void onRevokeMessage(IMMessage item, String revokeAccount) {
        if (item == null) {
            return;
        }
        IMMessage message = MessageBuilder.createTipMessage(item.getSessionId(), item.getSessionType());
        message.setContent(MessageRevokeTip.getRevokeTipContent(item, revokeAccount));
        message.setStatus(MsgStatusEnum.success);
        CustomMessageConfig config = new CustomMessageConfig();
        config.enableUnreadCount = false;
        message.setConfig(config);
        NIMClient.getService(MsgService.class).saveMessageToLocalEx(message, true, item.getTime());
    }

    /**
     * 从 mItems 按顺序取出被勾选的消息
     *
     * @return 被勾选的消息
     */
    public LinkedList<IMMessage> getCheckedItems(List<IMMessage> items) {
        LinkedList<IMMessage> checkedList = new LinkedList<>();
        for (IMMessage msg : items) {
            if (msg.isChecked()) {
                checkedList.add(msg);
            }
        }
        return checkedList;
    }

    /**
     * 通过id和type，从本地存储中查询对应的群名或用户名
     *
     * @param id          群或用户的id
     * @param sessionType 会话类型
     * @return id对应的昵称
     */
    public String getStoredNameFromSessionId(final String id, final SessionTypeEnum sessionType) {
        switch (sessionType) {
            case P2P:
                //读取对方用户名称
                NimUserInfo userInfo = NIMClient.getService(UserService.class).getUserInfo(id);
                if (userInfo == null) {
                    return null;
                }
                return userInfo.getName();
            default:
                return null;
        }
    }



    /**
     * 判断消息是否被加入合并转发
     *
     * @param message 待测消息
     * @return true: 可以; false: 不能
     */
    public boolean isAvailableInMultiRetweet(IMMessage message) {
        if (message == null) {
            return false;
        }
        MsgTypeEnum msgType = message.getMsgType();
        //过滤掉不能单条转发的消息、null、未知类型消息、音视频通话、通知消息和提醒类消息
        return  msgType != null && !MsgTypeEnum.undef.equals(msgType) && !MsgTypeEnum.avchat.equals(msgType) && !MsgTypeEnum.notification.equals(msgType) && !MsgTypeEnum.tip.equals(msgType);
    }


    /**
     * 根据ids字段设置P2P AVChat消息的发送方向和发送者
     *
     * @param message 点对点AVChat消息
     */
    public static void adjustAVChatMsgDirect(IMMessage message) {
        if (message == null || message.getMsgType() != MsgTypeEnum.avchat || message.getAttachment() == null) {
            return;
        }
        String attachmentStr = message.getAttachment().toJson(false);
        try {
            JSONObject attachmentJson = new JSONObject(attachmentStr);
            JSONObject dataJson = attachmentJson.getJSONObject("data");
            String fromAccount = dataJson.optString("from");
            if (TextUtils.isEmpty(fromAccount)) {
                JSONArray arr = dataJson.optJSONArray("ids");
                fromAccount = (String) arr.get(0);
            }
            message.setDirect(fromAccount.equals(NimUIKit.getAccount()) ? MsgDirectionEnum.Out : MsgDirectionEnum.In);
            message.setFromAccount(fromAccount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getJsonStringFromMap(final Map<String, Object> map) {
        String result = null;
        if (map != null && !map.isEmpty()) {
            try {
                JSONObject json = new JSONObject(map);
                result = json.toString();
            } catch (Exception e) {
                LogUtils.e(TAG, "getJsonStringFromMap exception =" + e.getMessage());
            }
        }

        return result;
    }

    public static Map<String, Object> getMapFromJsonString(final String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }

        try {
            JSONObject json = new JSONObject(jsonStr);
            return recursiveParseJsonObject(json);
        } catch (org.json.JSONException e) {
            LogUtils.e(TAG, "getMapFromJsonString exception =" + e.getMessage());
        }

        return null;
    }

    private static Map<String, Object> recursiveParseJsonObject(JSONObject json) throws org.json.JSONException {
        if (json == null) {
            return null;
        }

        Map<String, Object> map = new HashMap<>(json.length());
        String key;
        Object value;
        Iterator<String> i = json.keys();
        while (i.hasNext()) {
            key = i.next();
            value = json.get(key);
            if (value instanceof JSONArray) {
                map.put(key, recursiveParseJsonArray((JSONArray) value));
            } else if (value instanceof JSONObject) {
                map.put(key, recursiveParseJsonObject((JSONObject) value));
            } else {
                map.put(key, value);
            }
        }

        return map;
    }

    private static List recursiveParseJsonArray(JSONArray array) throws org.json.JSONException {
        if (array == null) {
            return null;
        }

        List list = new ArrayList(array.length());
        Object value;
        for (int m = 0; m < array.length(); m++) {
            value = array.get(m);
            if (value instanceof JSONArray) {
                list.add(recursiveParseJsonArray((JSONArray) value));
            } else if (value instanceof JSONObject) {
                list.add(recursiveParseJsonObject((JSONObject) value));
            } else {
                list.add(value);
            }
        }

        return list;
    }
}
