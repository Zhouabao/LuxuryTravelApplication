package com.sdy.luxurytravelapplication.nim.business.session.actions;

import android.content.Intent;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.sdy.luxurytravelapplication.nim.business.session.constant.RequestCode;
import com.sdy.luxurytravelapplication.nim.common.util.string.MD5;

import java.io.File;
import java.util.List;

import static com.sdy.luxurytravelapplication.ext.CommonFunctionKt.onTakePhoto;

/**
 * Created by hzxuwen on 2015/6/12.
 */
public class ImageAction extends BaseAction {

    private static final int PICK_IMAGE_COUNT = 9;

    public static final String MIME_JPEG = "image/jpeg";

    public ImageAction() {
    }


    @Override
    public void onClick() {
        onTakePhoto(getContainer().activity, PICK_IMAGE_COUNT,
                makeRequestCode(RequestCode.PICK_IMAGE), PictureMimeType.ofVideo() & PictureMimeType.ofImage(), true, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RequestCode.PICK_IMAGE:
                List<LocalMedia> localMediaList = PictureSelector.obtainMultipleResult(data);
                if (localMediaList == null || localMediaList.isEmpty()) {
                    return;
                } else {
                    for (int i = 0; i < localMediaList.size(); i++) {
                        LocalMedia localMedia = localMediaList.get(i);
                        String path = "";
                        if (localMedia.isCompressed()) {
                            path = localMedia.getCompressPath();
                        } else if (localMedia.getAndroidQToPath() != null && !localMedia.getAndroidQToPath().isEmpty()) {
                            path = localMedia.getAndroidQToPath();
                        } else {
                            path = localMedia.getPath();
                        }
                        if (PictureMimeType.eqVideo(localMedia.getMimeType())) {
                            long duration = localMedia.getDuration();
                            int height = localMedia.getHeight();
                            int width = localMedia.getWidth();
                            sendVideoMessage(new File(path), duration, width, height, path);
                        } else {
                            sendImageMessage(new File(path));
                        }
                    }
                }
                break;

            case RequestCode.GET_LOCAL_VIDEO:
                List<LocalMedia> medias = PictureSelector.obtainMultipleResult(data);
                if (medias == null || medias.size() == 0) {
                    return;
                }
                LocalMedia mediaPlayer = medias.get(0);
                long duration = mediaPlayer == null ? 0 : mediaPlayer.getDuration();
                int height = mediaPlayer == null ? 0 : mediaPlayer.getHeight();
                int width = mediaPlayer == null ? 0 : mediaPlayer.getWidth();

                String path;
                if (mediaPlayer.isCompressed()) {
                    path = mediaPlayer.getCompressPath();
                } else if (mediaPlayer.getAndroidQToPath().isEmpty()) {
                    path = mediaPlayer.getAndroidQToPath();
                } else {
                    path = mediaPlayer.getPath();
                }
                sendVideoMessage(new File(path), duration, width, height, path);
                break;

        }
    }


    /**
     * 发送图片
     */
    private void sendImageMessage(File file) {
        if (!file.exists()) {
            return;
        }
        IMMessage message = MessageBuilder.createImageMessage(getAccount(), getSessionType(), file, file.getName());
        sendMessage(message);
    }

    /**
     * 发送视频
     *
     * @param file
     * @param duration
     * @param width
     * @param height
     * @param path
     */
    private void sendVideoMessage(File file, long duration, int width, int height, String path) {
        if (!file.exists()) {
            return;
        }
        IMMessage message = MessageBuilder.createVideoMessage(getAccount(), getSessionType(), file, duration, width, height, MD5.getStreamMD5(path));
        sendMessage(message);
    }


}

