package com.sdy.luxurytravelapplication.nim.business.session.viewholder;

import com.netease.nimlib.sdk.msg.attachment.VideoAttachment;
import com.sdy.luxurytravelapplication.R;
import com.sdy.luxurytravelapplication.nim.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.sdy.luxurytravelapplication.nim.common.util.media.BitmapDecoder;
import com.sdy.luxurytravelapplication.nim.business.session.activity.WatchVideoActivity;

/**
 * Created by zhoujianghua on 2015/8/5.
 */
public class MsgViewHolderVideo extends MsgViewHolderThumbBase {

    public MsgViewHolderVideo(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int maskBg() {
        return R.drawable.nim_message_item_round_bg;
    }

    @Override
    public int getContentResId() {
        return R.layout.nim_message_item_video;
    }

    @Override
    public void onItemClick() {
        WatchVideoActivity.Companion.start(context, message);
    }

    @Override
    public String thumbFromSourceFile(String path) {
        VideoAttachment attachment = (VideoAttachment) message.getAttachment();
        String thumb = attachment.getThumbPathForSave();
        return BitmapDecoder.extractThumbnail(path, thumb) ? thumb : null;
    }
}
