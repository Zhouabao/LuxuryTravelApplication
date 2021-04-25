package com.sdy.luxurytravelapplication.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import com.luck.picture.lib.compress.Luban
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.mvp.model.bean.ContactInfo
import com.sdy.luxurytravelapplication.mvp.model.bean.MediaBean
import com.sdy.luxurytravelapplication.nim.config.NimSDKOptionConfig
import java.io.File

/**
 *    author : ZFM
 *    date   : 2019/7/129:32
 *    desc   :
 *    version: 1.0
 */
object UriUtils {

    /**
     * 获取文件格式名
     */
    fun getFormatName(fileName: String): String {
        var fileName = fileName
        //去掉首尾的空格
        fileName = fileName.trim { it <= ' ' }
        val s = fileName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return if (s.size >= 2) {
            s[s.size - 1]
        } else ""
    }


    fun getDuration(path: String, context: Context): Int {
        var duration = 0
        val contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val columns = arrayOf(
            MediaStore.Audio.Media._ID, // 歌曲ID
            MediaStore.Audio.Media.DURATION
        )// 歌曲的总播放时长
        val mResolver = context.contentResolver
        val selection = MediaStore.Audio.Media.DATA + "=?"
        val selectionArgs = arrayOf(path)
        val cursor = mResolver.query(contentUri, null, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
            cursor.close()
        }

        return duration
    }


    //录音展示时间格式化
    fun getShowTime(countTime: Int): String {
        var result = ""
        if (countTime < 10)
            result = "00:0$countTime"
        else if (countTime < 60)
            result = "00:$countTime"
        else {
            val minute = countTime / 60
            val mod = countTime % 60
            if (minute < 10)
                result += "0$minute:"
            else {
                result += "$minute:"
            }
            if (mod < 10)
                result += "0$mod"
            else {
                result += mod
            }

        }
        return result
    }


    //录音展示时间格式化
    fun stringToTimeInt(countTime: String): Int {
        var result = countTime.split(":")
        return result[0].toInt() * 60 + result[1].toInt()
    }


    fun ms2HMS(longMills: Long): String {
        var mills = longMills
        val HMStime: String
        mills /= 1000
        val hour = mills / 3600
        val mint = mills % 3600 / 60
        val sed = mills % 60
        var hourStr = hour.toString()
        if (hour < 10) {
            hourStr = "0$hourStr"
        }
        var mintStr = mint.toString()
        if (mint < 10) {
            mintStr = "0$mintStr"
        }
        var sedStr = sed.toString()
        if (sed < 10) {
            sedStr = "0$sedStr"
        }
        HMStime = "$hourStr:$mintStr:$sedStr"
        return HMStime
    }


