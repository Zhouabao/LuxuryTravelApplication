package com.sdy.luxurytravelapplication.nim.business.session;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.LocalAntiSpamResult;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.sdy.luxurytravelapplication.R;
import com.sdy.luxurytravelapplication.nim.api.NimUIKit;
import com.sdy.luxurytravelapplication.nim.api.model.recent.RecentCustomization;
import com.sdy.luxurytravelapplication.nim.api.model.session.SessionCustomization;
import com.sdy.luxurytravelapplication.nim.api.model.session.SessionEventListener;
import com.sdy.luxurytravelapplication.nim.api.wrapper.NimMessageRevokeObserver;
import com.sdy.luxurytravelapplication.nim.attachment.ChatDatingAttachment;
import com.sdy.luxurytravelapplication.nim.attachment.ChatUpAttachment;
import com.sdy.luxurytravelapplication.nim.attachment.ContactAttachment;
import com.sdy.luxurytravelapplication.nim.attachment.ContactCandyAttachment;
import com.sdy.luxurytravelapplication.nim.attachment.CustomAttachParser;
import com.sdy.luxurytravelapplication.nim.attachment.RedPacketAttachment;
import com.sdy.luxurytravelapplication.nim.attachment.SendCustomTipAttachment;
import com.sdy.luxurytravelapplication.nim.attachment.SendGiftAttachment;
import com.sdy.luxurytravelapplication.nim.attachment.ShareSquareAttachment;
import com.sdy.luxurytravelapplication.nim.attachment.StickerAttachment;
import com.sdy.luxurytravelapplication.nim.business.module.MsgRevokeFilter;
import com.sdy.luxurytravelapplication.nim.business.session.actions.BaseAction;
import com.sdy.luxurytravelapplication.nim.business.session.viewholder.MsgViewHolderChatDating;
import com.sdy.luxurytravelapplication.nim.business.session.viewholder.MsgViewHolderChatUp;
import com.sdy.luxurytravelapplication.nim.business.session.viewholder.MsgViewHolderContact;
import com.sdy.luxurytravelapplication.nim.business.session.viewholder.MsgViewHolderContactCandy;
import com.sdy.luxurytravelapplication.nim.business.session.viewholder.MsgViewHolderSendCustomTip;
import com.sdy.luxurytravelapplication.nim.business.session.viewholder.MsgViewHolderSendGift;
import com.sdy.luxurytravelapplication.nim.business.session.viewholder.MsgViewHolderShareSquare;
import com.sdy.luxurytravelapplication.nim.business.session.viewholder.MsgViewHolderTip;
import com.sdy.luxurytravelapplication.nim.impl.cache.DemoCache;
import com.sdy.luxurytravelapplication.nim.impl.customization.DefaultRecentCustomization;

import java.util.ArrayList;

/**
 * UIKit????????????????????????????????????
 */
public class SessionHelper {

    private static final int ACTION_HISTORY_QUERY_PERSIST_CLEAR = 0;

    private static final int ACTION_HISTORY_QUERY_NOT_PERSIST_CLEAR = 1;

    private static final int ACTION_SEARCH_MESSAGE = 2;

    private static final int ACTION_CLEAR_MESSAGE_RECORD = 3;

    private static final int ACTION_CLEAR_MESSAGE_NOT_RECORD = 4;

    private static final int ACTION_CLEAR_P2P_MESSAGE = 5;

    private static SessionCustomization p2pCustomization;

    private static SessionCustomization normalTeamCustomization;

    private static SessionCustomization advancedTeamCustomization;

    private static SessionCustomization myP2pCustomization;

    private static SessionCustomization robotCustomization;

    private static RecentCustomization recentCustomization;

    public static final boolean USE_LOCAL_ANTISPAM = true;


    public static void init() {
        // ????????????????????????????????????
        NIMClient.getService(MsgService.class).registerCustomAttachmentParser(new CustomAttachParser());
        // ???????????????????????????????????????ViewHolder
        registerViewHolders();
        // ???????????????????????????????????????
        setSessionListener();
        // ???????????????????????????
        registerMsgRevokeFilter();
        // ???????????????????????????
        registerMsgRevokeObserver();
        NimUIKit.setCommonP2PSessionCustomization(getP2pCustomization());
        NimUIKit.setRecentCustomization(getRecentCustomization());
    }

    public static void startP2PSession(Context context, String account) {
        startP2PSession(context, account, null);
    }

    public static void startP2PSession(Context context, String account, IMMessage anchor) {
        if (!DemoCache.getAccount().equals(account)) {
            NimUIKit.startP2PSession(context, account, anchor);
        } else {
            NimUIKit.startChatting(context, account, SessionTypeEnum.P2P, getMyP2pCustomization(), anchor);
        }
    }


