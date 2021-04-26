package com.sdy.luxurytravelapplication.widgets.player;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;

import java.io.File;

/**
 * Created by _SOLID
 * Date:2016/3/22
 * Time:16:31
 */
public class MediaRecorderHelper {
    //录制音频的几种状态
    public static final int ACTION_NORMAL = 0; //初态
    public static final int ACTION_RECORDING = 1; //录制中
    public static final int ACTION_COMMPLETE = 2;//录制完成
    public static final int ACTION_PLAYING = 3;//播放
    public static final int ACTION_PAUSE = 4;//暂停
    public static final int ACTION_DONE = 5;//选择完成


    private MediaRecorder mMediaRecorder;
    private String mSavePath;
    private String mCurrentFilePath;


    public MediaRecorderHelper(Context context) {
        mSavePath = getSDPath(context);
        File file = new File(mSavePath);
        if (!file.exists()) file.mkdirs();

    }

//    private String getRecorderFilePath(Context context) {
//        String path = "";
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            path = Environment.getExternalStorageDirectory().getAbsolutePath();
//        } else {
//            path = context.getCacheDir().getAbsolutePath();
//
//        }
//        return path + File.separator + AppUtils.getAppPackageName() + File.separator + "audio";
//    }

    //安卓10之后兼容文件夹创建 避免无法创建录音等问题
    private String getSDPath(Context context) {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
        if (sdCardExist) {
            if (Build.VERSION.SDK_INT>=29){
                //Android10之后
                sdDir = context.getExternalFilesDir(null);
            }else {
                sdDir = Environment.getExternalStorageDirectory();// 获取SD卡根目录
            }
        } else {
            sdDir = Environment.getRootDirectory();// 获取跟目录
        }
        return sdDir.toString()+ File.separator + "audio";
    }

    /**
     * 开始录音
     */
    public void startRecord() {
        try {
            mMediaRecorder = new MediaRecorder();
            File file = new File(mSavePath, generateFileName());
//            file.createNewFile();
            mCurrentFilePath = file.getAbsolutePath();
            // 设置录音文件的保存位置
            mMediaRecorder.setOutputFile(mCurrentFilePath);
            // 设置录音的来源（从哪里录音）
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置录音的保存格式,七牛上AMR_NB放不出来 MPEG_4
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            // 设置录音的编码,七牛上AMR_NB放不出来
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.prepare();
            mMediaRecorder.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录音
     */
    public void stopAndRelease() {
        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.setOnInfoListener(null);
                mMediaRecorder.setOnErrorListener(null);
                mMediaRecorder.setPreviewDisplay(null);
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                //e.printStackTrace();
                mMediaRecorder = null;
                mMediaRecorder = new MediaRecorder();
            }
            mMediaRecorder.release();
            mMediaRecorder = null;
        }

    }

    /***
     * 取消本次录音操作
     */
    public void cancel() {
        this.stopAndRelease();
        if (mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }

    private String generateFileName() {
        return "AUD" + System.currentTimeMillis() + ".mp3";
    }

    /**
     * 得到录音文件的路径
     *
     * @return
     */
    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

}