    /**
     * 读取手机中所有图片信息
     */
    fun getAllPhotoInfo(context: Context): MutableList<MediaBean> {
        val medias = mutableListOf<MediaBean>()
//        LoaderManager.getInstance(this).initLoader(1, null, this)
        Thread(Runnable {
            val mediaBeen = ArrayList<MediaBean>()
            val mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projImage = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT
            )
            val mCursor = context.contentResolver.query(
                mImageUri,
                projImage,
                MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                arrayOf("image/jpeg", "image/png", "image/jpg"),
                MediaStore.Images.Media.DATE_MODIFIED + " desc"
            )

            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    val id = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media._ID))
                    val path =
                        mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA))
                            ?: ""
                    val size =
                        mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.SIZE)) / 1024L
                    val displayName =
                        mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                            ?: ""
                    val width =
                        mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.WIDTH))
                    val height =
                        mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.HEIGHT))
                    //用于展示相册初始化界面
                    mediaBeen.add(
                        MediaBean(
                            id,
                            MediaBean.TYPE.IMAGE,
                            path,
                            displayName,
                            "",
                            0,
                            size,
                            width = width,
                            height = height
                        )
                    )
                }
                mCursor.close()
            }
            medias.addAll(mediaBeen)
        }).start()

        return medias
    }


    /**
     * 获取手机中所有视频的信息
     */
    fun getAllVideoInfos(context: Context): MutableList<MediaBean> {
        val medias = mutableListOf<MediaBean>()
        Thread(Runnable {
            val mediaBeen = ArrayList<MediaBean>()
            val mImageUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            val proj = arrayOf(
                MediaStore.Video.Thumbnails._ID,
                MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_MODIFIED
            )
            val mCursor = context.contentResolver.query(
                mImageUri, proj, null, null,
                //                MediaStore.Video.Media.MIME_TYPE + "=?",
                //                arrayOf("video/mp4"),
                MediaStore.Video.Media.DATE_MODIFIED + " desc"
            )
            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    // 获取视频的路径
                    val videoId = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media._ID))
                    val path =
                        mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DATA))
                    val duration =
                        mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                    var size =
                        mCursor.getLong(mCursor.getColumnIndex(MediaStore.Video.Media.SIZE)) / 1024 //单位kb
                    if (size < 0) {
                        //某些设备获取size<0，直接计算
                        Log.e("dml", "this video size < 0 $path")
                        size = File(path).length() / 1024
                    }
                    val displayName =
                        if (mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)) != null) {
                            mCursor.getString(mCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))
                        } else {
                            ""
                        }

                    //提前生成缩略图，再获取：http://stackoverflow.com/questions/27903264/how-to-get-the-video-thumbnail-path-and-not-the-bitmap
                    MediaStore.Video.Thumbnails.getThumbnail(
                        context.contentResolver,
                        videoId.toLong(),
                        MediaStore.Video.Thumbnails.MICRO_KIND,
                        null
                    )
                    val projection = arrayOf(
                        MediaStore.Video.Thumbnails._ID,
                        MediaStore.Video.Thumbnails.DATA,
                        MediaStore.Video.Thumbnails.WIDTH,
                        MediaStore.Video.Thumbnails.HEIGHT
                    )
                    val cursor = context.contentResolver.query(
                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        projection,
                        MediaStore.Video.Thumbnails.VIDEO_ID + "=?",
                        arrayOf(videoId.toString() + ""),
                        null
                    )
                    var thumbPath = ""
                    var height = 0
                    var width = 0
                    while (cursor!!.moveToNext()) {
                        thumbPath =
                            cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA))
                        width =
                            cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Thumbnails.WIDTH))
                        height =
                            cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Thumbnails.HEIGHT))
                    }
                    cursor.close()
                    if (duration > 0)
                        mediaBeen.add(
                            MediaBean(
                                videoId,
                                MediaBean.TYPE.VIDEO,
                                path,
                                displayName,
                                thumbPath,
                                duration,
                                size,
                                width = width,
                                height = height
                            )
                        )

                }
                mCursor.close()
            }
            medias.addAll(mediaBeen)
        }).start()

        return medias
    }


    /**
     * 获取图片文件的宽高
     */
    fun getImageWidthHeight(path: String): IntArray {
        val options = BitmapFactory.Options()

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true
        val bitmap = BitmapFactory.decodeFile(path, options) // 此时返回的bitmap为null
        /**
         * options.outHeight为原始图片的高
         */
        return intArrayOf(options.outWidth, options.outHeight)
    }


    /**
     * 获取联系人数据
     *
     * @param context
     * @return
     */
    fun getPhoneContacts(context: Context): List<ContactInfo> {
        val list = ArrayList<ContactInfo>()

        val photosProjection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
        )

        val resolver = context.contentResolver
        //获取手机联系人
        val phoneCorsur =
            resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                photosProjection,
                null,
                null,
                null
            )

        if (phoneCorsur != null) {
            while (phoneCorsur.moveToNext()) {
                //得到手机号码
                val phoneNumber = phoneCorsur.getString(1)
                if (phoneNumber.isNullOrEmpty()) {
                    continue
                }
                //得到联系人名称
                val contactName =
                    phoneCorsur.getString(0).replace(" ", "").replace("-", "").replace("\\+86", "")
                //得到联系人的id
                val id = phoneCorsur.getString(3)
                list.add(
                    ContactInfo(
                        id,
                        contactName,
                        phoneNumber
                    )
                )
            }
            phoneCorsur.close()
        }
        return list
    }

    /**
     * android刷新媒体库
     */
    fun updateMedia(context: Context, path: String) =
        if (SDK_INT >= Build.VERSION_CODES.KITKAT) {//当大于等于Android 4.4时
            MediaScannerConnection.scanFile(context, arrayOf(path), null) { p0, uri ->
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                mediaScanIntent.data = uri
                context.sendBroadcast(mediaScanIntent)
            }

        } else {//Andrtoid4.4以下版本
            context.sendBroadcast(
                Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.fromFile((File(path).parentFile))
                )
            )
        }


    fun getLubanBuilder(context: Context): Luban.Builder {
        return Luban.with(context)
            .filter {
                !(it.isEmpty() || it.toLowerCase().endsWith(".gif"))
            }
            .ignoreBy(100)
            .setTargetDir(getCacheDir(context))
    }


    /**s
     * 设置缓存文件夹地址
     */
    fun getCacheDir(context: Context): String {
        return NimSDKOptionConfig.getAppCacheDir(context).plus(Constants.CACHE_DIR)
//        return ""
    }


    fun getImageContentUri(context: Context, path: String): Uri? {
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID), MediaStore.Images.Media.DATA + "=? ",
            arrayOf(path), null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            val baseUri = Uri.parse("content://media/external/images/media")
            return Uri.withAppendedPath(baseUri, "" + id)
        } else {
            // 如果图片不在手机的共享图片数据库，就先把它插入。
            if (File(path).exists()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, path);
                return context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )
            } else {
                return null;
            }
        }
    }


}