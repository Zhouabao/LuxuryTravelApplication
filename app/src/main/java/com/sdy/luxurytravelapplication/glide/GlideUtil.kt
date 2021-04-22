package com.sdy.luxurytravelapplication.glide

import android.app.Notification
import android.content.Context
import android.graphics.Bitmap
import android.graphics.YuvImage
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import android.widget.RemoteViews
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.*
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.NotificationTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.duluduludala.xkvideo.base.config.GlideSmartRoundedCornersTransform
import com.sdy.luxurytravelapplication.R
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import java.io.File

/**
 * @author Zhou Fengmei
 * @create 2018/4/20
 * @Describe glide模板
 */
object GlideUtil {
    interface BitmapLoadCallbacks {
        fun getBitmapResult(bitmap: Bitmap?)
    }

    /**
     * 清除缓存
     *
     * @param context
     */
    fun clearCache(context: Context?) {
        clearMemoryCache(context)
        Thread(Runnable { clearDiskCache(context) }).start()
    }

    /**
     * 清除内存缓存
     *
     * @param context
     */
    fun clearMemoryCache(context: Context?) {
        Glide.get(context!!).clearMemory()
    }

    /**
     * 清除磁盘缓存
     *
     * @param context
     */
    fun clearDiskCache(context: Context?) {
        Glide.get(context!!).clearDiskCache()
    }

