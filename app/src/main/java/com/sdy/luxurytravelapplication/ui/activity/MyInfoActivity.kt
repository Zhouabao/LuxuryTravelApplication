package com.sdy.luxurytravelapplication.ui.activity

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.*
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.google.gson.Gson
import com.kongzue.dialog.util.BaseDialog
import com.kongzue.dialog.v3.BottomMenu
import com.kongzue.dialog.v3.MessageDialog
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.sdy.luxurytravelapplication.R
import com.sdy.luxurytravelapplication.base.BaseMvpActivity
import com.sdy.luxurytravelapplication.constant.Constants
import com.sdy.luxurytravelapplication.constant.UserManager
import com.sdy.luxurytravelapplication.databinding.ActivityMyInfoBinding
import com.sdy.luxurytravelapplication.databinding.ItemMoreInfoBinding
import com.sdy.luxurytravelapplication.event.AccountDangerEvent
import com.sdy.luxurytravelapplication.event.UserCenterEvent
import com.sdy.luxurytravelapplication.ext.CommonFunction
import com.sdy.luxurytravelapplication.liveface.FaceLivenessExpActivity
import com.sdy.luxurytravelapplication.mvp.contract.MyInfoContract
import com.sdy.luxurytravelapplication.mvp.model.bean.FindTagBean
import com.sdy.luxurytravelapplication.mvp.model.bean.MyPhotoBean
import com.sdy.luxurytravelapplication.mvp.model.bean.UserInfoSettingBean
import com.sdy.luxurytravelapplication.mvp.presenter.MyInfoPresenter
import com.sdy.luxurytravelapplication.ui.adapter.MoreInfoAdapter
import com.sdy.luxurytravelapplication.ui.adapter.UserPhotoAdapter
import com.sdy.luxurytravelapplication.ui.dialog.AccountDangerDialog
import com.sdy.luxurytravelapplication.utils.RandomUtils
import com.sdy.luxurytravelapplication.utils.ToastUtil
import com.sdy.luxurytravelapplication.widgets.DividerItemDecoration
import com.sdy.luxurytravelapplication.widgets.OnRecyclerItemClickListener
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import java.text.SimpleDateFormat
import java.util.*

/**
 * 个人信息修改界面
 */
