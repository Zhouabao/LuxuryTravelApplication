package com.sdy.luxurytravelapplication.nim.business.session.activity

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.netease.nimlib.sdk.AbortableFuture
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.MsgService
import com.netease.nimlib.sdk.msg.MsgServiceObserve
import com.netease.nimlib.sdk.msg.attachment.ImageAttachment
import com.netease.nimlib.sdk.msg.constant.AttachStatusEnum
import com.netease.nimlib.sdk.msg.constant.MsgDirectionEnum
import com.netease.nimlib.sdk.msg.constant.MsgStatusEnum
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum
import com.netease.nimlib.sdk.msg.model.IMMessage
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseActivity
import com.sdy.luxurytravelapplication.databinding.ActivityWatchMessagePictureBinding
import com.sdy.luxurytravelapplication.databinding.LayoutActionbarBinding
import com.sdy.luxurytravelapplication.databinding.NimWatchMediaDownloadProgressLayoutBinding
import com.sdy.luxurytravelapplication.event.SavePictureEvent
import com.sdy.luxurytravelapplication.nim.common.ToastHelper
import com.sdy.luxurytravelapplication.nim.common.ui.imageview.BaseZoomableImageView
import com.sdy.luxurytravelapplication.nim.common.ui.imageview.ImageGestureListener
import com.sdy.luxurytravelapplication.nim.common.util.C
import com.sdy.luxurytravelapplication.nim.common.util.file.AttachmentStore
import com.sdy.luxurytravelapplication.nim.common.util.media.BitmapDecoder
import com.sdy.luxurytravelapplication.nim.common.util.media.ImageUtil
import com.sdy.luxurytravelapplication.nim.common.util.storage.StorageUtil
import com.sdy.luxurytravelapplication.nim.common.util.sys.TimeUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivity
import java.io.File

/**
 * 查看图片消息
 */
class WatchMessagePictureActivity : BaseActivity<ActivityWatchMessagePictureBinding>() {
    private lateinit var message: IMMessage
    private val isShowMenu by lazy { intent.getBooleanExtra(INTENT_EXTRA_MENU, false) }
    private val mode by lazy { if (ImageUtil.isGif((message.attachment as ImageAttachment).extension)) MODE_GIF else MODE_NORMAL }
    private val imageMsgList: ArrayList<IMMessage> = ArrayList()
    private var firstDisplayImageIndex = 0
    private lateinit var adapter: PagerAdapter

    private var downloadFuture: AbortableFuture<Void>? = null

    companion object {
        private val TAG: String = WatchMessagePictureActivity::class.java.simpleName
        private const val INTENT_EXTRA_IMAGE = "INTENT_EXTRA_IMAGE"
        private const val INTENT_EXTRA_MENU = "INTENT_EXTRA_MENU"

        private const val MODE_NORMAL = 0
        private const val MODE_GIF = 1
        fun start(context: Context, message: IMMessage, showMenu: Boolean = false) {
            context.startActivity<WatchMessagePictureActivity>(
                INTENT_EXTRA_IMAGE to message,
                INTENT_EXTRA_MENU to showMenu
            )
        }
    }

    private val titleBinding: LayoutActionbarBinding by lazy {
        LayoutActionbarBinding.inflate(
            layoutInflater
        )
    }
    private val loadingBinding :NimWatchMediaDownloadProgressLayoutBinding by lazy {
        NimWatchMediaDownloadProgressLayoutBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_watch_message_picture)

