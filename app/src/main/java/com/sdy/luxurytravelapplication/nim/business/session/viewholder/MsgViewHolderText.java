package com.sdy.luxurytravelapplication.nim.business.session.viewholder;


import android.graphics.Color;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.TextView;

import com.sdy.luxurytravelapplication.R;
import com.sdy.luxurytravelapplication.nim.api.NimUIKit;
import com.sdy.luxurytravelapplication.nim.business.emoji.MoonUtil;
import com.sdy.luxurytravelapplication.nim.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.sdy.luxurytravelapplication.nim.impl.NimUIKitImpl;


/**
 * Created by zhoujianghua on 2015/8/4.
 */
public class MsgViewHolderText extends MsgViewHolderBase {

    protected TextView bodyTextView;

    public MsgViewHolderText(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_text;
    }

    @Override
    public void inflateContentView() {
        bodyTextView = findViewById(R.id.nim_message_item_text_body);
    }

    @Override
    public void bindContentView() {
        layoutDirection();
        bodyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick();
            }
        });
        MoonUtil.identifyFaceExpression(NimUIKit.getContext(), bodyTextView, getDisplayText(), ImageSpan.ALIGN_BOTTOM);
        bodyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        bodyTextView.setOnLongClickListener(longClickListener);
    }

    private void layoutDirection() {
        if (isReceivedMessage()) {
            bodyTextView.setBackgroundResource(NimUIKitImpl.getOptions().messageLeftBackground);
            bodyTextView.setTextColor(Color.parseColor("#333333"));
        } else {
            bodyTextView.setBackgroundResource(NimUIKitImpl.getOptions().messageRightBackground);
            bodyTextView.setTextColor(Color.WHITE);
        }
    }

    @Override
    protected int leftBackground() {
        return R.drawable.shape_nim_left_bg;
    }

    @Override
    protected int rightBackground() {
        return R.drawable.shape_nim_right_bg;
    }

    protected String getDisplayText() {
        return message.getContent();
    }
}