    // ?????????????????????????????????????????????????????????null??????
    private static SessionCustomization getP2pCustomization() {
        if (p2pCustomization == null) {
            p2pCustomization = new SessionCustomization() {

                // ????????????Activity Result??? ????????????????????????
                @Override
                public void onActivityResult(final Activity activity, int requestCode, int resultCode, Intent data) {
                    super.onActivityResult(activity, requestCode, resultCode, data);

                }

                @Override
                public boolean isAllowSendMessage(IMMessage message) {
                    return checkLocalAntiSpam(message);
                }

                @Override
                public MsgAttachment createStickerAttachment(String category, String item) {
                    return new StickerAttachment(category, item);
                }

                @Override
                public String getMessageDigest(IMMessage message) {
                    return getMsgDigest(message);
                }
            };
            // ??????
            //            p2pCustomization.backgroundColor = Color.BLUE;
            //            p2pCustomization.backgroundUri = "file:///android_asset/xx/bk.jpg";
            //            p2pCustomization.backgroundUri = "file:///sdcard/Pictures/bk.png";
            //            p2pCustomization.backgroundUri = "android.resource://com.netease.nim.demo/drawable/bk"
            // ????????????????????????????????????????????? ??????????????????????????????????????????
            ArrayList<BaseAction> actions = new ArrayList<>();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                actions.add(new AVChatAction(AVChatType.AUDIO));
//                actions.add(new AVChatAction(AVChatType.VIDEO));
            }

            p2pCustomization.actions = actions;
            p2pCustomization.withSticker = true;
            // ??????ActionBar?????????????????????????????????
            ArrayList<SessionCustomization.OptionsButton> buttons = new ArrayList<>();
            SessionCustomization.OptionsButton cloudMsgButton = new SessionCustomization.OptionsButton() {

                @Override
                public void onClick(Context context, View view, String sessionId) {
                }
            };
            cloudMsgButton.iconId = R.drawable.icon_more_black;
            SessionCustomization.OptionsButton infoButton = new SessionCustomization.OptionsButton() {

                @Override
                public void onClick(Context context, View view, String sessionId) {
//                    MessageInfoActivity.startActivity(context, sessionId); //??????????????????
                }
            };
//            infoButton.iconId = R.drawable.nim_ic_message_actionbar_p2p_add;
            buttons.add(cloudMsgButton);
            buttons.add(infoButton);
            p2pCustomization.buttons = buttons;
        }
        return p2pCustomization;
    }

    private static SessionCustomization getMyP2pCustomization() {
        if (myP2pCustomization == null) {
            myP2pCustomization = new SessionCustomization() {

                // ????????????Activity Result??? ????????????????????????
                @Override
                public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
                }

                @Override
                public boolean isAllowSendMessage(IMMessage message) {
                    return checkLocalAntiSpam(message);
                }

                @Override
                public MsgAttachment createStickerAttachment(String category, String item) {
                    return new StickerAttachment(category, item);
                }

                @Override
                public String getMessageDigest(IMMessage message) {
                    return getMsgDigest(message);
                }
            };
            // ????????????????????????????????????????????? ??????????????????????????????????????????
            ArrayList<BaseAction> actions = new ArrayList<>();
            myP2pCustomization.actions = actions;
            myP2pCustomization.withSticker = true;
            // ??????ActionBar?????????????????????????????????
            ArrayList<SessionCustomization.OptionsButton> buttons = new ArrayList<>();
            SessionCustomization.OptionsButton cloudMsgButton = new SessionCustomization.OptionsButton() {

                @Override
                public void onClick(Context context, View view, String sessionId) {
                }
            };
//            cloudMsgButton.iconId = R.drawable.nim_ic_messge_history;
            buttons.add(cloudMsgButton);
            myP2pCustomization.buttons = buttons;
        }
        return myP2pCustomization;
    }

    private static boolean checkLocalAntiSpam(IMMessage message) {
        if (!USE_LOCAL_ANTISPAM) {
            return true;
        }
        LocalAntiSpamResult result = NIMClient.getService(MsgService.class).checkLocalAntiSpam(message.getContent(),
                "**");
        int operator = result == null ? 0 : result.getOperator();
        switch (operator) {
            case 1: // ?????????????????????
                message.setContent(result.getContent());
                return true;
            case 2: // ????????????????????????
                return false;
            case 3: // ??????????????????????????????
                message.setClientAntiSpam(true);
                return true;
            case 0:
            default:
                break;
        }
        return true;
    }

    /**
     * ?????????????????????
     *
     * @param msg ??????
     * @return ??????
     */
    private static String getMsgDigest(IMMessage msg) {
//        switch (msg.getMsgType()) {
//            case avchat:
//                MsgAttachment attachment = msg.getAttachment();
//                AVChatAttachment avchat = (AVChatAttachment) attachment;
//                if (avchat.getState() == AVChatRecordState.Missed && !msg.getFromAccount().equals(
//                        NimUIKit.getAccount())) {
//                    // ??????????????????
//                    StringBuilder sb = new StringBuilder("[??????");
//                    if (avchat.getType() == AVChatType.VIDEO) {
//                        sb.append("????????????]");
//                    } else {
//                        sb.append("????????????]");
//                    }
//                    return sb.toString();
//                } else if (avchat.getState() == AVChatRecordState.Success) {
//                    StringBuilder sb = new StringBuilder();
//                    if (avchat.getType() == AVChatType.VIDEO) {
//                        sb.append("[????????????]: ");
//                    } else {
//                        sb.append("[????????????]: ");
//                    }
//                    sb.append(TimeUtil.secToTime(avchat.getDuration()));
//                    return sb.toString();
//                } else {
//                    if (avchat.getType() == AVChatType.VIDEO) {
//                        return ("[????????????]");
//                    } else {
//                        return ("[????????????]");
//                    }
//                }
//            case text:
//            case tip:
//                return msg.getContent();
//            case image:
//                return "[??????]";
//            case video:
//                return "[??????]";
//            case audio:
//                return "[????????????]";
//            case location:
//                return "[??????]";
//            case file:
//                return "[??????]";
//            case notification:
//                return TeamNotificationHelper.getTeamNotificationText(msg.getSessionId(),
//                        msg.getFromAccount(),
//                        (NotificationAttachment) msg.getAttachment());
//            case robot:
//                return "[???????????????]";
//            default:
//                return "[???????????????] ";
//        }
        return "";
    }

    private static RecentCustomization getRecentCustomization() {
        if (recentCustomization == null) {
            recentCustomization = new DefaultRecentCustomization() {

                @Override
                public String getDefaultDigest(RecentContact recent) {
                    /*if (recent.getAttachment() instanceof ChatHiAttachment) {
                        if (((ChatHiAttachment) recent.getAttachment())
                                .getShowType() == ChatHiAttachment.CHATHI_CHATUP_FRIEND) {
                            return DemoCache.getContext().getString(R.string.msg_unlock_chat);
                        }

                    } else*/ if (recent.getAttachment() instanceof ShareSquareAttachment) {
                        return DemoCache.getContext().getString(R.string.msg_share_square);

                    } else if (recent.getAttachment() instanceof SendGiftAttachment) {
                        return DemoCache.getContext().getString(R.string.msg_candy_gift);
                    }  else if (recent.getAttachment() instanceof SendCustomTipAttachment) {
                        return ((SendCustomTipAttachment) recent.getAttachment()).getContent();
                    } else if (recent.getAttachment() instanceof ContactCandyAttachment) {
                        return DemoCache.getContext().getString(R.string.msg_unlock_contact);
                    }
                    return super.getDefaultDigest(recent);
                }
            };
        }
        return recentCustomization;
    }


    private static void registerViewHolders() {
//        NimUIKit.registerMsgItemViewHolder(AccostGiftAttachment.class, MsgViewHolderAccostGift.class);
//        NimUIKit.registerMsgItemViewHolder(ChatHiAttachment.class, MsgViewHolderChatHi.class);
        NimUIKit.registerMsgItemViewHolder(ChatDatingAttachment.class, MsgViewHolderChatDating.class);
        NimUIKit.registerMsgItemViewHolder(ChatUpAttachment.class, MsgViewHolderChatUp.class);
        NimUIKit.registerMsgItemViewHolder(ContactCandyAttachment.class, MsgViewHolderContactCandy.class);
        NimUIKit.registerMsgItemViewHolder(SendGiftAttachment.class, MsgViewHolderSendGift.class);
        NimUIKit.registerMsgItemViewHolder(SendCustomTipAttachment.class, MsgViewHolderSendCustomTip.class);
        NimUIKit.registerMsgItemViewHolder(ShareSquareAttachment.class, MsgViewHolderShareSquare.class);
        NimUIKit.registerMsgItemViewHolder(ContactAttachment.class, MsgViewHolderContact.class);
        NimUIKit.registerTipMsgViewHolder(MsgViewHolderTip.class);

    }


    private static void setSessionListener() {
        SessionEventListener listener = new SessionEventListener() {

            @Override
            public void onAvatarClicked(Context context, IMMessage message) {
                // ????????????????????????????????????
//                if (message.getDirect() == MsgDirectionEnum.In && !message.getFromAccount().equals(Constants.ASSISTANT_ACCID))
//                    TargetUserActivity.Companion.startToTarget(context, message.getFromAccount());
            }

            @Override
            public void onAvatarLongClicked(Context context, IMMessage message) {
                // ??????????????????@????????????????????????????????????????????????????????????
            }

            @Override
            public void onAckMsgClicked(Context context, IMMessage message) {
                // ????????????????????????????????????????????????????????????????????????????????????????????????
//                AckMsgInfoActivity.start(context, message);
            }
        };
        NimUIKit.setSessionListener(listener);
    }

    /**
     * ?????????????????????
     */
    private static void registerMsgRevokeFilter() {
        NimUIKit.setMsgRevokeFilter(new MsgRevokeFilter() {

            @Override
            public boolean shouldIgnore(IMMessage message) {
                if (message.getAttachment() != null && (/*message.getAttachment() instanceof AVChatAttachment ||*/
                        message.getAttachment() instanceof RedPacketAttachment)) {
                    // ???????????????????????????????????????????????? ???????????????
                    return true;
                } else if (DemoCache.getAccount().equals(message.getSessionId())) {
                    // ?????????????????? ???????????????
                    return true;
                }
                return false;
            }
        });
    }

    private static void registerMsgRevokeObserver() {
        NIMClient.getService(MsgServiceObserve.class).observeRevokeMessage(new NimMessageRevokeObserver(), true);
    }


