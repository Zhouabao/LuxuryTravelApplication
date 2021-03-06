package com.sdy.luxurytravelapplication.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.services.core.PoiItem
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.*
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.google.gson.Gson
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityPublishBinding
import com.sdy.luxurytravelapplication.event.AnnounceEvent
import com.sdy.luxurytravelapplication.event.RecordCompleteEvent
import com.sdy.luxurytravelapplication.event.UploadEvent
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.mvp.contract.PublishContract
import com.sdy.luxurytravelapplication.mvp.model.bean.MediaBean
import com.sdy.luxurytravelapplication.mvp.model.bean.MediaParamBean
import com.sdy.luxurytravelapplication.mvp.presenter.PublishPresenter
import com.sdy.luxurytravelapplication.ui.adapter.ChoosePhotosAdapter
import com.sdy.luxurytravelapplication.ui.adapter.EmojAdapter
import com.sdy.luxurytravelapplication.ui.dialog.RecordContentDialog
import com.sdy.luxurytravelapplication.utils.AMapManager
import com.sdy.luxurytravelapplication.utils.RandomUtils
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.widgets.emoj.EmojiSource
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult

/**
 * ????????????
 */
class PublishActivity :
    BaseMvpActivity<PublishContract.View, PublishContract.Presenter, ActivityPublishBinding>(),
    PublishContract.View,
    View.OnClickListener {
    override fun createPresenter(): PublishContract.Presenter = PublishPresenter()
    override fun useEventBus(): Boolean = true

    companion object {
        const val MAX_RECORD_TIME = 60 * 5
        const val MAX_PHOTO_SIZE = 9
        const val REQUSET_PHOTOS = 10004
        const val REQUSET_VIDEO = 10005
        const val REQUEST_CODE_MAP = 30 //????????????
        const val REQUEST_CODE_TITILE = 40 //????????????


        fun startToPublish(activity: Activity) {
            activity.startActivity<PublishActivity>()
        }

        fun startToPublish(activity: Activity, title: String) {
            activity.startActivity<PublishActivity>("title" to title)
        }
    }

    private var pickedPhotos: MutableList<MediaBean> = mutableListOf()
    private val pickedPhotoAdapter by lazy { ChoosePhotosAdapter(1) }//???????????????

    private var recordCompleteEvent: RecordCompleteEvent? = null

    override fun initData() {
        mPresenterDetach = false
        binding.apply {
            barCl.actionbarTitle.text = "????????????"
            barCl.divider.isVisible = true
            barCl.rightTextBtn.isVisible = true
            barCl.rightTextBtn.setTextColor(Color.WHITE)
            barCl.rightTextBtn.setBackgroundResource(R.drawable.selector_button_14dp)

            if (!intent.getStringExtra("title").isNullOrEmpty()) {
                publishTopic.text = intent.getStringExtra("title")
                publishTopic.setTextColor(resources.getColor(R.color.colorAccent))
                publishTopic.setBackgroundResource(R.drawable.shape_c7f3e9_13dp)

                publishTopic.setCompoundDrawablesWithIntrinsicBounds(
                    getDrawable(R.drawable.icon_topic_small),
                    null,
                    getDrawable(R.drawable.icon_delete_green),
                    null
                )
            }
            ClickUtils.applySingleDebouncing(
                arrayOf(
                    barCl.btnBack,
                    barCl.rightTextBtn,
                    publishPicBtn,
                    publishVideoBtn,
                    publishAudioBtn,
                    publishEmojBtn,
                    audioDeleteBtn,
                    binding.publishTopic,
                    binding.publishLocation
                ), this@PublishActivity
            )

            publishContentEt?.setOnFocusChangeListener { v, hasFocus ->
                if (binding.emojRv.isVisible) {
                    binding.publishEmojBtn.setImageResource(R.drawable.icon_publish_emoj)
                    binding.emojRv.isVisible = false
                }
            }


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


            //??????????????????
            initEmojRv()

        }
    }


    override fun start() {

    }


    /***************???????????????********************/
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
            binding.barCl.rightTextBtn -> {//??????
                if (binding.publishContentEt.text.isEmpty()) {
                    ToastUtil.toast("??????????????????")
                    return
                }
                startToUploadAndPublsih()
            }
            binding.publishPicBtn -> {//????????????
                binding.publishContentEt.clearFocus()
                if (pickedPhotos.isNotEmpty()) {
                    if (pickedPhotos[0].fileType == MediaBean.TYPE.VIDEO) {
                        ToastUtil.toast("?????????????????????????????????")
                        return
                    } else if (pickedPhotoAdapter.data.size == MAX_PHOTO_SIZE) {
                        ToastUtil.toast("??????????????????${MAX_PHOTO_SIZE}?????????")
                        return
                    }
                } else if (!(recordCompleteEvent == null || recordCompleteEvent?.filePath.isNullOrEmpty())) {
                    ToastUtil.toast("?????????????????????????????????")
                    return
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
                            CommonFunction.onTakePhoto(
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
            binding.publishVideoBtn -> {//????????????
                binding.publishContentEt.clearFocus()
                if (pickedPhotos.isNotEmpty()) {
                    if (pickedPhotos[0].fileType == MediaBean.TYPE.IMAGE) {
                        ToastUtil.toast("?????????????????????????????????")
                        return
                    } else {
                        ToastUtil.toast("??????????????????????????????")
                        return
                    }
                } else if (!(recordCompleteEvent == null || recordCompleteEvent?.filePath.isNullOrEmpty())) {
                    ToastUtil.toast("?????????????????????????????????")
                    return
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
                            CommonFunction.onTakePhoto(
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
            binding.publishAudioBtn -> {//????????????
                binding.publishContentEt.clearFocus()
                if (pickedPhotos.isNotEmpty() && pickedPhotos[0].fileType == MediaBean.TYPE.IMAGE) {
                    ToastUtil.toast("?????????????????????????????????")
                    return
                }
                if (pickedPhotos.isNotEmpty() && pickedPhotos[0].fileType == MediaBean.TYPE.VIDEO) {
                    ToastUtil.toast("?????????????????????????????????")
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
            binding.publishEmojBtn -> {//??????
                binding.publishContentEt.clearFocus()
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
                // previewAudio????????????
                binding.previewAudio.release()
                recordCompleteEvent = null
            }
            binding.publishTopic -> {
                if (binding.publishTopic.text.isEmpty()) {
                    startActivityForResult<ChooseTitleActivity>(REQUEST_CODE_TITILE)
                } else {
                    binding.publishTopic.text = ""
                    binding.publishTopic.setTextColor(Color.parseColor("#ff8a909f"))
                    binding.publishTopic.setBackgroundResource(R.drawable.shape_background_13dp)
                    binding.publishTopic.setCompoundDrawablesWithIntrinsicBounds(
                        getDrawable(R.drawable.icon_publish_topic),
                        null,
                        null,
                        null
                    )
                }
            }
            binding.publishLocation -> {
                PermissionUtils.permissionGroup(
                    PermissionConstants.LOCATION
                )
                    .rationale { activity, shouldRequest ->
                        shouldRequest.again(true)
                    }
                    .callback { isAllGranted, granted, deniedForever, denied ->
                        if (isAllGranted) {
                            if (binding.publishLocation.text.isEmpty()) {
                                if (binding.emojRv.visibility == View.VISIBLE) {
                                    binding.emojRv.visibility = View.GONE
                                }
                                startActivityForResult<LocationActivity>(REQUEST_CODE_MAP)
                            } else {
                                binding.publishLocation.text = ""
                                binding.publishLocation.setTextColor(Color.parseColor("#ff8a909f"))
                                binding.publishLocation.setBackgroundResource(R.drawable.shape_background_13dp)

                                binding.publishLocation.setCompoundDrawablesWithIntrinsicBounds(
                                    getDrawable(R.drawable.icon_publish_location),
                                    null,
                                    null,
                                    null
                                )
                            }
                        } else {
                            ToastUtil.toast(getString(R.string.permission_location))
                        }
                    }
                    .request()


            }
        }

    }

    private var positionItem: PoiItem? = null

    override fun onResume() {
        super.onResume()
        AMapManager.initLocation(this)

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
                REQUEST_CODE_TITILE -> {
                    binding.publishTopic.text = ""
                    if (data?.getStringExtra("title") != null) {
                        binding.publishTopic.text = data.getStringExtra("title") ?: ""
                        binding.publishTopic.setTextColor(resources.getColor(R.color.colorAccent))
                        binding.publishTopic.setBackgroundResource(R.drawable.shape_c7f3e9_13dp)

                        binding.publishTopic.setCompoundDrawablesWithIntrinsicBounds(
                            getDrawable(R.drawable.icon_topic_small),
                            null,
                            getDrawable(R.drawable.icon_delete_green),
                            null
                        )
                    }
                }
                REQUEST_CODE_MAP -> {
                    if (data?.getParcelableExtra<PoiItem>("poiItem") != null) {
                        positionItem = data.getParcelableExtra<PoiItem>("poiItem")
                        binding.publishLocation.text =
                            (positionItem!!.cityName
                                ?: "") + if (!positionItem!!.cityName.isNullOrEmpty()) {
                                "??"
                            } else {
                                ""
                            } + positionItem!!.title
//                    +  + (positionItem!!.adName ?: "") + (positionItem!!.businessArea ?: "") + (positionItem!!.snippet ?: "")

                        binding.publishLocation.ellipsize = TextUtils.TruncateAt.MARQUEE
                        binding.publishLocation.maxLines = 1
                        binding.publishLocation.isSelected = true
                        binding.publishLocation.isFocusable = true
                        binding.publishLocation.isFocusableInTouchMode = true

                        binding.publishLocation.setTextColor(resources.getColor(R.color.colorAccent))
                        binding.publishLocation.setBackgroundResource(R.drawable.shape_c7f3e9_13dp)

                        binding.publishLocation.setCompoundDrawablesWithIntrinsicBounds(
                            getDrawable(R.drawable.icon_location),
                            null,
                            getDrawable(R.drawable.icon_delete_green),
                            null
                        )
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


    /***************?????????????????????******************/

    private fun startToUploadAndPublsih() {
//        if (!NetworkUtils.isAvailable()) {
//            ToastUtil.toast(getString(R.string.open_internet))
//            return
//        }

        val type =
            if (pickedPhotos.isNullOrEmpty() && (recordCompleteEvent == null || recordCompleteEvent?.filePath.isNullOrEmpty())) {
                0
            } else if (recordCompleteEvent != null && recordCompleteEvent!!.filePath.isNotEmpty()) {
                3
            } else if (pickedPhotos.isNotEmpty() && pickedPhotos.size > 0 && pickedPhotos[0].fileType == MediaBean.TYPE.IMAGE) {
                1
            } else {
                2
            }

        val titles = mutableListOf<String>()
        if (binding.publishTopic.text.toString().isNotEmpty()) {
            titles.add(binding.publishTopic.text.toString())
        }
        val param = hashMapOf<String, Any>(
            "token" to UserManager.token,
            "accid" to UserManager.accid,
            "descr" to "${binding.publishContentEt.text}",
            "lat" to if (positionItem == null) {
                UserManager.latitude
            } else {
                positionItem!!.latLonPoint?.latitude ?: 0.0
            },
            "lng" to if (positionItem == null) {
                UserManager.longtitude
            } else {
                positionItem!!.latLonPoint?.longitude ?: 0.0
            },
            "province_name" to if (positionItem == null) {
                UserManager.province
            } else {
                positionItem!!.provinceName ?: ""
            },
            "city_name" to if (positionItem == null) {
                UserManager.city
            } else {
                positionItem!!.cityName ?: ""
            },
            "puber_address" to if (binding.publishLocation.text.toString() == getString(R.string.dont_show_location)) {
                ""
            } else {
                binding.publishLocation.text.toString()
            },
            //?????????????????????0,???????????? 1????????? 2????????? 3?????????
            "type" to type,
            "titles" to Gson().toJson(titles)
        )


        UserManager.publishParams = param
        UserManager.publishState = 1

        if (pickedPhotos.isNullOrEmpty() && (recordCompleteEvent == null || recordCompleteEvent?.filePath.isNullOrEmpty())) {//??????
            publish()
        } else if (!(recordCompleteEvent == null || recordCompleteEvent?.filePath.isNullOrEmpty())) {//??????
            //??????????????????
            UserManager.mediaBeans.add(
                MediaParamBean(
                    url = recordCompleteEvent!!.filePath,
                    duration = recordCompleteEvent!!.duration
                )
            )
            //TODO????????????
            val audioQnPath =
                "${Constants.FILE_NAME_INDEX}${Constants.PUBLISH}${SPUtils.getInstance(Constants.SPNAME)
                    .getString(
                        "accid"
                    )}/${System.currentTimeMillis()}/${RandomUtils.getRandomString(
                    16
                )}"
            EventBus.getDefault().postSticky(UploadEvent(1, 1, 0.0))
            mPresenter?.uploadFile(1, 1, recordCompleteEvent!!.filePath, audioQnPath, 3)
        } else if (pickedPhotos.isNotEmpty() && pickedPhotos.size > 0 && pickedPhotos[0].fileType == MediaBean.TYPE.IMAGE) { //??????
            for (photo in pickedPhotos) {
                UserManager.mediaBeans.add(
                    MediaParamBean(
                        url = photo.filePath.toString(),
                        width = photo.width,
                        height = photo.height
                    )
                )
            }
            EventBus.getDefault().postSticky(UploadEvent(pickedPhotos.size, uploadCount + 1, 0.0))
            uploadPictures()
        } else {//??????
            //??????????????????
            UserManager.mediaBeans.add(
                MediaParamBean(
                    url = pickedPhotos[0].filePath.toString(),
                    duration = pickedPhotos[0].duration,
                    width = pickedPhotos[0].width,
                    height = pickedPhotos[0].height
                )
            )
            //TODO????????????
            val videoQnPath =
                "${Constants.FILE_NAME_INDEX}${Constants.PUBLISH}${SPUtils.getInstance(Constants.SPNAME)
                    .getString(
                        "accid"
                    )}/${System.currentTimeMillis()}/${RandomUtils.getRandomString(
                    16
                )}"
            EventBus.getDefault().postSticky(UploadEvent(1, 1, 0.0))
            mPresenter?.uploadFile(1, 1, pickedPhotos[0].filePath.toString(), videoQnPath, 2)
        }
        finish()
    }


    //msg.what  0?????????????????????????????????    1???????????????????????????     2??????????????????  3??????????????????????????????
    private var uploadCount = 0

    /**
     * ?????????????????????
     */
    private val keyList: MutableList<String> = mutableListOf()

    /**
     * ???????????????
     */
    private fun publish() {
        // ????????????????????????????????????
        val type =
            if (pickedPhotos.isNullOrEmpty() && (recordCompleteEvent == null || recordCompleteEvent?.filePath.isNullOrEmpty())) {
                0
            } else if (!(recordCompleteEvent == null || recordCompleteEvent?.filePath.isNullOrEmpty())) {
                3
            } else if (pickedPhotos.isNotEmpty() && pickedPhotos.size > 0 && pickedPhotos[0].fileType == MediaBean.TYPE.IMAGE) {
                1
            } else {
                2
            }

        val titles = mutableListOf<String>()
        if (binding.publishTopic.text.toString().isNotEmpty()) {
            titles.add(binding.publishTopic.text.toString())
        }
        val param = hashMapOf<String, Any>(
            "descr" to "${binding.publishContentEt.text}",
            "lat" to if (positionItem == null) {
                UserManager.latitude
            } else {
                positionItem!!.latLonPoint?.latitude ?: 0.0
            },
            "lng" to if (positionItem == null) {
                UserManager.latitude
            } else {
                positionItem!!.latLonPoint?.longitude ?: 0.0
            },
            "province_name" to if (positionItem == null) {
                UserManager.province
            } else {
                positionItem!!.provinceName ?: ""
            },
            "city_name" to if (positionItem == null) {
                UserManager.city
            } else {
                positionItem!!.cityName ?: ""
            },
            "puber_address" to if (binding.publishLocation.text.toString() == getString(R.string.dont_show_location)) {
                ""
            } else {
                binding.publishLocation.text.toString()
            },
            //?????????????????????0,???????????? 1????????? 2????????? 3?????????
            "type" to type,
            "titles" to Gson().toJson(titles)
        )

        UserManager.publishParams = param

        mPresenter?.publishContent(
            type, UserManager.publishParams, keyList = keyList
        )
    }


    private fun uploadPictures() {
        //????????????
        val imagePath =
            "${Constants.FILE_NAME_INDEX}${Constants.PUBLISH}${SPUtils.getInstance(Constants.SPNAME)
                .getString(
                    "accid"
                )}/${System.currentTimeMillis()}/${RandomUtils.getRandomString(
                16
            )}"
        Log.d("uploadPictures", "${imagePath}")
        mPresenter?.uploadFile(
            pickedPhotos.size,
            uploadCount + 1,
            pickedPhotos[uploadCount].filePath.toString(),
            imagePath,
            1
        )
    }


    /**
     * ????????????????????????
     *   //?????????????????????0,???????????? 1????????? 2????????? 3?????????
     */
    override fun onSquareAnnounceResult(type: Int, success: Boolean, code: Int) {
        EventBus.getDefault().postSticky(AnnounceEvent(success, code))

        if (success) {
            if (intent.getIntExtra("from", 1) == 2) {
                EventBus.getDefault()
                    .postSticky(UploadEvent(1, 1, 1.0, from = UploadEvent.FROM_USERCENTER))
            }
            if (!this.isFinishing) {
                finish()
            }
            mPresenter?.detachView()
            this.mPresenter = null
        }

    }

    //?????????????????????0,???????????? 1????????? 2????????? 3?????????
    override fun onQnUploadResult(success: Boolean, type: Int, key: String) {
        if (success) {
            when (type) {
                0 -> {
                    publish()
                }
                1 -> {
                    keyList.add(
                        uploadCount,
                        Gson().toJson(
                            MediaParamBean(
                                key,
                                pickedPhotos[uploadCount].duration,
                                pickedPhotos[uploadCount].width,
                                pickedPhotos[uploadCount].height
                            )
                        )
                    )
                    uploadCount++
                    if (uploadCount == pickedPhotos.size) {
                        publish()
                    } else {
                        uploadPictures()
                    }
                }
                2 -> {
                    keyList.add(
                        uploadCount,
                        Gson().toJson(
                            MediaParamBean(
                                key,
                                pickedPhotos[uploadCount].duration,
                                pickedPhotos[uploadCount].width,
                                pickedPhotos[uploadCount].height
                            )
                        )
                    )
                    publish()
                }
                3 -> {
                    keyList.add(
                        uploadCount,
                        Gson().toJson(MediaParamBean(key, recordCompleteEvent!!.duration, 0, 0))
                    )
                    publish()
                }
            }
        } else {
            EventBus.getDefault().postSticky(UploadEvent(qnSuccess = false))
        }

    }

    override fun onCheckBlockResult(result: Boolean) {

    }

    override fun onDestroy() {
        super.onDestroy()
        AMapManager.destory()
    }
}