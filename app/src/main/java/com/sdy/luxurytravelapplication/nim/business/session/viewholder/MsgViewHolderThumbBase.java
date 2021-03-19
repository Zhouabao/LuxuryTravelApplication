package com.sdy.luxurytravelapplication.nim.business.session.viewholder;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.attachment.FileAttachment;
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment;
import com.netease.nimlib.sdk.msg.attachment.VideoAttachment;
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.sdy.luxurytravelapplication.R;
import com.sdy.luxurytravelapplication.nim.common.ui.imageview.MsgThumbImageView;
import com.sdy.luxurytravelapplication.nim.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.sdy.luxurytravelapplication.nim.common.util.media.BitmapDecoder;
import com.sdy.luxurytravelapplication.nim.common.util.media.ImageUtil;
import com.sdy.luxurytravelapplication.nim.common.util.string.StringUtil;
import com.sdy.luxurytravelapplication.nim.common.util.sys.ScreenUtil;

import java.io.File;

/**
 * Created by zhoujianghua on 2015/8/4.
 */
public abstract class MsgViewHolderThumbBase extends MsgViewHolderBase {

    public MsgViewHolderThumbBase(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    protected MsgThumbImageView thumbnail;
    protected View progressCover;
    protected TextView progressLabel;

    @Override
    public void inflateContentView() {
        thumbnail = findViewById(R.id.message_item_thumb_thumbnail);
        progressBar = findViewById(R.id.message_item_thumb_progress_bar); // 覆盖掉
        progressCover = findViewById(R.id.message_item_thumb_progress_cover);
        progressLabel = findViewById(R.id.message_item_thumb_progress_text);
    }

    @Override
    public void bindContentView() {
        FileAttachment msgAttachment = (FileAttachment) message.getAttachment();
        String path = msgAttachment.getPath();
        String thumbPath = msgAttachment.getThumbPath();
        if (!TextUtils.isEmpty(thumbPath)) {
            loadThumbnailImage(thumbPath, false, msgAttachment.getExtension());
        } else if (!TextUtils.isEmpty(path)) {
            loadThumbnailImage(thumbFromSourceFile(path), true, msgAttachment.getExtension());
        } else {
            loadThumbnailImage(null, false, msgAttachment.getExtension());
            if (message.getAttachStatus() == AttachStatusEnum.transferred
                    || message.getAttachStatus() == AttachStatusEnum.def) {
                downloadAttachment(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void param) {
                        loadThumbnailImage(msgAttachment.getThumbPath(), false, msgAttachment.getExtension());
                        refreshStatus();
                    }

                    @Override
                    public void onFailed(int code) {

                    }

                    @Override
                    public void onException(Throwable exception) {

                    }
                });
            }
        }
        refreshStatus();

    }

    private void refreshStatus() {
        FileAttachment attachment = (FileAttachment) message.getAttachment();
        if (TextUtils.isEmpty(attachment.getPath()) && TextUtils.isEmpty(attachment.getThumbPath())) {
            if (message.getAttachStatus() == AttachStatusEnum.fail || message.getStatus() == MsgStatusEnum.fail) {
                alertButton.setVisibility(View.VISIBLE);
            } else {
                alertButton.setVisibility(View.GONE);
            }
        }

        if ((message.getStatus() == MsgStatusEnum.sending
                || (isReceivedMessage() && message.getAttachStatus() == AttachStatusEnum.transferring)) && message.getRemoteExtension() == null) {
            progressCover.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            progressLabel.setVisibility(View.VISIBLE);
            progressLabel.setText(StringUtil.getPercentString(getMsgAdapter().getProgress(message)));
        } else {
            progressCover.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            progressLabel.setVisibility(View.GONE);
        }
    }

    private void loadThumbnailImage(String path, boolean isOriginal, String ext) {
        setImageSize(path);
        if (path != null) {
            //thumbnail.loadAsPath(thumbPath, getImageMaxEdge(), getImageMaxEdge(), maskBg());
            thumbnail.loadAsPath(path, getImageMaxEdge(), getImageMaxEdge(), maskBg(), ext);
        } else {
            //todo 更换默认图片
            thumbnail.loadAsResource(R.drawable.default_image_5dp, maskBg());
        }
    }

    private void setImageSize(String thumbPath) {
        int[] bounds = null;
        if (thumbPath != null) {
            bounds = BitmapDecoder.decodeBound(new File(thumbPath));
        }
        if (bounds == null) {
            if (message.getMsgType() == MsgTypeEnum.image) {
                ImageAttachment attachment = (ImageAttachment) message.getAttachment();
                bounds = new int[]{attachment.getWidth(), attachment.getHeight()};
            } else if (message.getMsgType() == MsgTypeEnum.video) {
                VideoAttachment attachment = (VideoAttachment) message.getAttachment();
                bounds = new int[]{attachment.getWidth(), attachment.getHeight()};
            }
        }

        if (bounds != null) {
            ImageUtil.ImageSize imageSize = ImageUtil.getThumbnailDisplaySize(bounds[0], bounds[1], getImageMaxEdge(), getImageMinEdge());
            setLayoutParams(imageSize.width, imageSize.height, thumbnail);
        }
    }

    protected abstract int maskBg();

    public static int getImageMaxEdge() {
        return (int) (165.0 / 320.0 * ScreenUtil.screenWidth);
    }

    public static int getImageMinEdge() {
        return (int) (76.0 / 320.0 * ScreenUtil.screenWidth);
    }

    protected abstract String thumbFromSourceFile(String path);

    @Override
    protected int leftBackground() {
        return R.drawable.shape_rectangle_white_5dp;
    }

    @Override
    protected int rightBackground() {
        return R.drawable.shape_rectangle_white_5dp;
    }
}