        initView()
        loadMsgAndDisplay()
        registerObservers(true)

    }


    override fun onDestroy() {
        registerObservers(false)
        EventBus.getDefault().unregister(this)
          binding.viewPagerImage.adapter = null
        if (downloadFuture != null) {
            downloadFuture!!.abort()
            downloadFuture = null
        }
        super.onDestroy()
    }


    // 加载并显示
    private fun loadMsgAndDisplay() {
        if (mode == MODE_NORMAL && message.status == MsgStatusEnum.success) {
            queryImageMessages()
        } else {
            displaySimpleImage(mode == MODE_GIF)
        }
    }

    // 显示单个gif图片
    private fun displaySimpleImage(isGif: Boolean = true) {
        val path = (message.attachment as ImageAttachment).path
        val thumbPath = (message.attachment as ImageAttachment).thumbPath
        if (!TextUtils.isEmpty(path)) {
            if (isGif)
                Glide.with(this).asGif().load(File(path)).into(binding.simpleImageView)
            else
                Glide.with(this).load(File(path)).into(binding.simpleImageView)
            return
        }
        if (!TextUtils.isEmpty(thumbPath)) {
            if (isGif)
                Glide.with(this).asGif().load(File(thumbPath)).into(binding.simpleImageView)
            else
                Glide.with(this).load(File(path)).into(binding.simpleImageView)
        }
        if (message.direct == MsgDirectionEnum.In) {
            requestOriImage(message)
        }
    }

    private val handler by lazy { Handler() }

    // 若图片已下载，直接显示图片；若图片未下载，则下载图片
    private fun requestOriImage(msg: IMMessage) {
        if (isOriginImageHasDownloaded(msg)) {
            onDownloadSuccess(msg)
            return
        }

        // async download original image
        onDownloadStart(msg)
        message = msg // 下载成功之后，判断是否是同一条消息时需要使用
        downloadFuture = NIMClient.getService(MsgService::class.java).downloadAttachment(msg, false)
    }

    // 查询并显示图片，带viewPager
    private fun queryImageMessages() {
        val anchor =
            MessageBuilder.createEmptyMessage(
                message.sessionId,
                message.sessionType,
                0
            )
        NIMClient.getService(MsgService::class.java)
            .queryMessageListByType(MsgTypeEnum.image, anchor, Int.MAX_VALUE)
            .setCallback(object : RequestCallback<List<IMMessage>> {
                override fun onSuccess(param: List<IMMessage>) {
                    for (imMessage in param) {
                        val attachment = imMessage.attachment
                        if (attachment is ImageAttachment && !ImageUtil.isGif(attachment.extension)) {
                            imageMsgList.add(imMessage)
                        }
                    }
                    // imageMsgList.addAll(param);
                    imageMsgList.reverse()
                    setDisplayIndex()
                    setViewPagerAdapter()
                }

                override fun onFailed(code: Int) {
                    Log.i(TAG, "query msg by type failed, code:$code")
                }

                override fun onException(exception: Throwable) {}
            })
    }

    private var newPageSelected = false

    private fun setViewPagerAdapter() {
        adapter = object : PagerAdapter() {
            override fun getCount(): Int {
                return imageMsgList?.size ?: 0
            }

            override fun notifyDataSetChanged() {
                super.notifyDataSetChanged()
            }

            override fun destroyItem(
                container: ViewGroup,
                position: Int,
                `object`: Any
            ) {
                val layout = `object` as View
                val iv: BaseZoomableImageView =
                    layout.findViewById<View>(R.id.watch_image_view) as BaseZoomableImageView
                iv.clear()
                container.removeView(layout)
            }

            override fun isViewFromObject(
                view: View,
                `object`: Any
            ): Boolean {
                return view === `object`
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val layout: ViewGroup
                layout = LayoutInflater.from(this@WatchMessagePictureActivity)
                    .inflate(R.layout.nim_image_layout_multi_touch, null) as ViewGroup
                layout.setBackgroundColor(Color.BLACK)
                container.addView(layout)
                layout.tag = position
                if (position == firstDisplayImageIndex) {
                    onViewPagerSelected(position)
                }
                return layout
            }

            override fun getItemPosition(`object`: Any): Int {
                return POSITION_NONE
            }
        }
        binding.viewPagerImage.adapter = adapter
          binding.viewPagerImage.offscreenPageLimit = 2
          binding.viewPagerImage.currentItem = firstDisplayImageIndex
          binding.viewPagerImage.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                if (positionOffset == 0f && newPageSelected) {
                    newPageSelected = false
                    onViewPagerSelected(position)
                }
            }

            override fun onPageSelected(position: Int) {
                newPageSelected = true
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun onViewPagerSelected(position: Int) {
        if (downloadFuture != null) {
            downloadFuture!!.abort()
            downloadFuture = null
        }

        titleBinding.actionbarTitle.text = String.format(
            getString(
                R.string.pic_send_at_time,
                TimeUtil.getDateString(imageMsgList[position].time)
            )
        )
        updateCurrentImageView(position)
        onImageViewFound(image)

    }

    private lateinit var image: BaseZoomableImageView

    // 初始化每个view的image
    protected fun updateCurrentImageView(position: Int) {
        val currentLayout: View =   binding.viewPagerImage.findViewWithTag(position)
        if (currentLayout == null) {
            ViewCompat.postOnAnimation(  binding.viewPagerImage) { updateCurrentImageView(position) }
            return
        }
        image = currentLayout.findViewById<View>(R.id.watch_image_view) as BaseZoomableImageView
        requestOriImage(imageMsgList[position])
    }


    // 设置第一个选中的图片index
    private fun setDisplayIndex() {
        for (i in imageMsgList.indices) {
            val imageObject = imageMsgList[i]
            if (compareObjects(message, imageObject)) {
                firstDisplayImageIndex = i
                break
            }
        }
    }

    protected fun compareObjects(t1: IMMessage, t2: IMMessage): Boolean {
        return t1.uuid == t2.uuid
    }


    /**
     * ********************************* 下载 ****************************************
     */

    private fun registerObservers(register: Boolean) {
        NIMClient.getService(
            MsgServiceObserve::class.java
        ).observeMsgStatus(statusObserver, register)

    }

    private val statusObserver =
        Observer<IMMessage> { msg ->
            if (!msg.isTheSame(message) || isFinishing()) {
                return@Observer
            }
            if (isOriginImageHasDownloaded(msg)) {
                onDownloadSuccess(msg)
            } else if (msg.attachStatus == AttachStatusEnum.fail) {
                onDownloadFailed()
            }
        }


    private fun onDownloadStart(msg: IMMessage) {
        if (TextUtils.isEmpty((msg.attachment as ImageAttachment).path)) {
            loadingBinding.loadingLayout.visibility = View.VISIBLE
        } else {
            loadingBinding.loadingLayout.visibility = View.GONE
        }
        if (mode == MODE_NORMAL) {
            setThumbnail(msg)
        }
    }


    private fun onDownloadSuccess(msg: IMMessage) {
        loadingBinding.loadingLayout.visibility = View.GONE
        if (mode == MODE_NORMAL) {
            handler.post({ setImageView(msg) })
        } else if (mode == MODE_GIF) {
            displaySimpleImage()
        }
    }


    private fun onDownloadFailed() {
        loadingBinding.loadingLayout.setVisibility(View.GONE)
        if (mode == MODE_NORMAL) {
            image.imageBitmap = ImageUtil.getBitmapFromDrawableRes(getImageResOnFailed())
        } else if (mode == MODE_GIF) {
            binding.simpleImageView.setImageBitmap(ImageUtil.getBitmapFromDrawableRes(getImageResOnFailed()))
        }
        ToastHelper.showToastLong(this, R.string.download_picture_fail)
    }


    /**
     * ******************************** 设置图片 *********************************
     */
    private fun setThumbnail(msg: IMMessage) {
        val thumbPath =
            (msg.attachment as ImageAttachment).thumbPath
        val path =
            (msg.attachment as ImageAttachment).path
        var bitmap: Bitmap? = null
        if (!TextUtils.isEmpty(thumbPath)) {
            bitmap = BitmapDecoder.decodeSampledForDisplay(
                thumbPath
            )
            bitmap = ImageUtil.rotateBitmapInNeeded(
                thumbPath,
                bitmap
            )
        } else if (!TextUtils.isEmpty(path)) {
            bitmap =
                BitmapDecoder.decodeSampledForDisplay(path)
            bitmap =
                ImageUtil.rotateBitmapInNeeded(path, bitmap)
        }
        if (bitmap != null) {
            image.setImageBitmap(bitmap)
            return
        }
        image.setImageBitmap(
            ImageUtil.getBitmapFromDrawableRes(
                getImageResOnLoading()
            )
        )
    }

    private fun setImageView(msg: IMMessage) {
        val path =
            (msg.attachment as ImageAttachment).path
        if (TextUtils.isEmpty(path)) {
            image.setImageBitmap(
                ImageUtil.getBitmapFromDrawableRes(
                    getImageResOnLoading()
                )
            )
            return
        }
        var bitmap: Bitmap =
            BitmapDecoder.decodeSampledForDisplay(
                path,
                false
            )
        bitmap =
            ImageUtil.rotateBitmapInNeeded(path, bitmap)
        if (bitmap == null) {
            ToastHelper.showToastLong(
                this,
                R.string.picker_image_error
            )
            image.setImageBitmap(
                ImageUtil.getBitmapFromDrawableRes(
                    getImageResOnFailed()
                )
            )
        } else {
            image.setImageBitmap(bitmap)
        }
    }

    private fun getImageResOnLoading(): Int {
        return R.drawable.nim_image_default
    }

    private fun getImageResOnFailed(): Int {
        return R.drawable.nim_image_download_failed
    }


    /**
     * ***********************************图片点击事件*******************************************
     */
    // 设置图片点击事件
    protected fun onImageViewFound(imageView: BaseZoomableImageView) {
        imageView.setImageGestureListener(object :
            ImageGestureListener {
            override fun onImageGestureLongPress() {
                showWatchPictureAction()
            }

            override fun onImageGestureSingleTapConfirmed() {
                onImageViewTouched()
            }

            override fun onImageGestureFlingDown() {
                finish()
            }

        })
    }

    // 图片单击
    protected fun onImageViewTouched() {
        finish()
    }


    //图片长按
    private fun showWatchPictureAction() {
//        ShareDialog(this, ShareDialog.TYPE_MEDIA_MESSAGE, null).show()

//        val path = (message.attachment as ImageAttachment).thumbPath
//        if (TextUtils.isEmpty(path)) {
//            return
//        }
//        val titles = arrayListOf<String>()
//        if (!TextUtils.isEmpty((message.attachment as ImageAttachment).path)) {
//            titles.add(getString(R.string.save_to_device))
//        }


//        BottomMenu.show(this, titles.toList()) { _, _ ->
//            savePicture()
//        }.cancelable = true


    }

    // 保存图片
    fun savePicture() {
        val attachment =
            message.attachment as ImageAttachment
        val path = attachment.path
        if (TextUtils.isEmpty(path)) {
            return
        }
        var srcFilename = attachment.fileName
        //默认jpg
        val extension =
            if (TextUtils.isEmpty(attachment.extension)) "jpg" else attachment.extension
        srcFilename += ".$extension"
        val picPath: String =
            StorageUtil.getSystemImagePath()
        val dstPath = picPath + srcFilename
        if (AttachmentStore.copy(path, dstPath) != -1L) {
            try {
                val values = ContentValues(2)
                values.put(
                    MediaStore.Images.Media.MIME_TYPE,
                    C.MimeType.MIME_JPEG
                )
                values.put(MediaStore.Images.Media.DATA, dstPath)
                contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )
                ToastHelper.showToastLong(
                    this@WatchMessagePictureActivity,
                    getString(R.string.picture_save_to)
                )
            } catch (e: Exception) {
                // may be java.lang.UnsupportedOperationException
                ToastHelper.showToastLong(
                    this@WatchMessagePictureActivity,
                    getString(R.string.picture_save_fail)
                )
            }
        } else {
            ToastHelper.showToastLong(
                this@WatchMessagePictureActivity,
                getString(R.string.picture_save_fail)
            )
        }
    }

    private fun isOriginImageHasDownloaded(message: IMMessage): Boolean {
        return message.attachStatus == AttachStatusEnum.transferred &&
                !TextUtils.isEmpty((message.attachment as ImageAttachment).path)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSavePictureEvent(event: SavePictureEvent) {
        savePicture()
    }

    override fun initData() {
        message = intent.getSerializableExtra(INTENT_EXTRA_IMAGE) as IMMessage
        titleBinding.actionbarTitle.text = String.format(
            getString(
                R.string.pic_send_at_time,
                TimeUtil.getDateString(message.getTime())
            )
        )
        titleBinding.actionbarTitle.setTextColor(Color.WHITE)
        titleBinding.actionbarTitle.isInvisible = true
        titleBinding.btnBack.isVisible = false

        binding.apply {
            if (mode == MODE_NORMAL && message.status == MsgStatusEnum.success) {
                simpleImageView.isVisible = false
                  binding.viewPagerImage.isVisible = true
            } else {
                simpleImageView.isVisible = true
                simpleImageView.setOnLongClickListener {
                    if (isOriginImageHasDownloaded(message)) {
                        showWatchPictureAction()
                    }
                    true
                }
                  binding.viewPagerImage.isVisible = false
            }

        }
    }

    override fun start() {
    }

    override fun initView() {
        
    }
}