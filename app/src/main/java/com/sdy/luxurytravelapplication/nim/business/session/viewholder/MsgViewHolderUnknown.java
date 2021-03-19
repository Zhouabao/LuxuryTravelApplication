package com.sdy.luxurytravelapplication.nim.business.session.viewholder;

import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.sdy.luxurytravelapplication.R;
import com.sdy.luxurytravelapplication.nim.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;

/**
 * Created by zhoujianghua on 2015/8/6.
 */
public class MsgViewHolderUnknown extends MsgViewHolderBase {

    public MsgViewHolderUnknown(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_unknown;
    }

    @Override
    protected boolean isShowHeadImage() {
        if (message.getSessionType() == SessionTypeEnum.ChatRoom) {
            return false;
        }
        return true;
    }

    @Override
    public void inflateContentView() {
    }

    @Override
    public void bindContentView() {
    }
}
