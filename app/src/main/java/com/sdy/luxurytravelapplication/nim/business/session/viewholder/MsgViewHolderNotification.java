package com.sdy.luxurytravelapplication.nim.business.session.viewholder;


import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.sdy.luxurytravelapplication.R;
import com.sdy.luxurytravelapplication.nim.business.emoji.MoonUtil;
import com.sdy.luxurytravelapplication.nim.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;


public class MsgViewHolderNotification extends MsgViewHolderBase {

    public MsgViewHolderNotification(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    protected TextView notificationTextView;

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_notification;
    }

    @Override
    public void inflateContentView() {
        notificationTextView = (TextView) view.findViewById(R.id.message_item_notification_label);
    }

    @Override
    public void bindContentView() {
        handleTextNotification(getDisplayText());
    }

    protected String getDisplayText() {
        return "";
    }

    private void handleTextNotification(String text) {
        MoonUtil.identifyFaceExpressionAndATags(context, notificationTextView, text, ImageSpan.ALIGN_BOTTOM);
        notificationTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected boolean isMiddleItem() {
        return true;
    }
}

