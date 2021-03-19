package com.sdy.luxurytravelapplication.nim.business.session.viewholder;

import android.text.Html;
import android.widget.TextView;

import com.sdy.luxurytravelapplication.R;
import com.sdy.luxurytravelapplication.nim.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;


/**
 * Created by huangjun on 2015/11/25.
 * Tip类型消息ViewHolder
 */
public class MsgViewHolderTip extends MsgViewHolderBase {

    protected TextView notificationTextView;

    public MsgViewHolderTip(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

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
        notificationTextView.setText(message.getContent());
    }

    private void handleTextNotification(String text) {
        notificationTextView.setText(Html.fromHtml(text));
        // MoonUtil.identifyFaceExpressionAndATags(context, notificationTextView, text, ImageSpan.ALIGN_BOTTOM);
        // notificationTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected boolean shouldDisplayReceipt() {
        return false;
    }

    @Override
    protected boolean isMiddleItem() {
        return true;
    }

}
