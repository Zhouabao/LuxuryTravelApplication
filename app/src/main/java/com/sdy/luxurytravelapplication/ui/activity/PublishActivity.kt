package com.sdy.luxurytravelapplication.ui.activity

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.*
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.databinding.ActivityPublishBinding
import com.sdy.luxurytravelapplication.event.RecordCompleteEvent
import com.sdy.luxurytravelapplication.ext.onTakePhoto
import com.sdy.luxurytravelapplication.mvp.contract.PublishContract
import com.sdy.luxurytravelapplication.mvp.model.bean.MediaBean
import com.sdy.luxurytravelapplication.mvp.presenter.PublishPresenter
import com.sdy.luxurytravelapplication.ui.adapter.ChoosePhotosAdapter
import com.sdy.luxurytravelapplication.ui.adapter.EmojAdapter
import com.sdy.luxurytravelapplication.ui.dialog.RecordContentDialog
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.widgets.emoj.EmojiSource
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivity

/**
 * 发布动态
 */
class PublishActivity :
    BaseMvpActivity<PublishContract.View, PublishContract.Presenter, ActivityPublishBinding>(),
    View.OnClickListener {
    override fun createPresenter(): PublishContract.Presenter = PublishPresenter()
    override fun useEventBus(): Boolean = true

    companion object {
        const val MAX_RECORD_TIME = 60 * 5
        const val MAX_PHOTO_SIZE = 9
        const val REQUSET_PHOTOS = 10004
        const val REQUSET_VIDEO = 10005


        fun start(context: Context) {
            context.startActivity<PublishActivity>()

        }
    }

    //https://www.jb51.net/article/128982.htm
    private var keyboardHeight = 0F

    private var pickedPhotos: MutableList<MediaBean> = mutableListOf()
    private val pickedPhotoAdapter by lazy { ChoosePhotosAdapter(1) }//选中的封面

    private var recordCompleteEvent: RecordCompleteEvent? = null

    override fun initData() {
        binding.apply {
            barCl.actionbarTitle.text = "动态发布"
            barCl.divider.isVisible = true
            barCl.rightTextBtn.isVisible = true
            barCl.rightTextBtn.setTextColor(Color.WHITE)
            barCl.rightTextBtn.setBackgroundResource(R.drawable.selector_button_14dp)

            ClickUtils.applySingleDebouncing(
                arrayOf(
                    barCl.btnBack,
                    barCl.rightTextBtn,
                    publishPicBtn,
                    publishVideoBtn,
                    publishAudioBtn,
                    publishEmojBtn,
                    audioDeleteBtn
                ), this@PublishActivity
            )

            previewResourceRv.layoutManager =
                LinearLayoutManager(this@PublishActivity, RecyclerView.HORIZONTAL, false)
            pickedPhotoAdapter.draggableModule.isDragEnabled = true
            pickedPhotoAdapter.draggableModule.setOnItemDragListener(object : OnItemDragListener {
                override fun onItemDragMoving(
                    source: RecyclerView.ViewHolder?,
                    from: Int,
                    target: RecyclerView.ViewHolder?,
                    to: Int
                ) {


                }

                override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                    ToastUtil.toast("start=$pos")
                }

                override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                    ToastUtil.toast("end=$pos")
                    pickedPhotoAdapter.notifyDataSetChanged()
                }

            })

            pickedPhotoAdapter.addChildClickViewIds(R.id.choosePhotoDel, R.id.choosePhoto)
            previewResourceRv.adapter = pickedPhotoAdapter
            pickedPhotoAdapter.setNewInstance(pickedPhotos)
            pickedPhotoAdapter.setOnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.choosePhotoDel -> {
//                        pickedPhotos.remove(pickedPhotoAdapter.data[position])
//                        pickedPhotoAdapter.notifyItemChanged(position)
                        pickedPhotoAdapter.removeAt(position)
                        if (pickedPhotoAdapter.data.isEmpty()) {
                            previewResourceRv.isVisible = false
                        }
                    }
                }
            }


            //初始化表情包
            initEmojRv()

            KeyboardUtils.registerSoftInputChangedListener(this@PublishActivity) {
                LogUtils.d("===================$it")
                if (!binding.emojRv.isVisible) {
                    if (it > 0) {
                        keyboardHeight = it.toFloat()
                        val animator =
                            ObjectAnimator.ofFloat(
                                publishBottomCl,
                                "translationY",
                                0F,
                                -it.toFloat()
                            )
                        animator.duration = 200
                        animator.start()
                    } else {
                        val animator =
                            ObjectAnimator.ofFloat(
                                publishBottomCl,
                                "translationY",
                                -keyboardHeight,
                                0F
                            )
                        animator.duration = 200
                        animator.start()
                    }
                } else {
                    if (it > 0) {
                        val animator0 =
                            ObjectAnimator.ofFloat(
                                publishBottomCl,
                                "translationY",
                                -keyboardHeight,
                                0F
                            )
                        animator0.duration = 200
                        animator0.start()
                        val animator =
                            ObjectAnimator.ofFloat(
                                publishBottomCl,
                                "translationY",
                                0F,
                                -SizeUtils.dp2px(35F).toFloat()
                            )
                        animator.duration = 200
                        animator.start()
                    } else {
                        val animator =
                            ObjectAnimator.ofFloat(
                                publishBottomCl,
                                "translationY",
                                -SizeUtils.dp2px(235F).toFloat(),
                                0F
                            )
                        animator.duration = 200
                        animator.start()
                    }
                }
            }

        }
    }


    override fun start() {

    }


    /***************设置表情包********************/
    private val emojAdapter by lazy { EmojAdapter() }

    private fun initEmojRv() {
        binding.emojRv.layoutManager = GridLayoutManager(this, 8, RecyclerView.VERTICAL, false)
        binding.emojRv.adapter = emojAdapter

        emojAdapter.addData(EmojiSource.people.toMutableList())
        emojAdapter.addData(EmojiSource.objects.toMutableList())
        emojAdapter.addData(EmojiSource.nature.toMutableList())
        emojAdapter.addData(EmojiSource.places.toMutableList())
        emojAdapter.addData(EmojiSource.symbol.toMutableList())
        emojAdapter.setOnItemClickListener { _, view, position ->
            binding.publishContentEt.append(emojAdapter.data[position])
            binding.publishContentEt.setSelection(binding.publishContentEt.length())
            checkConfirmEnable()
        }

    }

    private fun checkConfirmEnable() {
        binding.barCl.rightTextBtn.isEnabled = binding.publishContentEt.text.isNotEmpty()

    }


    override fun onClick(v: View) {
        when (v) {
            binding.barCl.btnBack -> {
                finish()
            }
            binding.barCl.rightTextBtn -> {//发布
            }
            binding.publishPicBtn -> {//图片选择
                if (pickedPhotos.isNotEmpty()) {
                    if (pickedPhotos[0].fileType == MediaBean.TYPE.VIDEO) {
                        ToastUtil.toast("图片和视频不能同时选择")
                        return
                    } else if (pickedPhotoAdapter.data.size == MAX_PHOTO_SIZE) {
                        ToastUtil.toast("最多只能选择${MAX_PHOTO_SIZE}张图片")
                        return
                    }
                }
                PermissionUtils.permissionGroup(
                    PermissionConstants.STORAGE,
                    PermissionConstants.CAMERA
                )
                    .rationale { activity, shouldRequest ->
                        shouldRequest.again(true)
                    }
                    .callback { isAllGranted, granted, deniedForever, denied ->
                        if (isAllGranted) {
                            onTakePhoto(
                                this,
                                MAX_PHOTO_SIZE - pickedPhotoAdapter.data.size,
                                REQUSET_PHOTOS,
                                compress = true
                            )
                        } else {
                            ToastUtil.toast(getString(R.string.permission_storage))
                        }
                    }
                    .request()

            }
            binding.publishVideoBtn -> {//视频选择
                if (pickedPhotos.isNotEmpty()) {
                    if (pickedPhotos[0].fileType == MediaBean.TYPE.IMAGE) {
                        ToastUtil.toast("视频和图片不能同时选择")
                        return
                    } else {
                        ToastUtil.toast("最多只能选择一个视频")
                        return
                    }
                }
                PermissionUtils.permissionGroup(
                    PermissionConstants.STORAGE,
                    PermissionConstants.CAMERA
                )
                    .rationale { activity, shouldRequest ->
                        shouldRequest.again(true)
                    }
                    .callback { isAllGranted, granted, deniedForever, denied ->
                        if (isAllGranted) {
                            onTakePhoto(
                                this,
                                1 - pickedPhotoAdapter.data.size,
                                REQUSET_VIDEO,
                                PictureMimeType.ofVideo(),
                                compress = true
                            )
                        } else {
                            ToastUtil.toast(getString(R.string.permission_storage))
                        }
                    }
                    .request()

            }
            binding.publishAudioBtn -> {//语音选择
                if (pickedPhotos.isNotEmpty() && pickedPhotos[0].fileType == MediaBean.TYPE.IMAGE) {
                    ToastUtil.toast("语音和照片不能同时选择")
                    return
                }
                if (pickedPhotos.isNotEmpty() && pickedPhotos[0].fileType == MediaBean.TYPE.VIDEO) {
                    ToastUtil.toast("语音和视频不能同时选择")
                    return
                }

                PermissionUtils.permissionGroup(PermissionConstants.MICROPHONE)
                    .callback { isAllGranted, granted, deniedForever, denied ->
                        if (isAllGranted) {
                            RecordContentDialog().show()
                        } else {
                            ToastUtil.toast(getString(R.string.permission_record_rejected))
                        }
                    }
                    .request()

            }
            binding.publishEmojBtn -> {//表情
                binding.emojRv.isVisible = !binding.emojRv.isVisible
                KeyboardUtils.hideSoftInput(this)
                if (binding.emojRv.isVisible) {
                    binding.publishEmojBtn.setImageResource(R.drawable.icon_publish_emoj_selected)
                } else {
                    binding.publishEmojBtn.setImageResource(R.drawable.icon_publish_emoj)
                }

            }
            binding.audioDeleteBtn -> {
                binding.previewAudioLl.visibility = View.GONE
                // previewAudio停止播放
                binding.previewAudio.release()
                recordCompleteEvent = null
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUSET_PHOTOS -> {
                    val data = PictureSelector.obtainMultipleResult(data)
                    if (!data.isNullOrEmpty()) {
                        data.forEach {
                            val mediaBean = MediaBean(
                                it.id.toInt(),
                                MediaBean.TYPE.IMAGE,
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                                    it.androidQToPath
                                } else {
                                    it.path
                                },
                                FileUtils.getFileName(
                                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                                        it.androidQToPath
                                    } else {
                                        it.path
                                    }
                                ),
                                "", 0, it.size, true, it.width, it.height
                            )
                            binding.previewResourceRv.isVisible = true
                            pickedPhotoAdapter.addData(0, mediaBean)
                        }
                    }
                }
                REQUSET_VIDEO -> {
                    val data = PictureSelector.obtainMultipleResult(data)
                    if (!data.isNullOrEmpty()) {
                        val mediaBean = MediaBean(
                            data[0].id.toInt(),
                            MediaBean.TYPE.VIDEO,
                            when {
                                data[0].isCompressed -> {
                                    data[0].compressPath
                                }
                                Build.VERSION.SDK_INT > Build.VERSION_CODES.P -> {

                                    data[0].androidQToPath
                                }
                                else -> {
                                    data[0].path
                                }
                            },
                            FileUtils.getFileName(
                                when {
                                    data[0].isCompressed -> {
                                        data[0].compressPath
                                    }
                                    Build.VERSION.SDK_INT > Build.VERSION_CODES.P -> {
                                        data[0].androidQToPath
                                    }
                                    else -> {
                                        data[0].path
                                    }
                                }
                            ),
                            "",
                            data[0].duration.toInt(),
                            data[0].size,
                            true,
                            data[0].width,
                            data[0].height
                        )
                        binding.previewResourceRv.isVisible = true
                        pickedPhotoAdapter.addData(0, mediaBean)
                    }

                }
            }

        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordCompleteEvent(event: RecordCompleteEvent) {
        recordCompleteEvent = event
        binding.apply {
            previewAudioLl.isVisible = true
            previewAudio.prepareAudio(event.filePath, event.duration, 0)
        }
    }


}