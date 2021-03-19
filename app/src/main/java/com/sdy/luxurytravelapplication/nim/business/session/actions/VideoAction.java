package com.sdy.luxurytravelapplication.nim.business.session.actions;

import android.content.Intent;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.sdy.luxurytravelapplication.ext.CommonFunctionKt;
import com.sdy.luxurytravelapplication.nim.business.session.constant.RequestCode;
import com.sdy.luxurytravelapplication.nim.common.util.string.MD5;

import java.io.File;
import java.util.List;

/**
 * Created by hzxuwen on 2015/6/12.
 */
public class VideoAction extends BaseAction {


    public VideoAction() { }

    @Override
    public void onClick() {
       CommonFunctionKt.onTakePhoto(getContainer().activity,1, RequestCode.GET_LOCAL_VIDEO, PictureMimeType.ofVideo());
    }

    /**
     * ********************** 视频 *******************************
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
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
                IMMessage message = MessageBuilder.createVideoMessage(getAccount(), getSessionType(), new File(path), duration, width, height, MD5.getStreamMD5(path));
                sendMessage(message);
                break;

        }
    }


}