class MyInfoActivity :
    BaseMvpActivity<MyInfoContract.View, MyInfoContract.Presenter, ActivityMyInfoBinding>(),
    MyInfoContract.View, View.OnClickListener {

    companion object {
        const val IMAGE_SIZE = 9
        const val REPLACE_REQUEST = 187
        private const val MSG_LOAD_SUCCESS = 100
        private const val MSG_LOAD_FAILED = -100
        private const val MSG_LOAD_DATA = 1
    }

    private var isChange = false
    private var photos: MutableList<MyPhotoBean?> = mutableListOf()
    private var originalPhotos: MutableList<MyPhotoBean?> = mutableListOf()//用于对比用户是否改变过相册信息
    private val adapter by lazy { UserPhotoAdapter() }
    private val moreInfoAdapter by lazy { MoreInfoAdapter() }
    private var data: UserInfoSettingBean? = null
    override fun createPresenter(): MyInfoContract.Presenter {
        return MyInfoPresenter()
    }

    override fun initData() {
        binding.apply {
            barCl.actionbarTitle.text = "个人资料"
            barCl.rightTextBtn.isVisible = true
            barCl.rightTextBtn.text = "保存"
            barCl.rightTextBtn.setTextColor(Color.WHITE)
            barCl.rightTextBtn.setBackgroundResource(R.drawable.selector_button_14dp)
            ClickUtils.applySingleDebouncing(
                arrayOf(
                    barCl.btnBack,
                    userNickName,
                    userBirth,
                    userNickSign,
                    barCl.rightTextBtn,
                    userContact
                ), this@MyInfoActivity
            )


            //更多信息
            rvMoreInfo.layoutManager =
                LinearLayoutManager(this@MyInfoActivity, RecyclerView.VERTICAL, false)
            rvMoreInfo.adapter = moreInfoAdapter
            moreInfoAdapter.setOnItemClickListener { _, view, position ->
                val binding = ItemMoreInfoBinding.bind(view)
                val item = moreInfoAdapter.data[position]
                showConditionPicker(
                    binding.moreInfoContent,
                    item.title,
                    item.id.toString(),
                    item.find_tag,
                    if (item.child.isEmpty()) {
                        val heights = mutableListOf<FindTagBean>()
                        for (i in 60 until 250) {
                            heights.add(FindTagBean(-1, "${i}cm"))
                        }
                        heights
                    } else {
                        item.child
                    }, position
                )
            }


            userPhotosRv.layoutManager =
                GridLayoutManager(this@MyInfoActivity, 3, RecyclerView.VERTICAL, false)
            userPhotosRv.addItemDecoration(
                DividerItemDecoration(
                    this@MyInfoActivity,
                    DividerItemDecoration.BOTH_SET,
                    SizeUtils.dp2px(15F),
                    resources.getColor(R.color.colorWhite)
                )
            )
            userPhotosRv.adapter = adapter
            //开启拖拽
            adapter.draggableModule.isDragEnabled = true
            adapter.draggableModule.isDragOnLongPressEnabled = true
            adapter.draggableModule.setOnItemDragListener(object : OnItemDragListener {
                override fun onItemDragMoving(
                    source: RecyclerView.ViewHolder?,
                    from: Int,
                    target: RecyclerView.ViewHolder?,
                    to: Int
                ) {

                }

                override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                    if (pos == adapter.data.size - 1) {
                        return
                    }
                }

                override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                    val photos = arrayListOf<Int>()
                    for (data in adapter.data) {
                        if (data.itemType == MyPhotoBean.PHOTO) {
                            photos.add(data.id)
                        }
                    }
                    adapter.notifyDataSetChanged()
//                    mPresenter.savePersonalInfo(PARAM_PHOTOS, Gson().toJson(photos))
                }

            })
            userPhotosRv.addOnItemTouchListener(object : OnRecyclerItemClickListener(userPhotosRv) {
                override fun onItemLongClick(holder: RecyclerView.ViewHolder) {
                    if (adapter.data[holder.layoutPosition].itemType == MyPhotoBean.COVER) {
                        adapter.draggableModule.isDragEnabled = false
                    } else {
                        adapter.draggableModule.isDragEnabled = true
                        adapter.draggableModule.itemTouchHelper.startDrag(holder)
                    }
                }

                override fun onItemClick(vh: RecyclerView.ViewHolder) {
                    val position = vh.layoutPosition
                    if (adapter.data[position].itemType == MyPhotoBean.COVER) {
                        if (adapter.data.size == IMAGE_SIZE + 1) {
                            ToastUtil.toast(getString(R.string.nine_most))
                            return
                        }
                        PermissionUtils.permission(PermissionConstants.CAMERA)
                            .callback(object : PermissionUtils.SimpleCallback {
                                override fun onGranted() {
                                    CommonFunction.onTakePhoto(
                                        this@MyInfoActivity,
                                        1,
                                        PictureConfig.CHOOSE_REQUEST,
                                        PictureMimeType.ofImage(),
                                        compress = true
                                    )
                                }

                                override fun onDenied() {
                                    ToastUtil.toast(getString(R.string.permission_storage))
                                }

                            })
                            .request()
                    } else {
                        if (adapter.data.size > 1) {
                            showDeleteDialog(position)
                        }
                    }
                }
            })


        }
    }

    override fun start() {
        mPresenter?.personalInfo()
    }


    private fun showDeleteDialog(position: Int) {
        val datas = if (position == 0) {
            arrayOf(getString(R.string.replace_avatar), "取消")
        } else {
            arrayOf(getString(R.string.set_avatar), "删除", "取消")
        }
        BottomMenu.show(this, datas) { text, index ->
            if (position == 0) {
                PermissionUtils.permissionGroup(
                    PermissionConstants.CAMERA,
                    PermissionConstants.STORAGE
                )
                    .callback(object : PermissionUtils.SimpleCallback {
                        override fun onGranted() {
                            CommonFunction.onTakePhoto(
                                this@MyInfoActivity,
                                1,
                                REPLACE_REQUEST,
                                PictureMimeType.ofImage(), cropEnable = true
                            )

                        }

                        override fun onDenied() {
                            ToastUtil.toast(getString(R.string.permission_storage))
                        }

                    })
                    .request()
            } else {
                when (index) {
                    0 -> {
                        Collections.swap(photos, position, 0)
                        Collections.swap(adapter.data, position, 0)
                        adapter.notifyDataSetChanged()
                        isChange = true
                        checkSaveEnable()
                    }
                    1 -> {
                        isChange = true
                        checkSaveEnable()
                        adapter.data.removeAt(position)
                        photos.removeAt(position)
                        adapter.notifyDataSetChanged()
                        setScroeProgress()
                    }
                    2 -> {
                    }
                }

            }
        }
            .setAlign(BaseDialog.ALIGN.BOTTOM)
            .setShowCancelButton(false)

    }


    override fun onPersonalInfoResult(data: UserInfoSettingBean) {
        setData(data)
        if (intent.getBooleanExtra("showToast", false)) {
            ToastUtil.toast(getString(R.string.click_first_to_replace_avatar))
        }
    }

    private fun setData(data: UserInfoSettingBean) {
        binding.apply {

            this@MyInfoActivity.data = data
            //更新本地缓存
            UserManager.nickname = data.nickname
            UserManager.gender = data.gender
            UserManager.avatar = data.avatar

            userNickName.text = data.nickname
            userGender.text = if (data.gender == 1) {
                getString(R.string.gender_man)
            } else {
                getString(R.string.gender_woman)
            }
            userBirth.text = "${data.birth}/${data.constellation}"


            //	新增字段 认证状态 0 未认证且无视频 1 认证通过的 2 认证中 3认证不通过-需要更换头像认证
            //verifyNotice.isVisible = data.mv_faced == 3
            //updateVerifyState(data.mv_faced)


            moreInfoAdapter.setNewInstance(data.answer_list)


            for (photoWallBean in data.photos_wall.withIndex()) {
                photoWallBean.value.itemType = MyPhotoBean.PHOTO
                if (photoWallBean.index == 0) {
                    originalAvator = photoWallBean.value.url
                    if (UserManager.isNeedChangeAvator() && !UserManager.avatar.contains(
                            photoWallBean.value.url
                        )
                    ) {
                        isChange = true
                        checkSaveEnable()
                    }
                }
            }

            adapter.setNewInstance(data.photos_wall)
            photos.addAll(data.photos_wall)
            originalPhotos.addAll(data.photos_wall)
            adapter.addData(
                MyPhotoBean(
                    itemType = MyPhotoBean.COVER,
                    photoScore = data.score_rule?.photo ?: 0
                )
            )

            setScroeProgress()
        }
    }


    /**
     * type  1 个人信息 2 头像
     */
    override fun onSavePersonalResult(result: Boolean, type: Int, from: Int) {
        if (result) {
            isChange = false
            checkSaveEnable()
            EventBus.getDefault().post(UserCenterEvent(true))
            if (from == 1) {
                setResult(Activity.RESULT_OK)
                super.onBackPressed()
            }
        } else {
            isChange = true
            checkSaveEnable()
        }
    }

    private fun checkSaveEnable() {

        binding.barCl.rightTextBtn.isEnabled = isChange
    }

    override fun onAddPhotoWallResult(replaceAvator: Boolean, result: MyPhotoBean) {
        isChange = true
        checkSaveEnable()
        result.itemType = MyPhotoBean.PHOTO
        if (replaceAvator) {
            adapter.setData(0, result)
            photos[0] = result
        } else {
            adapter.data.add(adapter.data.size - 1, result)
            photos.add(result)
            adapter.notifyDataSetChanged()
            setScroeProgress()
        }
    }

    override fun uploadImgResult(b: Boolean, key: String, replaceAvator: Boolean) {
        if (b) {
            mPresenter?.addPhotoWall(replaceAvator, key)
        }
    }

    private var originalAvator = ""
    private fun checkIsForceChangeAvator() {
        if (adapter.data.isNullOrEmpty()) {
            ToastUtil.toast(getString(R.string.least_upload_count))
            return
        }


        //如果已经换了头像,并且要求强制替换头像
        Log.d("OKhttp", "${UserManager.avatar.contains(adapter.data[0].url)}")
        if (adapter.data.isNotEmpty() && !UserManager.avatar
                .contains(Constants.DEFAULT_EMPTY_AVATAR)
            && !UserManager.avatar
                .contains(adapter.data[0].url) && UserManager.isNeedChangeAvator()
        ) {
            UserManager.saveForceChangeAvator(true)
        }


        //如果修改了信息 更新本地筛选信息
        if (SPUtils.getInstance(Constants.SPNAME).getInt("audit_only", -1) != -1) {
            if (!UserManager.avatar.contains(adapter.data[0].url)) {
                UserManager.isverify = 2
                SPUtils.getInstance(Constants.SPNAME).remove("audit_only")
            }
        }

        //如果更改过相册信息并且没有是强制替换头像,就新增
        if (isChange && !UserManager.isNeedChangeAvator()) {
            MessageDialog.show(
                this as AppCompatActivity,
                getString(R.string.save_content),
                getString(R.string.is_save_content),
                getString(R.string.give_up),
                getString(R.string.save)
            )
                .setOnCancelButtonClickListener { _, v ->
                    setResult(Activity.RESULT_OK)
                    finish()
                    false
                }
                .setOnOkButtonClickListener { _, v ->
                    updatePhotos(1)
                    false
                }
        } else {
            setResult(Activity.RESULT_OK)
            finish()
            if (adapter.data.isNotEmpty() && !UserManager.avatar
                    .contains(Constants.DEFAULT_EMPTY_AVATAR)
                && !UserManager.avatar.contains(adapter.data[0].url)
                && (UserManager.getAccountDanger() || UserManager.getAccountDangerAvatorNotPass())
            ) { //账号异常
                UserManager.isverify = 2
                EventBus.getDefault().postSticky(AccountDangerEvent(AccountDangerDialog.VERIFY_ING))
            }
        }


    }


    /**
     * type 为1表示点击返回上传信息
     */
    private fun updatePhotos(type: Int = 0) {
        if (photos.isNullOrEmpty()) {
            ToastUtil.toast(getString(R.string.least_upload_count))
            return
        }


        var hasChangePhotos = false
        if (originalPhotos.size != photos.size) {
            hasChangePhotos = true
        } else {
            for (i in 0 until photos.size) {
                if (originalPhotos[i]?.id != photos[i]?.id) {
                    hasChangePhotos = true
                    break
                }
            }
        }

        if (moreInfoAdapter.params.isNotEmpty()) {
            savePersonalParams["detail_json"] = Gson().toJson(moreInfoAdapter.params)
        }
        if (hasChangePhotos) {
            val photosId = mutableListOf<Int?>()
            for (data in photos.withIndex()) {
                if (data.value?.itemType == MyPhotoBean.PHOTO) {
                    photosId.add(data.value?.id)
                }
            }
            mPresenter?.addPhotoV2(
                savePersonalParams,
                photosId,
                2
            )
        } else {
            mPresenter?.addPhotoV2(
                savePersonalParams,
                mutableListOf(),
                type
            )
        }
    }


    private fun setScroeProgress() {
        binding.apply {
            val params = userFinishProgress.layoutParams as RelativeLayout.LayoutParams
            params.leftMargin = SizeUtils.dp2px(70F)
            params.rightMargin = SizeUtils.dp2px(15F)
            userFinishProgress.layoutParams = params
            //汇总每次的得分
            var totalGetScore = 0
            //相册的分数
            if (!data!!.photos_wall.isNullOrEmpty()) {
                totalGetScore += (data!!.photos_wall.size - 2) * data!!.score_rule.photo
            }
            //个签分数
            if (!data!!.sign.isEmpty() && data!!.sign.trim().isNotEmpty()) {
                userNickSign.text = data!!.sign
                totalGetScore += data!!.score_rule.about
            }
            //更多信息分數
            for (data in moreInfoAdapter.data) {
                if (data.find_tag != null && data.find_tag!!.title.isNotEmpty()) {
                    totalGetScore += data.point
                }
            }


            var progress = (totalGetScore * 1.0F / (data!!.score_rule!!.base_total) * 100).toInt()
            userScore20.text = "${progress}%"
            if (!UserManager.isvip && UserManager.gender == 1) {
                progress = (progress * 0.8).toInt()
            }
            if (Build.VERSION.SDK_INT >= 24) {
                userFinishProgress.setProgress(progress, true)
            } else {
                userFinishProgress.progress = progress
            }

            val translate = ObjectAnimator.ofFloat(
                userScore20, "translationX",
                ((ScreenUtils.getScreenWidth() - SizeUtils.dp2px(70F + 15 * 2) - SizeUtils.dp2px(38F)) * userFinishProgress.progress * 1.0f / 100)
            )
            translate.duration = 100
            translate.start()

        }

    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnBack -> {
                onBackPressed()
            }


            R.id.userNickName -> {//昵称
                startActivityForResult<NickNameActivity>(
                    102,
                    "type" to 1,
                    "content" to binding.userNickName.text.toString()
                )
            }

            R.id.userBirth -> { //生日
                showCalender(binding.userBirth)
            }
            R.id.userNickSign -> {//关于我
                startActivityForResult<NickNameActivity>(
                    105,
                    "type" to 2,
                    "content" to binding.userNickSign.text.toString()
                )
            }
            R.id.rightTextBtn -> {
                updatePhotos(0)
            }
            R.id.userContact -> { //更改用户的联系方式
                startActivity<ChangeUserContactActivity>()
            }

        }

    }

    /**
     * 展示条件选择器
     */
    private fun showConditionPicker(
        textview: TextView,
        title: String,
        param: String,
        findTagBean: FindTagBean? = null,
        optionsItems1: MutableList<FindTagBean>,
        indexOfData: Int
    ) {
        //条件选择器
        val pvOptions = OptionsPickerBuilder(this,
            OnOptionsSelectListener { options1, options2, options3, v ->
                if (optionsItems1[options1].id == -1)
                    moreInfoAdapter.params[param] = optionsItems1[options1].title
                else
                    moreInfoAdapter.params[param] = optionsItems1[options1].id
                textview.text = "${optionsItems1[options1].title}"
                moreInfoAdapter.data[indexOfData].find_tag =
                    FindTagBean(optionsItems1[options1].id, optionsItems1[options1].title)
                setScroeProgress()
                binding.barCl.rightTextBtn.isEnabled = true
            })
            .setSubmitText(getString(R.string.ok))
            .setCancelText(getString(R.string.cancel))
            .setTitleText(title)
            .setTitleColor(resources.getColor(R.color.colorBlack))
            .setTitleSize(16)
            .setDividerColor(resources.getColor(R.color.colorDivider))
            .setContentTextSize(20)
            .setDecorView((window.decorView.findViewById(android.R.id.content)) as ViewGroup)
            .setSubmitColor(resources.getColor(R.color.colorAccent))
            .build<FindTagBean>()

        //身高默认选中，男170 女160
        if (findTagBean != null && !findTagBean.title.isNullOrEmpty()) {
            for (data in optionsItems1.withIndex()) {
                if (title == getString(R.string.height)) {
                    if (data.value.title == findTagBean.title) {
                        pvOptions.setSelectOptions(data.index)
                        break
                    }
                } else {
                    if (data.value.id == findTagBean.id) {
                        pvOptions.setSelectOptions(data.index)
                        break
                    }
                }
            }
        } else if (title == getString(R.string.height)) {
            if (UserManager.gender == 1) { //男
                pvOptions.setSelectOptions(175 - 60)
            } else {
                pvOptions.setSelectOptions(165 - 60)
            } //女
        }
        pvOptions.setPicker(optionsItems1)
        pvOptions.show()
    }

    /**
     * 保存个人信息参数列表
     */
    private val savePersonalParams: HashMap<String, Any?> by lazy {
        hashMapOf(
            "accid" to UserManager.accid,
            "gender" to data?.gender
        )
    }

    /**
     * 展示日历
     */
    //错误使用案例： startDate.set(2013,1,1);  endDate.set(2020,12,1);
    //正确使用案例： startDate.set(2013,0,1);  endDate.set(2020,11,1);
    private fun showCalender(userBirth: TextView) {
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        startDate.set(endDate.get(Calendar.YEAR) - 50, 0, 1)
        endDate.set(
            endDate.get(Calendar.YEAR) - 18,
            endDate.get(Calendar.MONTH),
            endDate.get(Calendar.DATE)
        )
        val clOptions = TimePickerBuilder(this, OnTimeSelectListener { date, v ->
            //            getZodiac
            userBirth.text =
                "${TimeUtils.date2String(
                    date,
                    SimpleDateFormat("yyyy-MM-dd")
                )}/${TimeUtils.getZodiac(date)}"
            savePersonalParams["birth"] = TimeUtils.date2Millis(date) / 1000L
            isChange = true
            checkSaveEnable()
        })
            .setRangDate(startDate, endDate)
            .setDate(endDate)
            .setTitleText(getString(R.string.choose_birthday))
            .setTitleColor(Color.BLACK)//标题文字颜色
            .build()
        clOptions.show()
    }


    /**
     * 七牛上传图片
     */
    private fun uploadPicture(replaceAvator: Boolean = false, path: String) {
        val userProfile =
            "${Constants.FILE_NAME_INDEX}${Constants.USERCENTER}${SPUtils.getInstance(Constants.SPNAME)
                .getString(
                    "accid"
                )}/${System.currentTimeMillis()}/${RandomUtils.getRandomString(
                16
            )}"
        mPresenter?.uploadProfile(path, userProfile, replaceAvator)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.apply {
            if (resultCode == Activity.RESULT_OK) {
                when (requestCode) {
                    102 -> { //昵称
                        if (data != null) {
                            if (data.getIntExtra("type", 0) == 1) {
                                userNickName.text = data.getStringExtra("content")
                                savePersonalParams["nickname"] = userNickName.text.toString()
                                isChange = true
                                checkSaveEnable()
                            }
                        }
                    }
                    105 -> {//关于我
                        userNickSign.text = data?.getStringExtra("content")
                        savePersonalParams["sign"] = data?.getStringExtra("content")
                        this@MyInfoActivity.data!!.sign = data?.getStringExtra("content") ?: ""
                        setScroeProgress()
                        isChange = true
                        checkSaveEnable()
                    }
                    REPLACE_REQUEST,//替换头像
                    PictureConfig.CHOOSE_REQUEST
                    -> {
                        if (data != null) {
                            if (!PictureSelector.obtainMultipleResult(data).isNullOrEmpty()) {
                                uploadPicture(
                                    requestCode == REPLACE_REQUEST,
                                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                                        if (PictureSelector.obtainMultipleResult(data)[0].androidQToPath.isNullOrEmpty()) {
                                            PictureSelector.obtainMultipleResult(data)[0].path
                                        } else {
                                            PictureSelector.obtainMultipleResult(data)[0].androidQToPath
                                        }
                                    } else {
                                        PictureSelector.obtainMultipleResult(data)[0].path
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        checkIsForceChangeAvator()
        if (intent.getIntExtra(
                "type",
                FaceLivenessExpActivity.TYPE_ACCOUNT_NORMAL
            ) == FaceLivenessExpActivity.TYPE_ACCOUNT_DANGER
        )
            EventBus.getDefault().postSticky(AccountDangerEvent(AccountDangerDialog.VERIFY_ING))
    }

}