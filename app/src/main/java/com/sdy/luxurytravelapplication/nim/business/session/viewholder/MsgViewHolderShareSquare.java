package com.sdy.luxurytravelapplication.nim.business.session.viewholder;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.SizeUtils;
import com.sdy.luxurytravelapplication.R;
import com.sdy.luxurytravelapplication.glide.GlideUtil;
import com.sdy.luxurytravelapplication.mvp.model.bean.SquareBean;
import com.sdy.luxurytravelapplication.nim.attachment.ShareSquareAttachment;
import com.sdy.luxurytravelapplication.nim.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;

/**
 * 动态分享
 */
public class MsgViewHolderShareSquare extends MsgViewHolderBase {
    private TextView shareDesc; //分享描述文本
    private TextView shareContent; //分享文字内容
    private TextView shareTypeText; //分享文字内容
    private ImageView shareImg;//分享的图片
    private ImageView shareTypeMedia;//分享的类型
    private ShareSquareAttachment attachment;

    public MsgViewHolderShareSquare(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_message_share_square;
    }

    @Override
    public void inflateContentView() {
        //初始化数据
        shareDesc = findViewById(R.id.shareDesc);
        shareContent = findViewById(R.id.shareContent);
        shareImg = findViewById(R.id.shareImg);
        shareTypeMedia = findViewById(R.id.shareTypeMedia);
        shareTypeText = findViewById(R.id.shareTypeText);
    }

    @Override
    public void bindContentView() {
        attachment = (ShareSquareAttachment) message.getAttachment();
        if (attachment.getContent() != null && !attachment.getContent().isEmpty()) {
            shareDesc.setText(attachment.getContent());
        } else {
            shareDesc.setText(R.string.share_a_square_for_u);
        }
        shareContent.setText(attachment.getDesc());
        GlideUtil.INSTANCE.loadRoundImgCenterCrop(context, attachment.getImg(), shareImg, SizeUtils.dp2px(5F));
        if (attachment.getShareType() == SquareBean.VIDEO) {
            shareTypeMedia.setVisibility(View.VISIBLE);
            shareTypeText.setVisibility(View.GONE);
            shareTypeMedia.setImageResource(R.drawable.icon_play_transparent);
            GlideUtil.INSTANCE.loadRoundImgCenterCrop(context, attachment.getImg(), shareImg, SizeUtils.dp2px(10F));
        } else if (attachment.getShareType() == SquareBean.AUDIO) {
            shareTypeMedia.setVisibility(View.VISIBLE);
            shareTypeText.setVisibility(View.GONE);
            shareImg.setBackgroundResource(R.drawable.gradient_purple_10dp);
            shareTypeMedia.setImageResource(R.drawable.icon_voice_white);
        } else if (attachment.getShareType() == SquareBean.PIC) {
            shareTypeMedia.setVisibility(View.GONE);
            shareTypeText.setVisibility(View.GONE);
            GlideUtil.INSTANCE.loadRoundImgCenterCrop(context, attachment.getImg(), shareImg, SizeUtils.dp2px(10F));
        } else {
            shareTypeMedia.setVisibility(View.GONE);
            shareTypeText.setVisibility(View.VISIBLE);
            if (!attachment.getDesc().isEmpty())
                shareTypeText.setText(attachment.getDesc().subSequence(0, 1));
            shareImg.setBackgroundResource(R.drawable.shape_rectangle_ffeaefff_10dp);
        }
    }

    @Override
    public void onItemClick() {
//        FindCommentActivity.Companion.startToFindComment(context,null,attachment.getSquareId());
//        FindDetailActivity.Companion.startActivity((Activity) context, null, null, null, FindRecommendFragment.TYPE_RECOMMEND, attachment.getSquareId());
    }

//
//    @Override
//    protected boolean shouldDisplayReceipt() {
//        return false;
//    }
//
//    @Override
//    protected boolean shouldDisplayNick() {
//        return false;
//    }
//
//
//    @Override
//    protected boolean isShowBubble() {
//        return false;
//    }

}