//            case ACTION_HISTORY_QUERY_PERSIST_CLEAR:
//                MessageHistoryActivity.start(item.getContext(), item.getSessionId(),item.getSessionTypeEnum(), true); // ????????????????????????????????????????????????
//                break;
//            case ACTION_HISTORY_QUERY_NOT_PERSIST_CLEAR:
//                MessageHistoryActivity.start(item.getContext(), item.getSessionId(),item.getSessionTypeEnum(), false); // ????????????????????????????????????????????????
//                break;
//            case ACTION_SEARCH_MESSAGE:
//                SearchMessageActivity.start(item.getContext(), item.getSessionId(), item.getSessionTypeEnum());
//                break;
//            case ACTION_CLEAR_MESSAGE_RECORD:
//                EasyAlertDialogHelper.createOkCancelDiolag(item.getContext(), null, "?????????????????????", true,
//                                                           new EasyAlertDialogHelper.OnDialogActionListener() {
//
//                                                               @Override
//                                                               public void doCancelAction() {
//                                                               }
//
//                                                               @Override
//                                                               public void doOkAction() {
//                                                                   NIMClient.getService(MsgService.class)
//                                                                            .clearChattingHistory(
//                                                                                    item.getSessionId(),
//                                                                                    item.getSessionTypeEnum(),
//                                                                                    false);
//                                                                   MessageListPanelHelper.getInstance()
//                                                                                         .notifyClearMessages(
//                                                                                                 item.getSessionId());
//                                                               }
//                                                           }).show();
//                break;
//            case ACTION_CLEAR_MESSAGE_NOT_RECORD:
//                EasyAlertDialogHelper.createOkCancelDiolag(item.getContext(), null, "?????????????????????", true,
//                                                            new EasyAlertDialogHelper.OnDialogActionListener() {
//
//                                                                @Override
//                                                                public void doCancelAction() {
//                                                                }
//
//                                                                @Override
//                                                                public void doOkAction() {
//                                                                    NIMClient.getService(MsgService.class)
//                                                                            .clearChattingHistory(
//                                                                                    item.getSessionId(),
//                                                                                    item.getSessionTypeEnum(),
//                                                                                    true);
//                                                                    MessageListPanelHelper.getInstance()
//                                                                            .notifyClearMessages(
//                                                                                    item.getSessionId());
//                                                                }
//                                                            }).show();
//                break;
//            case ACTION_CLEAR_P2P_MESSAGE:
//                String title = item.getContext().getString(R.string.message_p2p_clear_tips);
//                CustomAlertDialog alertDialog = new CustomAlertDialog(item.getContext());
//                alertDialog.setTitle(title);
//                alertDialog.addItem("??????", new CustomAlertDialog.onSeparateItemClickListener() {
//
//                    @Override
//                    public void onClick() {
//                        NIMClient.getService(MsgService.class).clearServerHistory(item.getSessionId(),
//                                                                                  item.getSessionTypeEnum());
//                        MessageListPanelHelper.getInstance().notifyClearMessages(item.getSessionId());
//                    }
//                });
//                String itemText = item.getContext().getString(R.string.sure_keep_roam);
//                alertDialog.addItem(itemText, new CustomAlertDialog.onSeparateItemClickListener() {
//
//                    @Override
//                    public void onClick() {
//                        NIMClient.getService(MsgService.class).clearServerHistory(item.getSessionId(),
//                                                                                  item.getSessionTypeEnum(), false);
//                        MessageListPanelHelper.getInstance().notifyClearMessages(item.getSessionId());
//                    }
//                });
//                alertDialog.addItem("??????", new CustomAlertDialog.onSeparateItemClickListener() {
//
//                    @Override
//                    public void onClick() {
//                    }
//                });
//                alertDialog.show();
//                break;
//        }


}