        private val options: RequestOptions
            private get() = RequestOptions()
                .priority(Priority.NORMAL)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)

        /**
         * 返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
         * 传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
         * 切记不要胡乱强转！
         */
        fun loadImg(context: Context, url: Any, targetImg: ImageView) {
            Glide.with(context)
                .load(url)
                .apply(options)
                .into(targetImg)
        }

        /**
         * 返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
         * 传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
         * 切记不要胡乱强转！
         */
        fun loadAvatorImg(context: Context, url: Any, targetImg: ImageView) {
            Glide.with(context!!)
                .load(url)
                .apply(
                    options.placeholder(R.drawable.icon_default_avator)
                        .error(R.drawable.icon_default_avator)
                )
                .into(targetImg)
        }

        /**
         * 返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
         * 传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
         * 切记不要胡乱强转！
         */
        fun loadImgWithError(context: Context, url: Any, targetImg: ImageView) {
            Glide.with(context)
                .load(url)
                .apply(options.error(R.drawable.icon_default_avator))
                .into(targetImg)
        }

        /**
         * 加载圆形图片
         *
         * @param context
         * @param url
         * @param targetImg
         */
        //    circleCrop().
        fun loadCircleImg(context: Context, url: Any, targetImg: ImageView) {
            Glide.with(context)
                .load(url)
                .apply(
                    options.placeholder(R.drawable.icon_default_avator)
                        .error(R.drawable.icon_default_avator)
                )
                .into(targetImg)
        }

        fun loadCircleImg(
            context: Context?,
            circle: Boolean = true,
            url: Any?,
            targetImg: ImageView?
        ) {
            val multiTransformation: MultiTransformation<Bitmap> = MultiTransformation<Bitmap>(CircleCrop())
            Glide.with(context!!)
                .load(url)
                .apply(
                    options.placeholder(R.drawable.icon_default_avator).error(R.drawable.icon_default_avator)

                )
                .transform(multiTransformation)
                .into(targetImg!!)
        }

        /**
         * 加载圆角图片内部居中
         *
         * @param context
         * @param url
         * @param tartgetImg
         * @param scale      是否缩放 0.0-1.0F
         */
        fun loadRoundImgCenterinside(
            context: Context,
            url: Any,
            tartgetImg: ImageView,
            scale: Float,
            radius: Int
        ) {
            val multiTransformation: MultiTransformation<Bitmap> =
                MultiTransformation<Bitmap>(
                    CenterInside(),
                    RoundedCornersTransformation(radius, 0)
                )
            Glide.with(context)
                .load(url)
                .priority(Priority.HIGH)
                .thumbnail(scale)
                .transform(multiTransformation)
                .into(tartgetImg)
        }

        fun loadCover(
            imageView: ImageView?,
            url: String?,
            context: Context?,
            radius: Int
        ) {
            val multiTransformation: MultiTransformation<Bitmap> =
                MultiTransformation<Bitmap>(
                    CenterCrop(),
                    GlideSmartRoundedCornersTransform(radius, 0)
                )
            Glide.with(context!!)
                .load(url)
                .frame(1000 * 1000.toLong())
                .centerCrop()
                .error(R.drawable.default_image_10dp)
                .placeholder(R.drawable.default_image_10dp)
                .transform(multiTransformation)
                .into(imageView!!)
        }


        /**
         * 加载圆角图片铺满居中
         *
         * @param context
         * @param url
         * @param tartgetImg
         * @param type       圆角的方式
         */
        @JvmOverloads
        fun loadRoundImgCenterCrop(
            context: Context,
            url: Any,
            tartgetImg: ImageView,
            radius: Int,
            type: RoundedCornersTransformation.CornerType=RoundedCornersTransformation.CornerType.ALL

        ) {
            val multiTransformation: MultiTransformation<Bitmap> =
                MultiTransformation<Bitmap>(
                    CenterCrop(),
                    RoundedCornersTransformation(radius, 0,type)
                )
            Glide.with(context)
                .load(url)
                .priority(Priority.NORMAL)
                .placeholder(R.drawable.default_image_10dp)
//                .error(R.drawable.default_image)
//                .thumbnail(0.5F)
                .transform(multiTransformation)
                .into(tartgetImg)


        }


        fun loadRoundImgBlurCenterCrop(
            context: Context?,
            url: Any?,
            tartgetImg: ImageView?,
            radius: Int
        ) {
            val multiTransformation: MultiTransformation<Bitmap> =
                MultiTransformation<Bitmap>(
                    BlurTransformation(25, 5),
                    CenterCrop(),
                    RoundedCorners(radius)
                )
            Glide.with(context!!)
                .load(url)
                .priority(Priority.NORMAL)
                .centerCrop()
                .placeholder(R.drawable.default_image_10dp)
                .error(R.drawable.default_image_10dp) //                .thumbnail(0.5F)
                .transform(multiTransformation)
                .into(tartgetImg!!)
        }

        fun loadImgBlurCenterCrop(
            context: Context?,
            url: Any?,
            tartgetImg: ImageView?,
            radius: Int,
            type: RoundedCornersTransformation.CornerType=RoundedCornersTransformation.CornerType.ALL
        ) {
            val multiTransformation: MultiTransformation<Bitmap> =
                MultiTransformation<Bitmap>(
                    BlurTransformation(25, 2),
                    CenterCrop(),
                    RoundedCornersTransformation(radius,0,type)
//                    RoundedCorners(radius)
                )
            Glide.with(context!!)
                .load(url)
                .priority(Priority.NORMAL)
                .centerCrop()
                .placeholder(R.drawable.default_image_10dp)
                .error(R.drawable.default_image_10dp) //                .thumbnail(0.5F)
                .transform(multiTransformation)
                .into(tartgetImg!!)
        }


        fun loadRoundImgFitcenter(
            context: Context?,
            url: Any?,
            tartgetImg: ImageView?,
            radius: Int,
            type: RoundedCornersTransformation.CornerType = RoundedCornersTransformation.CornerType.ALL
        ) {
            val multiTransformation: MultiTransformation<Bitmap> =
                MultiTransformation<Bitmap>(
                    FitCenter(),
                    RoundedCornersTransformation(radius, 0, type)
                )
            Glide.with(context!!)
                .load(url)
                .priority(Priority.NORMAL) //                .placeholder(R.drawable.default_image)
                //                .error(R.drawable.default_image)
                //                .thumbnail(0.5F)
                .transform(multiTransformation)
                .into(tartgetImg!!)
        }

        /**
         * 加载圆角图片铺满居中
         *
         * @param context
         * @param url
         * @param tartgetImg
         */
        fun loadRoundImgCenterCropNoHolder(
            context: Context?,
            url: Any?,
            tartgetImg: ImageView?,
            radius: Int
        ) {
            val multiTransformation: MultiTransformation<Bitmap> =
                MultiTransformation<Bitmap>(
                    CenterCrop(),
                    RoundedCornersTransformation(radius, 0)
                )
            Glide.with(context!!)
                .load(url)
                .priority(Priority.NORMAL) //                .thumbnail(0.5F)
                .transform(multiTransformation)
                .into(tartgetImg!!)
        }

        fun loadRoundImgCenterCropWH(
            context: Context?,
            url: String?,
            tartgetImg: ImageView?,
            radius: Int,
            width: Int,
            height: Int
        ) {
            val multiTransformation: MultiTransformation<Bitmap> =
                MultiTransformation<Bitmap>(
                    CenterCrop(),
                    RoundedCornersTransformation(radius, 0)
                )
            Glide.with(context!!)
                .load(url)
                .priority(Priority.NORMAL)
                .placeholder(R.drawable.default_image_5dp)
                .override(width, height)
                .error(R.drawable.default_image_5dp) //                .thumbnail(0.5F)
                .transform(multiTransformation)
                .into(tartgetImg!!)
        }

        /**
         * 加载圆角图片铺满居中
         *
         * @param context
         * @param url
         * @param tartgetImg
         */
        fun loadImgCenterCrop(
            context: Context?,
            url: Any?,
            tartgetImg: ImageView?
        ) {
            Glide.with(context!!)
                .load(url)
                .priority(Priority.LOW) //                .thumbnail(0.1F)
                .transform(CenterCrop())
                .into(tartgetImg!!)
        }

        /**
         * 根据资源ID加载图片
         *
         * @param context
         * @param resourceId
         * @param target
         */
        fun loadResourseImg(
            context: Context?,
            resourceId: Int,
            target: ImageView?
        ) {
            Glide.with(context!!)
                .load(resourceId)
                .apply(options)
                .into(target!!)
        }

        /**
         * 加载图片不需要缓存的
         *
         * @param context
         * @param url
         * @param target
         */
        fun loadSourseImgWithNoCache(
            context: Context?,
            url: String?,
            target: ImageView?
        ) {
            Glide.with(context!!)
                .load(url)
                .apply(
                    options
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                )
                .into(target!!)
        }

        /**
         * 根据图片路径加载图片
         *
         * @param context
         * @param imgFile
         * @param target
         * @param defaultId
         */
        fun loadFileImg(
            context: Context?,
            imgFile: File?,
            target: ImageView?,
            defaultId: Int
        ) {
            Glide.with(context!!)
                .load(imgFile)
                .apply(options)
                .into(target!!)
        }

        /**
         * 加载Gif为一张静态图片
         *
         * @param context
         * @param url
         */
        fun LoadGiftAsBitmap(
            context: Context?,
            url: String?,
            imageView: ImageView?
        ) {
            Glide.with(context!!).asBitmap()
                .apply(options).load(url).into(imageView!!)
        }

        /**
         * 你想只有加载对象是Gif时才能加载成功
         *
         * @param context
         * @param url
         */
        fun LoadGiftAsGist(
            context: Context?,
            url: String?,
            imageView: ImageView?,
            erroId: Int
        ) {
            Glide.with(context!!).asGif().apply(options)
                .load(url).into(imageView!!)
        }

        /**
         * 加载缩略图,会自动与传入的fragment绑定生命周期,加载请求现在会自动在onStop中暂停在，onStart中重新开始。
         * 需要保证 ScaleType 的设置是正确的。
         *
         * @param fragment
         * @param url
         * @param imageView
         */
        fun LoadThumbNail(
            fragment: Fragment?,
            url: String?,
            imageView: ImageView?
        ) {
            Glide.with(fragment!!).load(url)
                .apply(options).thumbnail(0.1f).into(imageView!!)
        }

        /**
         * 上传一张大小为xPx*yPx像素的用户头像的图片bytes数据
         *
         * @param context
         * @param url
         * @param xPx
         * @param yPx
         */
        fun decodeResorse(
            context: Context?,
            url: File?,
            xPx: Int,
            yPx: Int
        ) {
            Glide.with(context!!)
                .load(url)
                .apply(options)
                .into(object : SimpleTarget<Drawable?>(xPx, yPx) {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable?>?
                    ) {
                        //上传动作
                    }

                })
        }

        /**
         * 显示本地视频(网络视频无效)
         *
         * @param context
         * @param filePath
         * @param imageView
         */
        fun LoadShowLocalVidio(
            context: Context?,
            filePath: String?,
            imageView: ImageView?
        ) {
            Glide.with(context!!)
                .load(Uri.fromFile(File(filePath)))
                .apply(options).into(imageView!!)
        }

        /**
         * 在通知栏中显示从网络上请求的图片
         *
         * @param context
         * @param remoteViews
         * @param viewId
         * @param notification
         * @param notificationId
         * @param url
         */
        fun ShowImgInNotification(
            context: Context,
            remoteViews: RemoteViews?,
            viewId: Int,
            notification: Notification?,
            notificationId: Int,
            url: String?
        ) {
            val target =
                NotificationTarget(
                    context,
                    viewId,
                    remoteViews,
                    notification,
                    notificationId
                )
            Glide.with(context.applicationContext).asBitmap()
                .apply(options).load(url)
                .into(target)
        }

        /**
         * 下载图片,耗时操作不能放在主线程中进行
         *
         * @param context
         * @param url
         */
        fun downLoadImage(
            context: Context?,
            url: String?,
            callbacks: BitmapLoadCallbacks
        ) {
            try {
                Glide.with(context!!).asBitmap()
                    .apply(options).load(url)
                    .listener(object : RequestListener<Bitmap?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any,
                            target: Target<Bitmap?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any,
                            target: Target<Bitmap?>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            callbacks.getBitmapResult(resource)
                            return false
                        }
                    }).submit().get()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
}